package com.example.focusfrog.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.example.focusfrog.ui.components.FrogMood
import com.example.focusfrog.ui.components.FrogStage

object FrogBitmapRenderer {

    fun renderFrogBitmap(
        stage: FrogStage,
        mood: FrogMood,
        hasHat: Boolean = false,
        hasWizardHat: Boolean = false,
        hasSunglasses: Boolean = false,
        hasCrown: Boolean = false,
        hasBowTie: Boolean = false,
        hasHeadphones: Boolean = false,
        hasLeafUmbrella: Boolean = false,
        widthPx: Int = 160,
        heightPx: Int = 160
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val w = widthPx.toFloat()
        val h = heightPx.toFloat()

        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#7CB342") }
        val bellyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#DCF6BD") }
        val cheekPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#80FF8A80") }
        val mouthPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#33691E")
            style = Paint.Style.STROKE
            strokeWidth = w * 0.03f
            strokeCap = Paint.Cap.ROUND
        }
        val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        val blackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }

        // Draw Stage Body & Face
        when (stage) {
            FrogStage.TADPOLE -> {
                val tailPath = Path().apply {
                    moveTo(w * 0.55f, h * 0.55f)
                    quadTo(w * 0.75f, h * 0.45f, w * 0.85f, h * 0.65f)
                    quadTo(w * 0.75f, h * 0.70f, w * 0.55f, h * 0.60f)
                    close()
                }
                canvas.drawPath(tailPath, bodyPaint)

                canvas.drawCircle(w * 0.42f, h * 0.5f, w * 0.28f, bodyPaint)
                canvas.drawCircle(w * 0.42f, h * 0.52f, w * 0.18f, bellyPaint)

                canvas.drawCircle(w * 0.32f, h * 0.38f, w * 0.06f, whitePaint)
                canvas.drawCircle(w * 0.48f, h * 0.38f, w * 0.06f, whitePaint)

                val pupilOffset = if (mood == FrogMood.HUNGRY) h * 0.01f else 0f
                canvas.drawCircle(w * 0.32f, h * 0.38f + pupilOffset, w * 0.03f, blackPaint)
                canvas.drawCircle(w * 0.48f, h * 0.38f + pupilOffset, w * 0.03f, blackPaint)

                canvas.drawCircle(w * 0.26f, h * 0.48f, w * 0.04f, cheekPaint)
                canvas.drawCircle(w * 0.54f, h * 0.48f, w * 0.04f, cheekPaint)

                drawBitmapMouth(canvas, mood, w * 0.40f, h * 0.48f, w * 0.12f, mouthPaint)
            }
            FrogStage.FROGLET -> {
                canvas.drawOval(RectF(w * 0.65f, h * 0.55f, w * 0.83f, h * 0.67f), bodyPaint)

                canvas.drawCircle(w * 0.28f, h * 0.62f, w * 0.1f, bodyPaint)
                canvas.drawCircle(w * 0.62f, h * 0.62f, w * 0.1f, bodyPaint)

                canvas.drawCircle(w * 0.45f, h * 0.48f, w * 0.28f, bodyPaint)
                canvas.drawCircle(w * 0.45f, h * 0.52f, w * 0.18f, bellyPaint)

                canvas.drawCircle(w * 0.35f, h * 0.28f, w * 0.08f, bodyPaint)
                canvas.drawCircle(w * 0.55f, h * 0.28f, w * 0.08f, bodyPaint)

                canvas.drawCircle(w * 0.35f, h * 0.28f, w * 0.06f, whitePaint)
                canvas.drawCircle(w * 0.55f, h * 0.28f, w * 0.06f, whitePaint)

                canvas.drawCircle(w * 0.35f, h * 0.28f, w * 0.03f, blackPaint)
                canvas.drawCircle(w * 0.55f, h * 0.28f, w * 0.03f, blackPaint)

                canvas.drawCircle(w * 0.30f, h * 0.42f, w * 0.04f, cheekPaint)
                canvas.drawCircle(w * 0.60f, h * 0.42f, w * 0.04f, cheekPaint)

                drawBitmapMouth(canvas, mood, w * 0.45f, h * 0.42f, w * 0.14f, mouthPaint)
            }
            FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> {
                canvas.drawOval(RectF(w * 0.12f, h * 0.52f, w * 0.37f, h * 0.82f), bodyPaint)
                canvas.drawOval(RectF(w * 0.63f, h * 0.52f, w * 0.88f, h * 0.82f), bodyPaint)

                canvas.drawOval(RectF(w * 0.22f, h * 0.28f, w * 0.78f, h * 0.82f), bodyPaint)
                canvas.drawOval(RectF(w * 0.30f, h * 0.44f, w * 0.70f, h * 0.79f), bellyPaint)

                canvas.drawOval(RectF(w * 0.28f, h * 0.58f, w * 0.40f, h * 0.80f), bodyPaint)
                canvas.drawOval(RectF(w * 0.60f, h * 0.58f, w * 0.72f, h * 0.80f), bodyPaint)

                canvas.drawCircle(w * 0.35f, h * 0.26f, w * 0.11f, bodyPaint)
                canvas.drawCircle(w * 0.65f, h * 0.26f, w * 0.11f, bodyPaint)

                canvas.drawCircle(w * 0.35f, h * 0.26f, w * 0.08f, whitePaint)
                canvas.drawCircle(w * 0.65f, h * 0.26f, w * 0.08f, whitePaint)

                canvas.drawCircle(w * 0.35f, h * 0.26f, w * 0.04f, blackPaint)
                canvas.drawCircle(w * 0.65f, h * 0.26f, w * 0.04f, blackPaint)

                canvas.drawCircle(w * 0.30f, h * 0.40f, w * 0.05f, cheekPaint)
                canvas.drawCircle(w * 0.70f, h * 0.40f, w * 0.05f, cheekPaint)

                drawBitmapMouth(canvas, mood, w * 0.50f, h * 0.40f, w * 0.20f, mouthPaint)
            }
        }

        // Draw Bow Tie
        if (hasBowTie) {
            val bowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D32F2F") }
            val (neckX, neckY) = when (stage) {
                FrogStage.TADPOLE -> Pair(w * 0.42f, h * 0.58f)
                FrogStage.FROGLET -> Pair(w * 0.45f, h * 0.52f)
                FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> Pair(w * 0.50f, h * 0.50f)
            }
            val leftWing = Path().apply {
                moveTo(neckX, neckY)
                lineTo(neckX - w * 0.08f, neckY - h * 0.03f)
                lineTo(neckX - w * 0.08f, neckY + h * 0.03f)
                close()
            }
            val rightWing = Path().apply {
                moveTo(neckX, neckY)
                lineTo(neckX + w * 0.08f, neckY - h * 0.03f)
                lineTo(neckX + w * 0.08f, neckY + h * 0.03f)
                close()
            }
            canvas.drawPath(leftWing, bowPaint)
            canvas.drawPath(rightWing, bowPaint)
        }

        // Draw Sunglasses
        if (hasSunglasses) {
            val shadesPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#212121") }
            val (sy, sHeight, sw) = when (stage) {
                FrogStage.TADPOLE -> Triple(h * 0.33f, h * 0.10f, w * 0.36f)
                FrogStage.FROGLET -> Triple(h * 0.23f, h * 0.10f, w * 0.42f)
                FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> Triple(h * 0.20f, h * 0.12f, w * 0.56f)
            }
            val sx = (w - sw) / 2
            canvas.drawRoundRect(RectF(sx, sy, sx + sw, sy + sHeight), 6f, 6f, shadesPaint)
        }

        // Draw Hat / Wizard Hat
        if (hasWizardHat) {
            val wizardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#5E35B1") }
            val (brimTop, capTop, brimWidth) = when (stage) {
                FrogStage.TADPOLE -> Triple(h * 0.20f, h * 0.04f, w * 0.46f)
                FrogStage.FROGLET -> Triple(h * 0.16f, h * 0.02f, w * 0.52f)
                FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> Triple(h * 0.13f, h * 0.01f, w * 0.62f)
            }
            val centerOffset = (w - brimWidth) / 2
            val hatPath = Path().apply {
                moveTo(centerOffset + brimWidth * 0.15f, brimTop)
                lineTo(w * 0.50f, capTop)
                lineTo(centerOffset + brimWidth * 0.85f, brimTop)
                close()
            }
            canvas.drawPath(hatPath, wizardPaint)
        } else if (hasHat) {
            val hatBrimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#795548") }
            val hatCrownPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#8D6E63") }
            val (brimTop, capTop, brimWidth, capWidth) = when (stage) {
                FrogStage.TADPOLE -> Quad(h * 0.20f, h * 0.11f, w * 0.44f, w * 0.28f)
                FrogStage.FROGLET -> Quad(h * 0.16f, h * 0.07f, w * 0.50f, w * 0.32f)
                FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> Quad(h * 0.13f, h * 0.04f, w * 0.60f, w * 0.38f)
            }
            val centerOffset = (w - brimWidth) / 2
            val capX = centerOffset + (brimWidth - capWidth) / 2

            canvas.drawRoundRect(RectF(capX, capTop, capX + capWidth, brimTop), 6f, 6f, hatCrownPaint)
            canvas.drawRoundRect(RectF(centerOffset, brimTop, centerOffset + brimWidth, brimTop + h * 0.04f), 4f, 4f, hatBrimPaint)
        }

        // Draw Crown
        if (hasCrown || stage == FrogStage.ROYAL_FROG) {
            val crownPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFB300") }
            val crownY = when (stage) {
                FrogStage.TADPOLE -> h * 0.14f
                FrogStage.FROGLET -> h * 0.10f
                FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> h * 0.06f
            }
            val crownPath = Path().apply {
                moveTo(w * 0.38f, crownY + h * 0.10f)
                lineTo(w * 0.41f, crownY)
                lineTo(w * 0.46f, crownY + h * 0.06f)
                lineTo(w * 0.50f, crownY - h * 0.03f)
                lineTo(w * 0.54f, crownY + h * 0.06f)
                lineTo(w * 0.59f, crownY)
                lineTo(w * 0.62f, crownY + h * 0.10f)
                close()
            }
            canvas.drawPath(crownPath, crownPaint)
        }

        return bitmap
    }

    private fun drawBitmapMouth(
        canvas: Canvas,
        mood: FrogMood,
        cx: Float,
        cy: Float,
        width: Float,
        paint: Paint
    ) {
        when (mood) {
            FrogMood.HAPPY -> {
                val path = Path().apply {
                    moveTo(cx - width / 2, cy)
                    quadTo(cx, cy + width * 0.6f, cx + width / 2, cy)
                }
                canvas.drawPath(path, paint)
            }
            FrogMood.NEUTRAL -> {
                canvas.drawLine(cx - width / 2, cy + width * 0.2f, cx + width / 2, cy + width * 0.2f, paint)
            }
            FrogMood.HUNGRY -> {
                val path = Path().apply {
                    moveTo(cx - width / 2, cy + width * 0.4f)
                    quadTo(cx, cy, cx + width / 2, cy + width * 0.4f)
                }
                canvas.drawPath(path, paint)
            }
        }
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
