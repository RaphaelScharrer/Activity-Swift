package at.htl.activitiy_android.data.api

import at.htl.activitiy_android.domain.model.Player
import at.htl.activitiy_android.domain.model.Team
import kotlinx.coroutines.delay

/**
 * Mock Repository für lokale Entwicklung OHNE Backend
 * Simuliert API-Aufrufe mit Delays und speichert Daten im Memory
 */
object MockRepository {

    // In-Memory Datenbank
    private val teams = mutableListOf<Team>()
    private val players = mutableListOf<Player>()

    private var teamIdCounter = 1L
    private var playerIdCounter = 1L

    // Simuliert Netzwerk-Delay (500ms)
    private suspend fun simulateNetworkDelay() {
        delay(500)
    }

    // TEAMS
    suspend fun getAllTeams(): List<Team> {
        simulateNetworkDelay()
        return teams.toList()
    }

    suspend fun getTeam(id: Long): Team {
        simulateNetworkDelay()
        return teams.find { it.id == id }
            ?: throw Exception("Team mit ID $id nicht gefunden")
    }

    suspend fun createTeam(team: Team): Team {
        simulateNetworkDelay()
        val newTeam = team.copy(id = teamIdCounter++)
        teams.add(newTeam)
        return newTeam
    }

    suspend fun updateTeam(id: Long, team: Team): Team {
        simulateNetworkDelay()
        val index = teams.indexOfFirst { it.id == id }
        if (index == -1) throw Exception("Team mit ID $id nicht gefunden")

        val updated = team.copy(id = id)
        teams[index] = updated
        return updated
    }

    suspend fun deleteTeam(id: Long) {
        simulateNetworkDelay()
        teams.removeAll { it.id == id }
    }

    // PLAYERS
    suspend fun getAllPlayers(): List<Player> {
        simulateNetworkDelay()
        return players.toList()
    }

    suspend fun getPlayer(id: Long): Player {
        simulateNetworkDelay()
        return players.find { it.id == id }
            ?: throw Exception("Spieler mit ID $id nicht gefunden")
    }

    suspend fun createPlayer(player: Player): Player {
        simulateNetworkDelay()
        val newPlayer = player.copy(id = playerIdCounter++)
        players.add(newPlayer)
        return newPlayer
    }

    suspend fun updatePlayer(id: Long, player: Player): Player {
        simulateNetworkDelay()
        val index = players.indexOfFirst { it.id == id }
        if (index == -1) throw Exception("Spieler mit ID $id nicht gefunden")

        val updated = player.copy(id = id)
        players[index] = updated
        return updated
    }

    suspend fun deletePlayer(id: Long) {
        simulateNetworkDelay()
        players.removeAll { it.id == id }
    }

    // UTILITY
    fun clear() {
        teams.clear()
        players.clear()
        teamIdCounter = 1L
        playerIdCounter = 1L
    }

    fun printDebugInfo() {
        println("=== MockRepository Debug ===")
        println("Teams (${teams.size}):")
        teams.forEach { println("  - ${it.label} (ID: ${it.id}, Position: ${it.position})") }
        println("Players (${players.size}):")
        players.forEach { println("  - ${it.name} (ID: ${it.id}, Team: ${it.team}, Punkte: ${it.pointsEarned})") }
        println("===========================")
    }
}