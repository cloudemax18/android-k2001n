package com.cloudemax18.k2001nelmhelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Frame-based Gengar animation player for assets/animations.json.
 *
 * Expected asset layout:
 *   assets/animations.json
 *   assets/animations/<sequence-name>/<frame files>.png
 *
 * This helper deliberately falls back to the caller's existing GIF renderer
 * when the new sprite assets are absent or fail to load.
 */
final class GengarAnimationPlayer {
    private static final String MANIFEST = "animations.json";
    private final Context context;
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final Random random = new Random();
    private final JSONObject sequences;
    private String currentName = "idle";
    private List<Bitmap> currentFrames = Collections.emptyList();
    private long sequenceStartedAt;
    private int frameDurationMs = 150;
    private boolean loop = true;

    GengarAnimationPlayer(Context context) {
        this.context = context.getApplicationContext();
        JSONObject parsed = null;
        try (InputStream in = this.context.getAssets().open(MANIFEST)) {
            byte[] bytes = new byte[in.available()];
            int offset = 0;
            while (offset < bytes.length) {
                int count = in.read(bytes, offset, bytes.length - offset);
                if (count < 0) break;
                offset += count;
            }
            parsed = new JSONObject(new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            // No new assets yet; the legacy Movie renderer remains available.
        }
        sequences = parsed == null ? new JSONObject() : parsed.optJSONObject("sequences");
        if (sequences == null) {
            // Keep the player unavailable so the legacy renderer can continue.
            return;
        }
        play("idle");
    }

    boolean isAvailable() {
        return !currentFrames.isEmpty();
    }

    String getCurrentName() {
        return currentName;
    }

    boolean hasSequence(String name) {
        return sequences.has(name);
    }

    void play(String name) {
        JSONObject spec = sequences.optJSONObject(name);
        if (spec == null) return;
        String path = spec.optString("path", "animations/" + name + "/");
        int expectedFrames = spec.optInt("frames", 0);
        int duration = Math.max(30, spec.optInt("frameDurationMs", 150));
        boolean shouldLoop = spec.optBoolean("loop", false);
        ArrayList<Bitmap> decoded = new ArrayList<>();
        for (int i = 0; i < expectedFrames; i++) {
            Bitmap bitmap = loadFrame(path, i);
            if (bitmap != null) decoded.add(bitmap);
        }
        if (decoded.isEmpty()) return;
        releaseFrames(currentFrames);
        currentFrames = decoded;
        currentName = name;
        frameDurationMs = duration;
        loop = shouldLoop;
        sequenceStartedAt = android.os.SystemClock.uptimeMillis();
    }

    void playRandomIdleVariation() {
        ArrayList<String> candidates = new ArrayList<>();
        Iterator<String> keys = sequences.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject spec = sequences.optJSONObject(key);
            if (spec != null && spec.optBoolean("loop", false) && !"idle".equals(key)) {
                candidates.add(key);
            }
        }
        if (!candidates.isEmpty()) play(candidates.get(random.nextInt(candidates.size())));
        else play("idle");
    }

    boolean isFinished() {
        if (loop || currentFrames.isEmpty()) return false;
        long elapsed = android.os.SystemClock.uptimeMillis() - sequenceStartedAt;
        return elapsed >= (long) currentFrames.size() * frameDurationMs;
    }

    void draw(Canvas canvas, RectF destination) {
        if (currentFrames.isEmpty()) return;
        long elapsed = Math.max(0L, android.os.SystemClock.uptimeMillis() - sequenceStartedAt);
        int index = (int)(elapsed / frameDurationMs);
        if (loop) index %= currentFrames.size();
        else index = Math.min(index, currentFrames.size() - 1);
        canvas.drawBitmap(currentFrames.get(index), null, destination, paint);
    }

    void recycle() {
        releaseFrames(currentFrames);
        currentFrames = Collections.emptyList();
    }

    private Bitmap loadFrame(String directory, int index) {
        String base = directory.endsWith("/") ? directory : directory + "/";
        String[] names = new String[] {
            base + String.format(java.util.Locale.ROOT, "frame_%03d.png", index),
            base + String.format(java.util.Locale.ROOT, "%03d.png", index),
            base + String.format(java.util.Locale.ROOT, "%02d.png", index),
            base + index + ".png"
        };
        for (String name : names) {
            try (InputStream in = context.getAssets().open(name)) {
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                if (bitmap != null) return bitmap;
            } catch (Exception ignored) { }
        }
        return null;
    }

    private static void releaseFrames(List<Bitmap> frames) {
        for (Bitmap bitmap : frames) {
            if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        }
    }
}
