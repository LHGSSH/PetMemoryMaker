package com.example.petmemorymaker

import androidx.lifecycle.ViewModel

class MemoryListViewModel : ViewModel() {
    private val memoryRepository = MemoryRepository.get()
    val memories = memoryRepository.getMemories()
    val favoritedMemories = memoryRepository.getFavoritedMemories()

    fun addMemory(memory: Memory) {
        memoryRepository.addMemory(memory)
    }
}