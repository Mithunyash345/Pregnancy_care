package com.bloom.pregnancycare.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.pregnancycare.data.Doctor
import com.bloom.pregnancycare.data.MockData
import androidx.compose.foundation.border
import com.bloom.pregnancycare.ui.components.BoxDefaults
import com.bloom.pregnancycare.ui.components.CustomTopAppBar
import com.bloom.pregnancycare.ui.components.GlassCard
import com.bloom.pregnancycare.ui.components.PillButton
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsScreen(viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecFilter by remember { mutableStateOf("All") }
    var detailDoctor by remember { mutableStateOf<Doctor?>(null) }
    
    val scrollState = rememberScrollState()

    // Handle doctor detail view or primary listing
    if (detailDoctor != null) {
        DoctorProfileDetailScreen(
            doctor = detailDoctor!!,
            viewModel = viewModel,
            onBack = { detailDoctor = null }
        )
        return
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Doctor Consultation",
                subtitle = "Assists Decisions",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search doctor specialties, center...", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Text("🔍", fontSize = 14.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SoftPink,
                    unfocusedBorderColor = DividerGray
                )
            )

            // Specialization Filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "OB/GYN", "Fetal Specialist", "Nutritionist").forEach { spec ->
                    val isSelected = selectedSpecFilter == spec
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) SoftPink else DividerGray.copy(alpha = 0.5f))
                            .clickable { selectedSpecFilter = spec }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = spec,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF2C2C2C) else SoftGray
                        )
                    }
                }
            }

            // Doctor List cards
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MockData.doctors.forEach { doc ->
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Avatar placeholder
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DividerGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🩺", fontSize = 24.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(doc.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                Text(doc.specialty, fontSize = 10.sp, color = SoftGray)
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text("⭐ " + doc.rating, fontSize = 9.sp, color = DarkPink, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("${doc.experienceYears} Years Exp", fontSize = 9.sp, color = MutedSage)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { detailDoctor = doc },
                                        colors = ButtonDefaults.buttonColors(containerColor = OffWhite, contentColor = DarkCharcoal),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Profile", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { 
                                            // Pre-select doctor and go to booking page
                                            viewModel.navigateTo("booking")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SoftPink, contentColor = Color(0xFF2C2C2C)),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Book Video checkup", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileDetailScreen(doctor: Doctor, viewModel: MainViewModel, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Doctor Profile",
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DividerGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👩‍⚕️", fontSize = 36.sp)
                }
                
                Column(verticalArrangement = Arrangement.Center) {
                    Text(doctor.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Text(doctor.specialty, fontSize = 13.sp, color = DarkPink, fontWeight = FontWeight.SemiBold)
                    Text("Sanctuary Maternal Center", fontSize = 10.sp, color = SoftGray)
                }
            }

            // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("${doctor.experienceYears} yrs", "Experience"),
                    Pair("⭐ ${doctor.rating}", "Rating"),
                    Pair("450+", "Deliveries")
                ).forEach { pair ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SnowWhite)
                            .border(0.8.dp, DividerGray, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(pair.first, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = DarkCharcoal)
                            Text(pair.second, fontSize = 9.sp, color = MutedSage, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }

            // Biography
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Biography", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                Text(
                    text = doctor.bio,
                    fontSize = 11.sp,
                    color = SoftGray,
                    lineHeight = 16.sp
                )
            }

            // Prescription History archives
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Prescription Archives", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                viewModel.prescriptions.forEach { presc ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(presc.medicineName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                Text(presc.date, fontSize = 9.sp, color = MutedSage)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(presc.directions, fontSize = 10.sp, color = SoftGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PillButton(
                text = "Book Video Appointment",
                onClick = { viewModel.navigateTo("booking") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
