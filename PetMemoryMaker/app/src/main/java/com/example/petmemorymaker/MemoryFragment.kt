package com.example.petmemorymaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment
import java.util.*

private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0

class MemoryFragment: Fragment(), DatePickerFragment.Callbacks {
    private lateinit var memory: Memory
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var favoriteSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memory = Memory()
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

        return view
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

        updateUI()
    }

    fun updateUI() {
        dateButton.text = memory.date.toString()
    }

    override fun onDateSelected(date: Date) {
        memory.date = date
        updateUI()
    }
}