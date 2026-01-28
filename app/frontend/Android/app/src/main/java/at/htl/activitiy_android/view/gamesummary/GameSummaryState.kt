package at.htl.activitiy_android.view.gamesummary

import at.htl.activitiy_android.domain.model.Player
import at.htl.activitiy_android.domain.model.Team

data class GameSummaryState(
    val gameName: String = "",
    val teams: List<Team> = emptyList(),
    val allPlayers: List<Player> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
