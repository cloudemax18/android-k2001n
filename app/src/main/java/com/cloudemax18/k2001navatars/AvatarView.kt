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
import kotlin.math.sin
import kotlin.random.Random

/**
 * Overlay animado: ambos personajes se dibujan en cada fotograma.
 * Animación por transformaciones y estados; todos los diálogos están en español.
 */
class AvatarView(context: Context) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val reaper by lazy { load("reaper_main") }
    private val ghost by lazy { load("ghost_main") }

    private data class Actor(
        var x: Float, var y: Float, var vx: Float, var vy: Float,
        var action: String = "flotar", var actionUntil: Long = 0L,
        var phrase: String = "", var bubbleStart: Long = 0L, var bubbleUntil: Long = 0L,
        var facing: Float = 1f, var blinkUntil: Long = 0L, var nextBlink: Long = 0L,
        var phase: Float = Random.nextFloat() * 6.28f
    )
    private val reaperActor = Actor(.29f, .61f, .0013f, .0008f, phrase = "¡Hola! Soy el Segador.")
    private val ghostActor = Actor(.72f, .62f, -.0011f, -.0007f, phrase = "¡Hola! ¡Soy el Fantasma.")
    private val sayings = listOf(
        "¡Hola!", "¡Qué alegría!", "¡Vamos de paseo!", "¡Sorpresa!", "¡Abracadabra!",
        "¡Cuidado!", "¡Qué buen día!", "¡Estoy feliz!", "¡A correr!", "¡Mira esto!",
        "¡Qué susto!", "¡Hora de una aventura!", "¡Un cafecito!", "¡Eso ha sido genial!",
        "¡Aquí estoy!", "¡Vamos, compañero!", "¡No me atrapas!", "¡Tachán!", "¡Me encanta!",
        "¡Todo bajo control!", "¡Buenos días!", "¡Buenas tardes!", "¡Buenas noches!"
    )
    private val actions = listOf("flotar", "saludar", "saltar", "correr", "asustado", "feliz", "magia", "girar", "dormir", "bailar")
    private var lastFrame = 0L
    private var frame = 0
    var onWindowPositionRequested: ((Float, Float) -> Unit)? = null
    private var globalX = .05f
    private var globalY = .08f
    private var globalVX = .001f
    private var globalVY = .0007f
    private var touchCount = 0

    init {
        isClickable = true
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val now = System.currentTimeMillis()
        reaperActor.nextBlink = now + 1800
        ghostActor.nextBlink = now + 2600
        showBubble(reaperActor, "¡Hola! Soy el Segador.", now, 4200)
        showBubble(ghostActor, "¡Y yo soy el Fantasma!", now + 650, 4200)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width <= 0 || height <= 0) return
        val now = System.currentTimeMillis()
        val dt = if (lastFrame == 0L) 16L else (now - lastFrame).coerceIn(8L, 48L)
        lastFrame = now
        frame++
        updateActor(reaperActor, now, dt, .16f, .43f)
        updateActor(ghostActor, now, dt, .55f, .86f)

        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val night = hour >= 20 || hour < 6
        val size = minOf(width * .39f, height * .78f).coerceAtLeast(100f)
        drawActor(canvas, reaperActor, reaper, size * 1.02f, now, night, true)
        drawActor(canvas, ghostActor, ghost, size * .80f, now, night, false)

        // El contenedor transparente se desplaza por diferentes zonas de la pantalla.
        // El movimiento es deliberadamente lento para no marear y se limita a los márgenes.
        globalX += globalVX * dt
        globalY += globalVY * dt
        if (globalX < .01f || globalX > .46f) globalVX = -globalVX
        if (globalY < .02f || globalY > .55f) globalVY = -globalVY
        if (frame % 3 == 0) onWindowPositionRequested?.invoke(globalX, globalY)

        postInvalidateDelayed(16L)
    }

    private fun updateActor(a: Actor, now: Long, dt: Long, minX: Float, maxX: Float) {
        if (now >= a.actionUntil) {
            if (Random.nextInt(100) < 3) {
                a.action = actions.random()
                a.actionUntil = now + Random.nextLong(900, 2400)
                if (Random.nextInt(3) == 0) showBubble(a, sayings.random(), now, Random.nextLong(1900, 3400))
            } else {
                a.action = "flotar"
                a.actionUntil = now + 700
            }
        }
        val speed = when (a.action) { "correr" -> 1.9f; "bailar" -> 1.5f; else -> 1f }
        a.x += a.vx * dt * speed
        a.y += a.vy * dt * speed
        if (a.x < minX || a.x > maxX) {
            a.vx = -a.vx
            a.facing = if (a.vx >= 0) 1f else -1f
            a.x = a.x.coerceIn(minX, maxX)
        }
        if (a.y < .32f || a.y > .78f) { a.vy = -a.vy; a.y = a.y.coerceIn(.32f, .78f) }
        if (now >= a.nextBlink) {
            a.blinkUntil = now + 140
            a.nextBlink = now + Random.nextLong(2200, 5000)
        }
        // En los momentos tranquilos hay un paseo suave, no una imagen fija.
        if (a.action == "flotar" && Random.nextInt(90) == 0) {
            a.vx = (Random.nextFloat() * .0015f + .0005f) * if (Random.nextBoolean()) 1 else -1
            a.vy = (Random.nextFloat() * .0009f + .0003f) * if (Random.nextBoolean()) 1 else -1
        }
    }

    private fun drawActor(c: Canvas, a: Actor, bitmap: Bitmap?, size: Float, now: Long, night: Boolean, isReaper: Boolean) {
        val bob = sin((now / 190.0) + a.phase).toFloat() * size * .035f
        val actionAngle = when (a.action) {
            "bailar" -> sin(now / 90.0).toFloat() * 13f
            "girar" -> sin(now / 130.0).toFloat() * 25f
            "asustado" -> sin(now / 35.0).toFloat() * 4f
            "saludar" -> sin(now / 85.0).toFloat() * 8f
            else -> sin(now / 360.0 + a.phase).toFloat() * 3f
        }
        val scale = when (a.action) {
            "saltar" -> 1f + kotlin.math.abs(sin(now / 100.0).toFloat()) * .18f
            "feliz" -> 1f + kotlin.math.abs(sin(now / 120.0).toFloat()) * .10f
            "asustado" -> .92f + kotlin.math.abs(sin(now / 50.0).toFloat()) * .08f
            "magia" -> .94f + kotlin.math.abs(sin(now / 75.0).toFloat()) * .12f
            else -> 1f
        }
        val x = width * a.x
        val y = height * a.y + bob + if (a.action == "saltar") -kotlin.math.abs(sin(now / 95.0).toFloat()) * size * .25f else 0f
        val rect = RectF(-size * .50f, -size * .58f, size * .50f, size * .58f)
        c.save()
        c.translate(x, y)
        c.rotate(actionAngle)
        c.scale(scale * a.facing, scale)
        if (a.action == "magia" && (now / 120L) % 5L == 0L) {
            p.color = Color.argb(95, 165, 115, 255)
            c.drawCircle(0f, 0f, size * (.62f + (now % 250L) / 700f), p)
        }
        if (bitmap != null) {
            p.alpha = if (a.action == "magia" && (now / 200L) % 4L == 0L) 110 else 255
            c.drawBitmap(bitmap, null, rect, p)
            p.alpha = 255
        } else {
            if (isReaper) drawFallbackReaper(c, size, night) else drawFallbackGhost(c, size, night)
        }
        // Partículas y efectos legibles alrededor del personaje.
        when (a.action) {
            "feliz", "bailar" -> drawSparkles(c, size, now)
            "asustado" -> {
                p.color = Color.rgb(110, 195, 255); p.textSize = size * .18f
                c.drawText("!", size * .42f, -size * .50f, p)
            }
            "saludar" -> {
                p.color = Color.rgb(255, 220, 80); p.textSize = size * .15f
                c.drawText("✦", size * .43f, -size * .48f, p)
            }
        }
        c.restore()
        if (now < a.bubbleUntil) drawBubble(c, x, y - size * .73f, a, now, size)
        if (a.blinkUntil > now) {
            p.color = Color.argb(150, 255, 255, 255)
            c.drawOval(RectF(x - size * .09f, y - size * .14f, x + size * .09f, y - size * .09f), p)
        }
    }

    private fun drawBubble(c: Canvas, x: Float, y: Float, a: Actor, now: Long, size: Float) {
        val elapsed = now - a.bubbleStart
        val remaining = a.bubbleUntil - now
        val appear = (elapsed / 180f).coerceIn(0f, 1f)
        val fade = (remaining / 260f).coerceIn(0f, 1f)
        val alpha = (255 * minOf(appear, fade)).toInt()
        val scale = .75f + .25f * appear
        val textSize = (size * .115f).coerceIn(16f, 26f)
        p.textSize = textSize
        p.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
        val visibleChars = if (elapsed < 600) (a.phrase.length * (elapsed / 600f)).toInt().coerceAtLeast(1) else a.phrase.length
        val shown = a.phrase.take(visibleChars)
        val tw = p.measureText(a.phrase)
        val bw = (tw + 34f).coerceIn(150f, width * .72f)
        val bh = textSize + 30f
        c.save()
        c.translate(x, y)
        c.scale(scale, scale)
        p.color = Color.argb((alpha * .95f).toInt(), 255, 255, 255)
        c.drawRoundRect(RectF(-bw/2, -bh/2, bw/2, bh/2), 22f, 22f, p)
        val tail = Path().apply { moveTo(-8f, bh/2 - 2); lineTo(0f, bh/2 + 13f); lineTo(9f, bh/2 - 2); close() }
        c.drawPath(tail, p)
        p.color = Color.argb(alpha, 28, 28, 34)
        p.textAlign = Paint.Align.CENTER
        c.drawText(shown, 0f, textSize * .34f, p)
        p.textAlign = Paint.Align.LEFT
        c.restore()
    }

    private fun drawSparkles(c: Canvas, size: Float, now: Long) {
        p.color = Color.rgb(255, 213, 75)
        p.textSize = size * .16f
        val drift = sin(now / 100.0).toFloat() * size * .04f
        c.drawText("✦", -size * .52f, -size * .42f + drift, p)
        c.drawText("✧", size * .38f, size * .38f - drift, p)
        c.drawText("✦", size * .45f, -size * .20f, p)
    }

    private fun showBubble(a: Actor, text: String, now: Long, duration: Long) {
        a.phrase = text
        a.bubbleStart = now
        a.bubbleUntil = now + duration
    }

    private fun load(name: String): Bitmap? {
        val id = resources.getIdentifier(name, "drawable", context.packageName)
        if (id == 0) return null
        return runCatching { BitmapFactory.decodeResource(resources, id) }.getOrNull()
    }

    private fun drawFallbackReaper(c: Canvas, s: Float, night: Boolean) {
        p.color = Color.BLACK
        val hood = Path().apply { moveTo(0f, -s*.55f); cubicTo(-s*.55f,-s*.4f,-s*.55f,s*.18f,-s*.35f,s*.5f); lineTo(s*.30f,s*.52f); cubicTo(s*.60f,s*.08f,s*.42f,-s*.4f,0f,-s*.55f); close() }
        c.drawPath(hood,p)
        p.color = if (night) Color.LTGRAY else Color.WHITE
        c.drawOval(RectF(-s*.29f,-s*.30f,s*.29f,s*.15f),p)
        p.color = Color.BLACK
        c.drawOval(RectF(-s*.25f,-s*.12f,-s*.04f,s*.02f),p); c.drawOval(RectF(s*.04f,-s*.12f,s*.25f,s*.02f),p)
        p.textSize=s*.35f; c.drawText("♥",-s*.13f,s*.42f,p)
    }
    private fun drawFallbackGhost(c: Canvas, s: Float, night: Boolean) {
        p.color = if (night) Color.rgb(250,248,235) else Color.WHITE
        c.drawOval(RectF(-s*.38f,-s*.52f,s*.38f,s*.48f),p)
        val hem=Path().apply { moveTo(-s*.37f,s*.28f); cubicTo(-s*.2f,s*.1f,-s*.16f,s*.55f,0f,s*.4f); cubicTo(s*.2f,s*.56f,s*.2f,s*.12f,s*.37f,s*.28f); lineTo(s*.34f,s*.43f); lineTo(-s*.34f,s*.43f); close() }
        c.drawPath(hem,p)
        p.color=Color.BLACK
        c.drawRoundRect(RectF(-s*.30f,-s*.13f,-s*.04f,s*.05f),s*.07f,s*.07f,p)
        c.drawRoundRect(RectF(s*.04f,-s*.13f,s*.30f,s*.05f),s*.07f,s*.07f,p)
        p.style=Paint.Style.STROKE; p.strokeWidth=s*.035f
        c.drawArc(RectF(-s*.12f,s*.02f,s*.13f,s*.24f),15f,150f,false,p)
        p.style=Paint.Style.FILL
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchCount++
            val now = System.currentTimeMillis()
            val actor = if (event.x < width * .5f) reaperActor else ghostActor
            actor.action = listOf("feliz", "asustado", "saludar", "magia", "correr", "saltar", "bailar", "girar").random()
            actor.actionUntil = now + Random.nextLong(1600, 3000)
            actor.vx = (Random.nextFloat() * .0025f + .001f) * if (Random.nextBoolean()) 1 else -1
            actor.vy = (Random.nextFloat() * .0015f + .0005f) * if (Random.nextBoolean()) 1 else -1
            showBubble(actor, listOf("¡Ja, me tocaste!", "¡Sorpresa!", "¡Mira mi magia!", "¡Qué alegría!", "¡A correr!", "¡Uy, qué susto!", "¡Vamos!", "¡Tachán!", "¡Lo hiciste!").random(), now, 2800)
            val other = if (actor === reaperActor) ghostActor else reaperActor
            if (Random.nextBoolean()) showBubble(other, listOf("¿Qué pasó?", "¡Yo también!", "¡Eh, espera!", "¡Qué divertido!").random(), now + 350, 2200)
            // Un toque también cambia el rumbo del paseo general.
            globalVX = (Random.nextFloat() * .002f + .0007f) * if (Random.nextBoolean()) 1 else -1
            globalVY = (Random.nextFloat() * .0012f + .0004f) * if (Random.nextBoolean()) 1 else -1
            invalidate()
            return true
        }
        return true
    }
}
