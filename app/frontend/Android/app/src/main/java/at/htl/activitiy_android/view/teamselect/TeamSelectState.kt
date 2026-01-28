package at.htl.activitiy_android.view.teamselect

import at.htl.activitiy_android.domain.model.PlayerWithTeam
import at.htl.activitiy_android.domain.model.Team

data class TeamSelectState(
    val nameInput: String = "",
    val players: List<PlayerWithTeam> = emptyList(),
    val teams: List<Team> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val messageType: MessageType = MessageType.INFO,
    val isLoading: Boolean = false,
    val selectedTeamId: Long? = null,
    val hasChanges: Boolean = false
)