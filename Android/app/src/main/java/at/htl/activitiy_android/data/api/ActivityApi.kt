package at.htl.activitiy_android.data.api

import at.htl.activitiy_android.domain.model.Player
import at.htl.activitiy_android.domain.model.Team
import at.htl.activitiy_android.domain.model.Word
import retrofit2.http.*

interface ActivityApi {

    // Teams
    @GET("api/teams")
    suspend fun getAllTeams(): List<Team>

    @POST("api/teams")
    suspend fun createTeam(@Body team: Team): Team

    // Players
    @GET("api/players")
    suspend fun getAllPlayers(): List<Player>

    @POST("api/players")
    suspend fun createPlayer(@Body player: Player): Player

    @PUT("api/players/{id}") // Spieler updaten (für Team-Zuweisung!)
    suspend fun updatePlayer(@Path("id") id: Long, @Body player: Player): Player

    @DELETE("api/players/{id}")
    suspend fun deletePlayer(@Path("id") id: Long)

    // Words
    @GET("api/words")
    suspend fun getAllWords(): List<Word>

    @GET("api/words/{id}")
    suspend fun getWord(@Path("id") id: Long): Word

    @POST("api/words")
    suspend fun createWord(@Body word: Word): Word

    @PUT("api/words/{id}")
    suspend fun updateWord(@Path("id") id: Long, @Body word: Word): Word

    @DELETE("api/words/{id}")
    suspend fun deleteWord(@Path("id") id: Long)
}