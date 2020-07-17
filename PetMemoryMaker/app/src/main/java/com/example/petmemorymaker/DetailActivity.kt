package com.example.petmemorymaker

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

const val EXTRA_MEMORY_ID = "com.example.petmemorymaker.memory_id"

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val memoryIdString = intent.getStringExtra(EXTRA_MEMORY_ID)
        val memoryId = UUID.fromString(memoryIdString)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_detail)

        if (currentFragment == null) {
            val fragment = MemoryFragment.newInstance(memoryId)

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container_detail, fragment)
                .commit()
        }
    }
}