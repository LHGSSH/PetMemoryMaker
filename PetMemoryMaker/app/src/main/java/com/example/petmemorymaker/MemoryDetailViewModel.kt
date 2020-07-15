package com.example.petmemorymaker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class MemoryDetailViewModel() : ViewModel() {
    private val memoryRepository = MemoryRepository.get()
    private val memoryId = MutableLiveData<UUID>()

    var memoryData: LiveData<Memory?> = Transformations.switchMap(memoryId) {
        memoryId -> memoryRepository.getMemory(memoryId)
    }

    fun loadMemory(newMemoryId: UUID) {
        memoryId.value = newMemoryId
    }

    fun saveMemory(memory: Memory) {
        memoryRepository.updateMemory(memory)
    }
}