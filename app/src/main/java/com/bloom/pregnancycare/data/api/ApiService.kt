package com.bloom.pregnancycare.data.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTHENTICATION ---
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Query("email") email: String,
        @Query("otp") otp: String
    ): Response<SimpleStatusResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Query("email") email: String
    ): Response<SimpleStatusResponse>

    // --- PROFILE & SETTINGS ---
    @GET("users/me")
    suspend fun getMyProfile(): Response<UserResponse>

    @GET("users/settings")
    suspend fun getSettings(): Response<SettingsResponse>

    @PUT("users/settings")
    suspend fun updateSettings(@Body request: SettingsRequest): Response<SettingsResponse>

    @GET("users/profile")
    suspend fun getPregnancyProfile(): Response<PregnancyProfileResponse>

    @PUT("users/profile")
    suspend fun updatePregnancyProfile(@Body request: PregnancyProfileRequest): Response<PregnancyProfileResponse>

    // --- WELLNESS ---
    @GET("wellness/water")
    suspend fun getWaterLog(@Query("log_date") date: String): Response<WaterResponse>

    @POST("wellness/water")
    suspend fun updateWaterLog(
        @Query("glasses") glasses: Int,
        @Query("log_date") date: String
    ): Response<WaterResponse>

    @GET("wellness/steps")
    suspend fun getStepLog(@Query("log_date") date: String): Response<StepsResponse>

    @POST("wellness/steps")
    suspend fun updateStepLog(
        @Query("steps") steps: Int,
        @Query("log_date") date: String
    ): Response<StepsResponse>

    @GET("wellness/sleep")
    suspend fun getSleepLog(@Query("log_date") date: String): Response<SleepResponse>

    @POST("wellness/sleep")
    suspend fun updateSleepLog(
        @Query("sleep_hours") hours: Double,
        @Query("log_date") date: String
    ): Response<SleepResponse>

    @GET("wellness/metrics")
    suspend fun getHealthMetrics(): Response<List<HealthMetricResponse>>

    @POST("wellness/metrics")
    suspend fun logHealthMetric(@Body request: HealthMetricRequest): Response<HealthMetricResponse>

    // --- DOCTORS & APPOINTMENTS ---
    @GET("doctors")
    suspend fun getDoctors(): Response<List<DoctorResponse>>

    @POST("doctors/profile")
    suspend fun updateDoctorProfile(@Body request: DoctorProfileRequest): Response<DoctorResponse>

    @GET("doctors/appointments")
    suspend fun getAppointments(): Response<List<AppointmentResponse>>

    @POST("doctors/appointments")
    suspend fun bookAppointment(@Body request: BookAppointmentRequest): Response<AppointmentResponse>

    @PUT("doctors/appointments/{id}/status")
    suspend fun changeAppointmentStatus(
        @Path("id") appointmentId: String,
        @Body request: AppointmentStatusRequest
    ): Response<AppointmentResponse>

    // --- COMPANION LINKAGE ---
    @GET("companions/code")
    suspend fun getCompanionCode(): Response<CompanionCodeResponse>

    @POST("companions/link")
    suspend fun linkCompanion(@Body request: LinkCompanionRequest): Response<CompanionLinkResponse>

    @GET("companions/linked")
    suspend fun getLinkedAccounts(): Response<List<CompanionLinkResponse>>

    @POST("companions/alert")
    suspend fun triggerPanicAlert(@Query("message") message: String): Response<SimpleStatusResponse>

    // --- MEDICATIONS ---
    @GET("medications")
    suspend fun getMedications(): Response<List<MedicationResponse>>

    @POST("medications")
    suspend fun addMedication(@Body request: MedicationRequest): Response<MedicationResponse>

    @DELETE("medications/{id}")
    suspend fun deleteMedication(@Path("id") medicationId: String): Response<SimpleStatusResponse>

    // --- NOTIFICATIONS ---
    @GET("notifications")
    suspend fun getNotifications(): Response<List<NotificationResponse>>

    @PUT("notifications/{id}/read")
    suspend fun readNotification(@Path("id") id: String): Response<NotificationResponse>

    @GET("notifications/milestones")
    suspend fun getMilestones(): Response<List<MilestoneResponse>>

    // --- EMERGENCY CONTACTS ---
    @GET("emergency/contacts")
    suspend fun getEmergencyContacts(): Response<List<EmergencyContactResponse>>

    @POST("emergency/contacts")
    suspend fun addEmergencyContact(@Body request: EmergencyContactRequest): Response<EmergencyContactResponse>

    @DELETE("emergency/contacts/{id}")
    suspend fun deleteEmergencyContact(@Path("id") id: String): Response<SimpleStatusResponse>

    // --- AI CHAT ASSISTANT ---
    @GET("chat/history")
    suspend fun getChatHistory(): Response<List<ChatHistoryResponse>>

    @POST("chat/message")
    suspend fun sendChatMessage(@Body request: ChatMessageRequest): Response<ChatHistoryResponse>

    // --- REPORTS ---
    @GET("reports")
    suspend fun getReports(): Response<List<ReportResponse>>

    @POST("reports/upload")
    suspend fun uploadReport(
        @Query("report_name") name: String,
        @Query("report_type") type: String,
        @Query("file_url") url: String
    ): Response<ReportResponse>

    @POST("reports/{id}/scan")
    suspend fun scanReport(@Path("id") reportId: String): Response<OCRResponse>
}

