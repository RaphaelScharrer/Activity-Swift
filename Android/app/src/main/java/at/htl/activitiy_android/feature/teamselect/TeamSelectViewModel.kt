package at.htl.activitiy_android.feature.teamselect

import androidx.lifecycle.ViewModel
import at.htl.activitiy_android.domain.model.Player
import at.htl.activitiy_android.domain.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class TeamSelectViewModel : ViewModel() {

    private val _state = MutableStateFlow(TeamSelectState())
    val state: StateFlow<TeamSelectState> = _state

    fun onEvent(event: TeamSelectEvent) {
        when (event) {
            is TeamSelectEvent.NameChanged -> _state.update {
                it.copy(nameInput = event.value, error = null)
            }

            TeamSelectEvent.AddPlayer -> addPlayer()

            is TeamSelectEvent.RemovePlayer -> _state.update { s ->
                s.copy(players = s.players.filterNot { it.id == event.playerId })
            }

            is TeamSelectEvent.CycleTeam -> _state.update { s ->
                s.copy(
                    players = s.players.map { p ->
                        if (p.id == event.playerId) p.copy(team = p.team.next()) else p
                    }
                )
            }

            TeamSelectEvent.AssignRandomTeams -> assignRandomTeams()

            TeamSelectEvent.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun addPlayer() {
        val s = _state.value
        val name = s.nameInput.trim()

        if (name.isEmpty()) {
            _state.update { it.copy(error = "Bitte einen Spielernamen eingeben.") }
            return
        }
        // optional: Duplikate verhindern
        if (s.players.any { it.name.equals(name, ignoreCase = true) }) {
            _state.update { it.copy(error = "Spieler existiert bereits.") }
            return
        }

        val player = Player(name = name, team = s.defaultTeam)

        _state.update {
            it.copy(
                nameInput = "",
                players = it.players + player,
                error = null
            )
        }
    }

    private fun assignRandomTeams() {
        val s = _state.value
        if (s.players.isEmpty()) return

        // Gleichmäßig verteilen: shuffle + round-robin über alle Teams
        val teams = Team.entries.toList()
        val shuffled = s.players.shuffled(Random(System.currentTimeMillis()))
        val reassigned = shuffled.mapIndexed { idx, p ->
            p.copy(team = teams[idx % teams.size])
        }

        _state.update { it.copy(players = reassigned, error = null) }
    }
}
