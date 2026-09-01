package com.bloom.pregnancycare.ui.navigation

sealed class Screen(val route: String) {
    // Core App lifecycle
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")

    // Authentication Flow
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object OtpVerification : Screen("otp_verification")

    // Main bottom navigation tabs for Mother
    object Home : Screen("home")
    object Assistant : Screen("assistant")
    object Reports : Screen("reports")
    object Doctors : Screen("doctors")
    object Profile : Screen("profile")

    // Sub-screens & details
    object ReportScanner : Screen("report_scanner")
    object ReportDetail : Screen("report_detail")
    object DoctorProfile : Screen("doctor_profile")
    object Booking : Screen("booking")
    object WaitingRoom : Screen("waiting_room")
    object WellnessDashboard : Screen("wellness_dashboard")
    object EmergencySupport : Screen("emergency_support")
    object NotificationsCenter : Screen("notifications_center")

    // Alternate role portals
    object CompanionDashboard : Screen("companion_dashboard")
    object InviteCompanion : Screen("invite_companion")
    object DoctorDashboard : Screen("doctor_dashboard")
}
