package at.htl.activitiy_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import at.htl.activitiy_android.view.gamegeneration.GameGenerationScreen
import at.htl.activitiy_android.view.teamgeneration.TeamGenerationScreen
import at.htl.activitiy_android.view.teamselect.PlayerCreationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // ✅ State für aktuellen Screen UND Game-ID
    var currentScreen by remember { mutableStateOf<Screen>(Screen.GameGeneration) }
    var currentGameId by remember { mutableStateOf<Long?>(null) }

    when (currentScreen) {
        // 1️⃣ Spiel erstellen (NEUE STARTSEITE)
        Screen.GameGeneration -> {
            GameGenerationScreen(
                onGameCreated = { gameId ->
                    currentGameId = gameId  // Game-ID speichern
                    currentScreen = Screen.TeamGeneration
                }
            )
        }

        // 2️⃣ Teams generieren
        Screen.TeamGeneration -> {
            currentGameId?.let { gameId ->
                TeamGenerationScreen(
                    gameId = gameId,  // ← Game-ID übergeben
                    onTeamsCreated = {
                        currentScreen = Screen.PlayerCreation
                    }
                )
            }
        }

        // 3️⃣ Spieler zuweisen
        Screen.PlayerCreation -> {
            currentGameId?.let { gameId ->
                PlayerCreationScreen(
                    gameId = gameId  // ← Game-ID übergeben
                )
            }
        }
    }
}

sealed class Screen {
    data object GameGeneration : Screen()      // ← NEU!
    data object TeamGeneration : Screen()
    data object PlayerCreation : Screen()
}