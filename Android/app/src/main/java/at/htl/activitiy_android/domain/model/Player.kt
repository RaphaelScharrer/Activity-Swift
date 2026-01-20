package at.htl.activitiy_android.domain.model

import java.util.UUID

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val team: Team
)