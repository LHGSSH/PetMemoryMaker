package com.example.petmemorymaker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.petmemorymaker.database.MemoryDatabase
import java.lang.IllegalStateException
import java.util.*

private const val DATABASE_NAME = "memory-database"

class MemoryRepository private constructor(context: Context) {
    private val database : MemoryDatabase = Room.databaseBuilder(
        context.applicationContext,
        MemoryDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val memoryDao = database.memoryDao()

    fun getMemories() : LiveData<List<Memory>> = memoryDao.getMemories()
    fun getMemory(id: UUID): LiveData<Memory?> = memoryDao.getMemory(id)

    companion object {
        private var INSTANCE: MemoryRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MemoryRepository(context)
            }
        }

        fun get(): MemoryRepository {
            return INSTANCE ?: throw IllegalStateException("MemoryRepository must be initialized.")
        }
    }
}