// --- DATA TRANSFER OBJECTS (DTOs) ---

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val phone: String?,
    val role: String,
    val password: String = "", // Will be assigned via constructor or setter
    val profile_image: String? = null
) {
    // Helper constructor to avoid variable naming clash
    constructor(full_name: String, email: String, phone: String?, role: String, password_raw: String) : 
        this(full_name, email, phone, role, password_raw, null)
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val role: String,
    val user_id: String,
    val full_name: String
)

data class UserResponse(
    val user_id: String,
    val full_name: String,
    val email: String,
    val phone: String?,
    val role: String,
    val profile_image: String?,
    val is_verified: Boolean
)

data class SimpleStatusResponse(
    val status: String,
    val message: String
)

data class SettingsRequest(
    val language: String,
    val notifications_enabled: Boolean,
    val dark_mode: Boolean
)

data class SettingsResponse(
    val setting_id: String,
    val user_id: String,
    val language: String,
    val notifications_enabled: Boolean,
    val dark_mode: Boolean
)

data class PregnancyProfileRequest(
    val due_date: String, // "YYYY-MM-DD"
    val pregnancy_week: Int,
    val trimester: Int,
    val blood_group: String?,
    val height_cm: Double?,
    val pre_pregnancy_weight: Double?,
    val current_weight: Double?,
    val medical_conditions: String?,
    val allergies: String?
)

data class PregnancyProfileResponse(
    val profile_id: String,
    val user_id: String,
    val due_date: String,
    val pregnancy_week: Int,
    val trimester: Int,
    val blood_group: String?,
    val height_cm: Double?,
    val pre_pregnancy_weight: Double?,
    val current_weight: Double?,
    val medical_conditions: String?,
    val allergies: String?,
    val created_at: String
)

data class WaterResponse(
    val water_id: String,
    val mother_id: String,
    val intake_date: String,
    val glasses: Int
)

data class StepsResponse(
    val step_id: String,
    val mother_id: String,
    val tracking_date: String,
    val steps: Int
)

data class SleepResponse(
    val sleep_id: String,
    val mother_id: String,
    val tracking_date: String,
    val sleep_hours: Double
)

data class HealthMetricRequest(
    val weight: Double?,
    val blood_pressure: String?,
    val blood_sugar: Double?,
    val heart_rate: Int?
)

data class HealthMetricResponse(
    val metric_id: String,
    val mother_id: String,
    val weight: Double?,
    val blood_pressure: String?,
    val blood_sugar: Double?,
    val heart_rate: Int?,
    val recorded_at: String
)

data class DoctorResponse(
    val doctor_id: String,
    val user_id: String,
    val specialization: String?,
    val hospital_name: String?,
    val experience_years: Int?,
    val consultation_fee: Double?,
    val about: String?,
    val available: Boolean,
    val full_name: String?
)

data class DoctorProfileRequest(
    val specialization: String?,
    val hospital_name: String?,
    val experience_years: Int?,
    val consultation_fee: Double?,
    val about: String?,
    val available: Boolean
)

data class BookAppointmentRequest(
    val doctor_id: String,
    val appointment_date: String, // "YYYY-MM-DDTHH:MM:SS"
    val consultation_type: String,
    val notes: String?
)

data class AppointmentStatusRequest(
    val status: String // "Pending", "Approved", "Completed", "Cancelled"
)

data class AppointmentResponse(
    val appointment_id: String,
    val mother_id: String,
    val doctor_id: String,
    val doctor_name: String?,
    val patient_name: String?,
    val appointment_date: String,
    val consultation_type: String?,
    val status: String,
    val notes: String?,
    val created_at: String
)

data class CompanionCodeResponse(
    val link_code: String,
    val mother_email: String
)

data class LinkCompanionRequest(
    val link_code: String,
    val relationship: String? = "Companion"
)

data class CompanionLinkResponse(
    val companion_id: String,
    val mother_id: String,
    val companion_user_id: String,
    val companion_name: String?,
    val relationship: String?,
    val created_at: String
)

data class MedicationRequest(
    val medicine_name: String,
    val dosage: String,
    val reminder_time: String, // "HH:MM:SS"
    val start_date: String, // "YYYY-MM-DD"
    val end_date: String // "YYYY-MM-DD"
)

data class MedicationResponse(
    val medication_id: String,
    val mother_id: String,
    val medicine_name: String,
    val dosage: String,
    val reminder_time: String,
    val start_date: String,
    val end_date: String
)

data class NotificationResponse(
    val notification_id: String,
    val user_id: String,
    val title: String,
    val message: String,
    val notification_type: String,
    val is_read: Boolean,
    val created_at: String
)

data class MilestoneResponse(
    val milestone_id: String,
    val week_number: Int,
    val title: String,
    val description: String
)

data class EmergencyContactRequest(
    val name: String,
    val relationship: String,
    val phone: String
)

data class EmergencyContactResponse(
    val contact_id: String,
    val mother_id: String,
    val name: String,
    val relationship: String,
    val phone: String,
    val created_at: String
)

data class ChatMessageRequest(
    val user_message: String
)

data class ChatHistoryResponse(
    val chat_id: String,
    val mother_id: String,
    val user_message: String,
    val ai_response: String,
    val created_at: String
)

data class OCRResponse(
    val ocr_id: String,
    val extracted_text: String?,
    val processed: Boolean,
    val created_at: String
)

data class ReportResponse(
    val report_id: String,
    val mother_id: String,
    val report_name: String,
    val report_type: String,
    val file_url: String?,
    val uploaded_at: String,
    val ocr_results: List<OCRResponse> = emptyList()
)
