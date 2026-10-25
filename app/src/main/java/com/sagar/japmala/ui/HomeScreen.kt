package com.sagar.japmala.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sagar.japmala.JapState
import com.sagar.japmala.JapViewModel
import com.sagar.japmala.R
import com.sagar.japmala.data.Mantra
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(vm: JapViewModel) {
    val s by vm.state.collectAsStateWithLifecycle()
    var showPicker by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var confirmReset by remember { mutableStateOf(false) }
    val dark = isSystemInDarkTheme()

    val view = LocalView.current
    DisposableEffect(s.keepScreenOn) {
        view.keepScreenOn = s.keepScreenOn
        onDispose { view.keepScreenOn = false }
    }

    Box(Modifier.fillMaxSize().background(japBackground(dark))) {
        // Large faint Om watermark
        Icon(
            painter = painterResource(R.drawable.ic_om),
            contentDescription = null,
            tint = JapColors.Gold.copy(alpha = if (dark) 0.07f else 0.10f),
            modifier = Modifier.align(Alignment.Center).size(380.dp),
        )

        Column(
            Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Header(onSettings = { showSettings = true })
            Spacer(Modifier.height(10.dp))
            MantraCard(s.selected, onClick = { showPicker = true })

            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                val side = minOf(maxWidth, maxHeight).coerceAtMost(360.dp)
                MalaCounter(
                    size = side,
                    beads = s.beads,
                    target = s.target,
                    malas = s.malas,
                    showHint = s.total == 0L,
                    onTap = vm::increment,
                )
            }

            StatsRow(s)
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TextButton(onClick = vm::undo, enabled = s.total > 0) { Text("↶  पीछे") }
                TextButton(onClick = { confirmReset = true }, enabled = s.total > 0) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("नया आरंभ")
                }
            }
        }

        CompletionBanner(
            visible = s.showCompleted,
            malas = s.malas,
            modifier = Modifier.align(Alignment.TopCenter).systemBarsPadding().padding(top = 68.dp),
        )
    }

    if (showPicker) {
        MantraSheet(
            state = s,
            onSelect = { vm.selectMantra(it); showPicker = false },
            onAdd = vm::addMantra,
            onDelete = vm::deleteMantra,
            onDismiss = { showPicker = false },
        )
    }

    if (showSettings) {
        SettingsSheet(
            state = s,
            onTarget = vm::setTarget,
            onHaptics = vm::setHaptics,
            onKeepScreenOn = vm::setKeepScreenOn,
            onVolumeKeys = vm::setVolumeKeys,
            onDismiss = { showSettings = false },
        )
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("नया आरंभ करें?") },
            text = { Text("“${s.selected.title}” की माला और कुल जप शून्य हो जाएँगे। आज का जप और दिन-क्रम बना रहेगा।") },
            confirmButton = {
                TextButton(onClick = { vm.resetCurrent(); confirmReset = false }) { Text("हाँ, आरंभ करें") }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("रहने दें") } },
        )
    }
}

