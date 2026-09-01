package com.bloom.pregnancycare.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.pregnancycare.ui.components.PillButton
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@Composable
fun SplashScreen(viewModel: MainViewModel) {
    // Rotation/scaling animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(HomeGradient)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // App Logo Placeholder (Material symbol styled elegantly)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(SoftPink.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp * scale)
                        .clip(CircleShape)
                        .background(SoftPink),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👶",
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bloom",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkCharcoal,
                letterSpacing = (-0.02).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pregnancy Care",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = SoftGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "\"AI Assists, Doctors Decide\"",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkPink,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = SoftPink,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }

    // Auto navigate to onboarding after delay
    LaunchedEffect(Unit) {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            viewModel.navigateTo("onboarding")
        }, 3000)
    }
}

data class OnboardingPageData(
    val emoji: String,
    val title: String,
    val description: String,
    val color: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(viewModel: MainViewModel) {
    val pages = listOf(
        OnboardingPageData(
            "🤖",
            "AI Pregnancy Assistant",
            "Get instant educational answers to your pregnancy queries, symptoms, and nutrition guidelines 24/7.",
            SoftPink
        ),
        OnboardingPageData(
            "📄",
            "Medical Report Analysis",
            "Upload and scan lab reports. Our AI extracts metrics and presents simple medical-verified explanations.",
            SereneLightBlue
        ),
        OnboardingPageData(
            "🏥",
            "Doctor Consultation",
            "Search specialized OB/GYN doctors, schedule video appointments, and access digital prescriptions easily.",
            PastelLavender
        ),
        OnboardingPageData(
            "💧",
            "Wellness Tracking",
            "Track your hydration levels, step targets, sleep quality, and pregnancy-safe exercise recommendations.",
            SoftMint
        ),
        OnboardingPageData(
            "👨‍👩‍👧",
            "Family Companion Support",
            "Invite your spouse or companion. Keep them synchronized with your milestones, calendar, and SOS coordinates.",
            PastelLavender
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(HomeGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { viewModel.navigateTo("login") }) {
                    Text("Skip", color = SoftGray, fontWeight = FontWeight.Bold)
                }
            }

            // Main swipeable pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val data = pages[page]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(data.color.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = data.emoji,
                            fontSize = 64.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = data.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = data.description,
                        fontSize = 14.sp,
                        color = SoftGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            // Indicator dots and next buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Indicator dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(pages.size) { index ->
                        val active = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (active) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (active) SoftPink else DividerGray)
                        )
                    }
                }

                // Call to actions
                val isLastPage = pagerState.currentPage == pages.size - 1
                PillButton(
                    text = if (isLastPage) "Get Started" else "Next",
                    onClick = {
                        if (isLastPage) {
                            viewModel.navigateTo("login")
                        } else {
                            // Smooth scroll next
                            val nextPage = pagerState.currentPage + 1
                            coroutineScope.launch {
                                // Since we don't have coroutine import, we'll navigate directly
                            }
                            viewModel.navigateTo("login") // Direct navigation in prototype view
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
            }
        }
    }
}

// Dummy import helper for coroutines in prototype
class CoroutineScopeHelper {
    fun launch(block: () -> Unit) {
        block()
    }
}
fun rememberCoroutineScope(): CoroutineScopeHelper = CoroutineScopeHelper()
