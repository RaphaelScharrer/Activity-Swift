package at.htl.activitiy_android.view.teamgeneration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.htl.activitiy_android.data.api.RetrofitInstance
import at.htl.activitiy_android.domain.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeamGenerationViewModel(
    private val gameId: Long
) : ViewModel() {

    private val api = RetrofitInstance.api

    private val _state = MutableStateFlow(TeamGenerationState())
    val state: StateFlow<TeamGenerationState> = _state

    private var lastSavedTeamCount: Int? = null

    init {
        loadExistingTeams()
    }

    fun onEvent(event: TeamGenerationEvent) {
        when (event) {
            is TeamGenerationEvent.TeamCountChanged -> _state.update {
                it.copy(teamCountInput = event.value, error = null)
            }

            TeamGenerationEvent.GenerateTeams -> generateTeams()

            is TeamGenerationEvent.SaveTeams -> validateGenerateAndSave(event.onSuccess)

            TeamGenerationEvent.ClearMessages -> _state.update {
                it.copy(error = null, successMessage = null)
            }
        }
    }

    private fun loadExistingTeams() {
        viewModelScope.launch {
            try {
                val allTeams = api.getAllTeams()
                val existingTeams = allTeams.filter { it.gameId == gameId }.sortedBy { it.position }

                if (existingTeams.isNotEmpty()) {
                    lastSavedTeamCount = existingTeams.size
                    _state.update {
                        it.copy(
                            teamCountInput = existingTeams.size.toString(),
                            teams = existingTeams
                        )
                    }
                }
            } catch (e: Exception) {
                println("Error loading existing teams: ${e.message}")
            }
        }
    }

    private fun generateTeams() {
        val count = _state.value.teamCountInput.toIntOrNull()

        if (count == null) {
            _state.update {
                it.copy(error = "Bitte eine gültige Zahl eingeben")
            }
            return
        }

        if (count < 1 || count > 4) {
            _state.update {
                it.copy(error = "Bitte eine Zahl zwischen 1 und 4 eingeben")
            }
            return
        }

        val newTeams = (0 until count).map { i ->
            Team(
                id = null,
                position = i,
                gameId = gameId,
                playerIds = null
            )
        }

        _state.update {
            it.copy(
                teams = newTeams,
                hasChanges = true,
                error = null
            )
        }
    }

    private fun validateGenerateAndSave(onSuccess: () -> Unit) {
        val count = _state.value.teamCountInput.toIntOrNull()

        if (count == null) {
            _state.update {
                it.copy(error = "Bitte eine gültige Zahl eingeben")
            }
            return
        }

        if (count < 1 || count > 4) {
            _state.update {
                it.copy(error = "Bitte eine Zahl zwischen 1 und 4 eingeben")
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val oldTeams = api.getAllTeams().filter { it.gameId == gameId }

                // ✅ CHECK: Did team count actually change?
                val teamCountChanged = lastSavedTeamCount != null && lastSavedTeamCount != count

                if (teamCountChanged) {
                    // Team count changed - delete everything and start fresh
                    oldTeams.forEach { team ->
                        team.id?.let { teamId ->
                            api.deleteTeam(teamId)
                        }
                    }

                    val allPlayers = api.getAllPlayers()
                    allPlayers.forEach { player ->
                        if (oldTeams.any { it.id == player.team }) {
                            player.id?.let { playerId ->
                                api.deletePlayer(playerId)
                            }
                        }
                    }

                    // Create new teams
                    val newTeams = (0 until count).map { i ->
                        Team(
                            id = null,
                            position = i,
                            gameId = gameId,
                            playerIds = null
                        )
                    }

                    val savedTeams = mutableListOf<Team>()
                    newTeams.forEach { team ->
                        val saved = api.createTeam(team)
                        savedTeams.add(saved)
                    }

                    lastSavedTeamCount = count

                    _state.update {
                        it.copy(
                            teams = savedTeams,
                            isLoading = false,
                            hasChanges = false,
                        )
                    }
                } else if (lastSavedTeamCount == null) {
                    // First time creating teams
                    val newTeams = (0 until count).map { i ->
                        Team(
                            id = null,
                            position = i,
                            gameId = gameId,
                            playerIds = null
                        )
                    }

                    val savedTeams = mutableListOf<Team>()
                    newTeams.forEach { team ->
                        val saved = api.createTeam(team)
                        savedTeams.add(saved)
                    }

                    lastSavedTeamCount = count

                    _state.update {
                        it.copy(
                            teams = savedTeams,
                            isLoading = false,
                            hasChanges = false,
                        )
                    }
                } else {
                    // Same count - just navigate forward without touching data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hasChanges = false,
                        )
                    }
                }

                onSuccess()

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Fehler beim Speichern: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}

class TeamGenerationViewModelFactory(
    private val gameId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamGenerationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeamGenerationViewModel(gameId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}