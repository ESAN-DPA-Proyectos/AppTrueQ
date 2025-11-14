package edu.esandpa202502.apptrueq.need.viewmodel

import androidx.lifecycle.ViewModel
import edu.esandpa202502.apptrueq.model.Need
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NeedViewModel : ViewModel() {
    private val _needs = MutableStateFlow<List<Need>>(emptyList())
    val needs = _needs.asStateFlow()

    private var lastId = 0

    fun addNeed(description: String, category: String) {
        lastId++
        val newNeed = Need(
            id = lastId,
            description = description,
            category = category,
            status = "Activo"
        )
        _needs.value = _needs.value + newNeed
    }
}
