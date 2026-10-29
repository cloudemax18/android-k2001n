package com.cloudemax18.k2001navatars

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
 * Personajes vectoriales provisionales dibujados directamente en la aplicación.
 * Todos los diálogos visibles para el usuario deben estar en español.
 */
class AvatarView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    // Carga recursos PNG transparentes si existen; mantiene el dibujo vectorial como respaldo.
    private val reaperBitmap: Bitmap? by lazy { loadAvatarBitmap("reaper_main") }
    private val ghostBitmap: Bitmap? by lazy { loadAvatarBitmap("ghost_main") }
    private val spriteSheet: Bitmap? by lazy { loadAvatarBitmap("avatar_sprites") }
    private val phrases = listOf(
        "¡Hola, compañero!", "¡Vaya, vaya!", "¡En marcha!", "Lo he visto todo…",
        "¡Qué aventura!", "¡Soy increíble!", "¡Cuidado, fantasma!",
        "¿Un cafecito?", "¡Abracadabra!", "¡Ahora vuelvo!",
        "¡Qué buen día!", "¡No tengo miedo!", "¿Rodamos, jefe?",
        "¡Buenos días!", "¡Buenas tardes!", "¡Buenas noches!",
        "¡Qué susto!", "¡Estoy de buen humor!", "¡Hora de una siesta!",
        "¡Mira mi magia!", "¡Vamos de paseo!", "¡Eso ha sido genial!"
    )
    private var phrase = "¡Hola! Soy el Segador."
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

        val hour = SimpleDateFormat("H", Locale.getDefault()).format(Date()).toIntOrNull() ?: 12
        val night = hour >= 20 || hour < 6
        val scale = if (width < height) 0.72f else 0.95f
        val cy = height * yFactor
        val bob = kotlin.math.sin(tick / 12.0).toFloat() * height * 0.012f
        val avatarSize = minOf(width, height) * 0.22f * scale
        val rx = width * reaperX
        val gx = width * ghostX
        val reaperRect = RectF(rx - avatarSize * 1.42f, cy + bob - avatarSize * 1.42f,
            rx + avatarSize * 1.42f, cy + bob + avatarSize * 1.42f)
        val ghostRect = RectF(gx - avatarSize * 1.28f, cy - bob - avatarSize * 1.28f,
            gx + avatarSize * 1.28f, cy - bob + avatarSize * 1.28f)
        val animatedMood = mood > 0 && tick % 5 != 0
        if (animatedMood && spriteSheet != null) {
            val reaperFrame = mood % 7
            val ghostFrame = 7 + (mood % 7)
            drawSpriteFrame(canvas, spriteSheet!!, reaperFrame, reaperRect)
            drawSpriteFrame(canvas, spriteSheet!!, ghostFrame, ghostRect)
        } else {
            if (reaperBitmap != null) canvas.drawBitmap(reaperBitmap!!, null, reaperRect, paint)
            else drawReaper(canvas, rx, cy + bob, avatarSize, night)
            if (ghostBitmap != null) canvas.drawBitmap(ghostBitmap!!, null, ghostRect, paint)
            else drawGhost(canvas, gx, cy - bob, avatarSize * 0.88f, night)
        }
        drawBubble(canvas, width * 0.5f, height * 0.22f, width * 0.55f, phrase)
        postInvalidateDelayed(42)
    }

    // Atlas: celdas de 96 px, cinco columnas. Fotogramas 0-6 Segador y 7-13 Fantasma.
    private fun drawSpriteFrame(canvas: Canvas, sheet: Bitmap, index: Int, destination: RectF) {
        val cell = 96
        val source = android.graphics.Rect((index % 5) * cell, (index / 5) * cell,
            (index % 5 + 1) * cell, (index / 5 + 1) * cell)
        canvas.drawBitmap(sheet, source, destination, paint)
    }

    private fun loadAvatarBitmap(resourceName: String): Bitmap? {
        val resourceId = resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resourceId == 0) return null
        return runCatching {
            BitmapFactory.decodeResource(resources, resourceId)
        }.getOrNull()
    }

    private fun drawReaper(c: Canvas, x: Float, y: Float, r: Float, night: Boolean) {
        c.save()
        c.translate(x, y)

        paint.style = Paint.Style.FILL
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND

        // Guadaña: mango oscuro detrás de la capucha y hoja plateada curva.
        paint.color = Color.rgb(28, 28, 31)
        paint.strokeWidth = r * 0.12f
        c.drawLine(-r * 0.78f, r * 1.18f, r * 0.72f, -r * 1.65f, paint)
        paint.color = Color.rgb(176, 180, 187)
        paint.strokeWidth = r * 0.035f
        val blade = Path().apply {
            moveTo(r * 0.55f, -r * 1.34f)
            cubicTo(r * 0.95f, -r * 1.72f, r * 1.45f, -r * 1.52f, r * 1.58f, -r * 0.78f)
            cubicTo(r * 1.30f, -r * 1.10f, r * 0.98f, -r * 1.20f, r * 0.72f, -r * 1.08f)
            close()
        }
        paint.style = Paint.Style.FILL
        c.drawPath(blade, paint)
        paint.color = Color.rgb(235, 237, 240)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = r * 0.025f
        c.drawPath(blade, paint)
        paint.style = Paint.Style.FILL

        // Libro negro flotante con símbolo de calavera, a la izquierda.
        c.save()
        c.rotate(-24f, -r * 0.98f, -r * 0.72f)
        paint.color = Color.rgb(12, 12, 14)
        c.drawRoundRect(RectF(-r * 1.30f, -r * 1.02f, -r * 0.68f, -r * 0.40f), r * 0.08f, r * 0.08f, paint)
        paint.color = Color.rgb(190, 193, 197)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = r * 0.025f
        c.drawLine(-r * 0.61f, -r * 0.98f, -r * 0.61f, -r * 0.45f, paint)
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(225, 225, 225)
        c.drawCircle(-r * 0.99f, -r * 0.75f, r * 0.13f, paint)
        paint.color = Color.BLACK
        c.drawCircle(-r * 1.03f, -r * 0.78f, r * 0.035f, paint)
        c.drawCircle(-r * 0.95f, -r * 0.78f, r * 0.035f, paint)
        c.drawRect(-r * 1.04f, -r * 0.72f, -r * 0.94f, -r * 0.68f, paint)
        c.restore()

        // Silueta de la capucha triangular y túnica.
        paint.color = Color.BLACK
        val hood = Path().apply {
            moveTo(0f, -r * 1.25f)
            cubicTo(-r * 0.72f, -r * 0.98f, -r * 1.02f, -r * 0.12f, -r * 0.72f, r * 0.48f)
            cubicTo(-r * 0.66f, r * 0.92f, -r * 0.42f, r * 1.24f, -r * 0.18f, r * 1.30f)
            lineTo(r * 0.42f, r * 1.20f)
            cubicTo(r * 0.78f, r * 0.88f, r * 0.85f, r * 0.28f, r * 0.70f, -r * 0.08f)
            cubicTo(r * 0.55f, -r * 0.70f, r * 0.28f, -r * 1.05f, 0f, -r * 1.25f)
            close()
        }
        c.drawPath(hood, paint)

        // Pliegues sutiles de la túnica.
        paint.color = Color.rgb(35, 36, 39)
        val robeFold = Path().apply {
            moveTo(-r * 0.50f, r * 0.40f)
            cubicTo(-r * 0.42f, r * 0.72f, -r * 0.55f, r * 1.05f, -r * 0.18f, r * 1.30f)
            cubicTo(-r * 0.06f, r * 0.92f, -r * 0.20f, r * 0.65f, -r * 0.16f, r * 0.48f)
            close()
        }
        c.drawPath(robeFold, paint)

        // Calavera sombreada, cuencas grandes y nariz triangular.
        paint.color = if (night) Color.rgb(224, 225, 228) else Color.rgb(239, 239, 237)
        c.drawOval(RectF(-r * 0.52f, -r * 0.54f, r * 0.52f, r * 0.45f), paint)
        paint.color = Color.rgb(155, 158, 163)
        c.drawOval(RectF(-r * 0.47f, -r * 0.43f, r * 0.47f, r * 0.28f), paint)
        paint.color = Color.rgb(15, 15, 17)
        c.drawOval(RectF(-r * 0.43f, -r * 0.20f, -r * 0.06f, r * 0.05f), paint)
        c.drawOval(RectF(r * 0.06f, -r * 0.20f, r * 0.43f, r * 0.05f), paint)
        val nose = Path().apply {
            moveTo(0f, -r * 0.01f)
            lineTo(-r * 0.09f, r * 0.17f)
            lineTo(r * 0.08f, r * 0.17f)
            close()
        }
        c.drawPath(nose, paint)
        paint.color = Color.rgb(30, 30, 32)
        c.drawRoundRect(RectF(-r * 0.23f, r * 0.25f, r * 0.23f, r * 0.36f), r * 0.025f, r * 0.025f, paint)
        paint.color = Color.rgb(205, 207, 210)
        paint.strokeWidth = r * 0.018f
        for (i in -1..2) c.drawLine(i * r * 0.09f, r * 0.26f, i * r * 0.09f, r * 0.34f, paint)

        // Brazos oscuros y taza gris con corazón negro.
        paint.color = Color.rgb(18, 18, 20)
        c.drawRoundRect(RectF(-r * 0.45f, r * 0.42f, r * 0.46f, r * 0.70f), r * 0.12f, r * 0.12f, paint)
        paint.color = Color.rgb(174, 177, 182)
        c.drawRoundRect(RectF(-r * 0.30f, r * 0.40f, r * 0.22f, r * 0.76f), r * 0.06f, r * 0.06f, paint)
        c.drawRoundRect(RectF(r * 0.17f, r * 0.48f, r * 0.34f, r * 0.66f), r * 0.05f, r * 0.05f, paint)
        paint.color = Color.rgb(18, 18, 20)
        val heart = Path().apply {
            moveTo(-r * 0.04f, r * 0.50f)
            cubicTo(-r * 0.17f, r * 0.40f, -r * 0.22f, r * 0.57f, -r * 0.04f, r * 0.67f)
            cubicTo(r * 0.14f, r * 0.57f, r * 0.09f, r * 0.40f, -r * 0.04f, r * 0.50f)
            close()
        }
        c.drawPath(heart, paint)

        // Vapor que sale de la taza.
        paint.color = Color.argb(135, 210, 213, 218)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = r * 0.035f
        val steam = Path().apply {
            moveTo(-r * 0.12f, r * 0.37f)
            cubicTo(-r * 0.28f, r * 0.23f, -r * 0.02f, r * 0.18f, -r * 0.14f, r * 0.05f)
            moveTo(r * 0.02f, r * 0.37f)
            cubicTo(-r * 0.08f, r * 0.25f, r * 0.18f, r * 0.20f, r * 0.08f, r * 0.08f)
        }
        c.drawPath(steam, paint)
        paint.style = Paint.Style.FILL

        if (mood == 1) {
            paint.color = Color.YELLOW
            c.drawCircle(r * 0.58f, -r * 0.8f, r * 0.18f, paint)
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
