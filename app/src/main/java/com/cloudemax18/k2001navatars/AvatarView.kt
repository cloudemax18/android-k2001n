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
        "Salut, mon pote !", "Oh là là !", "C'est parti !", "J'ai tout vu…",
        "Quelle aventure !", "Je suis trop fort !", "Attention, fantôme !",
        "Un petit café ?", "Abracadabra !", "Je reviens tout de suite !",
        "Quelle belle journée !", "Même pas peur !", "On roule, chef ?"
    )
    private var phrase = "Salut, mon pote !"
    private var phraseUntil = System.currentTimeMillis() + 3500
    private var mood = 0
    private var reaperX = .24f
    private var ghostX = .72f
    private var yFactor = .63f
    private var vx = .0025f
    private var vy = .0014f
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
        ghostX -= vx * .72f
        yFactor += vy
        if (reaperX !in 0.12f..0.40f) vx = -vx
        if (yFactor !in 0.48f..0.76f) vy = -vy
        if (ghostX !in 0.60f..0.86f) ghostX = if (ghostX < .60f) .86f else .60f

        val hour = SimpleDateFormat("H", Locale.FRANCE).format(Date()).toIntOrNull() ?: 12
        val night = hour >= 20 || hour < 6
        val scale = if (width < height) .72f else .95f
        val cy = height * yFactor
        val bob = kotlin.math.sin(tick / 12.0).toFloat() * height * .012f
        drawReaper(canvas, width * reaperX, cy + bob, minOf(width, height) * .105f * scale, night)
        drawGhost(canvas, width * ghostX, cy - bob, minOf(width, height) * .105f * scale, night)
        drawBubble(canvas, width * .5f, height * .22f, width * .55f, phrase)
        postInvalidateDelayed(42)
    }

    private fun drawReaper(c: Canvas, x: Float, y: Float, r: Float, night: Boolean) {
        c.save()
        c.translate(x, y)
        // Hood silhouette
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        val hood = Path().apply {
            moveTo(0f, -r * 1.25f); cubicTo(-r * .7f, -r * .9f, -r * 1.0f, -r * .1f, -r * .7f, r * .45f)
            cubicTo(-r * .55f, r * .95f, r * .55f, r * .95f, r * .7f, r * .45f)
            cubicTo(r * 1.0f, -.1f * r, r * .7f, -r * .9f, 0f, -r * 1.25f); close()
        }
        c.drawPath(hood, paint)
        paint.color = Color.WHITE
        c.drawOval(RectF(-r * .52f, -r * .48f, r * .52f, r * .48f), paint)
        paint.color = Color.BLACK
        c.drawOval(RectF(-r * .42f, -r * .18f, -r * .08f, -r * .02f), paint)
        c.drawOval(RectF(r * .08f, -r * .18f, r * .42f, -r * .02f), paint)
        val grin = Path().apply { moveTo(-r*.22f, r*.20f); lineTo(-r*.12f, r*.36f); lineTo(0f, r*.22f); lineTo(r*.12f, r*.36f); lineTo(r*.22f, r*.20f); close() }
        c.drawPath(grin, paint)
        if (mood == 1) {
            paint.color = Color.YELLOW
            c.drawCircle(r*.58f, -r*.8f, r*.18f, paint)
        }
        c.restore()
    }

    private fun drawGhost(c: Canvas, x: Float, y: Float, r: Float, night: Boolean) {
        c.save()
        c.translate(x, y)
        paint.color = if (night) Color.rgb(250, 248, 235) else Color.WHITE
        paint.style = Paint.Style.FILL
        c.drawOval(RectF(-r*.72f, -r*1.0f, r*.72f, r*.78f), paint)
        val hem = Path().apply {
            moveTo(-r*.7f, r*.38f); cubicTo(-r*.4f, r*.15f, -r*.3f, r*.95f, 0f, r*.68f)
            cubicTo(r*.25f, r*.95f, r*.35f, r*.25f, r*.7f, r*.42f)
            lineTo(r*.58f, r*.72f); lineTo(-r*.56f, r*.72f); close()
        }
        c.drawPath(hem, paint)
        paint.color = Color.BLACK
        c.drawRoundRect(RectF(-r*.60f, -r*.26f, -r*.03f, r*.20f), r*.12f, r*.12f, paint)
        c.drawRoundRect(RectF(r*.03f, -r*.26f, r*.60f, r*.20f), r*.12f, r*.12f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = r*.045f
        c.drawArc(RectF(-r*.18f, r*.08f, r*.22f, r*.42f), 15f, 150f, false, paint)
        paint.style = Paint.Style.FILL
        if (mood == 2) {
            paint.color = Color.rgb(255, 190, 30)
            c.drawCircle(r*.68f, -r*.75f, r*.14f, paint)
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
        paint.textSize = minOf(32f, width * .032f)
        c.drawText(text, cx, cy + paint.textSize*.34f, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val now = System.currentTimeMillis()
            mood = Random.nextInt(0, 7)
            phrase = listOf("Ah !", "Coucou !", "Tu m'as trouvé !", "Oh non !", "Youpi !", "Magie !", "Je cours !").random()
            phraseUntil = now + 3500
            vx = (Random.nextFloat() * .008f + .002f) * if (Random.nextBoolean()) 1 else -1
            vy = (Random.nextFloat() * .004f + .001f) * if (Random.nextBoolean()) 1 else -1
            lastTouch = now
            invalidate()
            return true
        }
        return true
    }
}
