package at.htl.activitiy_android.feature.teamgeneration

import at.htl.activitiy_android.domain.model.Team

data class TeamGenerationState(
    val teamCountInput: String = "",
    val teams: List<Team> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val isLoading: Boolean = false,
    val hasChanges: Boolean = false
)

