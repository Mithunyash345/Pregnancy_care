package com.bloom.pregnancycare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.pregnancycare.ui.components.BoxDefaults
import com.bloom.pregnancycare.ui.components.CustomProgressBar
import com.bloom.pregnancycare.ui.components.GlassCard
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotherDashboardScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SoftPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👩", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Good Morning, Elena", fontSize = 12.sp, color = SoftGray)
                            Text("Week 24, Day 3", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo("notifications_center") }) {
                        Text("🔔", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo("emergency_support") },
                containerColor = CriticalRose,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 60.dp) // Cushion above bottom nav
            ) {
                Text("🆘", fontSize = 24.sp)
            }
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
            // circular / card statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wellness Score Card
                GlassCard(modifier = Modifier.weight(1.2f)) {
                    Text("Wellness Score", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Text("Baseline: Optimal", fontSize = 9.sp, color = MutedSage)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${viewModel.wellnessScore.value}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkPink
                        )
                        Text(
                            text = "/100",
                            fontSize = 12.sp,
                            color = SoftGray,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    CustomProgressBar(progress = viewModel.wellnessScore.value.toFloat() / 100f)
                }

                // Baby Size Card
                GlassCard(modifier = Modifier.weight(0.8f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(SoftPink.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🥭", fontSize = 28.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Baby is size of a", fontSize = 9.sp, color = SoftGray)
                        Text("Mango", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        Text("109 Days to go", fontSize = 9.sp, color = MutedSage)
                    }
                }
            }

            // Quick access to AI Assistant
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPink.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ask Bloom AI Companion", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable { 
                                    viewModel.navigateTo("assistant")
                                    viewModel.sendChatMessage("Why do I have heartburn in week 24?")
                                }
                                .padding(8.dp)
                        ) {
                            Text("🔍 Heartburn normal?", fontSize = 10.sp, color = DarkCharcoal, maxLines = 1)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable { 
                                    viewModel.navigateTo("assistant")
                                    viewModel.sendChatMessage("What is a normal kick count?")
                                }
                                .padding(8.dp)
                        ) {
                            Text("🔍 Normal kick count?", fontSize = 10.sp, color = DarkCharcoal, maxLines = 1)
                        }
                    }
                }
            }

            // Vital Vitals Row
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Vital Tracking", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Water Card (interactive)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("wellness_dashboard") }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💧", fontSize = 16.sp)
                                Button(
                                    onClick = { viewModel.addWater(250) },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(24.dp),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = SereneLightBlue)
                                ) {
                                    Text("+", fontSize = 12.sp, color = Color.DarkGray)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Water", fontSize = 9.sp, color = SoftGray)
                            Text("${viewModel.waterLogged.value}ml", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            Text("/ ${viewModel.waterTarget}ml", fontSize = 9.sp, color = MutedSage)
                        }
                    }

                    // Steps Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("wellness_dashboard") }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("👟", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Steps", fontSize = 9.sp, color = SoftGray)
                            Text("${viewModel.stepsLogged.value}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            Text("/ ${viewModel.stepsTarget}", fontSize = 9.sp, color = MutedSage)
                        }
                    }

                    // Sleep Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("wellness_dashboard") }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🛌", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Sleep", fontSize = 9.sp, color = SoftGray)
                            Text("8.2 hrs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            Text("Average Rest", fontSize = 9.sp, color = MutedSage)
                        }
                    }
                }
            }

            // Medication Schedule list
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Schedule & Medication", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                viewModel.medications.forEach { med ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = med.isTaken,
                                    onCheckedChange = { viewModel.toggleMedication(med.id) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = med.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (med.isTaken) SoftGray else DarkCharcoal,
                                        textDecoration = if (med.isTaken) TextDecoration.LineThrough else null
                                    )
                                    Text(
                                        text = "${med.dosage} • ${med.time}",
                                        fontSize = 9.sp,
                                        color = MutedSage
                                    )
                                }
                            }
                            TextButton(
                                onClick = { viewModel.toggleMedication(med.id) },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (med.isTaken) SoftGray else DarkPink
                                )
                            ) {
                                Text(if (med.isTaken) "Undo" else "Take", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Upcoming Consultations
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Upcoming Consultations", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                viewModel.appointments.forEach { app ->
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
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(app.doctorName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                    Text(app.type, fontSize = 9.sp, color = SoftGray, lineHeight = 12.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SoftMint)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("CONFIRMED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF319795))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⏰ " + app.dateTime,
                                    fontSize = 10.sp,
                                    color = MutedSage,
                                    fontWeight = FontWeight.Medium
                                )
                                if (app.isToday) {
                                    Button(
                                        onClick = { viewModel.navigateTo("waiting_room") },
                                        colors = ButtonDefaults.buttonColors(containerColor = SereneLightBlue, contentColor = Color.DarkGray),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Join Video call", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Milestones Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnowWhite),
                border = BoxDefaults.borderStroke(DividerGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Milestone Tracker", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🟢", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("First Heartbeat heard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                Text("Week 10 completed", fontSize = 9.sp, color = MutedSage)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🟡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Sugar Tolerance Screening", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                Text("Week 24 (In Progress Today)", fontSize = 9.sp, color = MutedSage)
                            }
                        }
                    }
                }
            }
        }
    }
}
