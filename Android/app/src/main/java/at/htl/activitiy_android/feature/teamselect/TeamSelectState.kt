package at.htl.activitiy_android.feature.teamselect

import at.htl.activitiy_android.domain.model.Player
import at.htl.activitiy_android.domain.model.Team

data class TeamSelectState(
    val nameInput: String = "",
    val players: List<Player> = emptyList(),
    val error: String? = null,
    val defaultTeam: Team = Team.RED
)