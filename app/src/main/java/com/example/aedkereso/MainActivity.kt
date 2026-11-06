package com.example.aedkereso

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aedkereso.data.AedRepository
import com.example.aedkereso.ui.theme.AEDKeresoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val apiService = AedNetworkClient.apiService
        val repository = AedRepository(this, apiService)
        val viewModel = AedViewModel(repository)

        setContent {
            AEDKeresoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AedScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AedScreen(viewModel: AedViewModel, modifier: Modifier = Modifier) {
    val aedItems by viewModel.aedItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        // ÉLŐ KERESŐSÁV SZÖVEGES IKONNAL (Így biztosan nem akad el az importokon)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Keresés település vagy helyszín alapján...") },
            leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 8.dp)) },
            singleLine = true
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (aedItems.isEmpty()) {
                Text(
                    text = "Nincs a keresésnek megfelelő találat.",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 16.sp
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(aedItems) { uiItem ->
                        AedCard(uiItem = uiItem, onClick = {
                            openGoogleMaps(context, uiItem.element.lat, uiItem.element.lng, uiItem.element.name)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun AedCard(uiItem: AedUiItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = uiItem.element.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Helyszín: ${uiItem.element.locationDetails}", fontSize = 14.sp)
            Text(text = "Elérhetőség: ${uiItem.element.operatorName}", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "📍 Kattints ide az útvonaltervezéshez", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

fun openGoogleMaps(context: Context, lat: Double?, lng: Double?, label: String) {
    if (lat != null && lng != null) {
        val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lng")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
            context.startActivity(webIntent)
        }
    } else {
        Toast.makeText(context, "Sajnáljuk, a koordináták nem elérhetőek!", Toast.LENGTH_SHORT).show()
    }
}