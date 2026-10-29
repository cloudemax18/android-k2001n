package com.cloudemax18.k2001navatars

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager

class AvatarOverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var avatarView: AvatarView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (avatarView != null) return START_STICKY
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        avatarView = AvatarView(this)
        val params = WindowManager.LayoutParams(
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
        try {
            windowManager?.addView(avatarView, params)
        } catch (_: Exception) {
            avatarView = null
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        avatarView?.let { view -> try { windowManager?.removeView(view) } catch (_: Exception) {} }
        avatarView = null
        windowManager = null
        super.onDestroy()
    }
}
