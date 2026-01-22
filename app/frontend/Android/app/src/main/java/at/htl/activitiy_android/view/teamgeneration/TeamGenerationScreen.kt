package at.htl.activitiy_android.view.teamgeneration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamGenerationScreen(
    gameId: Long,  // ← NEU: Game-ID als Parameter
    onTeamsCreated: () -> Unit,
    vm: TeamGenerationViewModel = viewModel(
        factory = TeamGenerationViewModelFactory(gameId)  // ← Factory mit gameId
    )
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Teams generieren") })
        },
        bottomBar = {
            if (state.teams.isNotEmpty()) {
                Surface(
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = {
                                vm.onEvent(TeamGenerationEvent.SaveTeams(onTeamsCreated))
                            },
                            enabled = !state.isLoading && state.hasChanges,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Teams speichern & weiter")
                        }
                    }
                }
            }
        }
    ) { padding ->
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

                        Button(
                            onClick = { vm.onEvent(TeamGenerationEvent.GenerateTeams) },
                            enabled = !state.isLoading &&
                                    state.teamCountInput.toIntOrNull() != null &&
                                    state.teamCountInput.toIntOrNull()!! > 0,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Teams generieren")
                        }
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

                // Success
                if (state.successMessage != null) {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            state.successMessage ?: "",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Team List
                if (state.teams.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Generierte Teams (${state.teams.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.teams, key = { it.position }) { team ->
                            TeamCard(team = team)
                        }
                    }
                }
            }

            // Loading overlay
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun TeamCard(team: at.htl.activitiy_android.domain.model.Team) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(team.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${team.position + 1}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(16.dp))

            Text(
                team.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}