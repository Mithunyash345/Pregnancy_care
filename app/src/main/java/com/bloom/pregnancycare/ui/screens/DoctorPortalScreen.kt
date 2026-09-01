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
import com.bloom.pregnancycare.ui.components.PillButton
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    var activePatient by remember { mutableStateOf<String?>("Elena Rostova") }
    
    // Prescription Input state
    var drugName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var prescriptionSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Doctor Portal (Dr. Jenkins)",
                subtitle = "Active Consultations",
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SereneLightBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🩺", fontSize = 14.sp)
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
            // Patient Consultation requests queue
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Appointment Requests Queue", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                
                if (viewModel.doctorRequests.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No pending requests. Queue cleared.", fontSize = 11.sp, color = MutedSage)
                        }
                    }
                }

                viewModel.doctorRequests.forEach { req ->
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
                                Column {
                                    Text(req.patientName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                    Text(req.time, fontSize = 9.sp, color = SoftGray)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CriticalRose.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(req.urgency, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = CriticalRose)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PillButton(
                                    text = "Accept Call",
                                    onClick = { 
                                        activePatient = req.patientName
                                        viewModel.approveRequest(req.id)
                                    },
                                    containerColor = SereneLightBlue,
                                    modifier = Modifier.weight(1f).height(34.dp)
                                )
                                PillButton(
                                    text = "Decline",
                                    onClick = { viewModel.approveRequest(req.id) },
                                    containerColor = OffWhite,
                                    modifier = Modifier.weight(1f).height(34.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Consultation Details panel for Active patient
            activePatient?.let { patient ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Current Consultation Details: $patient", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SnowWhite),
                        border = BoxDefaults.borderStroke(DividerGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Patient info snapshot
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SoftPink.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👩", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(patient, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                                    Text("Week 24 • Due: Oct 12, 2026", fontSize = 9.sp, color = SoftGray)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Scan / Lab values review
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = OffWhite),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("PATIENT LAB METRIC ALERTS (OCR)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CriticalRose)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Hemoglobin", fontSize = 11.sp, color = DarkCharcoal)
                                        Text("11.2 g/dL (LOW)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CriticalRose)
                                    }
                                    Text("Normal reference: 12.0 - 15.0 g/dL", fontSize = 9.sp, color = MutedSage)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Prescription Form
                            Text("Issue Digital Prescription & Chart Sync", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = drugName,
                                onValueChange = { drugName = it },
                                placeholder = { Text("Drug Name (e.g., Calcium Citrate)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftPink)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = dosage,
                                onValueChange = { dosage = it },
                                placeholder = { Text("Dosage (e.g., 500mg)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftPink)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = instructions,
                                onValueChange = { instructions = it },
                                placeholder = { Text("Instructions (e.g., Take twice daily with breakfast)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftPink)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (prescriptionSuccess) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SoftMint)
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Prescription synced to Patient Chart!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF319795))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            PillButton(
                                text = "Upload & Sync Patient Chart",
                                onClick = {
                                    if (drugName.isNotEmpty() && dosage.isNotEmpty()) {
                                        viewModel.doctorPrescribe(drugName, dosage, instructions)
                                        prescriptionSuccess = true
                                        drugName = ""
                                        dosage = ""
                                        instructions = ""
                                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                            prescriptionSuccess = false
                                        }, 3000)
                                    }
                                },
                                containerColor = SereneLightBlue,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = drugName.isNotEmpty() && dosage.isNotEmpty()
                            )
                        }
                    }
                }
            }
        }
    }
}
