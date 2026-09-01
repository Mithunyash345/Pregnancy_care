package com.bloom.pregnancycare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.pregnancycare.ui.components.BoxDefaults
import com.bloom.pregnancycare.ui.components.CustomTopAppBar
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    val alerts = listOf(
        NotificationAlert("💊 Medication Reminder", "Time to take your Iron Supplement (200mg) capsule. Recommend having it after lunch.", "10 mins ago", "Medication"),
        NotificationAlert("💧 Hydration reminder", "You have completed 1250ml water out of 2500ml target. Take another glass now.", "1 hour ago", "Wellness"),
        NotificationAlert("🏥 Appointment confirmed", "Dr. Sarah Jenkins Obstetrician review scheduled for today at 02:00 PM.", "3 hours ago", "Appointment"),
        NotificationAlert("🤰 Sugar tolerance test", "Glucose Panel report details uploaded and synced to your dashboard history.", "Yesterday", "Reports")
    )

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Notifications Center",
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Text("⬅️", fontSize = 18.sp)
                    }
                }
            )
        },
        containerColor = OffWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Active companion urgent alerts
            viewModel.companionAlert.value?.let { alertMessage ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CriticalRose),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Text("🚨", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(alertMessage, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { viewModel.dismissCompanionAlert() }) {
                            Text("❌", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }

            // Normal Alerts
            alerts.forEach { alert ->
                val badgeColor = when (alert.category) {
                    "Medication" -> SoftPink.copy(alpha = 0.4f)
                    "Wellness" -> SoftMint
                    "Appointment" -> SereneLightBlue.copy(alpha = 0.4f)
                    else -> PastelLavender.copy(alpha = 0.4f)
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SnowWhite),
                    border = BoxDefaults.borderStroke(DividerGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(alert.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(badgeColor)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(alert.category, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(alert.message, fontSize = 10.sp, color = SoftGray, lineHeight = 14.sp)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(alert.time, fontSize = 8.sp, color = MutedSage)
                    }
                }
            }
        }
    }
}

data class NotificationAlert(
    val title: String,
    val message: String,
    val time: String,
    val category: String
)
