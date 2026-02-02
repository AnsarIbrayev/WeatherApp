package com.example.weatherapp.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FavoritesScreen() {
    val vm = remember { FavoritesViewModel() }
    val st by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.start() }

    var addDialog by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<FavoriteItem?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { addDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            when {
                st.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                st.error != null -> Text("Error: ${st.error}", Modifier.align(Alignment.Center))
                else -> {
                    Column(Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Favorites & Notes", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(6.dp))
                        Text("UID: ${st.uid}", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(12.dp))

                        if (st.items.isEmpty()) {
                            Text("Empty. Tap + to add.")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(st.items, key = { it.id }) { item ->
                                    Card {
                                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                                            if (item.note.isNotBlank()) {
                                                Spacer(Modifier.height(6.dp))
                                                Text(item.note)
                                            }
                                            Spacer(Modifier.height(10.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                OutlinedButton(onClick = { editItem = item }) { Text("Edit") }
                                                Button(onClick = { vm.delete(item.id) }) { Text("Delete") }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (addDialog) {
        FavoriteDialog(
            title = "Add Favorite",
            initTitle = "",
            initNote = "",
            onDismiss = { addDialog = false },
            onSave = { t, n -> vm.add(t, n); addDialog = false }
        )
    }

    editItem?.let { item ->
        FavoriteDialog(
            title = "Edit Favorite",
            initTitle = item.title,
            initNote = item.note,
            onDismiss = { editItem = null },
            onSave = { t, n -> vm.update(item.id, t, n); editItem = null }
        )
    }
}

@Composable
private fun FavoriteDialog(
    title: String,
    initTitle: String,
    initNote: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var t by remember { mutableStateOf(initTitle) }
    var n by remember { mutableStateOf(initNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("City") })
                OutlinedTextField(value = n, onValueChange = { n = it }, label = { Text("Note") })
            }
        },
        confirmButton = {
            Button(onClick = { onSave(t, n) }, enabled = t.trim().isNotEmpty()) { Text("Save") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
