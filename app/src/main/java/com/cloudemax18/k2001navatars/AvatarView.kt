package com.cloudemax18.k2001navatars

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Provisional vector characters. Replace these drawing functions with Gemini PNG/sprite assets
 * once the designer delivers the transparent sprite sheets.
 */
class AvatarView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val phrases = listOf(
        "¡Hola, compañero!", "¡Vaya, vaya!", "¡En marcha!", "Lo he visto todo…",
        "¡Qué aventura!", "¡Soy increíble!", "¡Cuidado, fantasma!",
        "¿Un cafecito?", "¡Abracadabra!", "¡Ahora vuelvo.",
        "¡Qué buen día!", "¡No tengo miedo!", "¿Rodamos, jefe?",
        "¡Buenos días!", "¡Buenas tardes!", "¡Buenas noches!",
        "¡Qué susto!", "¡Estoy de buen humor!", "¡Hora de una siesta!",
        "¡Mira mi magia!", "¡Vamos de paseo!", "¡Eso ha sido genial!"
    )
    private var phrase = "Salut, mon pote !"
    private var phraseUntil = System.currentTimeMillis() + 3500
    private var mood = 0
    private var reaperX = 0.24f
    private var ghostX = 0.72f
    private var yFactor = 0.63f
    private var vx = 0.0025f
    private var vy = 0.0014f
    private var tick = 0
    private var lastTouch = 0L

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width <= 0 || height <= 0) return
        tick++
        val now = System.currentTimeMillis()
        if (now >= phraseUntil) {
            phrase = phrases.random()
            phraseUntil = now + Random.nextLong(2500, 6500)
            if (Random.nextInt(4) == 0) mood = Random.nextInt(0, 6)
        }
        reaperX += vx
        ghostX -= vx * 0.72f
        yFactor += vy
        if (reaperX !in 0.12f..0.40f) vx = -vx
        if (yFactor !in 0.48f..0.76f) vy = -vy
        if (ghostX !in 0.60f..0.86f) ghostX = if (ghostX < 0.60f) 0.86f else 0.60f

        val hour = SimpleDateFormat("H", Locale.FRANCE).format(Date()).toIntOrNull() ?: 12
        val night = hour >= 20 || hour < 6
        val scale = if (width < height) 0.72f else 0.95f
        val cy = height * yFactor
        val bob = kotlin.math.sin(tick / 12.0).toFloat() * height * 0.012f
        drawReaper(canvas, width * reaperX, cy + bob, minOf(width, height) * 0.105f * scale, night)
        drawGhost(canvas, width * ghostX, cy - bob, minOf(width, height) * 0.105f * scale, night)
        drawBubble(canvas, width * 0.5f, height * 0.22f, width * 0.55f, phrase)
        postInvalidateDelayed(42)
    }

    private fun drawReaper(c: Canvas, x: Float, y: Float, r: Float, night: Boolean) {
        c.save()
        c.translate(x, y)
        // Hood silhouette
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        val hood = Path().apply {
            moveTo(0f, -r * 1.25f); cubicTo(-r * 0.7f, -r * 0.9f, -r * 1.0f, -r * 0.1f, -r * 0.7f, r * 0.45f)
            cubicTo(-r * 0.55f, r * 0.95f, r * 0.55f, r * 0.95f, r * 0.7f, r * 0.45f)
            cubicTo(r * 1.0f, -0.1f * r, r * 0.7f, -r * 0.9f, 0f, -r * 1.25f); close()
        }
        c.drawPath(hood, paint)
        paint.color = Color.WHITE
        c.drawOval(RectF(-r * 0.52f, -r * 0.48f, r * 0.52f, r * 0.48f), paint)
        paint.color = Color.BLACK
        c.drawOval(RectF(-r * 0.42f, -r * 0.18f, -r * 0.08f, -r * 0.02f), paint)
        c.drawOval(RectF(r * 0.08f, -r * 0.18f, r * 0.42f, -r * 0.02f), paint)
        val grin = Path().apply { moveTo(-r*0.22f, r*0.20f); lineTo(-r*0.12f, r*0.36f); lineTo(0f, r*0.22f); lineTo(r*0.12f, r*0.36f); lineTo(r*0.22f, r*0.20f); close() }
        c.drawPath(grin, paint)
        if (mood == 1) {
            paint.color = Color.YELLOW
            c.drawCircle(r*0.58f, -r*0.8f, r*0.18f, paint)
        }
        c.restore()
    }

    private fun drawGhost(c: Canvas, x: Float, y: Float, r: Float, night: Boolean) {
        c.save()
        c.translate(x, y)
        paint.color = if (night) Color.rgb(250, 248, 235) else Color.WHITE
        paint.style = Paint.Style.FILL
        c.drawOval(RectF(-r*0.72f, -r*1.0f, r*0.72f, r*0.78f), paint)
        val hem = Path().apply {
            moveTo(-r*0.7f, r*0.38f); cubicTo(-r*0.4f, r*0.15f, -r*0.3f, r*0.95f, 0f, r*0.68f)
            cubicTo(r*0.25f, r*0.95f, r*0.35f, r*0.25f, r*0.7f, r*0.42f)
            lineTo(r*0.58f, r*0.72f); lineTo(-r*0.56f, r*0.72f); close()
        }
        c.drawPath(hem, paint)
        paint.color = Color.BLACK
        c.drawRoundRect(RectF(-r*0.60f, -r*0.26f, -r*0.03f, r*0.20f), r*0.12f, r*0.12f, paint)
        c.drawRoundRect(RectF(r*0.03f, -r*0.26f, r*0.60f, r*0.20f), r*0.12f, r*0.12f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = r*0.045f
        c.drawArc(RectF(-r*0.18f, r*0.08f, r*0.22f, r*0.42f), 15f, 150f, false, paint)
        paint.style = Paint.Style.FILL
        if (mood == 2) {
            paint.color = Color.rgb(255, 190, 30)
            c.drawCircle(r*0.68f, -r*0.75f, r*0.14f, paint)
        }
        c.restore()
    }

    private fun drawBubble(c: Canvas, cx: Float, cy: Float, w: Float, text: String) {
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(225, 255, 255, 255)
        val box = RectF(cx-w/2, cy-38f, cx+w/2, cy+38f)
        c.drawRoundRect(box, 28f, 28f, paint)
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = minOf(32f, width * 0.032f)
        c.drawText(text, cx, cy + paint.textSize*0.34f, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val now = System.currentTimeMillis()
            mood = Random.nextInt(0, 7)
            phrase = listOf("¡Ah!", "¡Hola!", "¡Me encontraste!", "¡Oh, no!", "¡Yupi!", "¡Magia!", "¡A correr!", "¡Qué alegría!", "¡Socorro!", "¡Sorpresa!").random()
            phraseUntil = now + 3500
            vx = (Random.nextFloat() * 0.008f + 0.002f) * if (Random.nextBoolean()) 1 else -1
            vy = (Random.nextFloat() * 0.004f + 0.001f) * if (Random.nextBoolean()) 1 else -1
            lastTouch = now
            invalidate()
            return true
        }
        return true
    }
}
