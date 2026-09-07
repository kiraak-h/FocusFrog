package com.example.focusfrog.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class FrogStage {
    TADPOLE,
    FROGLET,
    BIG_FROG,
    ROYAL_FROG
}

enum class FrogMood {
    HAPPY,
    NEUTRAL,
    HUNGRY
}

@Composable
fun FrogAvatar(
    stage: FrogStage,
    mood: FrogMood,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    hasHat: Boolean = false,
    hasWizardHat: Boolean = false,
    hasSunglasses: Boolean = false,
    hasCrown: Boolean = false,
    hasBowTie: Boolean = false,
    hasHeadphones: Boolean = false,
    hasLeafUmbrella: Boolean = false,
    triggerJump: Boolean = false,
    onJumpFinished: () -> Unit = {}
) {
    val offsetY = remember { Animatable(0f) }
    val scaleY = remember { Animatable(1f) }

    LaunchedEffect(triggerJump) {
        if (triggerJump) {
            // Squat down
            scaleY.animateTo(0.85f, animationSpec = tween(150))
            // Jump up!
            offsetY.animateTo(-60f, animationSpec = tween(300, easing = FastOutSlowInEasing))
            scaleY.animateTo(1.1f, animationSpec = tween(150))
            // Fall back down
            offsetY.animateTo(0f, animationSpec = tween(250, easing = FastOutSlowInEasing))
            scaleY.animateTo(1f, animationSpec = tween(150))
            onJumpFinished()
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                translationY = offsetY.value
                this.scaleY = scaleY.value
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val width = size.toPx()
            val height = size.toPx()

            // 1. Draw Body & Face
            when (stage) {
                FrogStage.TADPOLE -> drawTadpole(width, height, mood)
                FrogStage.FROGLET -> drawFroglet(width, height, mood)
                FrogStage.BIG_FROG -> drawBigFrog(width, height, mood, isRoyal = false)
                FrogStage.ROYAL_FROG -> drawBigFrog(width, height, mood, isRoyal = true)
            }

            // 2. Draw Bow Tie (Neck)
            if (hasBowTie) {
                drawBowTie(width, height, stage)
            }

            // 3. Draw Sunglasses (Eyes)
            if (hasSunglasses) {
                drawSunglasses(width, height, stage)
            }

            // 4. Draw Headphones (Ears/Sides)
            if (hasHeadphones) {
                drawHeadphones(width, height, stage)
            }

            // 5. Draw Hat / Wizard Hat / Crown (Top of Head)
            if (hasWizardHat) {
                drawWizardHat(width, height, stage)
            } else if (hasHat) {
                drawHat(width, height, stage)
            }
            if (hasCrown || stage == FrogStage.ROYAL_FROG) {
                drawCrownOverlay(width, height, stage)
            }

            // 6. Draw Leaf Umbrella (Hand/Side)
            if (hasLeafUmbrella) {
                drawLeafUmbrella(width, height, stage)
            }
        }
    }
}

private fun DrawScope.drawTadpole(w: Float, h: Float, mood: FrogMood) {
    val bodyColor = Color(0xFF7CB342)
    val bellyColor = Color(0xFFDCF6BD)
    val cheekColor = Color(0xFFFF8A80)

    val tailPath = Path().apply {
        moveTo(w * 0.55f, h * 0.55f)
        quadraticTo(w * 0.75f, h * 0.45f, w * 0.85f, h * 0.65f)
        quadraticTo(w * 0.75f, h * 0.7f, w * 0.55f, h * 0.6f)
        close()
    }
    drawPath(tailPath, color = bodyColor)

    drawCircle(color = bodyColor, radius = w * 0.28f, center = Offset(w * 0.42f, h * 0.5f))
    drawCircle(color = bellyColor, radius = w * 0.18f, center = Offset(w * 0.42f, h * 0.52f))

    drawCircle(color = Color.White, radius = w * 0.06f, center = Offset(w * 0.32f, h * 0.38f))
    drawCircle(color = Color.White, radius = w * 0.06f, center = Offset(w * 0.48f, h * 0.38f))

    val pupilOffset = if (mood == FrogMood.HUNGRY) h * 0.01f else 0f
    drawCircle(color = Color.Black, radius = w * 0.03f, center = Offset(w * 0.32f, h * 0.38f + pupilOffset))
    drawCircle(color = Color.Black, radius = w * 0.03f, center = Offset(w * 0.48f, h * 0.38f + pupilOffset))

    drawCircle(color = cheekColor.copy(alpha = 0.5f), radius = w * 0.04f, center = Offset(w * 0.26f, h * 0.48f))
    drawCircle(color = cheekColor.copy(alpha = 0.5f), radius = w * 0.04f, center = Offset(w * 0.54f, h * 0.48f))

    drawMoodMouth(w, mood, centerX = w * 0.40f, centerY = h * 0.48f, width = w * 0.12f)
}

