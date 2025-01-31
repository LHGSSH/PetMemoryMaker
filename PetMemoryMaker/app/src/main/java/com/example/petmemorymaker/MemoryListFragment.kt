package com.example.petmemorymaker

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*

private const val TAG = "MemoryListFragment"

class MemoryListFragment: Fragment() {
    interface Callbacks {
        fun onMemorySelected(memoryId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var memoryRecyclerView: RecyclerView
    private var adapter: MemoryAdapter? = MemoryAdapter(emptyList())

    private val memoryListViewModel: MemoryListViewModel by lazy {
        ViewModelProviders.of(this).get(MemoryListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_memory_list, container, false)

        memoryRecyclerView = view.findViewById(R.id.memory_recycler_view) as RecyclerView
        memoryRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        memoryListViewModel.memories.observe(
            viewLifecycleOwner,
            Observer { memories ->
                memories?.let {
                    Log.i(TAG, "Got memories ${memories.size}")
                    updateUI(memories)
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_memory_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_memory -> {
                val memory = Memory()
                memoryListViewModel.addMemory(memory)
                callbacks?.onMemorySelected(memory.id)
                true
            }
            R.id.show_favorited_memories -> {
                memoryListViewModel.favoritedMemories.observe(
                    viewLifecycleOwner,
                    Observer { favoritedMemories ->
                        favoritedMemories?.let {
                            Log.i(TAG, "Got favorited memories ${favoritedMemories.size}")
                            updateUI(favoritedMemories)
                        }
                    }
                )
                true
            }
            R.id.show_all_memories -> {
                memoryListViewModel.memories.observe(
                    viewLifecycleOwner,
                    Observer { memories ->
                        memories?.let {
                            Log.i(TAG, "Got memories ${memories.size}")
                            updateUI(memories)
                        }
                    }
                )
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(memories: List<Memory>) {
        adapter = MemoryAdapter(memories)
        memoryRecyclerView.adapter = adapter
    }

    private inner class MemoryAdapter(var memories: List<Memory>): RecyclerView.Adapter<MemoryHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryHolder {
            val view = layoutInflater.inflate(R.layout.list_item_view, parent, false)
            return MemoryHolder(view)
        }

        override fun getItemCount() = memories.size

        override fun onBindViewHolder(holder: MemoryHolder, position: Int) {
            val memory = memories[position]
            holder.bind(memory)
        }
    }

    private inner class MemoryHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var memory: Memory
        private val titleTextView: TextView = itemView.findViewById(R.id.memory_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.memory_date)
        private val favoritedImageView: ImageView = itemView.findViewById(R.id.favorited)
        private lateinit var photoFile: File
        private lateinit var photoUri: Uri
        private val photoView: ImageView = itemView.findViewById(R.id.memory_list_photo)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(memory: Memory) {
            this.memory = memory
            titleTextView.text = this.memory.title
            dateTextView.text = this.memory.date.toString()

            photoFile = memoryListViewModel.getPhotoFile(memory)
            photoUri = FileProvider.getUriForFile(
                requireActivity(),
                "com.example.petmemorymaker.fileprovider",
                photoFile
            )
            updatePhotoView()

            favoritedImageView.visibility = if (memory.isFavorited) {
                View.VISIBLE
            }
            else {
                View.GONE
            }
        }

        private fun updatePhotoView() {
            if (photoFile.exists()) {
                val bitmap = getScaledBitmap(photoFile.path, requireActivity())
                photoView.setImageBitmap(bitmap)
            } else {
                photoView.setImageBitmap(null)
            }
        }

        override fun onClick(view: View) {
            callbacks?.onMemorySelected(memory.id)
        }
    }

    companion object {
        fun newInstance() : MemoryListFragment {
            return MemoryListFragment()
        }
    }
}