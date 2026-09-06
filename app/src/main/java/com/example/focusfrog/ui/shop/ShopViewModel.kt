package com.example.focusfrog.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusfrog.data.local.datastore.UserPreferencesRepository
import com.example.focusfrog.data.local.db.ShopItemEntity
import com.example.focusfrog.data.repository.FocusRepository
import com.example.focusfrog.data.repository.ShopRepository
import com.example.focusfrog.ui.components.FrogStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopUiState(
    val items: List<ShopItemEntity> = emptyList(),
    val bugsBalance: Int = 0,
    val frogStage: FrogStage = FrogStage.TADPOLE,
    val snackbarMessage: String? = null
)

class ShopViewModel(
    private val shopRepository: ShopRepository,
    private val focusRepository: FocusRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    init {
        // Observe shop items
        viewModelScope.launch {
            shopRepository.allShopItems.collectLatest { items ->
                _uiState.update { it.copy(items = items) }
            }
        }

        // Observe user stats for bugs and frog stage
        viewModelScope.launch {
            focusRepository.userStatsFlow.collectLatest { stats ->
                val stage = when {
                    stats.totalSessions >= 100 -> FrogStage.ROYAL_FROG
                    stats.totalSessions >= 30 -> FrogStage.BIG_FROG
                    stats.totalSessions >= 10 -> FrogStage.FROGLET
                    else -> FrogStage.TADPOLE
                }
                _uiState.update {
                    it.copy(
                        bugsBalance = stats.bugsBalance,
                        frogStage = stage
                    )
                }
            }
        }
    }

    fun buyItem(item: ShopItemEntity) {
        if (_uiState.value.bugsBalance < item.price) {
            _uiState.update { it.copy(snackbarMessage = "Not enough bugs yet! 🪰") }
            return
        }
        viewModelScope.launch {
            val success = shopRepository.buyItem(item)
            if (!success) {
                _uiState.update { it.copy(snackbarMessage = "Not enough bugs yet! 🪰") }
            }
        }
    }

    fun equipItem(item: ShopItemEntity) {
        if (!item.isOwned) return

        // Crown constraint: requires Royal Frog stage (100+ sessions)
        if (item.type == "CROWN" && _uiState.value.frogStage != FrogStage.ROYAL_FROG) {
            _uiState.update { it.copy(snackbarMessage = "Requires Royal Frog stage! 👑") }
            return
        }

        viewModelScope.launch {
            shopRepository.equipItem(item)
            if (item.type == "THEME") {
                userPreferencesRepository.setEquippedTheme(item.name)
            }
        }
    }

    fun unequipItem(item: ShopItemEntity) {
        viewModelScope.launch {
            shopRepository.unequipItem(item)
            if (item.type == "THEME") {
                userPreferencesRepository.setEquippedTheme("Pond Theme")
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
