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

    fun onEvent(event: GameGenerationEvent) {
        when (event) {
            is GameGenerationEvent.GameNameChanged -> _state.update {
                it.copy(gameNameInput = event.value, error = null)
            }

            is GameGenerationEvent.CreateGame -> createGame(event.onSuccess)

            GameGenerationEvent.LoadRecentGames -> loadRecentGames()

            GameGenerationEvent.ClearMessages -> _state.update {
                it.copy(error = null, successMessage = null)
            }

            is GameGenerationEvent.LoadGame -> loadGame(event.gameId)

            is GameGenerationEvent.UpdateGame -> updateGame(event.gameId, event.onSuccess)
        }
    }

    private fun loadRecentGames() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val games = api.getAllGames()
                _state.update {
                    it.copy(
                        recentGames = games.take(5),
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

    fun loadGame(gameId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val game = api.getGame(gameId)
                _state.update {
                    it.copy(
                        currentGame = game,
                        gameNameInput = game.name ?: "",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Fehler beim Laden: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun updateGame(gameId: Long, onSuccess: (Long) -> Unit) {
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
                val currentGame = _state.value.currentGame
                val updatedGame = Game(
                    id = gameId,
                    name = gameName,
                    createdOn = currentGame?.createdOn,
                    teamIds = currentGame?.teamIds
                )

                val savedGame = api.updateGame(gameId, updatedGame)

                _state.update {
                    it.copy(
                        currentGame = savedGame,
                        isLoading = false,
                        successMessage = "Spiel aktualisiert!"
                    )
                }

                onSuccess(gameId)

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Fehler beim Aktualisieren: ${e.message}",
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
                    createdOn = null,
                    teamIds = null
                )

                val createdGame = api.createGame(newGame)

                _state.update {
                    it.copy(
                        currentGame = createdGame,
                        isLoading = false,
                        //successMessage = "Spiel '${createdGame.name}' erstellt!",
                        gameNameInput = ""
                    )
                }

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
}