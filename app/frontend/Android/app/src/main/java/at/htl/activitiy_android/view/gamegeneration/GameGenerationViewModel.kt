package at.htl.activitiy_android.view.gamegeneration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl.activitiy_android.data.api.RetrofitInstance
import at.htl.activitiy_android.domain.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameGenerationViewModel : ViewModel() {

    private val api = RetrofitInstance.api

    private val _state = MutableStateFlow(GameGenerationState())
    val state: StateFlow<GameGenerationState> = _state

    init {
        loadRecentGames()
    }

    fun onEvent(event: GameGenerationEvent) {
        when (event) {
            is GameGenerationEvent.GameNameChanged -> _state.update {
                it.copy(gameNameInput = event.value, error = null)
            }

            is GameGenerationEvent.CreateGame -> createGame(event.onSuccess)

            is GameGenerationEvent.SelectExistingGame -> selectExistingGame(event.game, event.onSuccess)

            GameGenerationEvent.LoadRecentGames -> loadRecentGames()

            GameGenerationEvent.ClearMessages -> _state.update {
                it.copy(error = null, successMessage = null)
            }
        }
    }

    private fun loadRecentGames() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val games = api.getAllGames()
                _state.update {
                    it.copy(
                        recentGames = games.take(5), // Nur die letzten 5 Spiele
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Fehler beim Laden der Spiele: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun createGame(onSuccess: (Long) -> Unit) {
        val gameName = _state.value.gameNameInput.trim()

        if (gameName.isEmpty()) {
            _state.update {
                it.copy(error = "Bitte einen Spielnamen eingeben")
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val newGame = Game(
                    id = null,
                    name = gameName,
                    createdOn = null,  // ← Wird vom Backend gesetzt
                    teamIds = null
                )

                val createdGame = api.createGame(newGame)

                _state.update {
                    it.copy(
                        currentGame = createdGame,
                        isLoading = false,
                        successMessage = "Spiel '${createdGame.name}' erstellt!",
                        gameNameInput = ""
                    )
                }

                // Navigate zur Team-Erstellung mit Game-ID
                createdGame.id?.let { gameId ->
                    onSuccess(gameId)
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Fehler beim Erstellen: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun selectExistingGame(game: Game, onSuccess: (Long) -> Unit) {
        _state.update {
            it.copy(
                currentGame = game,
                successMessage = "Spiel '${game.name}' ausgewählt"
            )
        }

        game.id?.let { gameId ->
            onSuccess(gameId)
        }
    }
}