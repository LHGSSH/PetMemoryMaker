package com.example.petmemorymaker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Memory(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isFavorited: Boolean = false,
    var description: String = ""
) {
    val photoFileName
        get() = "IMG_$id.jpg"
}