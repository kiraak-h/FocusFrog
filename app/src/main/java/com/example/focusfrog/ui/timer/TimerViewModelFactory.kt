package com.example.focusfrog.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.focusfrog.data.local.datastore.UserPreferencesRepository
import com.example.focusfrog.data.repository.FocusRepository
import com.example.focusfrog.data.repository.ShopRepository

class TimerViewModelFactory(
    private val focusRepository: FocusRepository,
    private val shopRepository: ShopRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            return TimerViewModel(focusRepository, shopRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
