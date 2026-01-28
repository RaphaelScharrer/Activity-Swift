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

    fun onEvent(event: TeamGenerationEvent) {
        when (event) {
            is TeamGenerationEvent.TeamCountChanged -> _state.update {
                it.copy(teamCountInput = event.value, error = null)
            }

            TeamGenerationEvent.GenerateTeams -> generateTeams()

            is TeamGenerationEvent.SaveTeams -> saveTeams(event.onSuccess)

            TeamGenerationEvent.ClearMessages -> _state.update {
                it.copy(error = null, successMessage = null)
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
                position = i,  // ← Integer position
                gameId = gameId,
                playerIds = null
            )
        }

        _state.update {
            it.copy(
                teams = newTeams,
                hasChanges = true,
                successMessage = "$count Teams generiert. Bitte speichern!",
                error = null
            )
        }
    }

    private fun saveTeams(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val savedTeams = mutableListOf<Team>()

                _state.value.teams.forEach { team ->
                    val saved = api.createTeam(team)
                    savedTeams.add(saved)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        hasChanges = false,
                        successMessage = "Teams erfolgreich gespeichert!"
                    )
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