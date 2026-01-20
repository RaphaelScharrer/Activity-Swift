package at.htl.activitiy_android.feature.teamselect

sealed interface TeamSelectEvent {
    data class NameChanged(val value: String) : TeamSelectEvent
    data object AddPlayer : TeamSelectEvent
    data class RemovePlayer(val playerId: Long) : TeamSelectEvent
    data class RemovePlayerByName(val name: String) : TeamSelectEvent
    data class ChangeTeam(val playerId: Long, val teamId: Long) : TeamSelectEvent
    data object ClearMessages : TeamSelectEvent
    data object LoadData : TeamSelectEvent
    data class SelectTeam(val teamId: Long) : TeamSelectEvent
    data object SaveTeamsAndPlayers : TeamSelectEvent
    data class ChangeTeamByName(val playerName: String, val teamId: Long) : TeamSelectEvent
}