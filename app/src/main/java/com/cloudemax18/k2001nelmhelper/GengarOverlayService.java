package com.cloudemax18.k2001nelmhelper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import java.util.Locale;

public class GengarOverlayService extends Service implements LocationListener, SensorEventListener {
    private static final String CHANNEL = "gengar_drive_companion";
    private WindowManager windowManager;
    private GhostView ghost;
    private LocationManager locationManager;
    private SensorManager sensorManager;
    private float speedKmh = 0f, lastAccel = 0f;
    private long lastLocationTime = 0L, stoppedSince = 0L, lastBrakeTime = 0L;
    private float lastSpeed = -1f;
    private String state = "Esperando GPS";
    private boolean parked = false;

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        Notification n = new Notification.Builder(this, CHANNEL)
            .setContentTitle("Gengar está flotando")
            .setContentText("Observando velocidad y movimiento del vehículo")
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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accel == null) accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accel != null) sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
        try {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, this);
                Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (last != null) onLocationChanged(last);
            } else state = "Falta permiso GPS";
        } catch (Exception e) { state = "GPS no disponible"; }
        ghost.invalidate();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "Gengar conductor", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        }
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override public IBinder onBind(Intent intent) { return null; }

    @Override public void onLocationChanged(Location loc) {
        long now = System.currentTimeMillis();
        float newSpeed = loc.hasSpeed() ? loc.getSpeed() * 3.6f : 0f;
        if (lastLocationTime > 0 && now > lastLocationTime) {
            float decel = (lastSpeed - newSpeed) / ((now - lastLocationTime) / 1000f);
            if (lastSpeed >= 12f && decel > 2.2f) lastBrakeTime = now;
        }
        speedKmh = newSpeed;
        lastSpeed = newSpeed;
        lastLocationTime = now;
        if (speedKmh < 2.0f) {
            if (stoppedSince == 0) stoppedSince = now;
            parked = now - stoppedSince > 120000L;
            state = parked ? "Parado · posible estacionado" : "Detenido";
        } else {
            stoppedSince = 0;
            parked = false;
            state = now - lastBrakeTime < 2500L ? "Frenada detectada" : "En movimiento";
        }
        if (ghost != null) ghost.invalidate();
    }
    @Override public void onProviderEnabled(String provider) { state = "GPS activo"; if (ghost != null) ghost.invalidate(); }
    @Override public void onProviderDisabled(String provider) { state = "Activa ubicación GPS"; if (ghost != null) ghost.invalidate(); }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override public void onSensorChanged(SensorEvent e) {
        if (e.values.length >= 3) {
            float mag = (float)Math.sqrt(e.values[0]*e.values[0] + e.values[1]*e.values[1] + e.values[2]*e.values[2]);
            if (e.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                if (speedKmh > 8 && mag > 2.5f) lastBrakeTime = System.currentTimeMillis();
            } else {
                lastAccel = mag;
            }
        }
        if (ghost != null) ghost.invalidate();
    }
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override public void onDestroy() {
        try { if (locationManager != null) locationManager.removeUpdates(this); } catch (Exception ignored) {}
        try { if (sensorManager != null) sensorManager.unregisterListener(this); } catch (Exception ignored) {}
        try { if (ghost != null && windowManager != null) windowManager.removeView(ghost); } catch (Exception ignored) {}
        super.onDestroy();
    }

    private class GhostView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float downX, downY, startX, startY;
        private long downAt;
        private boolean compact = false;
        private final int purple = Color.rgb(117, 54, 183);
        GhostView(Context c) { super(c); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
        @Override protected void onMeasure(int w, int h) { setMeasuredDimension(compact ? 104 : 156, compact ? 100 : 166); }
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float w = getWidth(), h = getHeight();
            long t = System.currentTimeMillis();
            float bob = (float)Math.sin(t / (state.contains("Movimiento") ? 160.0 : 320.0)) * 5f;
            if (state.contains("Frenada")) bob -= 7;
            c.save(); c.translate(0, bob);
            // Sombra flotante
            p.setColor(0x55301848); p.setStyle(Paint.Style.FILL);
            c.drawOval(new RectF(w*.24f,h*.79f,w*.78f,h*.88f),p);
            // Cuerpo fantasma con puntas y silueta original dibujada en Canvas
            Path body = new Path();
            body.moveTo(w*.20f,h*.67f); body.lineTo(w*.12f,h*.48f); body.lineTo(w*.23f,h*.42f);
            body.lineTo(w*.17f,h*.25f); body.lineTo(w*.36f,h*.31f); body.lineTo(w*.49f,h*.15f);
            body.lineTo(w*.61f,h*.30f); body.lineTo(w*.80f,h*.22f); body.lineTo(w*.75f,h*.43f);
            body.lineTo(w*.88f,h*.49f); body.lineTo(w*.78f,h*.68f); body.lineTo(w*.70f,h*.77f);
            body.lineTo(w*.57f,h*.72f); body.lineTo(w*.47f,h*.82f); body.lineTo(w*.37f,h*.73f);
            body.lineTo(w*.25f,h*.78f); body.close();
            p.setColor(purple); p.setStyle(Paint.Style.FILL); c.drawPath(body,p);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2.4f); p.setColor(0xFF47216F); c.drawPath(body,p);
            p.setStyle(Paint.Style.FILL);
            // Eyes react to braking
            p.setColor(Color.WHITE);
            if (state.contains("Frenada")) {
                c.drawOval(new RectF(w*.30f,h*.39f,w*.45f,h*.54f),p);
                c.drawOval(new RectF(w*.56f,h*.39f,w*.71f,h*.54f),p);
            } else {
                c.drawOval(new RectF(w*.31f,h*.40f,w*.45f,h*.52f),p);
                c.drawOval(new RectF(w*.56f,h*.40f,w*.70f,h*.52f),p);
            }
            p.setColor(0xFF171026);
            c.drawOval(new RectF(w*.35f,h*.42f,w*.42f,h*.51f),p);
            c.drawOval(new RectF(w*.60f,h*.42f,w*.67f,h*.51f),p);
            // Grin
            p.setColor(0xFF24102F); c.drawRoundRect(new RectF(w*.31f,h*.55f,w*.70f,h*.67f),8,8,p);
            p.setColor(0xFFFFE6F4);
            Path teeth = new Path(); teeth.moveTo(w*.34f,h*.56f); teeth.lineTo(w*.43f,h*.56f); teeth.lineTo(w*.39f,h*.61f);
            teeth.lineTo(w*.50f,h*.56f); teeth.lineTo(w*.58f,h*.56f); teeth.lineTo(w*.55f,h*.61f); teeth.lineTo(w*.67f,h*.56f);
            teeth.lineTo(w*.63f,h*.64f); teeth.lineTo(w*.38f,h*.64f); teeth.close(); c.drawPath(teeth,p);
            c.restore();
            // Speed chip
            p.setStyle(Paint.Style.FILL); p.setColor(0xDD171323); c.drawRoundRect(new RectF(4,h-27,w-4,h-3),12,12,p);
            p.setColor(Color.WHITE); p.setTextSize(11f); p.setTypeface(android.graphics.Typeface.create("sans-serif-medium",0));
            String label = String.format(Locale.getDefault(), "%.0f km/h · %s", speedKmh, state);
            if (label.length() > 22) label = String.format(Locale.getDefault(), "%.0f km/h · %s", speedKmh, state.contains("Frenada") ? "FRENADA" : (parked ? "PARADO" : (speedKmh < 2 ? "DETENIDO" : "MOVIENDO")));
            c.drawText(label, 10, h-11, p);
            postInvalidateDelayed(80);
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
                    compact = !compact; requestLayout();
                }
                return true;
            }
            return true;
        }
    }
}
