package at.htl.activitiy_android.view.gamegeneration

import at.htl.activitiy_android.domain.model.Game

data class GameGenerationState(
    val gameNameInput: String = "",
    val currentGame: Game? = null,
    val recentGames: List<Game> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val isLoading: Boolean = false
)