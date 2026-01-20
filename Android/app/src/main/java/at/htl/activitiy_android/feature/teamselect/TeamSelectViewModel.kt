package at.htl.activitiy_android.feature.teamselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl.activitiy_android.data.api.MockRepository
// import at.htl.activitiy_android.data.api.RetrofitInstance
import at.htl.activitiy_android.domain.model.Player
import at.htl.activitiy_android.domain.model.PlayerWithTeam
import at.htl.activitiy_android.domain.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeamSelectViewModel : ViewModel() {

    // MOCK: Verwende MockRepository statt echtem Backend
    // private val api = RetrofitInstance.api

    private val _state = MutableStateFlow(TeamSelectState())
    val state: StateFlow<TeamSelectState> = _state

    private val pendingPlayers = mutableListOf<Player>()

    fun onEvent(event: TeamSelectEvent) {
        when (event) {
            is TeamSelectEvent.NameChanged -> _state.update {
                it.copy(nameInput = event.value, error = null)
            }

            TeamSelectEvent.AddPlayer -> addPlayerLocally()
            is TeamSelectEvent.RemovePlayer -> removePlayerLocally(event.playerId)
            is TeamSelectEvent.RemovePlayerByName -> removePlayerByName(event.name)
            is TeamSelectEvent.ChangeTeam -> changeTeamLocally(event.playerId, event.teamId)
            TeamSelectEvent.ClearMessages -> _state.update {
                it.copy(error = null, successMessage = null)
            }
            TeamSelectEvent.LoadData -> loadData()
            is TeamSelectEvent.SelectTeam -> _state.update {
                it.copy(selectedTeamId = event.teamId)
            }
            TeamSelectEvent.SaveTeamsAndPlayers -> saveToBackend()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // MOCK: Verwende MockRepository
                val teams = MockRepository.getAllTeams()
                val players = MockRepository.getAllPlayers()

                val playersWithTeams = players.map { player ->
                    PlayerWithTeam(
                        player = player,
                        team = teams.find { it.id == player.team }
                    )
                }

                _state.update {
                    it.copy(
                        teams = teams,
                        players = playersWithTeams,
                        isLoading = false,
                        selectedTeamId = teams.firstOrNull()?.id,
                        hasChanges = false
                    )
                }

                pendingPlayers.clear()

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

    private fun addPlayerLocally() {
        val s = _state.value
        val name = s.nameInput.trim()

        if (name.isEmpty()) {
            _state.update { it.copy(error = "Bitte einen Spielernamen eingeben.") }
            return
        }

        if (s.teams.isEmpty()) {
            _state.update { it.copy(error = "Bitte zuerst Teams generieren!") }
            return
        }

        if (s.players.any { it.player.name.equals(name, ignoreCase = true) }) {
            _state.update { it.copy(error = "Spieler existiert bereits.") }
            return
        }

        val newPlayer = Player(
            id = null,
            name = name,
            team = s.selectedTeamId,
            pointsEarned = 0
        )

        pendingPlayers.add(newPlayer)

        val selectedTeam = s.teams.find { it.id == s.selectedTeamId }
        val newPlayerWithTeam = PlayerWithTeam(newPlayer, selectedTeam)

        _state.update {
            it.copy(
                nameInput = "",
                players = it.players + newPlayerWithTeam,
                hasChanges = true,
                successMessage = "Spieler lokal hinzugefügt. Bitte speichern!"
            )
        }
    }

    private fun removePlayerLocally(playerId: Long) {
        viewModelScope.launch {
            try {
                // Wenn Spieler bereits gespeichert ist, vom Backend löschen
                MockRepository.deletePlayer(playerId)

                // Spieler aus der Liste entfernen
                _state.update { s ->
                    s.copy(
                        players = s.players.filterNot { it.player.id == playerId },
                        hasChanges = false  // Wurde direkt gelöscht
                    )
                }

                // Auch aus pending entfernen falls vorhanden
                pendingPlayers.removeAll { it.id == playerId }

            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Fehler beim Löschen: ${e.message}")
                }
            }
        }
    }

    private fun removePlayerByName(name: String) {
        // Für neue Spieler ohne ID: nur lokal entfernen
        _state.update { s ->
            s.copy(
                players = s.players.filterNot { it.player.name == name }
                // hasChanges bleibt wie es ist
            )
        }

        // Aus pending entfernen
        pendingPlayers.removeAll { it.name == name }
    }

    private fun changeTeamLocally(playerId: Long, teamId: Long) {
        _state.update { s ->
            val updatedPlayers = s.players.map { playerWithTeam ->
                if (playerWithTeam.player.id == playerId) {
                    val updatedPlayer = playerWithTeam.player.copy(team = teamId)
                    val newTeam = s.teams.find { it.id == teamId }
                    PlayerWithTeam(updatedPlayer, newTeam)
                } else {
                    playerWithTeam
                }
            }
            s.copy(players = updatedPlayers, hasChanges = true)
        }
    }

    private fun saveToBackend() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // MOCK: Verwende MockRepository
                // Neue Spieler speichern
                if (pendingPlayers.isNotEmpty()) {
                    pendingPlayers.forEach { player ->
                        MockRepository.createPlayer(player)
                    }
                    pendingPlayers.clear()
                }

                // Bestehende Spieler updaten (Team-Zuweisungen)
                _state.value.players
                    .filter { it.player.id != null }
                    .forEach { playerWithTeam ->
                        val player = playerWithTeam.player
                        player.id?.let { id ->
                            MockRepository.updatePlayer(id, player)
                        }
                    }

                MockRepository.printDebugInfo()

                _state.update {
                    it.copy(
                        isLoading = false,
                        hasChanges = false,
                        successMessage = "Erfolgreich gespeichert!",
                        error = null
                    )
                }

                loadData()

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