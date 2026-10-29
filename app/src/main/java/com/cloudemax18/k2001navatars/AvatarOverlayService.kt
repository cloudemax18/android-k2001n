package com.cloudemax18.k2001navatars

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.util.DisplayMetrics

class AvatarOverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var avatarView: AvatarView? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (avatarView != null) return START_STICKY
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val view = AvatarView(this)
        avatarView = view
        val layout = WindowManager.LayoutParams(
            620,
            460,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 20
            y = 60
        }
        params = layout
        view.onWindowPositionRequested = { nx, ny ->
            val wm = windowManager ?: return@onWindowPositionRequested
            val lp = params ?: return@onWindowPositionRequested
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(metrics)
            val maxX = (metrics.widthPixels - lp.width).coerceAtLeast(0)
            val maxY = (metrics.heightPixels - lp.height).coerceAtLeast(0)
            val targetX = (nx * metrics.widthPixels).toInt().coerceIn(0, maxX)
            val targetY = (ny * metrics.heightPixels).toInt().coerceIn(0, maxY)
            if (kotlin.math.abs(lp.x - targetX) > 2 || kotlin.math.abs(lp.y - targetY) > 2) {
                lp.x = targetX
                lp.y = targetY
                try { wm.updateViewLayout(view, lp) } catch (_: Exception) { }
            }
        }
        try {
            windowManager?.addView(view, layout)
        } catch (_: Exception) {
            avatarView = null
            params = null
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        avatarView?.let { view ->
            view.onWindowPositionRequested = null
            try { windowManager?.removeView(view) } catch (_: Exception) { }
        }
        avatarView = null
        params = null
        windowManager = null
        super.onDestroy()
    }
}