private fun DrawScope.drawFroglet(w: Float, h: Float, mood: FrogMood) {
    val bodyColor = Color(0xFF7CB342)
    val bellyColor = Color(0xFFDCF6BD)
    val cheekColor = Color(0xFFFF8A80)

    drawOval(
        color = bodyColor,
        topLeft = Offset(w * 0.65f, h * 0.55f),
        size = Size(w * 0.18f, h * 0.12f)
    )

    drawCircle(color = bodyColor, radius = w * 0.1f, center = Offset(w * 0.28f, h * 0.62f))
    drawCircle(color = bodyColor, radius = w * 0.1f, center = Offset(w * 0.62f, h * 0.62f))

    drawCircle(color = bodyColor, radius = w * 0.28f, center = Offset(w * 0.45f, h * 0.48f))
    drawCircle(color = bellyColor, radius = w * 0.18f, center = Offset(w * 0.45f, h * 0.52f))

    drawCircle(color = bodyColor, radius = w * 0.08f, center = Offset(w * 0.35f, h * 0.28f))
    drawCircle(color = bodyColor, radius = w * 0.08f, center = Offset(w * 0.55f, h * 0.28f))

    drawCircle(color = Color.White, radius = w * 0.06f, center = Offset(w * 0.35f, h * 0.28f))
    drawCircle(color = Color.White, radius = w * 0.06f, center = Offset(w * 0.55f, h * 0.28f))

    drawCircle(color = Color.Black, radius = w * 0.03f, center = Offset(w * 0.35f, h * 0.28f))
    drawCircle(color = Color.Black, radius = w * 0.03f, center = Offset(w * 0.55f, h * 0.28f))

    drawCircle(color = cheekColor.copy(alpha = 0.5f), radius = w * 0.04f, center = Offset(w * 0.30f, h * 0.42f))
    drawCircle(color = cheekColor.copy(alpha = 0.5f), radius = w * 0.04f, center = Offset(w * 0.60f, h * 0.42f))

    drawMoodMouth(w, mood, centerX = w * 0.45f, centerY = h * 0.42f, width = w * 0.14f)
}

