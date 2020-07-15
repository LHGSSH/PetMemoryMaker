package com.example.petmemorymaker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.petmemorymaker.Memory
import java.util.*

@Dao
interface MemoryDao {
    @Query("SELECT * FROM Memory")
    fun getMemories() : LiveData<List<Memory>>

    @Query("SELECT * FROM Memory WHERE id=(:id)")
    fun getMemory(id: UUID) : LiveData<Memory?>
}