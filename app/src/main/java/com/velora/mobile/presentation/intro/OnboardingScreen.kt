package com.velora.mobile.presentation.intro

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import com.velora.mobile.R
private data class IntroPage(
    val title: String,
    val subtitle: String,
    @DrawableRes val artRes: Int
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    // Keep your previous text (same idea, upgraded visuals)
    val pages = listOf(
        IntroPage(
            title = stringResource(R.string.intro1),
            subtitle = stringResource(R.string.intro1_context),
            artRes = R.drawable.viewpager1
        ),
        IntroPage(
            title = stringResource(R.string.intro2),
            subtitle = stringResource(R.string.intro2_context),
            artRes = R.drawable.viewpager2
        ),
        IntroPage(
            title = stringResource(R.string.intro3),
            subtitle = stringResource(R.string.intro3_context),
            artRes = R.drawable.viewpager3
        )
    )

    val pager = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    SoftOnboardingBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            // ===== Top bar (logo + Skip) =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // tiny mark placeholder (you can replace with your velora_mark)
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                CircleShape
                            )
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Velora",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.weight(1f))

                TextButton(
                    onClick = onFinish,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(stringResource(R.string.skip), color = Color.Black.copy(alpha = 0.45f))
                }
            }

            Spacer(Modifier.height(10.dp))

            // ===== Pager =====
            HorizontalPager(
                state = pager,
                modifier = Modifier.weight(1f)
            ) { index ->
                val p = pages[index]
                OnboardingPageCard(page = p)
            }

            Spacer(Modifier.height(16.dp))

            // ===== Indicator + CTA =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DotsIndicator(
                    count = pages.size,
                    index = pager.currentPage
                )

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        if (pager.currentPage == pages.lastIndex) onFinish()
                        else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                    },
                    modifier = Modifier.height(54.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = if (pager.currentPage == pages.lastIndex) stringResource(R.string.get_started) else stringResource(
                            R.string.next
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ===== Bottom link =====
            TextButton(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.already_have_an_account),
                    color = Color.Black.copy(alpha = 0.55f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageCard(page: IntroPage) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // ===== Hero illustration area =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            // soft blob behind illustration (this is what makes it feel premium)
            SoftBlob(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(320.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )

            Image(
                painter = painterResource(page.artRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(280.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        // ===== Text like reference (big title + subtle text) =====
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.55f),
            lineHeight = 22.sp
        )

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun DotsIndicator(count: Int, index: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { i ->
            val selected = i == index
            val w by animateDpAsState(
                targetValue = if (selected) 18.dp else 8.dp,
                label = "dotW"
            )
            Box(
                Modifier
                    .height(8.dp)
                    .width(w)
                    .clip(RoundedCornerShape(99.dp))
                    .background(
                        if (selected) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.80f)
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f)
                    )
            )
        }
    }
}

/**
 * Very subtle background like the reference.
 */
@Composable
private fun SoftOnboardingBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // subtle top gradient glow
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = Offset(500f, 200f),
                        radius = 1000f
                    )
                )
        )
        content()
    }
}

/**
 * Minimal “orbital blob” decoration to mimic premium onboarding illustrations.
 */
@Composable
private fun SoftBlob(
    modifier: Modifier,
    tint: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(tint, Color.Transparent),
                    radius = 900f
                )
            )
    )
}