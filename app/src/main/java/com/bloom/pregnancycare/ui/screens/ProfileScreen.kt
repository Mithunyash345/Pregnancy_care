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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.pregnancycare.ui.components.BoxDefaults
import com.bloom.pregnancycare.ui.components.CustomTopAppBar
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Elena Rostov Profile",
                subtitle = "Sync active",
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Text("🏠", fontSize = 18.sp)
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
            // Profile Card Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnowWhite),
                border = BoxDefaults.borderStroke(DividerGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SoftPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👩", fontSize = 28.sp)
                    }

                    Column {
                        Text("Elena Rostov", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        Text("elena.rostova@gmail.com", fontSize = 11.sp, color = SoftGray)
                        Text("Maternity ID: #BL-99211", fontSize = 9.sp, color = MutedSage)
                    }
                }
            }

            // Pregnancy Metrics
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pregnancy Information", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SnowWhite),
                    border = BoxDefaults.borderStroke(DividerGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Current Progress", fontSize = 11.sp, color = SoftGray)
                            Text("Week 24 (Trimester 2)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Due Date", fontSize = 11.sp, color = SoftGray)
                            Text("October 12, 2026", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Primary Doctor", fontSize = 11.sp, color = SoftGray)
                            Text("Dr. Sarah Jenkins", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkPink)
                        }
                    }
                }
            }

            // Settings & Role Switcher
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Prototype Sandbox Tools", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SnowWhite),
                    border = BoxDefaults.borderStroke(DividerGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Switch Active Role Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Mother", "Companion", "Doctor").forEach { role ->
                                val isSelected = viewModel.currentRole.value == role.lowercase()
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) SoftPink else OffWhite)
                                        .border(0.8.dp, DividerGray, RoundedCornerShape(8.dp))
                                        .clickable { 
                                            viewModel.switchRole(role.lowercase())
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(role, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                }
                            }
                        }
                    }
                }
            }

            // Medical History checklist
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Medical History Notes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SnowWhite),
                    border = BoxDefaults.borderStroke(DividerGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("• Blood Type: A-Positive", fontSize = 11.sp, color = SoftGray)
                        Text("• Chronic conditions: None", fontSize = 11.sp, color = SoftGray)
                        Text("• Allergies: Penicillin sensitive", fontSize = 11.sp, color = SoftGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = DarkPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
