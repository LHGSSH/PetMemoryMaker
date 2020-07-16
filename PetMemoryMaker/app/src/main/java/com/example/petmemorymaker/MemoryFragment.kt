package com.example.petmemorymaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.text.format.DateFormat
import java.util.*

private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val ARG_MEMORY_ID = "memory_id"

class MemoryFragment: Fragment(), DatePickerFragment.Callbacks {
    private lateinit var memory: Memory
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var favoriteSwitch: Switch
    private lateinit var descriptionField: EditText
    private val memoryDetailViewModel: MemoryDetailViewModel by lazy {
        ViewModelProviders.of(this).get(MemoryDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memory = Memory()
        val memoryId: UUID = arguments?.getSerializable(ARG_MEMORY_ID) as UUID
        memoryDetailViewModel.loadMemory(memoryId)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_memory, container, false)

        titleField = view.findViewById(R.id.memory_title) as EditText
        dateButton = view.findViewById(R.id.memory_date) as Button
        favoriteSwitch = view.findViewById(R.id.favorite_switch) as Switch
        descriptionField = view.findViewById(R.id.memory_description) as EditText

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_memory, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share_memory -> {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getSharedMemory())
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_memory_subject))
                }.also { intent ->
                    val chooserIntent = Intent.createChooser(intent, getString(R.string.share_memory_dialog))
                    startActivity(chooserIntent)
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        memoryDetailViewModel.memoryData.observe(
            viewLifecycleOwner,
            Observer {
                memory ->
                memory?.let {
                    this.memory = memory
                    updateUI()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object: TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?,
                                           start: Int,
                                           count: Int,
                                           after: Int) {
                //This is left blank
            }

            override fun onTextChanged(sequence: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                memory.title = sequence.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //This is left blank
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        favoriteSwitch.apply {
            setOnCheckedChangeListener { _, isChecked -> memory.isFavorited = isChecked }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(memory.date).apply {
                setTargetFragment(this@MemoryFragment, REQUEST_DATE)
                show(this@MemoryFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        val descriptionWatcher = object: TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?,
                                           start: Int,
                                           count: Int,
                                           after: Int) {
                //This is left blank
            }

            override fun onTextChanged(sequence: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                memory.description = sequence.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //This is left blank
            }
        }
        descriptionField.addTextChangedListener(descriptionWatcher)

        updateUI()
    }

    override fun onStop() {
        super.onStop()
        memoryDetailViewModel.saveMemory(memory)
    }

    fun updateUI() {
        titleField.setText(memory.title)
        dateButton.text = memory.date.toString()
        favoriteSwitch.apply {
            isChecked = memory.isFavorited
            jumpDrawablesToCurrentState()
        }
        descriptionField.setText(memory.description)
    }

    override fun onDateSelected(date: Date) {
        memory.date = date
        updateUI()
    }

    private fun getSharedMemory(): String {
        val titleString = memory.title

        val dateString = DateFormat.format(DATE_FORMAT, memory.date).toString()

        val descriptionString = memory.description

        return getString(R.string.share_memory, titleString, dateString, descriptionString)
    }

    companion object {
        fun newInstance(memoryId: UUID) : MemoryFragment {
            val args = Bundle().apply {
                putSerializable(ARG_MEMORY_ID,  memoryId)
            }
            return MemoryFragment().apply {
                arguments = args
            }
        }
    }
}