package at.htl.activitiy_android.domain.model

import java.util.UUID

data class Player(
    val id: Long? = null,
    val team: Long? = null,  // FK to Team
    val name: String,
    val pointsEarned: Long = 0
)