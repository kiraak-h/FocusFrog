package com.example.focusfrog.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.focusfrog.data.local.datastore.UserPreferencesRepository
import com.example.focusfrog.data.repository.FocusRepository
import com.example.focusfrog.data.repository.ShopRepository

class ShopViewModelFactory(
    private val shopRepository: ShopRepository,
    private val focusRepository: FocusRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            return ShopViewModel(shopRepository, focusRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
