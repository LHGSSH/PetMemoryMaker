package com.example.petmemorymaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), MemoryListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_main)

        if (currentFragment == null) {
            val fragment = MemoryListFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container_main, fragment)
                .commit()
        }
    }

    override fun onMemorySelected(memoryId: UUID) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(EXTRA_MEMORY_ID, memoryId.toString())
        }
        startActivity(intent)
    }
}