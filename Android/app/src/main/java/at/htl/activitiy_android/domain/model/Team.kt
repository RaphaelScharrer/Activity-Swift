package at.htl.activitiy_android.domain.model

import androidx.compose.ui.graphics.Color

enum class Team(val label: String, val color: Color) {
    RED("Rot", Color(0xFFE53935)),
    BLUE("Blau", Color(0xFF1E88E5)),
    GREEN("Grün", Color(0xFF43A047)),
    YELLOW("Gelb", Color(0xFFFDD835));

    fun next(): Team {
        val all = entries
        val idx = all.indexOf(this)
        return all[(idx + 1) % all.size]
    }
}