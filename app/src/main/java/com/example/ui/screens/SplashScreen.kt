package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

data class CurrencyTip(
    val title: String,
    val text: String,
    val tag: String
)

val CURRENCY_TIPS = listOf(
    CurrencyTip(
        title = "Avoid Airport Kiosks",
        text = "Airport currency exchange counters charge 8% to 15% markups. Use bank ATMs with no-foreign-transaction-fee cards in the city instead.",
        tag = "Travel Smart"
    ),
    CurrencyTip(
        title = "Always Pay in Local Currency",
        text = "When paying abroad by card and prompted between home or local currency, always choose the local currency to avoid costly DCC markups.",
        tag = "Payment Tip"
    ),
    CurrencyTip(
        title = "The True Mid-Market Rate",
        text = "GlobalCash displays the real interbank mid-market exchange rate. Compare your bank's rate to this benchmark to check their fee.",
        tag = "FX Insight"
    ),
    CurrencyTip(
        title = "Weekend Forex Markup",
        text = "Global currency markets close on weekends. Many exchange services add an extra 0.5%–1.5% buffer on Saturdays and Sundays.",
        tag = "Market Rule"
    ),
    CurrencyTip(
        title = "Emergency Cash Rule",
        text = "Even in cashless destinations, keep $50–$100 equivalent in local small bills tucked away for taxis, tips, or unexpected outages.",
        tag = "Preparedness"
    ),
    CurrencyTip(
        title = "Offline Currency Conversion",
        text = "GlobalCash caches exchange rates in your device's local database, so you can calculate conversions offline without roaming fees.",
        tag = "App Feature"
    ),
    CurrencyTip(
        title = "Notify Your Bank Before Traveling",
        text = "Add a travel notice in your mobile banking app so overseas transactions aren't blocked by anti-fraud security filters.",
        tag = "Security"
    ),
    CurrencyTip(
        title = "Watchlist & Rate Tracking",
        text = "Pin your most important currency pairs to Favorites to monitor exchange rate trends before transferring funds.",
        tag = "Savings"
    )
)

@Composable
fun AnimatedSplashScreen(
    isLoadingRates: Boolean,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logoScale = remember { Animatable(0.4f) }
    val contentAlpha = remember { Animatable(0f) }
    val tipCardSlide = remember { Animatable(50f) }

    var currentTipIndex by remember { mutableIntStateOf((CURRENCY_TIPS.indices).random()) }
    var minTimeElapsed by remember { mutableStateOf(false) }

    // Pulsing halo animation around logo
    val infiniteTransition = rememberInfiniteTransition(label = "halo")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloScale"
    )
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloAlpha"
    )

    // Initial entrance spring animations
    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(150)
        contentAlpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 600)
        )
    }

    LaunchedEffect(Unit) {
        delay(250)
        tipCardSlide.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    // Minimum display duration of 2.2 seconds so user can read the tip
    LaunchedEffect(Unit) {
        delay(2200)
        minTimeElapsed = true
    }

    // Automatically transition when both min time passed and rates finished loading
    LaunchedEffect(minTimeElapsed, isLoadingRates) {
        if (minTimeElapsed && !isLoadingRates) {
            delay(300)
            onSplashFinished()
        }
    }

    val currentTip = CURRENCY_TIPS[currentTipIndex % CURRENCY_TIPS.size]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF09111E),
                        Color(0xFF0F1E36),
                        Color(0xFF070B14)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top branding / skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.clickable { onSplashFinished() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Skip",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Skip splash",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Center Brand Visual
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(logoScale.value)
            ) {
                // Pulsing Halo & Logo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(130.dp)
                ) {
                    // Pulsing ambient glow
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(haloScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF29B6F6).copy(alpha = haloAlpha * 1.5f),
                                        Color(0xFF00E676).copy(alpha = haloAlpha),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Secondary border ring
                    Box(
                        modifier = Modifier
                            .size(102.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                brush = Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF00E5FF),
                                        Color(0xFF00E676),
                                        Color(0xFFFFD54F),
                                        Color(0xFF00E5FF)
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    // Core Logo
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon_1784885544152),
                        contentDescription = "GlobalCash Logo",
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "GlobalCash",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Real-Time Currency Rates & Converter",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF90CAF9),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Tip of the Day Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD54F).copy(alpha = 0.4f),
                                Color(0xFF00E5FF).copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF13223A).copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFB300).copy(alpha = 0.18f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Tip icon",
                                    tint = Color(0xFFFFCA28),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TIP OF THE DAY",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFD54F),
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // Next tip action
                        IconButton(
                            onClick = {
                                currentTipIndex = (currentTipIndex + 1) % CURRENCY_TIPS.size
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Next tip",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AnimatedContent(
                        targetState = currentTip,
                        transitionSpec = {
                            fadeIn(tween(300)) + slideInVertically { it / 3 } togetherWith fadeOut(tween(200))
                        },
                        label = "tipAnimation"
                    ) { tip ->
                        Column {
                            Text(
                                text = tip.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = tip.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCFD8DC),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading Bar / Status or Continue Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoadingRates || !minTimeElapsed) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF00E5FF),
                        trackColor = Color.White.copy(alpha = 0.12f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isLoadingRates) "Syncing live global exchange rates..." else "Ready to convert!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Button(
                        onClick = onSplashFinished,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color(0xFF00363A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("splash_continue_button")
                    ) {
                        Text(
                            text = "Get Started",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Get Started",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
