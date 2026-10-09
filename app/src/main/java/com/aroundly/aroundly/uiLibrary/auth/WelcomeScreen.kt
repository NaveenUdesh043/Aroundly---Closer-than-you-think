package com.aroundly.aroundly.uiLibrary.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aroundly.aroundly.ui.theme.Amber
import com.aroundly.aroundly.ui.theme.AmberBg
import com.aroundly.aroundly.ui.theme.AmberBgLight
import com.aroundly.aroundly.ui.theme.AmberBorder
import com.aroundly.aroundly.ui.theme.AmberDark
import com.aroundly.aroundly.ui.theme.AmberGlow
import com.aroundly.aroundly.ui.theme.BgDark
import com.aroundly.aroundly.ui.theme.BgMid
import com.aroundly.aroundly.ui.theme.BgWarm
import com.aroundly.aroundly.ui.theme.BlueGlow
import com.aroundly.aroundly.ui.theme.SageBg
import com.aroundly.aroundly.ui.theme.SageBgLight
import com.aroundly.aroundly.ui.theme.SageBorder
import com.aroundly.aroundly.ui.theme.SageDark
import com.aroundly.aroundly.ui.theme.SageGlow
import com.aroundly.aroundly.ui.theme.SageGreen
import com.aroundly.aroundly.ui.theme.White
import com.aroundly.aroundly.ui.theme.White08
import com.aroundly.aroundly.ui.theme.White10
import com.aroundly.aroundly.ui.theme.White25
import com.aroundly.aroundly.ui.theme.White40
import com.aroundly.aroundly.ui.theme.White45
import com.aroundly.aroundly.ui.theme.White50
import com.aroundly.aroundly.ui.theme.White70
import kotlinx.coroutines.delay
import com.aroundly.aroundly.R

@Composable
fun WelcomeScreen(
    onExplorerClick: () -> Unit,
    onBusinessOwnerClick: () -> Unit,
    onSignInClick: () -> Unit
) {

    var animStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        animStarted = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0.0f to BgDark,
                        0.4f to BgMid,
                        1.0f to BgWarm
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 900f)
                )
            )
    ) {

        GlowBlobs()
        IconButton(
            onClick = {  },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 30.dp, end = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Change Language",
                    tint = White
                )
                Text(
                    text = "Language",
                    fontSize = 8.sp
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp)
                    .padding(top = 64.dp)
            ) {

                LogoSection(animStarted)

                Spacer(Modifier.height(36.dp))

                FeaturePillsRow(animStarted)

                Spacer(Modifier.height(36.dp))

                JoinAsLabel(animStarted)

                Spacer(Modifier.height(14.dp))

                RoleCard(
                    icon = R.drawable.tourists,
                    title = "Explorer",
                    subtitle = "Discover & explore places near you",
                    iconBrush = Brush.linearGradient(listOf(Amber, AmberDark)),
                    iconShadow = AmberGlow,
                    cardBrush = Brush.linearGradient(listOf(AmberBg, AmberBgLight)),
                    borderColor = AmberBorder,
                    visible = animStarted,
                    delayMs = 300,
                    onClick = onExplorerClick
                )

                Spacer(Modifier.height(12.dp))

                RoleCard(
                    icon = R.drawable.manager,
                    title = "Business Owner",
                    subtitle = "List your restaurant, hotel or rental",
                    iconBrush = Brush.linearGradient(listOf(SageGreen, SageDark)),
                    iconShadow = SageGlow,
                    cardBrush = Brush.linearGradient(listOf(SageBg, SageBgLight)),
                    borderColor = SageBorder,
                    visible = animStarted,
                    delayMs = 420,
                    onClick = onBusinessOwnerClick
                )
            }

            BottomSignInRow(
                visible = animStarted,
                onLogin = onSignInClick
            )
        }
    }
}


