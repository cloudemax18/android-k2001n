package com.cloudemax18.gengarfloat

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.os.SystemClock
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(7, 31, 54)
        window.navigationBarColor = Color.rgb(7, 31, 54)
        window.decorView.systemUiVisibility = 0
        setContentView(GengarPetView(this))
    }
}

private class GengarPetView(context: android.content.Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val background = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(7, 31, 54) }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 20f * resources.displayMetrics.scaledDensity
        typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(173, 199, 224)
        textSize = 13f * resources.displayMetrics.scaledDensity
        textAlign = Paint.Align.CENTER
    }

    private var gengar: Bitmap? = loadArtwork()
    private var startTime = SystemClock.uptimeMillis()
    private var reactionUntil = 0L
    private var reaction = Reaction.IDLE
    private var direction = 1f
    private var driftX = 0f
    private var driftY = 0f
    private var lastTapX = -1f
    private var lastTapY = -1f

    private enum class Reaction { IDLE, HAPPY, SURPRISED, SPIN }

    private fun loadArtwork(): Bitmap? {
        return try {
            // Add the provided sheet as app/src/main/assets/gengar_sheet.jpg.
            val source = context.assets.open("gengar_sheet.jpg").use {
                BitmapFactory.decodeStream(it)
            } ?: return null
            // The large Gengar illustration is on the right side of the supplied sheet.
            val left = (source.width * 0.55f).toInt().coerceIn(0, source.width - 1)
            val top = 0
            val right = (source.width * 0.99f).toInt().coerceAtMost(source.width)
            val bottom = (source.height * 0.44f).toInt().coerceAtMost(source.height)
            val crop = Bitmap.createBitmap(source, left, top, right - left, bottom - top)
            val pixels = IntArray(crop.width * crop.height)
            crop.getPixels(pixels, 0, crop.width, 0, 0, crop.width, crop.height)
            // Remove the sheet's flat teal background so Gengar floats cleanly.
            for (i in pixels.indices) {
                val c = pixels[i]
                val r = Color.red(c); val g = Color.green(c); val b = Color.blue(c)
                if (b > 105 && g in 55..125 && b > r + 45 && (b - r) > 75) {
                    pixels[i] = Color.TRANSPARENT
                }
            }
            val transparent = Bitmap.createBitmap(crop.width, crop.height, Bitmap.Config.ARGB_8888)
            transparent.setPixels(pixels, 0, crop.width, 0, 0, crop.width, crop.height)
            crop.recycle()
            source.recycle()
            transparent
        } catch (_: Exception) {
            null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), background)
        val now = SystemClock.uptimeMillis()
        val t = (now - startTime) / 1000f
        val activeReaction = now < reactionUntil
        if (!activeReaction) reaction = Reaction.IDLE

        val centerX = width * 0.5f + sin(t * 0.48f) * width * 0.20f + driftX
        val centerY = height * 0.47f + sin(t * 1.65f) * height * 0.035f + driftY
        val baseSize = (width * 0.74f).coerceAtMost(height * 0.48f)
        val pulse = 1f + sin(t * 2.2f) * 0.025f
        val reactionScale = when (reaction) {
            Reaction.SURPRISED -> 1.13f
            Reaction.HAPPY -> 1.04f + sin(t * 12f) * 0.035f
            Reaction.SPIN -> 1f
            else -> 1f
        }
        val size = baseSize * pulse * reactionScale
        val bob = sin(t * 1.65f) * 8f * resources.displayMetrics.density
        val dest = RectF(centerX - size / 2f, centerY - size / 2f + bob,
            centerX + size / 2f, centerY + size / 2f + bob)

        val save = canvas.save()
        canvas.rotate(if (reaction == Reaction.SPIN) (t * 900f) % 360f else sin(t * 0.9f) * 2.5f,
            centerX, centerY + bob)
        if (direction < 0f) {
            canvas.scale(-1f, 1f, centerX, centerY + bob)
        }
        val art = gengar
        if (art != null) {
            paint.alpha = 255
            canvas.drawBitmap(art, null, dest, paint)
        } else {
            drawFallbackGhost(canvas, centerX, centerY + bob, size)
        }
        canvas.restoreToCount(save)

        canvas.drawText("GENGAR FLOAT", width / 2f, 44f * resources.displayMetrics.density, titlePaint)
        canvas.drawText(
            when (reaction) {
                Reaction.HAPPY -> "¡Le gusta que lo acaricies!"
                Reaction.SURPRISED -> "¡Eh! ¿Qué fue eso?"
                Reaction.SPIN -> "¡Mareo fantasmal!"
                else -> "Tócame para ver qué pasa"
            },
            width / 2f, height - 38f * resources.displayMetrics.density, hintPaint
        )
        if (lastTapX >= 0f && now - reactionUntil < 650L && reaction == Reaction.IDLE) {
            lastTapX = -1f; lastTapY = -1f
        }
        postInvalidateOnAnimation()
    }

    private fun drawFallbackGhost(canvas: Canvas, cx: Float, cy: Float, size: Float) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(119, 72, 163) }
        val r = RectF(cx - size * .36f, cy - size * .30f, cx + size * .36f, cy + size * .30f)
        canvas.drawOval(r, p)
        p.color = Color.rgb(244, 61, 98)
        canvas.drawOval(RectF(cx - size*.22f, cy-size*.06f, cx-size*.04f, cy+size*.06f), p)
        canvas.drawOval(RectF(cx + size*.04f, cy-size*.06f, cx+size*.22f, cy+size*.06f), p)
        p.color = Color.WHITE
        canvas.drawArc(RectF(cx-size*.22f, cy+size*.02f, cx+size*.22f, cy+size*.23f), 0f, 180f, true, p)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val now = SystemClock.uptimeMillis()
            reaction = when (Random.nextInt(3)) {
                0 -> Reaction.HAPPY
                1 -> Reaction.SURPRISED
                else -> Reaction.SPIN
            }
            reactionUntil = now + if (reaction == Reaction.SPIN) 850L else 650L
            direction *= -1f
            driftX = Random.nextFloat() * width * 0.12f - width * 0.06f
            driftY = Random.nextFloat() * height * 0.08f - height * 0.04f
            lastTapX = event.x
            lastTapY = event.y
            performClick()
            invalidate()
            return true
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