private fun DrawScope.drawBigFrog(w: Float, h: Float, mood: FrogMood, isRoyal: Boolean) {
    val bodyColor = Color(0xFF7CB342)
    val bellyColor = Color(0xFFDCF6BD)
    val cheekColor = Color(0xFFFF8A80)

    if (isRoyal) {
        val capePath = Path().apply {
            moveTo(w * 0.22f, h * 0.45f)
            quadraticTo(w * 0.15f, h * 0.75f, w * 0.20f, h * 0.82f)
            lineTo(w * 0.80f, h * 0.82f)
            quadraticTo(w * 0.85f, h * 0.75f, w * 0.78f, h * 0.45f)
            close()
        }
        drawPath(capePath, color = Color(0xFFD32F2F))
    }

    drawOval(
        color = bodyColor,
        topLeft = Offset(w * 0.12f, h * 0.52f),
        size = Size(w * 0.25f, h * 0.30f)
    )
    drawOval(
        color = bodyColor,
        topLeft = Offset(w * 0.63f, h * 0.52f),
        size = Size(w * 0.25f, h * 0.30f)
    )

    drawOval(
        color = bodyColor,
        topLeft = Offset(w * 0.22f, h * 0.28f),
        size = Size(w * 0.56f, h * 0.54f)
    )

    drawOval(
        color = bellyColor,
        topLeft = Offset(w * 0.30f, h * 0.44f),
        size = Size(w * 0.40f, h * 0.35f)
    )

    drawOval(
        color = bodyColor,
        topLeft = Offset(w * 0.28f, h * 0.58f),
        size = Size(w * 0.12f, h * 0.22f)
    )
    drawOval(
        color = bodyColor,
        topLeft = Offset(w * 0.60f, h * 0.58f),
        size = Size(w * 0.12f, h * 0.22f)
    )

    drawCircle(color = bodyColor, radius = w * 0.11f, center = Offset(w * 0.35f, h * 0.26f))
    drawCircle(color = bodyColor, radius = w * 0.11f, center = Offset(w * 0.65f, h * 0.26f))

    drawCircle(color = Color.White, radius = w * 0.08f, center = Offset(w * 0.35f, h * 0.26f))
    drawCircle(color = Color.White, radius = w * 0.08f, center = Offset(w * 0.65f, h * 0.26f))

    drawCircle(color = Color.Black, radius = w * 0.04f, center = Offset(w * 0.35f, h * 0.26f))
    drawCircle(color = Color.Black, radius = w * 0.04f, center = Offset(w * 0.65f, h * 0.26f))

    drawCircle(color = Color.White, radius = w * 0.015f, center = Offset(w * 0.33f, h * 0.24f))
    drawCircle(color = Color.White, radius = w * 0.015f, center = Offset(w * 0.63f, h * 0.24f))

    drawCircle(color = cheekColor.copy(alpha = 0.5f), radius = w * 0.05f, center = Offset(w * 0.30f, h * 0.40f))
    drawCircle(color = cheekColor.copy(alpha = 0.5f), radius = w * 0.05f, center = Offset(w * 0.70f, h * 0.40f))

    drawMoodMouth(w, mood, centerX = w * 0.50f, centerY = h * 0.40f, width = w * 0.20f)
}

private fun DrawScope.drawBowTie(w: Float, h: Float, stage: FrogStage) {
    val bowColor = Color(0xFFD32F2F)
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
    drawPath(leftWing, color = bowColor)
    drawPath(rightWing, color = bowColor)
    drawCircle(color = Color(0xFFB71C1C), radius = w * 0.02f, center = Offset(neckX, neckY))
}

private fun DrawScope.drawSunglasses(w: Float, h: Float, stage: FrogStage) {
    val darkShadesColor = Color(0xFF212121)
    val lensShineColor = Color(0x66FFFFFF)

    when (stage) {
        FrogStage.TADPOLE -> {
            drawRoundRect(
                color = darkShadesColor,
                topLeft = Offset(w * 0.22f, h * 0.33f),
                size = Size(w * 0.36f, h * 0.10f),
                cornerRadius = CornerRadius(w * 0.02f)
            )
            drawLine(
                color = darkShadesColor,
                start = Offset(w * 0.18f, h * 0.35f),
                end = Offset(w * 0.62f, h * 0.35f),
                strokeWidth = w * 0.018f
            )
            drawRoundRect(
                color = lensShineColor,
                topLeft = Offset(w * 0.25f, h * 0.34f),
                size = Size(w * 0.08f, h * 0.03f),
                cornerRadius = CornerRadius(w * 0.01f)
            )
        }
        FrogStage.FROGLET -> {
            drawRoundRect(
                color = darkShadesColor,
                topLeft = Offset(w * 0.24f, h * 0.23f),
                size = Size(w * 0.42f, h * 0.10f),
                cornerRadius = CornerRadius(w * 0.02f)
            )
            drawLine(
                color = darkShadesColor,
                start = Offset(w * 0.20f, h * 0.25f),
                end = Offset(w * 0.70f, h * 0.25f),
                strokeWidth = w * 0.02f
            )
            drawRoundRect(
                color = lensShineColor,
                topLeft = Offset(w * 0.27f, h * 0.24f),
                size = Size(w * 0.09f, h * 0.03f),
                cornerRadius = CornerRadius(w * 0.01f)
            )
        }
        FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> {
            drawRoundRect(
                color = darkShadesColor,
                topLeft = Offset(w * 0.22f, h * 0.20f),
                size = Size(w * 0.56f, h * 0.12f),
                cornerRadius = CornerRadius(w * 0.03f)
            )
            drawLine(
                color = darkShadesColor,
                start = Offset(w * 0.18f, h * 0.22f),
                end = Offset(w * 0.82f, h * 0.22f),
                strokeWidth = w * 0.022f
            )
            drawRoundRect(
                color = lensShineColor,
                topLeft = Offset(w * 0.26f, h * 0.22f),
                size = Size(w * 0.12f, h * 0.04f),
                cornerRadius = CornerRadius(w * 0.01f)
            )
        }
    }
}

