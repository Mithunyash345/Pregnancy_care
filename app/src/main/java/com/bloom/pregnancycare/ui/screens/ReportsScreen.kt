package com.bloom.pregnancycare.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Brush
import com.bloom.pregnancycare.ui.components.BoxDefaults
import com.bloom.pregnancycare.ui.components.CustomTopAppBar
import com.bloom.pregnancycare.ui.components.GlassCard
import com.bloom.pregnancycare.ui.components.StatusBadge
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@Composable
fun ReportsScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Medical Report Analysis",
                subtitle = "OCR AI Diagnostics",
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
            // Upload / Scan Widget
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPink.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, SoftPink.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.simulateScanner {
                            viewModel.navigateTo("report_detail")
                        }
                        viewModel.navigateTo("report_scanner")
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SoftPink.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📄", fontSize = 28.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Analyze New Lab Report", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Text("Upload PDF, Blood Panel sheet, or Scan Report image", fontSize = 10.sp, color = SoftGray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SoftPink)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Upload & Analyze", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C2C2C))
                    }
                }
            }

            // Report History List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Report History", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                viewModel.reports.forEach { report ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo("report_detail") }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(report.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                Text("${report.date} • ${report.category}", fontSize = 9.sp, color = SoftGray)
                            }
                            Text("➡️", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportScannerScreen(viewModel: MainViewModel) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131411)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Scanner frame
            Box(
                modifier = Modifier
                    .size(width = 220.dp, height = 300.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(16.dp)
            ) {
                // Mock text lines
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.fillMaxWidth(0.8f).height(6.dp).background(Color.White.copy(alpha = 0.2f)))
                    Box(modifier = Modifier.fillMaxWidth(0.5f).height(6.dp).background(Color.White.copy(alpha = 0.2f)))
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.White.copy(alpha = 0.1f)))
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.White.copy(alpha = 0.1f)))
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.White.copy(alpha = 0.1f)))
                }

                // Laser scan line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .offset(y = (300 * laserPosition).dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(SoftPink, SoftPink.copy(alpha = 0.2f))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Processing Document OCR...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = viewModel.scanProgress.value ?: "Extracting metrics...",
                fontSize = 11.sp,
                color = SoftPink,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    val report = viewModel.reports.firstOrNull() ?: return

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "OCR Diagnostics Panel",
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("reports") }) {
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Lab Header
            Column {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(WarningAmber.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("ACTION RECOMMENDED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(report.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                Text("Scanned: ${report.date} • Verified by Bloom OCR", fontSize = 10.sp, color = SoftGray)
            }

            // Extracted values list
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Extracted Lab Values", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                report.values.forEach { extVal ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(
                            if (extVal.status == "Low") CriticalRose.copy(alpha = 0.3f) else DividerGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(extVal.markerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                Text("Healthy Range: ${extVal.normalRange}", fontSize = 9.sp, color = MutedSage)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(extVal.value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = extVal.status)
                            }
                        }
                    }
                }
            }

            // AI Explanation Cards
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPink.copy(alpha = 0.1f)),
                border = BoxDefaults.borderStroke(SoftPink.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bloom AI Clinical Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = report.aiInsights,
                        fontSize = 11.sp,
                        color = DarkCharcoal,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("RECOMMENDED ACTIONS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DarkPink)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            listOf(
                                "Ensure you take your Iron Supplement (200mg) with Vitamin C.",
                                "Focus on iron-rich foods (spinach, lentils, lean beef).",
                                "Follow up with Dr. Sarah Jenkins at your appointment today."
                            ).forEachIndexed { i, action ->
                                Text("${i+1}. $action", fontSize = 10.sp, color = SoftGray, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
