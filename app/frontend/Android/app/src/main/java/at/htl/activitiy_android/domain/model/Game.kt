package at.htl.activitiy_android.domain.model

data class Game (
    val id: Long? = null,
    val name: String? = null,
    val teams: List<Team>? = null
)