private fun DrawScope.drawHeadphones(w: Float, h: Float, stage: FrogStage) {
    val phoneColor = Color(0xFF263238)
    val padColor = Color(0xFF00ACC1)

    val (headTopY, earX1, earX2, earY) = when (stage) {
        FrogStage.TADPOLE -> Quad4(h * 0.22f, w * 0.18f, w * 0.66f, h * 0.48f)
        FrogStage.FROGLET -> Quad4(h * 0.18f, w * 0.20f, w * 0.70f, h * 0.42f)
        FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> Quad4(h * 0.15f, w * 0.22f, w * 0.78f, h * 0.38f)
    }

    val arcPath = Path().apply {
        moveTo(earX1 + w * 0.03f, earY)
        quadraticTo(w * 0.50f, headTopY - h * 0.08f, earX2 - w * 0.03f, earY)
    }
    drawPath(arcPath, color = phoneColor, style = Stroke(width = w * 0.035f, cap = StrokeCap.Round))

    drawOval(color = padColor, topLeft = Offset(earX1 - w * 0.04f, earY - h * 0.06f), size = Size(w * 0.08f, h * 0.12f))
    drawOval(color = padColor, topLeft = Offset(earX2 - w * 0.04f, earY - h * 0.06f), size = Size(w * 0.08f, h * 0.12f))
}

private fun DrawScope.drawHat(w: Float, h: Float, stage: FrogStage) {
    val hatBrimColor = Color(0xFF795548)
    val hatCrownColor = Color(0xFF8D6E63)
    val hatBandColor = Color(0xFFD32F2F)

    val (brimTop, capTop, brimWidth, capWidth, centerOffset) = when (stage) {
        FrogStage.TADPOLE -> Tuple5(h * 0.20f, h * 0.11f, w * 0.44f, w * 0.28f, w * 0.20f)
        FrogStage.FROGLET -> Tuple5(h * 0.16f, h * 0.07f, w * 0.50f, w * 0.32f, w * 0.20f)
        FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> Tuple5(h * 0.13f, h * 0.04f, w * 0.60f, w * 0.38f, w * 0.20f)
    }

    drawRoundRect(
        color = hatCrownColor,
        topLeft = Offset(centerOffset + (brimWidth - capWidth) / 2, capTop),
        size = Size(capWidth, brimTop - capTop),
        cornerRadius = CornerRadius(w * 0.02f)
    )
    drawRect(
        color = hatBandColor,
        topLeft = Offset(centerOffset + (brimWidth - capWidth) / 2, brimTop - h * 0.025f),
        size = Size(capWidth, h * 0.025f)
    )
    drawRoundRect(
        color = hatBrimColor,
        topLeft = Offset(centerOffset, brimTop),
        size = Size(brimWidth, h * 0.04f),
        cornerRadius = CornerRadius(w * 0.01f)
    )
}

