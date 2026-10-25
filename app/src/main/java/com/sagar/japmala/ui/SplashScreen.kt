package com.sagar.japmala.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sagar.japmala.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private val SplashBase = Color(0xFF2E0C05) // must match @color/splash_bg

/**
 * Animated hand-off from the system splash: a glowing Om inside a mala whose 108 beads
 * light up one after another, like the first round of jap.
 */
@Composable
fun JapSplash(onFinished: () -> Unit) {
    val finished by rememberUpdatedState(onFinished)
    val appear = remember { Animatable(0f) }
    val beads = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { appear.animateTo(1f, tween(1000, easing = FastOutSlowInEasing)) }
        beads.animateTo(1f, tween(2100, easing = LinearEasing))
        delay(450)
        finished()
    }

    val glow = rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.9f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowScale",
    )

    Box(Modifier.fillMaxSize().background(SplashBase)) {
        // Soft saffron aura behind the Om
        Canvas(Modifier.fillMaxSize().alpha(appear.value)) {
            val r = size.minDimension * 0.85f * glow.value
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFB8421A).copy(alpha = 0.75f), Color(0xFF6B1F0C).copy(alpha = 0.35f), Color.Transparent),
                    center = center,
                    radius = r,
                ),
                radius = r,
                center = center,
            )
        }

        Text(
            text = "॥ श्री गणेशाय नमः ॥",
            color = JapColors.LightGold,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 56.dp)
                .alpha(appear.value),
        )

        // Mala ring + Om, centred exactly where the system splash draws its icon
        Box(Modifier.align(Alignment.Center).size(264.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val ringR = size.minDimension / 2 - 10.dp.toPx()
                val count = 108
                val beadR = 2.6.dp.toPx()
                val lit = beads.value * count
                drawCircle(
                    color = JapColors.Gold.copy(alpha = 0.25f),
                    radius = ringR,
                    style = Stroke(width = 1.dp.toPx()),
                )
                for (i in 0 until count) {
                    val a = Math.toRadians(-90.0 + (i + 0.5) * 360.0 / count)
                    val p = Offset(center.x + ringR * cos(a).toFloat(), center.y + ringR * sin(a).toFloat())
                    val on = (lit - i).coerceIn(0f, 1f)
                    drawCircle(Color(0xFFFFF0D0).copy(alpha = 0.16f), beadR, p)
                    if (on > 0f) {
                        drawCircle(JapColors.LightGold.copy(alpha = on), beadR * (1f + 0.35f * (1f - on)), p)
                    }
                }
                // Sumeru bead
                drawCircle(JapColors.Saffron, beadR * 2.1f, Offset(center.x, center.y - ringR - beadR * 2.6f))
                drawCircle(JapColors.LightGold, beadR * 2.1f, Offset(center.x, center.y - ringR - beadR * 2.6f), style = Stroke(1.2.dp.toPx()))
            }
            Icon(
                painter = painterResource(R.drawable.ic_om),
                contentDescription = null,
                tint = Color(0xFFF0C050),
                modifier = Modifier.size(100.dp).scale((0.9f + 0.1f * appear.value) * glow.value.coerceAtMost(1.04f)),
            )
        }

        Column(
            Modifier.align(Alignment.Center).offset(y = 196.dp).alpha(appear.value),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "जप माला",
                color = JapColors.LightGold,
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "J A P   M A L A",
                color = Color(0xFFFFE9C8).copy(alpha = 0.8f),
                fontSize = 13.sp,
                letterSpacing = 3.sp,
            )
        }

        Text(
            text = "जपात् सिद्धिः",
            color = Color(0xFFFFE9C8).copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp)
                .alpha(appear.value),
        )
    }
}
