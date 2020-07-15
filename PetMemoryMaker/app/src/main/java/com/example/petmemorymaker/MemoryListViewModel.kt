package com.example.petmemorymaker

import androidx.lifecycle.ViewModel

class MemoryListViewModel : ViewModel() {
    private val memoryRepository = MemoryRepository.get()
    val memories = memoryRepository.getMemories()

    fun addMemory(memory: Memory) {
        memoryRepository.addMemory(memory)
    }
}