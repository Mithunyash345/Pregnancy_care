package com.bloom.pregnancycare.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.pregnancycare.data.MockData
import com.bloom.pregnancycare.ui.components.BoxDefaults
import com.bloom.pregnancycare.ui.components.CustomTopAppBar
import com.bloom.pregnancycare.ui.components.GlassCard
import com.bloom.pregnancycare.ui.components.MockActivityChart
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessDashboardScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Wellness Tracking",
                subtitle = "Active charts",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Water Tracker Progress
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnowWhite),
                border = BoxDefaults.borderStroke(DividerGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💧 Water Intake", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        Text("${viewModel.waterLogged.value} / ${viewModel.waterTarget} ml", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Weekly chart mock
                    MockActivityChart(points = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.8f, 0.6f, 0.5f), lineColor = Color(0xFF63B3ED))
                }
            }

            // Steps Tracker Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnowWhite),
                border = BoxDefaults.borderStroke(DividerGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👟 Step Tracker", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        Text("${viewModel.stepsLogged.value} / ${viewModel.stepsTarget}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Weekly steps chart mock
                    MockActivityChart(points = listOf(0.3f, 0.5f, 0.9f, 0.8f, 0.7f, 0.4f, 0.8f), lineColor = SoftPink)
                }
            }

            // Sleep stats
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnowWhite),
                border = BoxDefaults.borderStroke(DividerGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🛌 Sleep Quality", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Sleep", fontSize = 10.sp, color = SoftGray)
                            Text("8.2 hrs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        }
                        Column {
                            Text("Deep Sleep", fontSize = 10.sp, color = SoftGray)
                            Text("2.1 hrs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        }
                        Column {
                            Text("Awake time", fontSize = 10.sp, color = SoftGray)
                            Text("12 mins", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        }
                    }
                }
            }

            // Exercise recommendations
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pregnancy Exercise Recommendations", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                MockData.exercises.forEach { ex ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(ex.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(SoftMint)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(ex.duration, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF319795))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Benefits: ${ex.benefits}", fontSize = 10.sp, color = SoftGray, lineHeight = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
