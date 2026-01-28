package at.htl.activitiy_android.domain.model

import androidx.compose.ui.graphics.Color

data class Team(
    val id: Long? = null,
    val position: Int = 0,
    val gameId: Long? = null, //Fk to Game
    val playerIds: List<Long>? = null
) {
    // UI-Helper für Farben basierend auf Position
    val color: Color
        get() = when (position % 4) {
            0 -> Color(0xFFE53935) // Rot
            1 -> Color(0xFF1E88E5) // Blau
            2 -> Color(0xFF43A047) // Grün
            else -> Color(0xFFFDD835) // Gelb
        }

    val label: String
        get() = "Team ${position + 1}"
}