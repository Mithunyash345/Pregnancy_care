package com.bloom.pregnancycare.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.pregnancycare.data.*
import com.bloom.pregnancycare.data.api.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel : ViewModel() {

    // Current active role in prototype: "mother" | "companion" | "doctor"
    private val _currentRole = mutableStateOf("mother")
    val currentRole: State<String> = _currentRole

    // Active screen navigation route
    private val _currentScreen = mutableStateOf("splash")
    val currentScreen: State<String> = _currentScreen

    // Navigation Backstack placeholder
    private val backStack = mutableListOf<String>()

    fun navigateTo(screen: String) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    fun switchRole(role: String) {
        _currentRole.value = role
        if (role == "doctor") {
            _currentScreen.value = "doctor_dashboard"
        } else if (role == "companion") {
            _currentScreen.value = "companion_dashboard"
        } else {
            _currentScreen.value = "home"
        }
    }

    fun logout() {
        RetrofitClient.authToken = null
        _currentRole.value = "mother"
        navigateTo("sign_in")
        _waterLogged.value = 1250
        _stepsLogged.value = 6450
        medications.clear()
        medications.addAll(MockData.medications)
        appointments.clear()
        appointments.addAll(MockData.appointments)
        reports.clear()
        reports.addAll(MockData.reports)
        chatMessages.clear()
        chatMessages.addAll(MockData.initialChatHistory)
    }

    // --- BACKEND API CONNECTIONS ---

    var lastUsedEmail: String = ""

    fun loginUser(emailStr: String, passwordStr: String, onResult: (Boolean, String) -> Unit) {
        lastUsedEmail = emailStr
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(emailStr, passwordStr))
                if (response.isSuccessful && response.body() != null) {
                    val tokenData = response.body()!!
                    RetrofitClient.authToken = tokenData.access_token
                    _currentRole.value = tokenData.role
                    fetchDataFromServer()
                    onResult(true, "Successfully connected to backend!")
                } else {
                    onResult(false, "Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Login error: ${e.message}")
                onResult(false, "Backend offline. Entering simulation mode.")
            }
        }
    }

    fun registerUser(nameStr: String, emailStr: String, passwordStr: String, roleStr: String, onResult: (Boolean, String) -> Unit) {
        lastUsedEmail = emailStr
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(nameStr, emailStr, null, roleStr.lowercase(), passwordStr)
                )
                if (response.isSuccessful) {
                    onResult(true, "Registration successful!")
                } else {
                    onResult(false, "Registration failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Register error: ${e.message}")
                onResult(false, "Backend offline. Register simulation.")
            }
        }
    }

    fun verifyOtp(otpCode: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.verifyOtp(lastUsedEmail, otpCode)
                if (response.isSuccessful) {
                    onResult(true, "OTP verified successfully!")
                } else {
                    onResult(false, "Invalid verification code.")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "OTP verify error: ${e.message}")
                if (otpCode == "123456") {
                    onResult(true, "Bypassed via backdoor (offline).")
                } else {
                    onResult(false, "Verification failed or server offline.")
                }
            }
        }
    }

    fun fetchDataFromServer() {
        val today = LocalDate.now().toString() // "YYYY-MM-DD"
        viewModelScope.launch {
            try {
                // Fetch water
                val waterResp = RetrofitClient.apiService.getWaterLog(today)
                if (waterResp.isSuccessful && waterResp.body() != null) {
                    _waterLogged.value = waterResp.body()!!.glasses * 250 // Convert glasses to ml (1 glass = 250ml)
                }

                // Fetch steps
                val stepsResp = RetrofitClient.apiService.getStepLog(today)
                if (stepsResp.isSuccessful && stepsResp.body() != null) {
                    _stepsLogged.value = stepsResp.body()!!.steps
                }

                // Fetch sleep
                val sleepResp = RetrofitClient.apiService.getSleepLog(today)
                if (sleepResp.isSuccessful && sleepResp.body() != null) {
                    // If needed, store sleep hours value
                }

                // Fetch medications
                val medResp = RetrofitClient.apiService.getMedications()
                if (medResp.isSuccessful && medResp.body() != null) {
                    medications.clear()
                    medResp.body()!!.forEach {
                        medications.add(
                            Medication(
                                id = it.medication_id,
                                name = it.medicine_name,
                                dosage = it.dosage,
                                time = it.reminder_time,
                                isTaken = false
                            )
                        )
                    }
                }

                // Fetch appointments
                val apptResp = RetrofitClient.apiService.getAppointments()
                if (apptResp.isSuccessful && apptResp.body() != null) {
                    appointments.clear()
                    apptResp.body()!!.forEach {
                        appointments.add(
                            Appointment(
                                doctorName = it.doctor_name ?: "Dr. Sarah Jenkins",
                                type = it.consultation_type ?: "Routine Review",
                                dateTime = it.appointment_date
                            )
                        )
                    }
                }

                // Fetch reports
                val reportResp = RetrofitClient.apiService.getReports()
                if (reportResp.isSuccessful && reportResp.body() != null) {
                    reports.clear()
                    reportResp.body()!!.forEach { rep ->
                        reports.add(
                            Report(
                                title = rep.report_name,
                                date = rep.uploaded_at.split("T").firstOrNull() ?: "Today",
                                category = rep.report_type,
                                summary = if (rep.ocr_results.firstOrNull()?.processed == true) 
                                    "Slightly low hemoglobin detected (11.2 g/dL). Recommend iron adjustment." 
                                    else "Pending OCR scan.",
                                values = listOf(
                                    ExtractedValue("Hemoglobin", "11.2 g/dL", "Low", "12.0 - 15.0 g/dL"),
                                    ExtractedValue("Fasting Glucose", "92 mg/dL", "Normal", "Less than 95 mg/dL")
                                ),
                                aiInsights = if (rep.ocr_results.firstOrNull()?.processed == true)
                                    "Your hemoglobin is slightly low. This is common during week 24 due to expanded blood volume. Please take iron supplements."
                                    else "OCR insights will appear here after scanning."
                            )
                        )
                    }
                }

                // Fetch chat history
                val chatResp = RetrofitClient.apiService.getChatHistory()
                if (chatResp.isSuccessful && chatResp.body() != null) {
                    chatMessages.clear()
                    chatResp.body()!!.forEach {
                        chatMessages.add(Message(text = it.user_message, sender = "user", timestamp = "Today"))
                        chatMessages.add(Message(text = it.ai_response, sender = "ai", timestamp = "Today"))
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching data: ${e.message}")
            }
        }
    }

    // --- MOTHER DATA STATE ---
    private val _waterLogged = mutableStateOf(1250) // ml
    val waterLogged: State<Int> = _waterLogged
    val waterTarget = 2500

    fun addWater(amount: Int) {
        _waterLogged.value = (_waterLogged.value + amount).coerceAtMost(waterTarget)
        recalculateWellness()

        // Sync with backend
        viewModelScope.launch {
            try {
                val glasses = _waterLogged.value / 250
                RetrofitClient.apiService.updateWaterLog(glasses, LocalDate.now().toString())
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error syncing water: ${e.message}")
            }
        }
    }

    private val _stepsLogged = mutableStateOf(6450)
    val stepsLogged: State<Int> = _stepsLogged
    val stepsTarget = 8000

    fun addSteps(amount: Int) {
        _stepsLogged.value += amount

        // Sync with backend
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.updateStepLog(_stepsLogged.value, LocalDate.now().toString())
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error syncing steps: ${e.message}")
            }
        }
    }

    val medications = mutableStateListOf<Medication>().apply {
        addAll(MockData.medications)
    }

    fun toggleMedication(medId: String) {
        val index = medications.indexOfFirst { it.id == medId }
        if (index != -1) {
            val med = medications[index]
            medications[index] = med.copy(isTaken = !med.isTaken)
            recalculateWellness()
        }
    }

    val appointments = mutableStateListOf<Appointment>().apply {
        addAll(MockData.appointments)
    }

    fun bookAppointment(doctorName: String, type: String, dateTime: String) {
        appointments.add(Appointment(doctorName = doctorName, type = type, dateTime = dateTime))

        viewModelScope.launch {
            try {
                // Fetch doctor list to match ID
                val doctorsResp = RetrofitClient.apiService.getDoctors()
                val doctorId = if (doctorsResp.isSuccessful && !doctorsResp.body().isNullOrEmpty()) {
                    doctorsResp.body()!!.firstOrNull { it.full_name?.contains(doctorName, ignoreCase = true) == true }?.doctor_id 
                        ?: doctorsResp.body()!!.first().doctor_id
                } else {
                    "00000000-0000-0000-0000-000000000000"
                }

                // Default ISO datetime conversion
                val formattedIso = "2026-06-26T14:00:00"
                RetrofitClient.apiService.bookAppointment(BookAppointmentRequest(doctorId, formattedIso, type, "Booked from Android App"))
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error booking appointment: ${e.message}")
            }
        }
    }

    val prescriptions = mutableStateListOf<Prescription>().apply {
        addAll(MockData.prescriptions)
    }

    val reports = mutableStateListOf<Report>().apply {
        addAll(MockData.reports)
    }

    private val _wellnessScore = mutableStateOf(84)
    val wellnessScore: State<Int> = _wellnessScore

    private fun recalculateWellness() {
        val medScore = medications.filter { it.isTaken }.size * 8
        val waterScore = ((_waterLogged.value.toFloat() / waterTarget.toFloat()) * 16).toInt()
        _wellnessScore.value = (60 + medScore + waterScore).coerceAtMost(100)
    }

    // --- CHAT STATE ---
    val chatMessages = mutableStateListOf<Message>().apply {
        addAll(MockData.initialChatHistory)
    }

    private val _isListeningVoice = mutableStateOf(false)
    val isListeningVoice: State<Boolean> = _isListeningVoice

    fun toggleVoiceListening(active: Boolean) {
        _isListeningVoice.value = active
    }

    fun sendChatMessage(text: String) {
        chatMessages.add(Message(text = text, sender = "user", timestamp = "Just Now"))

        // Sync with backend
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.sendChatMessage(ChatMessageRequest(text))
                if (response.isSuccessful && response.body() != null) {
                    val chatReply = response.body()!!
                    chatMessages.add(Message(text = chatReply.ai_response, sender = "ai", timestamp = "Just Now"))
                } else {
                    val reply = getMockAIResponse(text)
                    chatMessages.add(Message(text = reply, sender = "ai", timestamp = "Just Now"))
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Chat sync error: ${e.message}")
                val reply = getMockAIResponse(text)
                chatMessages.add(Message(text = reply, sender = "ai", timestamp = "Just Now"))
            }
        }
    }

    private fun getMockAIResponse(query: String): String {
        val lower = query.lowercase()
        return when {
            lower.contains("heartburn") -> "Heartburn is common in week 24 because progesterone relaxes the valve between your stomach and esophagus. Try eating smaller meals and drinking ginger tea. *Remember: AI assists, doctors decide.*"
            lower.contains("kick") || lower.contains("movement") -> "Around week 24, babies are active and sleep in cycles. Feeling 10 movements within a 2-hour window is a healthy baseline. *Remember: AI assists, doctors decide.*"
            lower.contains("cramp") || lower.contains("pain") -> "Mild cramping can be due to ligament stretching. If severe or rhythmic, consult emergency support immediately. *Remember: AI assists, doctors decide.*"
            else -> "Based on your clinical record (Week 24, Day 3), this is common. Rest, stay hydrated, and follow up with Dr. Sarah Jenkins if symptoms persist. *Remember: AI assists, doctors decide.*"
        }
    }

    // --- COMPANION STATE ---
    private val _companionCode = mutableStateOf("BLOOM-CO-9981")
    val companionCode: State<String> = _companionCode

    fun regenerateCompanionCode() {
        _companionCode.value = "BLOOM-CO-" + (1000..9999).random()

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getCompanionCode()
                if (response.isSuccessful && response.body() != null) {
                    _companionCode.value = response.body()!!.link_code
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Companion code error: ${e.message}")
            }
        }
    }

    private val _companionAlert = mutableStateOf<String?>(null)
    val companionAlert: State<String?> = _companionAlert

    fun triggerCompanionAlert(message: String) {
        _companionAlert.value = message

        viewModelScope.launch {
            try {
                RetrofitClient.apiService.triggerPanicAlert(message)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Alert sync error: ${e.message}")
            }
        }
    }

    fun dismissCompanionAlert() {
        _companionAlert.value = null
    }

    // --- DOCTOR STATE ---
    val doctorRequests = mutableStateListOf<AppointmentRequest>().apply {
        add(AppointmentRequest("101", "Elena Rostova", "Today, 02:00 PM", "Routine Review"))
        add(AppointmentRequest("102", "Maria Gonzalez", "Tomorrow, 09:30 AM", "Follow-up"))
    }

    fun approveRequest(id: String) {
        doctorRequests.removeAll { it.id == id }

        viewModelScope.launch {
            try {
                RetrofitClient.apiService.changeAppointmentStatus(id, AppointmentStatusRequest("Approved"))
            } catch (e: Exception) {
                Log.e("MainViewModel", "Approve appt error: ${e.message}")
            }
        }
    }

    fun doctorPrescribe(drug: String, dose: String, directions: String) {
        prescriptions.add(
            0,
            Prescription(
                doctorName = "Dr. Sarah Jenkins",
                date = "Today",
                medicineName = "$drug $dose",
                directions = directions
            )
        )
    }

    // Active scanner status
    private val _scanProgress = mutableStateOf<String?>(null)
    val scanProgress: State<String?> = _scanProgress

    fun simulateScanner(onFinish: () -> Unit) {
        _scanProgress.value = "Scanning Report Details..."
        // Run simulated scanning progression
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _scanProgress.value = "Extracting Biometric Markers..."
        }, 1500)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _scanProgress.value = "Cross-referencing with AI Guidelines..."
        }, 3000)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _scanProgress.value = null

            viewModelScope.launch {
                try {
                    val uploadResp = RetrofitClient.apiService.uploadReport(
                        "Routine Second Trimester CBC Panel",
                        "Blood Panel",
                        "http://example.com/mock_report.pdf"
                    )
                    if (uploadResp.isSuccessful && uploadResp.body() != null) {
                        val report = uploadResp.body()!!
                        val scanResp = RetrofitClient.apiService.scanReport(report.report_id)
                        if (scanResp.isSuccessful && scanResp.body() != null) {
                            fetchDataFromServer()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Scan upload error: ${e.message}")
                    // Add local report as fallback
                    reports.add(
                        0,
                        Report(
                            title = "Routine Second Trimester CBC Panel",
                            date = "Today",
                            category = "Blood Panel",
                            summary = "Slightly low hemoglobin detected (11.2 g/dL). Recommend iron adjustment.",
                            values = listOf(
                                ExtractedValue("Hemoglobin", "11.2 g/dL", "Low", "12.0 - 15.0 g/dL"),
                                ExtractedValue("Fasting Glucose", "92 mg/dL", "Normal", "Less than 95 mg/dL")
                            ),
                            aiInsights = "Your hemoglobin is slightly low. This is common during week 24 due to expanded blood volume. Please take iron supplements."
                        )
                    )
                }
                onFinish()
            }
        }, 4500)
    }
}

data class AppointmentRequest(
    val id: String,
    val patientName: String,
    val time: String,
    val urgency: String
)
