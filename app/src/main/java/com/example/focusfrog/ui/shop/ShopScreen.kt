package com.example.focusfrog.ui.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusfrog.R
import com.example.focusfrog.data.local.db.ShopItemEntity
import com.example.focusfrog.ui.theme.BugAmber

@Composable
fun ShopScreen(
    viewModel: ShopViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            // Top Bar: Bug Balance Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pond Shop 🏪",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BugAmber.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = stringResource(id = R.string.bugs_count, uiState.bugsBalance),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Categories & Items
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                val categories = listOf("HAT", "SUNGLASSES", "CROWN", "THEME")
                categories.forEach { category ->
                    val categoryItems = uiState.items.filter { it.type == category }
                    if (categoryItems.isNotEmpty()) {
                        item {
                            Text(
                                text = getCategoryTitle(category),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(categoryItems) { item ->
                            ShopItemCard(
                                item = item,
                                onBuy = { viewModel.buyItem(context, item) },
                                onEquip = { viewModel.equipItem(context, item) },
                                onUnequip = { viewModel.unequipItem(context, item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItemCard(
    item: ShopItemEntity,
    onBuy: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (item.price > 0) {
                    Text(
                        text = "🪰 ${item.price} Bugs",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text = "Free / Default",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            when {
                !item.isOwned -> {
                    Button(
                        onClick = onBuy,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Buy")
                    }
                }
                item.isEquipped -> {
                    OutlinedButton(
                        onClick = onUnequip,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Equipped")
                    }
                }
                else -> {
                    Button(
                        onClick = onEquip,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Equip")
                    }
                }
            }
        }
    }
}

private fun getCategoryTitle(type: String): String {
    return when (type) {
        "HAT" -> "Hats 🎩"
        "SUNGLASSES" -> "Sunglasses 🕶️"
        "CROWN" -> "Crown 👑"
        "THEME" -> "Themes 🎨"
        else -> "Items"
    }
}
