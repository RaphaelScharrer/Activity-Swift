package at.htl.activitiy_android.feature.teamselect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import at.htl.activitiy_android.domain.model.PlayerWithTeam
import at.htl.activitiy_android.domain.model.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerCreationScreen(
    vm: TeamSelectViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.onEvent(TeamSelectEvent.LoadData)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Spieler erstellen") })
        },
        bottomBar = {
            if (state.hasChanges) {
                Surface(
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { vm.onEvent(TeamSelectEvent.SaveTeamsAndPlayers) },
                        enabled = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Speichern")
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
                    .fillMaxSize()
            ) {
                // Team Selection Dropdown
                if (state.teams.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedTeam = state.teams.find { it.id == state.selectedTeamId }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedTeam?.label ?: "Team wählen",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Standard-Team") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            state.teams.forEach { team ->
                                DropdownMenuItem(
                                    text = { Text(team.label) },
                                    onClick = {
                                        team.id?.let {
                                            vm.onEvent(TeamSelectEvent.SelectTeam(it))
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                } else {
                    // Keine Teams vorhanden
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            "Keine Teams gefunden. Bitte zuerst Teams erstellen!",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Input row - Spieler hinzufügen
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = state.nameInput,
                        onValueChange = { vm.onEvent(TeamSelectEvent.NameChanged(it)) },
                        label = { Text("Spielername") },
                        placeholder = { Text("z.B. Max Mustermann") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading && state.teams.isNotEmpty()
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { vm.onEvent(TeamSelectEvent.AddPlayer) },
                        enabled = !state.isLoading &&
                                state.nameInput.isNotBlank() &&
                                state.teams.isNotEmpty()
                    ) {
                        Text("Hinzufügen")
                    }
                }

                // Success Message
                if (state.successMessage != null) {
                    Spacer(Modifier.height(8.dp))
                    AssistChip(
                        onClick = { vm.onEvent(TeamSelectEvent.ClearMessages) },
                        label = { Text(state.successMessage ?: "") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                }

                // Error
                if (state.error != null) {
                    Spacer(Modifier.height(8.dp))
                    AssistChip(
                        onClick = { vm.onEvent(TeamSelectEvent.ClearMessages) },
                        label = { Text(state.error ?: "") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // List header
                Text("Spieler (${state.players.size})", fontWeight = FontWeight.SemiBold)

                Spacer(Modifier.height(8.dp))

                // Player list
                if (state.players.isEmpty() && !state.isLoading) {
                    Text(
                        "Noch keine Spieler hinzugefügt.",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.players, key = { it.player.id ?: it.player.name }) { playerWithTeam ->
                            PlayerRow(
                                playerWithTeam = playerWithTeam,
                                teams = state.teams,
                                onChangeTeam = { teamId ->
                                    playerWithTeam.player.id?.let { playerId ->
                                        vm.onEvent(TeamSelectEvent.ChangeTeam(playerId, teamId))
                                    }
                                },
                                onRemove = {
                                    playerWithTeam.player.id?.let { playerId ->
                                        vm.onEvent(TeamSelectEvent.RemovePlayer(playerId))
                                    }
                                },
                                enabled = !state.isLoading
                            )
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
private fun PlayerRow(
    playerWithTeam: PlayerWithTeam,
    teams: List<Team>,
    onChangeTeam: (Long) -> Unit,
    onRemove: () -> Unit,
    enabled: Boolean
) {
    var showTeamDialog by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable(enabled = enabled) { showTeamDialog = true }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(playerWithTeam.team?.color ?: MaterialTheme.colorScheme.outline)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(playerWithTeam.player.name, fontWeight = FontWeight.SemiBold)
                Text(
                    "${playerWithTeam.team?.label ?: "Kein Team"} • ${playerWithTeam.player.pointsEarned} Punkte",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TextButton(onClick = onRemove, enabled = enabled) {
                Text("Entfernen")
            }
        }
    }

    // Team Selection Dialog
    if (showTeamDialog) {
        AlertDialog(
            onDismissRequest = { showTeamDialog = false },
            title = { Text("Team wählen") },
            text = {
                Column {
                    teams.forEach { team ->
                        TextButton(
                            onClick = {
                                team.id?.let { onChangeTeam(it) }
                                showTeamDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(team.color)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(team.label)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTeamDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}