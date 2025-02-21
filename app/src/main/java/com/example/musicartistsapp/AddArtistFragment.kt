package com.example.musicartistsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.musicartistsapp.databinding.AddArtistFragmentBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.IllegalStateException

class AddArtistFragment : DialogFragment(), View.OnClickListener {
    private val REQUEST_CODE_IMAGE = 1
    private val REQUEST_CODE_VIDEO = 2

    private lateinit var addArtistFragmentBinding: AddArtistFragmentBinding
    private lateinit var storageReference: StorageReference

    private var imageUri: String? = null
    private var videoUri: String? = null

    interface AddArtistListener {
        fun onAddArtist(artistModel: ArtistModel)
    }

    private lateinit var addArtistListener: AddArtistListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        addArtistFragmentBinding = AddArtistFragmentBinding.inflate(inflater, container, false)
        addArtistFragmentBinding.addArtistSave.setOnClickListener(this)
        addArtistFragmentBinding.addArtistCancel.setOnClickListener(this)
        addArtistFragmentBinding.buttonImage.setOnClickListener(this)
        addArtistFragmentBinding.buttonVideo.setOnClickListener(this)
        storageReference = FirebaseStorage.getInstance().reference
        return addArtistFragmentBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageUri = null
        videoUri = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddArtistListener) {
            addArtistListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: throw IllegalStateException("Cannot retrieve dialog window")
        window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun onSubmitClicked() {
        val artist = ArtistModel()
        try {
            artist.name = addArtistFragmentBinding.artistNameEdit.text.toString()
            artist.description = addArtistFragmentBinding.artistDescriptionEdit.text.toString()
            artist.country = addArtistFragmentBinding.artistCountrySpinner.selectedItem as String
            artist.genres = listOf(addArtistFragmentBinding.artistGenreSpinner.selectedItem as String)
            artist.imagePath = imageUri
            artist.videoPath = videoUri
        } catch (e: Exception) {
            dismiss()
        }
        addArtistListener.onAddArtist(artist)
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

    private fun onSelectImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    private fun onSelectVideo() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "video/*"
        startActivityForResult(intent, REQUEST_CODE_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                    val imageLocalUri = data.data
                    imageLocalUri?.let { uploadImageFromFileUri(it) }
                }
                REQUEST_CODE_VIDEO -> if (resultCode == Activity.RESULT_OK) {
                    val videoLocalUri = data.data
                    videoLocalUri?.let { uploadVideoFromFileUri(it) }
                }
            }
        }
    }

    private fun uploadImageFromFileUri(localUri: Uri) {
        val lastPathSegment = localUri.lastPathSegment
        if (lastPathSegment == null) {
            Toast.makeText(activity, "Cannot upload image", Toast.LENGTH_LONG).show()
            return
        }
        val ref = storageReference.child(lastPathSegment)
        val uploadTask = ref.putFile(localUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Toast.makeText(activity, "Error when uploading image", Toast.LENGTH_LONG).show()
            }
            ref.downloadUrl
        }.addOnCompleteListener { task -> imageUri = if (task.isSuccessful) task.result.toString() else null }
    }

    private fun uploadVideoFromFileUri(localUri: Uri) {
        val lastPathSegment = localUri.lastPathSegment
        if (lastPathSegment == null) {
            Toast.makeText(activity, "Cannot upload video", Toast.LENGTH_LONG).show()
            return
        }
        val ref = storageReference.child(lastPathSegment)
        val uploadTask = ref.putFile(localUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Toast.makeText(activity, "Error when uploading video", Toast.LENGTH_LONG).show()
            }
            ref.downloadUrl
        }.addOnCompleteListener { task -> videoUri = if (task.isSuccessful) task.result.toString() else null }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_artist_save -> onSubmitClicked()
            R.id.add_artist_cancel -> onCancelClicked()
            R.id.button_image -> onSelectImage()
            R.id.button_video -> onSelectVideo()
        }
    }
}
