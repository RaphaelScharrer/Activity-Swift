package at.htl.activitiy_android.feature.teamselect

sealed interface TeamSelectEvent {
    data class NameChanged(val value: String) : TeamSelectEvent
    data object AddPlayer : TeamSelectEvent
    data class RemovePlayer(val playerId: Long) : TeamSelectEvent
    data class ChangeTeam(val playerId: Long, val teamId: Long) : TeamSelectEvent
    data object AssignRandomTeams : TeamSelectEvent
    data object ClearError : TeamSelectEvent
    data object LoadData : TeamSelectEvent
}