package at.htl.activitiy_android.feature.teamselect

import at.htl.activitiy_android.domain.model.PlayerWithTeam
import at.htl.activitiy_android.domain.model.Team

data class TeamSelectState(
    val nameInput: String = "",
    val players: List<PlayerWithTeam> = emptyList(),
    val teams: List<Team> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false,
    val selectedTeamId: Long? = null
)