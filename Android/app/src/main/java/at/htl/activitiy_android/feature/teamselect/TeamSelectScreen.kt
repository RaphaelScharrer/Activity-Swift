package at.htl.activitiy_android.feature.teamselect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.htl.activitiy_android.domain.model.Team
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamSelectScreen(
    vm: TeamSelectViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Teamzuweisung") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Input row
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = state.nameInput,
                    onValueChange = { vm.onEvent(TeamSelectEvent.NameChanged(it)) },
                    label = { Text("Spielername") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.onEvent(TeamSelectEvent.AddPlayer) }) {
                    Text("Hinzufügen")
                }
            }

            // Error
            if (state.error != null) {
                Spacer(Modifier.height(8.dp))
                AssistChip(
                    onClick = { vm.onEvent(TeamSelectEvent.ClearError) },
                    label = { Text(state.error ?: "") }
                )
            }

            Spacer(Modifier.height(16.dp))

            // List header + random assign
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Spieler", fontWeight = FontWeight.SemiBold)
                TextButton(onClick = { vm.onEvent(TeamSelectEvent.AssignRandomTeams) }) {
                    Text("Random Teams")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Player list
            if (state.players.isEmpty()) {
                Text("Noch keine Spieler hinzugefügt.", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.players, key = { it.id }) { player ->
                        PlayerRow(
                            name = player.name,
                            team = player.team,
                            onCycleTeam = { vm.onEvent(TeamSelectEvent.CycleTeam(player.id)) },
                            onRemove = { vm.onEvent(TeamSelectEvent.RemovePlayer(player.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerRow(
    name: String,
    team: Team,
    onCycleTeam: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onCycleTeam)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(team.color)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold)
                Text(team.label, style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onRemove) { Text("Entfernen") }
        }
    }
}
