package com.example.petmemorymaker

import androidx.lifecycle.ViewModel
import java.io.File

class MemoryListViewModel : ViewModel() {
    private val memoryRepository = MemoryRepository.get()
    val memories = memoryRepository.getMemories()
    val favoritedMemories = memoryRepository.getFavoritedMemories()

    fun addMemory(memory: Memory) {
        memoryRepository.addMemory(memory)
    }

    fun getPhotoFile(memory: Memory): File {
        return memoryRepository.getPhotoFile(memory)
    }
}