@Composable
fun GlowBlobs() {

    Box(Modifier.fillMaxSize()) {

        Box(
            Modifier
                .size(280.dp)
                .offset(130.dp, (-60).dp)
                .background(
                    Brush.radialGradient(
                        listOf(AmberGlow, Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Box(
            Modifier
                .size(200.dp)
                .offset((-80).dp, 100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(SageGlow, Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Box(
            Modifier
                .size(160.dp)
                .align(Alignment.BottomEnd)
                .offset(40.dp, (-200).dp)
                .background(
                    Brush.radialGradient(
                        listOf(BlueGlow, Color.Transparent)
                    ),
                    CircleShape
                )
        )
    }
}

@Composable
fun LogoSection(visible: Boolean) {

    val alpha by animateFloatAsState(
        if (visible) 1f else 0f,
        animationSpec = tween(600)
    )

    val slide by animateDpAsState(
        if (visible) 0.dp else 24.dp,
        animationSpec = tween(600)
    )

    Column(
        modifier = Modifier
            .alpha(alpha)
            .offset(y = slide)
    ) {

        Box(
            modifier = Modifier
                .size(72.dp)
                .shadow(20.dp, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.aroundly_app_icon),
                contentDescription = "App Icon",
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            buildAnnotatedString {

                withStyle(
                    SpanStyle(
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Around")
                }

                withStyle(
                    SpanStyle(
                        color = Amber,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("ly")
                }
            },
            fontSize = 42.sp
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "Discover cafés, restaurants, stays\n& rentals near you — instantly.",
            color = White50,
            fontSize = 15.sp
        )
    }
}

private val featurePills =
    listOf(
        "☕ Cafés", "🍽️ Food", "🏛️ Visit",
        "🏨 Stay", "🚲 Rent"
    )

@Composable
fun FeaturePillsRow(
    visible: Boolean
) {
    val rows = featurePills.chunked(3)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        rows.forEachIndexed { rowIndex, row ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                row.forEach { pill ->
                    FeaturePill(
                        text = pill,
                        visible = visible,
                        delayMs = 120 + (rowIndex * 120)
                    )
                }

                if (row.size < 3) {
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturePill(
    text: String,
    visible: Boolean,
    delayMs: Int
) {

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, delayMillis = delayMs),
        label = ""
    )

    Box(
        modifier = Modifier
            .alpha(alpha)
            .background(
                color = White10,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {

        Text(
            text = text,
            color = White70,
            fontSize = 13.sp
        )
    }
}

@Composable
fun JoinAsLabel(visible: Boolean) {

    val alpha by animateFloatAsState(
        if (visible) 1f else 0f,
        animationSpec = tween(400)
    )

    Text(
        "JOIN AS",
        fontSize = 11.sp,
        letterSpacing = 1.5.sp,
        color = White25,
        modifier = Modifier.alpha(alpha)
    )
}


@Composable
fun RoleCard(
    icon: Int,
    title: String,
    subtitle: String,
    iconBrush: Brush,
    iconShadow: Color,
    cardBrush: Brush,
    borderColor: Color,
    visible: Boolean,
    delayMs: Int,
    onClick: () -> Unit
) {

    val alpha by animateFloatAsState(
        if (visible) 1f else 0f,
        animationSpec = tween(500, delayMillis = delayMs)
    )

    Row(
        modifier = Modifier
            .alpha(alpha)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBrush)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            Modifier
                .size(52.dp)
                .background(iconBrush, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {

            Text(title, color = White, fontWeight = FontWeight.Bold)

            Text(
                subtitle,
                color = White45,
                fontSize = 12.sp
            )
        }

        Text("→", color = White25)
    }
}


@Composable
fun BottomSignInRow(
    visible: Boolean,
    onLogin: () -> Unit
) {

    val alpha by animateFloatAsState(
        if (visible) 1f else 0f,
        animationSpec = tween(500)
    )

    Column(
        modifier = Modifier
            .alpha(alpha)
            .fillMaxWidth()
            .padding(32.dp)
    ) {

        HorizontalDivider(color = White08)

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                "Already have an account? ",
                color = White40
            )

            Text(
                "Sign in",
                color = Amber,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLogin() }
            )
        }
    }
}
