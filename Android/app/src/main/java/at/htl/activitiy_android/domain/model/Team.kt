package at.htl.activitiy_android.domain.model

import androidx.compose.ui.graphics.Color

data class Team(
    val id: Long? = null,
    val position: Long = 0
) {
    // UI-Helper für Farben basierend auf Position
    val color: Color
        get() = when (position % 4) {
            0L -> Color(0xFFE53935) // Rot
            1L -> Color(0xFF1E88E5) // Blau
            2L -> Color(0xFF43A047) // Grün
            else -> Color(0xFFFDD835) // Gelb
        }

    val label: String
        get() = "Team ${position + 1}"
}