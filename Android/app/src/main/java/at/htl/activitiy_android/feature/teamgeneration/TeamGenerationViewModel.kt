package at.htl.activitiy_android.feature.teamgeneration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl.activitiy_android.data.api.MockRepository
import at.htl.activitiy_android.data.api.RetrofitInstance
import at.htl.activitiy_android.domain.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class TeamGenerationViewModel : ViewModel() {

    // MOCK: Verwende MockRepository statt echtem Backend
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
                position = i.toLong()
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
                    // MOCK: Verwende MockRepository
                    val saved = api.createTeam(team)
                    savedTeams.add(saved)
                }

                MockRepository.printDebugInfo()

                _state.update {
                    it.copy(
                        isLoading = false,
                        hasChanges = false,
                        successMessage = "Teams erfolgreich gespeichert!"
                    )
                }

                // Navigate zur Player-Erstellung
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