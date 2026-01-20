package at.htl.activitiy_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import at.htl.activitiy_android.feature.teamgeneration.TeamGenerationScreen
import at.htl.activitiy_android.feature.teamselect.PlayerCreationScreen

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
    var currentScreen by remember { mutableStateOf<Screen>(Screen.TeamGeneration) }

    when (currentScreen) {
        Screen.TeamGeneration -> {
            TeamGenerationScreen(
                onTeamsCreated = {
                    currentScreen = Screen.PlayerCreation
                }
            )
        }
        Screen.PlayerCreation -> {
            PlayerCreationScreen()
        }
    }
}

sealed class Screen {
    data object TeamGeneration : Screen()
    data object PlayerCreation : Screen()
}