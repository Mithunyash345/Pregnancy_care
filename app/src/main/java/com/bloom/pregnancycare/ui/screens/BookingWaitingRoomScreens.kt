package com.bloom.pregnancycare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.pregnancycare.ui.components.CustomTopAppBar
import com.bloom.pregnancycare.ui.components.PillButton
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(viewModel: MainViewModel) {
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Select Appointment slot",
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("doctors") }) {
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DividerGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👩‍⚕️", fontSize = 22.sp)
                }
                Column {
                    Text("Booking checkup with", fontSize = 10.sp, color = SoftGray)
                    Text("Dr. Sarah Jenkins", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Text("Obstetrician & Gynecologist", fontSize = 10.sp, color = DarkPink)
                }
            }

            // Date Picker Placeholder
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select date", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                // Select date chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Jun 26", "Jun 27", "Jun 28", "Jun 29").forEach { date ->
                        val isSelected = selectedDate == date
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SoftPink else SnowWhite)
                                .border(0.8.dp, if (isSelected) Color.Transparent else DividerGray, RoundedCornerShape(12.dp))
                                .clickable { selectedDate = date }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(date, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        }
                    }
                }
            }

            // Time Picker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select time", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                // Select time grid
                val times = listOf("09:00 AM", "11:30 AM", "02:00 PM", "04:30 PM")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        times.take(2).forEach { time ->
                            val isSelected = selectedTime == time
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) SoftPink else SnowWhite)
                                    .border(0.8.dp, if (isSelected) Color.Transparent else DividerGray, RoundedCornerShape(12.dp))
                                    .clickable { selectedTime = time }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(time, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        times.takeLast(2).forEach { time ->
                            val isSelected = selectedTime == time
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) SoftPink else SnowWhite)
                                    .border(0.8.dp, if (isSelected) Color.Transparent else DividerGray, RoundedCornerShape(12.dp))
                                    .clickable { selectedTime = time }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(time, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PillButton(
                text = "Confirm Booking Slot",
                onClick = {
                    if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                        viewModel.bookAppointment(
                            doctorName = "Dr. Sarah Jenkins",
                            type = "Pregnancy Checkup / Video Consultation",
                            dateTime = "$selectedDate at $selectedTime"
                        )
                        viewModel.navigateTo("home")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedDate.isNotEmpty() && selectedTime.isNotEmpty()
            )
        }
    }
}

@Composable
fun WaitingRoomScreen(viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Video Screen Area
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Live Call", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                
                TextButton(onClick = { viewModel.navigateTo("home") }) {
                    Text("Exit Room", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                }
            }

            // Central Video Frame representation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1A1A1A))
                    .border(0.8.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏥", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Connecting stream...", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Dr. Sarah Jenkins is preparing to join", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                }

                // Mother Selfie thumbnail
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(width = 80.dp, height = 110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray)
                        .border(0.8.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👩", fontSize = 24.sp)
                }
            }

            // Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF222222))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) { Text("🎙️", fontSize = 18.sp) }
                IconButton(onClick = {}) { Text("📹", fontSize = 18.sp) }
                IconButton(
                    onClick = { viewModel.navigateTo("home") },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                ) {
                    Text("📞", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}
