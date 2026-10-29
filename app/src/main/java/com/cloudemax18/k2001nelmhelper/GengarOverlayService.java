package com.cloudemax18.k2001nelmhelper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Movie;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import java.util.Locale;
import java.util.Random;

public class GengarOverlayService extends Service implements LocationListener, SensorEventListener {
    private static final String CHANNEL = "gengar_drive_companion";
    private WindowManager windowManager;
    private GhostView ghost;
    private LocationManager locationManager;
    private SensorManager sensorManager;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private float speedKmh = 0f, lastAccel = 0f, lateralAccel = 0f;
    private long lastLocationTime = 0L, stoppedSince = 0L, lastBrakeTime = 0L, lastTurnTime = 0L;
    private float lastSpeed = -1f;
    private String state = "idle";
    private String activePokemon = "gengar";
    private final Handler handler = new Handler();
    private final Random random = new Random();
    private final String[] phrases = new String[] {
        "¡Has vuelto!", "¡Vamos de aventura!", "¡Aquí estoy!", "¡Qué buen paseo!",
        "¡Boo! Je, je.", "¡Seguimos!", "¡Me gusta este camino!",
        "¡Todo bajo control!", "¡A por todas!", "¡Cañones listos!"
    };
    private final Runnable chatter = new Runnable() {
        @Override public void run() {
            if (ttsReady && ghost != null) speak(phrases[random.nextInt(phrases.length)]);
            handler.postDelayed(this, 45000L + random.nextInt(45000));
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        Notification.Builder nb = Build.VERSION.SDK_INT >= 26
            ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        Notification n = nb.setContentTitle("Compañero Pokémon flotante")
            .setContentText("Compañero animado con reacciones al movimiento")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true).build();
        startForeground(42, n);
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return; }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        ghost = new GhostView(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            android.graphics.PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.END;
        lp.x = 16; lp.y = 140;
        try { windowManager.addView(ghost, lp); }
        catch (Exception e) { Toast.makeText(this, "No se pudo mostrar la superposición", Toast.LENGTH_LONG).show(); stopSelf(); return; }

        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("es", "ES"));
                ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED;
                if (ttsReady) {
                    speak("¡Hola!");
                    handler.postDelayed(() -> { if (ttsReady && ghost != null) speak("¡Has vuelto!"); }, 4500L);
                    handler.postDelayed(chatter, 60000L);
                }
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accel == null) accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accel != null) sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
        Sensor rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotation != null) sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_UI);
        try {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, this);
                Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (last != null) onLocationChanged(last);
            } else state = "idle";
        } catch (Exception e) { state = "idle"; }
        ghost.invalidate();
    }

    private void speak(String phrase) {
        if (tts != null && ttsReady) {
            if (Build.VERSION.SDK_INT >= 21) tts.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "gengar-" + System.currentTimeMillis());
            else tts.speak(phrase, TextToSpeech.QUEUE_FLUSH, null);
            if (ghost != null) ghost.showBubble(phrase);
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "Compañero Pokémon", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        }
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String selected = intent.getStringExtra("pokemon");
            if ("blastoise".equals(selected)) activePokemon = "blastoise";
            else if ("gengar".equals(selected)) activePokemon = "gengar";
            if (ghost != null) ghost.setPokemon(activePokemon);
        }
        return START_STICKY;
    }
    @Override public IBinder onBind(Intent intent) { return null; }

    @Override public void onLocationChanged(Location loc) {
        long now = System.currentTimeMillis();
        float newSpeed = loc.hasSpeed() ? loc.getSpeed() * 3.6f : 0f;
        if (lastLocationTime > 0 && now > lastLocationTime) {
            float dt = (now - lastLocationTime) / 1000f;
            float decel = (lastSpeed - newSpeed) / dt;
            if (lastSpeed >= 12f && decel > 2.2f) {
                lastBrakeTime = now;
                state = "braking";
            } else if (Math.abs(newSpeed - lastSpeed) > 7f) {
                state = "boost";
            }
        }
        speedKmh = newSpeed;
        lastSpeed = newSpeed;
        lastLocationTime = now;
        if (speedKmh < 2f) {
            if (stoppedSince == 0) stoppedSince = now;
            if (!"braking".equals(state)) state = "idle";
        } else {
            stoppedSince = 0;
            if (now - lastBrakeTime < 2200L) state = "braking";
            else if (!"turning".equals(state) || now - lastTurnTime > 1800L) state = "moving";
        }
        if (ghost != null) ghost.invalidate();
    }
    @Override public void onProviderEnabled(String provider) { if (ghost != null) ghost.invalidate(); }
    @Override public void onProviderDisabled(String provider) { state = "idle"; if (ghost != null) ghost.invalidate(); }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override public void onSensorChanged(SensorEvent e) {
        long now = System.currentTimeMillis();
        if (e.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] matrix = new float[9];
            SensorManager.getRotationMatrixFromVector(matrix, e.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(matrix, orientation);
            lateralAccel = (float)Math.toDegrees(orientation[2]);
            if (Math.abs(lateralAccel) > 22f && speedKmh > 8f) {
                state = "turning";
                lastTurnTime = now;
            }
        } else if (e.values.length >= 3) {
            float x=e.values[0], y=e.values[1], z=e.values[2];
            float mag = (float)Math.sqrt(x*x + y*y + z*z);
            lastAccel = mag;
            if (e.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                if (speedKmh > 8f && mag > 2.5f) {
                    state = "braking";
                    lastBrakeTime = now;
                } else if (speedKmh > 8f && mag > 1.5f) {
                    state = "boost";
                }
            }
        }
        if (ghost != null) ghost.invalidate();
    }
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        try { if (locationManager != null) locationManager.removeUpdates(this); } catch (Exception ignored) {}
        try { if (sensorManager != null) sensorManager.unregisterListener(this); } catch (Exception ignored) {}
        try { if (ghost != null && windowManager != null) windowManager.removeView(ghost); } catch (Exception ignored) {}
        try { if (tts != null) { tts.stop(); tts.shutdown(); } } catch (Exception ignored) {}
        super.onDestroy();
    }

    private class GhostView extends View {
        private float downX, downY, startX, startY;
        private long downAt, bubbleUntil = 0L;
        private boolean compact = false;
        private String bubble = "";
        private Movie sprite;
        private Bitmap rangerSheet;
        private GengarAnimationPlayer animationPlayer;
        private long nextIdleVariationAt = 0L;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        GhostView(Context c) {
            super(c);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            try { animationPlayer = new GengarAnimationPlayer(c); } catch (Exception ignored) { animationPlayer = null; }
            setPokemon(activePokemon);
        }

        void setPokemon(String pokemon) {
            int resource = "blastoise".equals(pokemon) ? R.raw.blastoise : R.raw.gengar;
            try { sprite = Movie.decodeStream(getResources().openRawResource(resource)); }
            catch (Exception e) { sprite = null; }
            rangerSheet = null;
            if (animationPlayer != null && "gengar".equals(pokemon) && animationPlayer.hasSequence("idle")) {
                animationPlayer.play("idle");
                nextIdleVariationAt = System.currentTimeMillis() + 9000L;
            }
            if ("gengar".equals(pokemon)) {
                int sheetResource = getResources().getIdentifier("gengar_ranger2", "raw", getPackageName());
                if (sheetResource != 0) {
                    try {
                        Bitmap decoded = BitmapFactory.decodeStream(getResources().openRawResource(sheetResource));
                        rangerSheet = removeCyanBackground(decoded);
                    } catch (Exception ignored) { rangerSheet = null; }
                }
            }
            invalidate();
        }

        private Bitmap removeCyanBackground(Bitmap source) {
            if (source == null) return null;
            Bitmap result = source.copy(Bitmap.Config.ARGB_8888, true);
            int[] pixels = new int[result.getWidth() * result.getHeight()];
            result.getPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
            for (int i = 0; i < pixels.length; i++) {
                int color = pixels[i];
                int alpha = Color.alpha(color);
                int red = Color.red(color), green = Color.green(color), blue = Color.blue(color);
                if (alpha > 0 && red < 100 && green > 135 && blue > 135 && green - red > 45 && blue - red > 45) {
                    pixels[i] = Color.TRANSPARENT;
                }
            }
            result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
            return result;
        }

        void showBubble(String text) {
            bubble = text;
            bubbleUntil = System.currentTimeMillis() + 3200L;
            invalidate();
        }

        @Override protected void onMeasure(int w, int h) {
            setMeasuredDimension(compact ? 142 : 220, compact ? 128 : 198);
        }

        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            long now = System.currentTimeMillis();
            float w = getWidth(), h = getHeight();
            float bob = (float)Math.sin(now / ("moving".equals(state) ? 115.0 : 260.0)) * ("moving".equals(state) ? 8f : 4f);
            float shakeX = 0f, tilt = 0f, scaleBoost = 1f;
            if ("braking".equals(state)) { shakeX = (float)Math.sin(now / 24.0) * 7f; tilt = -7f; }
            else if ("turning".equals(state)) { shakeX = (float)Math.sin(now / 90.0) * 8f; tilt = (float)Math.sin(now / 150.0) * 8f; }
            else if ("boost".equals(state)) { scaleBoost = 1.10f + (float)Math.sin(now / 55.0) * 0.035f; bob -= 5f; }
            else if ("moving".equals(state)) { scaleBoost = 1f + (float)Math.sin(now / 180.0) * 0.025f; }

            if ("gengar".equals(activePokemon) && animationPlayer != null && animationPlayer.isAvailable()) {
                if (animationPlayer.isFinished()) {
                    animationPlayer.play("idle");
                    nextIdleVariationAt = now + 9000L + random.nextInt(12000);
                } else if ("idle".equals(animationPlayer.getCurrentName()) && now >= nextIdleVariationAt) {
                    animationPlayer.playRandomIdleVariation();
                    nextIdleVariationAt = now + 12000L + random.nextInt(18000);
                }
                float availW = w - 12f, availH = h - (now < bubbleUntil ? 42f : 8f);
                float size = Math.min(Math.min(availW, availH), 168f) * scaleBoost;
                RectF dst = new RectF((w-size)/2f + shakeX, (h-size)/2f + bob + (now < bubbleUntil ? 14f : 0f), (w+size)/2f + shakeX, (h+size)/2f + bob + (now < bubbleUntil ? 14f : 0f));
                c.save();
                c.rotate(tilt, w/2f + shakeX, h/2f + bob);
                animationPlayer.draw(c, dst);
                c.restore();
            } else if (rangerSheet != null && "gengar".equals(activePokemon)) {
                int row;
                long frameDelay;
                if ("moving".equals(state)) { row = 1; frameDelay = 95L; }
                else if ("boost".equals(state)) { row = 1; frameDelay = 55L; }
                else if ("braking".equals(state) || "turning".equals(state)) { row = 2; frameDelay = 110L; }
                else { row = 0; frameDelay = 180L; }
                int frame = (int)((now / frameDelay) % 8L);
                if ("turning".equals(state)) frame = 7 - frame;
                Rect src = new Rect(294 + frame * 32, 120 + row * 35, 324 + frame * 32, 152 + row * 35);
                float availW = w - 12f, availH = h - (now < bubbleUntil ? 42f : 8f);
                float size = Math.min(Math.min(availW, availH), 168f) * scaleBoost;
                RectF dst = new RectF(-size / 2f, -size / 2f, size / 2f, size / 2f);
                paint.setFilterBitmap(false);
                c.save();
                c.translate(w / 2f + shakeX, h / 2f + bob + (now < bubbleUntil ? 14f : 0f));
                c.rotate(tilt);
                c.drawBitmap(rangerSheet, src, dst, paint);
                c.restore();
            } else if (sprite != null) {
                int duration = Math.max(1, sprite.duration());
                sprite.setTime((int)(now % duration));
                float availW = w - 12f, availH = h - (now < bubbleUntil ? 42f : 8f);
                float scale = Math.min(availW / sprite.width(), availH / sprite.height()) * scaleBoost;
                float dw = sprite.width() * scale, dh = sprite.height() * scale;
                c.save();
                c.translate((w - dw) / 2f + shakeX, (h - dh) / 2f + bob + (now < bubbleUntil ? 14f : 0f));
                c.rotate(tilt, dw / 2f, dh / 2f);
                c.scale(scale, scale);
                sprite.draw(c, 0, 0);
                c.restore();
            }
            if (now < bubbleUntil && bubble.length() > 0) {
                paint.setColor(0xEEFFFFFF);
                RectF r = new RectF(4f, 3f, w - 4f, 37f);
                c.drawRoundRect(r, 16f, 16f, paint);
                paint.setColor(0xFF281B3C);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(13f);
                paint.setFakeBoldText(true);
                String shown = bubble.length() > 27 ? bubble.substring(0, 25) + "…" : bubble;
                c.drawText(shown, w / 2f, 25f, paint);
                paint.setFakeBoldText(false);
            }
            postInvalidateDelayed(40);
        }

        @Override protected void onDetachedFromWindow() {
            if (animationPlayer != null) animationPlayer.recycle();
            super.onDetachedFromWindow();
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            WindowManager.LayoutParams lp = (WindowManager.LayoutParams)getLayoutParams();
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                downX=e.getRawX(); downY=e.getRawY(); startX=lp.x; startY=lp.y; downAt=System.currentTimeMillis(); return true;
            }
            if (e.getAction() == MotionEvent.ACTION_MOVE) {
                lp.x = (int)(startX + downX - e.getRawX());
                lp.y = (int)(startY + e.getRawY() - downY);
                try { windowManager.updateViewLayout(this,lp); } catch(Exception ignored) {}
                return true;
            }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                if (Math.abs(e.getRawX()-downX)<12 && Math.abs(e.getRawY()-downY)<12) {
                    if (System.currentTimeMillis() - downAt > 900L) {
                        speak("¡Hasta luego!");
                        Toast.makeText(GengarOverlayService.this, "Compañero detenido", Toast.LENGTH_SHORT).show();
                        stopSelf();
                    } else {
                        compact = !compact;
                        requestLayout();
                        showBubble(compact ? "¡Modo pequeño!" : "¡Aquí estoy!");
                        if (!compact && ttsReady) speak("¡Aquí estoy!");
                    }
                }
                return true;
            }
            return true;
        }
    }
}
