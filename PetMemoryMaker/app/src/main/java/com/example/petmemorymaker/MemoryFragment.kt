package com.example.petmemorymaker

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.hardware.camera2.CameraAccessException
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.text.format.DateFormat
import android.widget.*
import androidx.core.content.FileProvider
import java.io.File
import java.util.*

private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_PHOTO = 1
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val ARG_MEMORY_ID = "memory_id"

class MemoryFragment : Fragment(), DatePickerFragment.Callbacks {
    private lateinit var memory: Memory
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var favoriteSwitch: Switch
    private lateinit var descriptionField: EditText
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView

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
        photoButton = view.findViewById(R.id.memory_camera) as ImageButton
        photoView = view.findViewById(R.id.memory_photo) as ImageView

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
                    val chooserIntent =
                        Intent.createChooser(intent, getString(R.string.share_memory_dialog))
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
            Observer { memory ->
                memory?.let {
                    this.memory = memory
                    photoFile = memoryDetailViewModel.getPhotoFile(memory)
                    photoUri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.example.petmemorymaker.fileprovider",
                        photoFile
                    )
                    updateUI()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //This is left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
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

        val descriptionWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //This is left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                memory.description = sequence.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //This is left blank
            }
        }
        descriptionField.addTextChangedListener(descriptionWatcher)

        photoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager

            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(
                    captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }

                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        memoryDetailViewModel.saveMemory(memory)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(
            photoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    fun updateUI() {
        titleField.setText(memory.title)
        dateButton.text = memory.date.toString()
        favoriteSwitch.apply {
            isChecked = memory.isFavorited
            jumpDrawablesToCurrentState()
        }
        descriptionField.setText(memory.description)
        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageBitmap(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                updatePhotoView()
            }
        }
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
        fun newInstance(memoryId: UUID): MemoryFragment {
            val args = Bundle().apply {
                putSerializable(ARG_MEMORY_ID, memoryId)
            }
            return MemoryFragment().apply {
                arguments = args
            }
        }
    }
}