package at.htl.activitiy_android.feature.teamgeneration

sealed interface TeamGenerationEvent {
    data class TeamCountChanged(val value: String) : TeamGenerationEvent
    data object GenerateTeams : TeamGenerationEvent
    data class SaveTeams(val onSuccess: () -> Unit) : TeamGenerationEvent
    data object ClearMessages : TeamGenerationEvent
}