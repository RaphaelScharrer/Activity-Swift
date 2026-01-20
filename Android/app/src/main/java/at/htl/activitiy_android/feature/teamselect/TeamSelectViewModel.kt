package at.htl.activitiy_android.feature.teamselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl.activitiy_android.data.api.RetrofitInstance
import at.htl.activitiy_android.domain.model.Player
import at.htl.activitiy_android.domain.model.PlayerWithTeam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeamSelectViewModel : ViewModel() {

    private val api = RetrofitInstance.api
    private val _state = MutableStateFlow(TeamSelectState())
    val state: StateFlow<TeamSelectState> = _state

    init {
        loadData()
    }

    fun onEvent(event: TeamSelectEvent) {
        when (event) {
            is TeamSelectEvent.NameChanged -> _state.update {
                it.copy(nameInput = event.value, error = null)
            }

            TeamSelectEvent.AddPlayer -> addPlayer()
            is TeamSelectEvent.RemovePlayer -> removePlayer(event.playerId)
            is TeamSelectEvent.ChangeTeam -> changeTeam(event.playerId, event.teamId)
            TeamSelectEvent.AssignRandomTeams -> assignRandomTeams()
            TeamSelectEvent.ClearError -> _state.update { it.copy(error = null) }
            TeamSelectEvent.LoadData -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val teams = api.getAllTeams()
                val players = api.getAllPlayers()

                // Players mit Teams zusammenführen
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
                        selectedTeamId = teams.firstOrNull()?.id
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

    private fun addPlayer() {
        val s = _state.value
        val name = s.nameInput.trim()

        if (name.isEmpty()) {
            _state.update { it.copy(error = "Bitte einen Spielernamen eingeben.") }
            return
        }

        viewModelScope.launch {
            try {
                val newPlayer = Player(
                    name = name,
                    team = s.selectedTeamId,
                    pointsEarned = 0
                )

                api.createPlayer(newPlayer)

                _state.update { it.copy(nameInput = "") }
                loadData() // Reload nach Erstellen

            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Fehler beim Hinzufügen: ${e.message}")
                }
            }
        }
    }

    private fun removePlayer(playerId: Long) {
        viewModelScope.launch {
            try {
                api.deletePlayer(playerId)
                loadData()
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Fehler beim Löschen: ${e.message}")
                }
            }
        }
    }

    private fun changeTeam(playerId: Long, teamId: Long) {
        viewModelScope.launch {
            try {
                val player = _state.value.players
                    .find { it.player.id == playerId }?.player ?: return@launch

                val updated = player.copy(team = teamId)
                api.updatePlayer(playerId, updated)

                loadData()
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Fehler beim Team-Wechsel: ${e.message}")
                }
            }
        }
    }

    private fun assignRandomTeams() {
        val s = _state.value
        if (s.players.isEmpty() || s.teams.isEmpty()) return

        viewModelScope.launch {
            try {
                val teams = s.teams
                val shuffled = s.players.shuffled()

                shuffled.forEachIndexed { idx, playerWithTeam ->
                    val player = playerWithTeam.player
                    val newTeamId = teams[idx % teams.size].id ?: return@forEachIndexed

                    player.id?.let { playerId ->
                        api.updatePlayer(playerId, player.copy(team = newTeamId))
                    }
                }

                loadData()
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Fehler beim Zuweisen: ${e.message}")
                }
            }
        }
    }
}