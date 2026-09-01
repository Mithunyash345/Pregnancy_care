package com.bloom.pregnancycare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.bloom.pregnancycare.ui.components.BoxDefaults
import com.bloom.pregnancycare.ui.components.CustomTopAppBar
import com.bloom.pregnancycare.ui.components.GlassCard
import com.bloom.pregnancycare.ui.components.PillButton
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionDashboardScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    var selectedReminderType by remember { mutableStateOf("water") }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Companion Mode Portal",
                subtitle = "Connected to Elena",
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PastelLavender),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍👩‍👧", fontSize = 14.sp)
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Invite Companion Code Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PastelLavender.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, PastelLavender.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("FAMILY ACCESS CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DarkLavender)
                    Text(
                        text = viewModel.companionCode.value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkCharcoal,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    Text(
                        text = "Give this code to Elena to synchronize companion alerts, locations, and milestones.",
                        fontSize = 10.sp,
                        color = SoftGray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    TextButton(onClick = { viewModel.regenerateCompanionCode() }) {
                        Text("Regenerate Code", color = DarkLavender, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Elena's snapshot
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Elena's Vitals Summary", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("💧 Hydration", fontSize = 10.sp, color = SoftGray)
                            Text("${viewModel.waterLogged.value} ml", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        }
                    }
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("👟 Steps Today", fontSize = 10.sp, color = SoftGray)
                            Text("${viewModel.stepsLogged.value}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        }
                    }
                }
            }

            // Send custom reminder widget
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnowWhite),
                border = BoxDefaults.borderStroke(DividerGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Push Quick Reminder to Elena", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reminder dropdown or select
                        listOf("water", "rest", "walk").forEach { type ->
                            val isSelected = selectedReminderType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(if (isSelected) PastelLavender else OffWhite)
                                    .clickable { selectedReminderType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(type.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    PillButton(
                        text = "Send Reminder Alert",
                        onClick = {
                            // Simulate sending alert
                            val msg = when (selectedReminderType) {
                                "water" -> "Mark sent a reminder: Please drink a glass of water!"
                                "rest" -> "Mark sent a reminder: Time to lie down and take a rest."
                                else -> "Mark sent a reminder: Let's go for a light healthy walk."
                            }
                            viewModel.triggerCompanionAlert(msg)
                        },
                        containerColor = PastelLavender,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Notifications / Logs
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Companion Notifications History", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                listOf(
                    Pair("Medication Reminder Alert", "Elena needs to take DHA Omega-3 at 08:00 PM."),
                    Pair("Daily Summary Logged", "Elena reached wellness score of 84/100 today."),
                    Pair("Milestone Cleared", "Elena completed week 24 sugar tolerance test.")
                ).forEach { logs ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(logs.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            Text(logs.second, fontSize = 10.sp, color = SoftGray, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
