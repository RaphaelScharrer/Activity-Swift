package at.htl.activitiy_android.view.gamegeneration

sealed interface GameGenerationEvent {
    data class GameNameChanged(val value: String) : GameGenerationEvent
    data class CreateGame(val onSuccess: (Long) -> Unit) : GameGenerationEvent
    data class SelectExistingGame(val game: at.htl.activitiy_android.domain.model.Game, val onSuccess: (Long) -> Unit) : GameGenerationEvent
    data object LoadRecentGames : GameGenerationEvent
    data object ClearMessages : GameGenerationEvent
}