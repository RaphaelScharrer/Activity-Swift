package at.htl.activitiy_android.data.api

import at.htl.activitiy_android.domain.model.Game
import at.htl.activitiy_android.domain.model.Player
import at.htl.activitiy_android.domain.model.Team
import at.htl.activitiy_android.domain.model.Word
import retrofit2.http.*

interface ActivityApi {

    // Teams
    @GET("api/teams")
    suspend fun getAllTeams(): List<Team>

    @GET("api/teams/game/{gameId}")
    suspend fun getTeamsByGame(@Path("gameId") gameId: Long): List<Team>

    @POST("api/teams")
    suspend fun createTeam(@Body team: Team): Team

    @PUT("api/teams/{id}")
    suspend fun updateTeam(@Path("id") id: Long, @Body team: Team): Team

    @DELETE("api/teams/{id}")
    suspend fun deleteTeam(@Path("id") id: Long)

    // Players
    @GET("api/players")
    suspend fun getAllPlayers(): List<Player>

    @POST("api/players")
    suspend fun createPlayer(@Body player: Player): Player

    @PUT("api/players/{id}") // Spieler updaten (für Team-Zuweisung!)
    suspend fun updatePlayer(@Path("id") id: Long, @Body player: Player): Player

    @DELETE("api/players/{id}")
    suspend fun deletePlayer(@Path("id") id: Long)

    // Games
    @GET("api/games")
    suspend fun getAllGames(): List<Game>

    @GET("api/games/{id}")
    suspend fun getGame(@Path("id") id: Long): Game

    @GET("api/games/{id}/full")
    suspend fun getGameWithTeams(@Path("id") id: Long): Game

    @POST("api/games")
    suspend fun createGame(@Body game: Game): Game

    @PUT("api/games/{id}")
    suspend fun updateGame(@Path("id") id: Long, @Body game: Game): Game

    @DELETE("api/games/{id}")
    suspend fun deleteGame(@Path("id") id: Long)
}