private fun DrawScope.drawWizardHat(w: Float, h: Float, stage: FrogStage) {
    val wizardPurple = Color(0xFF5E35B1)
    val starGold = Color(0xFFFFD54F)

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
    drawPath(hatPath, color = wizardPurple)

    drawRoundRect(
        color = Color(0xFF4527A0),
        topLeft = Offset(centerOffset, brimTop),
        size = Size(brimWidth, h * 0.035f),
        cornerRadius = CornerRadius(w * 0.01f)
    )

    drawCircle(color = starGold, radius = w * 0.025f, center = Offset(w * 0.48f, capTop + (brimTop - capTop) * 0.5f))
}

private fun DrawScope.drawCrownOverlay(w: Float, h: Float, stage: FrogStage) {
    val crownGold = Color(0xFFFFB300)
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
    drawPath(crownPath, color = crownGold)
    drawCircle(color = Color.Red, radius = w * 0.014f, center = Offset(w * 0.50f, crownY + h * 0.02f))
}

private fun DrawScope.drawLeafUmbrella(w: Float, h: Float, stage: FrogStage) {
    val leafColor = Color(0xFF43A047)
    val stemColor = Color(0xFF2E7D32)

    val (handX, handY) = when (stage) {
        FrogStage.TADPOLE -> Pair(w * 0.70f, h * 0.50f)
        FrogStage.FROGLET -> Pair(w * 0.72f, h * 0.50f)
        FrogStage.BIG_FROG, FrogStage.ROYAL_FROG -> Pair(w * 0.75f, h * 0.55f)
    }

    drawLine(
        color = stemColor,
        start = Offset(handX, handY),
        end = Offset(handX - w * 0.05f, handY - h * 0.45f),
        strokeWidth = w * 0.02f,
        cap = StrokeCap.Round
    )

    val leafX = handX - w * 0.05f
    val leafY = handY - h * 0.45f
    val leafPath = Path().apply {
        moveTo(leafX - w * 0.22f, leafY)
        quadraticTo(leafX, leafY - h * 0.18f, leafX + w * 0.22f, leafY)
        quadraticTo(leafX, leafY - h * 0.02f, leafX - w * 0.22f, leafY)
        close()
    }
    drawPath(leafPath, color = leafColor)
}

private fun DrawScope.drawMoodMouth(
    w: Float,
    mood: FrogMood,
    centerX: Float,
    centerY: Float,
    width: Float
) {
    val mouthColor = Color(0xFF33691E)
    when (mood) {
        FrogMood.HAPPY -> {
            val path = Path().apply {
                moveTo(centerX - width / 2, centerY)
                quadraticTo(centerX, centerY + width * 0.6f, centerX + width / 2, centerY)
            }
            drawPath(path, color = mouthColor, style = Stroke(width = w * 0.025f, cap = StrokeCap.Round))
        }
        FrogMood.NEUTRAL -> {
            drawLine(
                color = mouthColor,
                start = Offset(centerX - width / 2, centerY + width * 0.2f),
                end = Offset(centerX + width / 2, centerY + width * 0.2f),
                strokeWidth = w * 0.025f,
                cap = StrokeCap.Round
            )
        }
        FrogMood.HUNGRY -> {
            val path = Path().apply {
                moveTo(centerX - width / 2, centerY + width * 0.4f)
                quadraticTo(centerX, centerY, centerX + width / 2, centerY + width * 0.4f)
            }
            drawPath(path, color = mouthColor, style = Stroke(width = w * 0.025f, cap = StrokeCap.Round))

            val tearPath = Path().apply {
                moveTo(centerX + width * 0.9f, centerY - width * 0.1f)
                quadraticTo(centerX + width * 1.0f, centerY + width * 0.3f, centerX + width * 0.85f, centerY + width * 0.3f)
                quadraticTo(centerX + width * 0.70f, centerY + width * 0.3f, centerX + width * 0.9f, centerY - width * 0.1f)
                close()
            }
            drawPath(tearPath, color = Color(0xFF4FC3F7))
        }
    }
}

private data class Quad4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

private data class Tuple5<A, B, C, D, E>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E
)
