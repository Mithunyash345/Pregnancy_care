package com.bloom.pregnancycare.data

import java.util.UUID

data class Doctor(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val specialty: String,
    val rating: Float,
    val experienceYears: Int,
    val bio: String,
    val imageUrl: String,
    val availability: List<String>
)

data class Appointment(
    val id: String = UUID.randomUUID().toString(),
    val doctorName: String,
    val type: String,
    val dateTime: String,
    val isToday: Boolean = false,
    val status: String = "Confirmed"
)

data class Medication(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dosage: String,
    val time: String,
    val isTaken: Boolean = false
)

data class Report(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: String,
    val category: String,
    val summary: String,
    val values: List<ExtractedValue> = emptyList(),
    val aiInsights: String = ""
)

data class ExtractedValue(
    val markerName: String,
    val value: String,
    val status: String, // Normal, Low, High
    val normalRange: String
)

data class Prescription(
    val id: String = UUID.randomUUID().toString(),
    val doctorName: String,
    val date: String,
    val medicineName: String,
    val directions: String
)

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: String, // "user" | "ai"
    val timestamp: String
)

data class Milestone(
    val id: String = UUID.randomUUID().toString(),
    val week: Int,
    val title: String,
    val description: String,
    val status: String // Completed, Current, Upcoming
)

data class Exercise(
    val title: String,
    val duration: String,
    val difficulty: String,
    val benefits: String
)

object MockData {
    val doctors = listOf(
        Doctor(
            name = "Dr. Sarah Jenkins",
            specialty = "Obstetrician & Gynecologist",
            rating = 4.9f,
            experienceYears = 12,
            bio = "Expert in high-risk pregnancy management and personalized prenatal counseling. Dedicated to supporting mothers throughout their maternity journey.",
            imageUrl = "jenkins",
            availability = listOf("09:00 AM", "11:30 AM", "02:00 PM", "04:30 PM")
        ),
        Doctor(
            name = "Dr. Aris Thorne",
            specialty = "Fetal Medicine Specialist",
            rating = 4.8f,
            experienceYears = 15,
            bio = "Specialized in fetal imaging, prenatal diagnosis, and maternal-fetal medicine. Over 15 years of surgical and clinical expertise.",
            imageUrl = "thorne",
            availability = listOf("10:00 AM", "01:00 PM", "03:30 PM")
        ),
        Doctor(
            name = "Dr. Maya Lin",
            specialty = "Prenatal Nutritionist",
            rating = 4.7f,
            experienceYears = 8,
            bio = "Guiding expectant mothers through healthy gestational diets, addressing nausea, glucose tolerance, and dietary patterns.",
            imageUrl = "lin",
            availability = listOf("08:30 AM", "11:00 AM", "02:30 PM")
        )
    )

    val appointments = listOf(
        Appointment(
            doctorName = "Dr. Sarah Jenkins",
            type = "Obstetrics Consultation & Ultrasound Checkup",
            dateTime = "Today at 02:00 PM",
            isToday = true
        ),
        Appointment(
            doctorName = "Dr. Maya Lin",
            type = "Prenatal Nutrition Counseling Session",
            dateTime = "Jul 02, 2026 at 10:00 AM",
            isToday = false
        )
    )

    val medications = listOf(
        Medication(name = "Prenatal Multivitamin", dosage = "1 Capsule", time = "08:00 AM", isTaken = true),
        Medication(name = "Iron Supplement", dosage = "200 mg", time = "02:00 PM", isTaken = false),
        Medication(name = "DHA Omega-3", dosage = "1 Capsule", time = "08:00 PM", isTaken = false)
    )

    val reports = listOf(
        Report(
            title = "1st Trimester Genetic Screening",
            date = "Mar 12, 2026",
            category = "Ultrasound & Blood",
            summary = "All markers normal. Trisomy risk: Very Low.",
            values = listOf(
                ExtractedValue("Trisomy 21 Risk", "1:15000", "Normal", "Less than 1:250"),
                ExtractedValue("NT Thickness", "1.4 mm", "Normal", "Less than 2.5 mm")
            )
        ),
        Report(
            title = "Glucose Tolerance Panel",
            date = "May 20, 2026",
            category = "Blood Panel",
            summary = "Blood Glucose levels within limits. No signs of Gestational Diabetes.",
            values = listOf(
                ExtractedValue("Fasting Glucose", "92 mg/dL", "Normal", "Less than 95 mg/dL"),
                ExtractedValue("1-Hour Glucose", "135 mg/dL", "Normal", "Less than 140 mg/dL")
            )
        )
    )

    val prescriptions = listOf(
        Prescription(
            doctorName = "Dr. Sarah Jenkins",
            date = "Jun 15, 2026",
            medicineName = "Folic Acid 5mg",
            directions = "Take 1 tablet daily with milk in the morning"
        ),
        Prescription(
            doctorName = "Dr. Sarah Jenkins",
            date = "Jun 15, 2026",
            medicineName = "Iron Supplement 200mg",
            directions = "Take 1 capsule daily after lunch. Avoid taking with tea."
        )
    )

    val initialChatHistory = listOf(
        Message(text = "Hello Elena, I'm Bloom. How can I assist you with your pregnancy journey today?", sender = "ai", timestamp = "09:00 AM")
    )

    val milestones = listOf(
        Milestone(week = 6, title = "First Heartbeat Heard", description = "Baby's heart began beating at 110 bpm.", status = "Completed"),
        Milestone(week = 12, title = "First Trimester Scan", description = "Down syndrome screening and physical profile checks.", status = "Completed"),
        Milestone(week = 20, title = "Fetal Movement (Quickening)", description = "First soft kicks felt by mother.", status = "Completed"),
        Milestone(week = 24, title = "Sugar Tolerance Screening", description = "Blood test to screen for gestational diabetes.", status = "Current"),
        Milestone(week = 32, title = "Third Trimester Ultrasound", description = "Assessing growth velocity and amniotic fluid.", status = "Upcoming")
    )

    val exercises = listOf(
        Exercise("Prenatal Yoga Flow", "15 mins", "Easy", "Improves flexibility, pelvic floor strength and relieves stress."),
        Exercise("Pelvic Tilt Stretches", "10 mins", "Easy", "Relieves lower back pressure and stretches hip joints."),
        Exercise("Diaphragmatic Breathing", "8 mins", "Very Easy", "Deep relaxation technique to oxygenate blood and soothe anxiety.")
    )
}
