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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.pregnancycare.ui.components.BoxDefaults
import com.bloom.pregnancycare.ui.components.CustomTopAppBar
import com.bloom.pregnancycare.ui.components.PillButton
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencySupportScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency support", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CriticalRose) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Text("⬅️", fontSize = 18.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
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
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CRITICAL EMERGENCY ALARM",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CriticalRose,
                letterSpacing = 1.sp
            )

            // Pulsing SOS trigger
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(CriticalRose.copy(alpha = 0.15f))
                    .clickable {
                        viewModel.triggerCompanionAlert(" Elena has triggered a Critical Emergency SOS signal!")
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(CriticalRose),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🚨", fontSize = 36.sp)
                        Text("PRESS SOS", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }

            Text(
                text = "Pressing the SOS button instantly broadcasts your exact GPS coordinates to Mark (Companion) and Dr. Jenkins.",
                fontSize = 10.sp,
                color = SoftGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 14.sp
            )

            // Shared Location Mock Map panel
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnowWhite),
                border = BoxDefaults.borderStroke(DividerGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Live Location coordinates", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GPS: 37.7749° N, 122.4194° W", fontSize = 10.sp, color = SoftGray)
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SoftMint)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("SHARING LIVE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF319795))
                        }
                    }
                }
            }

            // Quick Call Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PillButton(
                    text = "Call Local Emergency Services (911)",
                    onClick = { /* Call 911 */ },
                    containerColor = CriticalRose,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                PillButton(
                    text = "Call Husband (Mark Rostov)",
                    onClick = { /* Call Mark */ },
                    containerColor = PastelLavender,
                    contentColor = DarkCharcoal,
                    modifier = Modifier.fillMaxWidth()
                )

                PillButton(
                    text = "Call Dr. Sarah Jenkins (OB/GYN)",
                    onClick = { /* Call Doctor */ },
                    containerColor = SereneLightBlue,
                    contentColor = DarkCharcoal,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Emergency Instructions checklist
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnowWhite),
                border = BoxDefaults.borderStroke(DividerGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Critical Symptom Guide", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("1. Severe bleeding or fluid loss: Lie down, elevate hips, do not insert anything vaginally. Call 911 immediately.", fontSize = 9.sp, color = SoftGray, lineHeight = 13.sp)
                        Text("2. Rhythmic Contractions: Count frequency. If contractions are less than 5 minutes apart, prepare for hospital transition.", fontSize = 9.sp, color = SoftGray, lineHeight = 13.sp)
                        Text("3. Severe headache or sudden swelling: Lie down in a dark room. Check blood pressure if possible.", fontSize = 9.sp, color = SoftGray, lineHeight = 13.sp)
                    }
                }
            }
        }
    }
}
