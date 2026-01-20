package at.htl.activitiy_android.feature.teamselect

sealed interface TeamSelectEvent {
    data class NameChanged(val value: String) : TeamSelectEvent
    data object AddPlayer : TeamSelectEvent
    data class RemovePlayer(val playerId: String) : TeamSelectEvent
    data class CycleTeam(val playerId: String) : TeamSelectEvent
    data object AssignRandomTeams : TeamSelectEvent
    data object ClearError : TeamSelectEvent
}