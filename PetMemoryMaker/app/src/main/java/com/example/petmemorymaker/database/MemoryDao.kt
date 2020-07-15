package com.example.petmemorymaker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.petmemorymaker.Memory
import java.util.*

@Dao
interface MemoryDao {
    @Query("SELECT * FROM Memory")
    fun getMemories() : LiveData<List<Memory>>

    @Query("SELECT * FROM Memory WHERE id=(:id)")
    fun getMemory(id: UUID) : LiveData<Memory?>

    @Update
    fun updateMemory(memory: Memory)

    @Insert
    fun addMemory(memory: Memory)
}