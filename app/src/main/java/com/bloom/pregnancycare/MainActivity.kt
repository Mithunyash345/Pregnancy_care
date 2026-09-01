package com.bloom.pregnancycare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.pregnancycare.ui.screens.*
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PregnancyCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigationHost(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigationHost(viewModel: MainViewModel) {
    val currentScreen = viewModel.currentScreen.value
    val role = viewModel.currentRole.value

    // Determine if the screen should show a bottom navigation bar.
    // Bottom nav bar is shown for major tabs: home, assistant, reports, doctors, profile.
    val showBottomNav = currentScreen in listOf("home", "assistant", "reports", "doctors", "profile")

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (showBottomNav) innerPadding else PaddingValues(0.dp))
        ) {
            when (currentScreen) {
                "splash" -> SplashScreen(viewModel)
                "onboarding" -> OnboardingScreen(viewModel)
                "login" -> LoginScreen(viewModel)
                "register" -> RegisterScreen(viewModel)
                "forgot_password" -> ForgotPasswordScreen(viewModel)
                "otp_verification" -> OtpVerificationScreen(viewModel)
                
                // Bottom tab targets
                "home" -> MotherDashboardScreen(viewModel)
                "assistant" -> AssistantScreen(viewModel)
                "reports" -> ReportsScreen(viewModel)
                "doctors" -> DoctorsScreen(viewModel)
                "profile" -> ProfileScreen(viewModel)

                // Sub-details and overlay targets
                "report_scanner" -> ReportScannerScreen(viewModel)
                "report_detail" -> ReportDetailScreen(viewModel)
                "booking" -> BookingScreen(viewModel)
                "waiting_room" -> WaitingRoomScreen(viewModel)
                "wellness_dashboard" -> WellnessDashboardScreen(viewModel)
                "emergency_support" -> EmergencySupportScreen(viewModel)
                "notifications_center" -> NotificationsScreen(viewModel)

                // Alter-role portals
                "companion_dashboard" -> CompanionDashboardScreen(viewModel)
                "doctor_dashboard" -> DoctorDashboardScreen(viewModel)

                // Default fallback
                else -> MotherDashboardScreen(viewModel)
            }

            // Quick Role Portal switcher for prototype verification (floating helper button on screens without bottom nav)
            if (!showBottomNav && currentScreen != "splash" && currentScreen != "onboarding") {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            // Back to profile sandbox to change roles
                            viewModel.navigateTo("profile")
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("⚙️ Sandbox", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavigationItem("home", "🏠", "Home"),
        NavigationItem("assistant", "🤖", "AI Assist"),
        NavigationItem("reports", "📄", "Reports"),
        NavigationItem("doctors", "🩺", "Doctors"),
        NavigationItem("profile", "👩", "Profile")
    )

    NavigationBar(
        containerColor = SnowWhite,
        tonalElevation = 8.dp,
        modifier = Modifier.height(72.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = item.emoji,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) DarkPink else SoftGray
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SoftPink.copy(alpha = 0.25f)
                )
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val emoji: String,
    val label: String
)
