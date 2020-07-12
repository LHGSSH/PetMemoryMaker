package com.example.petmemorymaker

import java.util.*

data class Memory ( val id: UUID = UUID.randomUUID(),
                    var title: String = "",
                    var date: Date = Date(),
                    var isFavorited: Boolean = false,
                    var description: String = ""
)