@Composable
private fun Header(onSettings: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_om),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(34.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                "जप माला",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                "॥ जपात् सिद्धिः ॥",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        IconButton(onClick = onSettings) {
            Icon(Icons.Default.Settings, contentDescription = "सेटिंग", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun MantraCard(mantra: Mantra, onClick: () -> Unit) {
    val long = mantra.text.length > 30
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    mantra.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "मंत्र बदलें",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                mantra.text,
                style = if (long) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (mantra.roman.isNotEmpty() && !long) {
                Text(
                    mantra.roman,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** The tappable mala: 108 (or 27/54) beads on a thread around a saffron disc that counts on touch. */
@Composable
private fun MalaCounter(
    size: Dp,
    beads: Int,
    target: Int,
    malas: Int,
    showHint: Boolean,
    onTap: () -> Unit,
) {
    val tap by rememberUpdatedState(onTap)
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "press",
    )
    val pulse = remember { Animatable(0f) }
    LaunchedEffect(beads, malas) {
        pulse.snapTo(1f)
        pulse.animateTo(0f, tween(500))
    }

    val inactive = MaterialTheme.colorScheme.outline.copy(alpha = 0.32f)

    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val ringR = this.size.minDimension / 2 - 16.dp.toPx()
            val step = 360.0 / target
            val beadR = (2 * Math.PI.toFloat() * ringR / target * 0.34f).coerceIn(2.2.dp.toPx(), 7.dp.toPx())

            drawCircle(JapColors.Gold.copy(alpha = 0.35f), ringR, style = Stroke(1.dp.toPx()))

            for (i in 0 until target) {
                val a = Math.toRadians(-90.0 + (i + 0.5) * step)
                val p = Offset(center.x + ringR * cos(a).toFloat(), center.y + ringR * sin(a).toFloat())
                if (i < beads) {
                    drawCircle(JapColors.Gold, beadR, p)
                    drawCircle(Color.White.copy(alpha = 0.55f), beadR * 0.35f, p - Offset(beadR * 0.3f, beadR * 0.3f))
                } else {
                    drawCircle(inactive, beadR, p)
                }
                if (i == beads - 1) {
                    drawCircle(JapColors.Saffron.copy(alpha = 0.55f * pulse.value), beadR * (1.6f + 1.8f * pulse.value), p)
                }
            }

            // Sumeru – the guru bead, where every round begins and ends
            val sumeru = Offset(center.x, center.y - ringR - beadR * 1.6f)
            drawCircle(JapColors.Kumkum, beadR * 1.9f, sumeru)
            drawCircle(JapColors.Gold, beadR * 1.9f, sumeru, style = Stroke(1.5.dp.toPx()))
        }

        val disc = size * 0.72f
        Box(
            Modifier
                .size(disc)
                .scale(scale)
                .shadow(14.dp, CircleShape, ambientColor = JapColors.Maroon, spotColor = JapColors.Maroon)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFFFC66B), Color(0xFFFF9A2E), Color(0xFFE0651A)),
                        center = Offset(disc.value * 1.5f, disc.value * 1.2f),
                    )
                )
                .border(3.dp, Brush.linearGradient(listOf(Color(0xFFFFE9A8), JapColors.Gold, Color(0xFFB5790C))), CircleShape)
                .pointerInput(Unit) {
                    // Count on touch-down so fast tapping feels like rolling real beads
                    detectTapGestures(onPress = {
                        pressed = true
                        tap()
                        tryAwaitRelease()
                        pressed = false
                    })
                }
                .semantics {
                    role = Role.Button
                    contentDescription = "जप गिनें। अभी $beads / $target"
                    onClick(label = "एक जप") { tap(); true }
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_om),
                contentDescription = null,
                tint = JapColors.Maroon.copy(alpha = 0.10f),
                modifier = Modifier.size(disc * 0.72f),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$beads",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = JapColors.Maroon,
                )
                Text(
                    "/ $target",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = JapColors.Maroon.copy(alpha = 0.75f),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    if (showHint) "छूकर जप करें" else "माला $malas",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = JapColors.Maroon.copy(alpha = 0.85f),
                )
            }
        }
    }
}

@Composable
private fun StatsRow(s: JapState) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Stat("आज", "%,d".format(s.today), Modifier.weight(1f))
        Stat("माला", "${s.malas}", Modifier.weight(1f))
        Stat("कुल जप", "%,d".format(s.total), Modifier.weight(1f))
        Stat("नित्य दिन", "${s.streak}", Modifier.weight(1f))
    }
}

@Composable
private fun Stat(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    ) {
        Column(Modifier.padding(vertical = 10.dp, horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                softWrap = false,
            )
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CompletionBanner(visible: Boolean, malas: Int, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it },
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = JapColors.Maroon,
            border = BorderStroke(1.5.dp, JapColors.Gold),
            shadowElevation = 8.dp,
        ) {
            Text(
                "🙏  माला पूर्ण हुई  ·  $malas माला",
                color = JapColors.LightGold,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
            )
        }
    }
}
