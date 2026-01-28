package at.htl.activitiy_android.view.gamesummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.htl.activitiy_android.data.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class GameSummaryViewModel(
    private val gameId: Long
) : ViewModel() {

    private val api = RetrofitInstance.api

    private val _state = MutableStateFlow(GameSummaryState())
    val state: StateFlow<GameSummaryState> = _state

    fun loadGameData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Load game
                val game = api.getGame(gameId)

                // Load teams for this game
                val allTeams = api.getAllTeams()
                val teams = allTeams.filter { it.gameId == gameId }.sortedBy { it.position }

                // Load all players for these teams
                val allPlayers = api.getAllPlayers()
                val players = allPlayers.filter { player ->
                    teams.any { team -> team.id == player.team }
                }

                _state.update {
                    it.copy(
                        gameName = game.name ?: "Unbenanntes Spiel",
                        teams = teams,
                        allPlayers = players,
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
}

class GameSummaryViewModelFactory(
    private val gameId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameSummaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameSummaryViewModel(gameId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
