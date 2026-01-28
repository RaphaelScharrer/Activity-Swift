package at.htl.activitiy_android.view.teamgeneration

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamGenerationScreen(
    gameId: Long,
    onTeamsCreated: () -> Unit,
    onBack: () -> Unit = {},
    vm: TeamGenerationViewModel = viewModel(
        factory = TeamGenerationViewModelFactory(gameId)
    )
) {
    val state by vm.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                }
            )
        }
    )
    { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    "Wie viele Teams brauchst du?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(24.dp))

                // Team Count Input
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = state.teamCountInput,
                            onValueChange = {
                                vm.onEvent(TeamGenerationEvent.TeamCountChanged(it))
                            },
                            label = { Text("Anzahl Teams") },
                            placeholder = { Text("Max. 4 Teams") },
                            singleLine = true,
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                    }

                    Button(
                        onClick = {
                            vm.onEvent(TeamGenerationEvent.SaveTeams(onTeamsCreated))
                        },
                        enabled = !state.isLoading && state.teamCountInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Weiter")
                    }
                }

                // Error
                if (state.error != null) {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                state.error ?: "",
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = {
                                vm.onEvent(TeamGenerationEvent.ClearMessages)
                            }) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
    }
}
