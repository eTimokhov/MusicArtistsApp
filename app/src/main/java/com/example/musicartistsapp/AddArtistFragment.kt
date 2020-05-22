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
import java.util.*

class AddArtistFragment : DialogFragment(), View.OnClickListener {
    private var addArtistFragmentBinding: AddArtistFragmentBinding? = null
    private var imageUri: String? = null
    private var videoUri: String? = null
    private var storageReference: StorageReference? = null

    internal interface AddArtistListener {
        fun onAddArtist(artistModel: ArtistModel?)
    }

    private var addArtistListener: AddArtistListener? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        addArtistFragmentBinding = AddArtistFragmentBinding.inflate(inflater, container, false)
        addArtistFragmentBinding.addArtistSave.setOnClickListener(this)
        addArtistFragmentBinding.addArtistCancel.setOnClickListener(this)
        addArtistFragmentBinding.buttonImage.setOnClickListener(this)
        addArtistFragmentBinding.buttonVideo.setOnClickListener(this)
        storageReference = FirebaseStorage.getInstance().reference
        return addArtistFragmentBinding.getRoot()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addArtistFragmentBinding = null
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
        dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun onSubmitClicked() {
        val artist = ArtistModel()
        try {
            artist.name = addArtistFragmentBinding!!.artistNameEdit.text.toString()
            artist.description = addArtistFragmentBinding!!.artistDescriptionEdit.text.toString()
            artist.country = addArtistFragmentBinding!!.artistCountrySpinner.selectedItem as String
            artist.genres = Arrays.asList(addArtistFragmentBinding!!.artistGenreSpinner.selectedItem as String)
            artist.imagePath = imageUri
            artist.videoPath = videoUri
        } catch (e: Exception) {
            dismiss()
        }
        if (addArtistListener != null) {
            addArtistListener!!.onAddArtist(artist)
        }
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
        when (requestCode) {
            REQUEST_CODE_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                val imageLocalUri = data!!.data
                imageLocalUri?.let { uploadImageFromFileUri(it) }
            }
            REQUEST_CODE_VIDEO -> if (resultCode == Activity.RESULT_OK) {
                val videoLocalUri = data!!.data
                videoLocalUri?.let { uploadVideoFromFileUri(it) }
            }
        }
    }

    private fun uploadImageFromFileUri(localUri: Uri) {
        val ref = storageReference!!.child(localUri.lastPathSegment!!)
        val uploadTask = ref.putFile(localUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Toast.makeText(activity, "Error when uploading image", Toast.LENGTH_LONG).show()
            }
            ref.downloadUrl
        }.addOnCompleteListener { task -> imageUri = if (task.isSuccessful) task.result.toString() else null }
    }

    private fun uploadVideoFromFileUri(localUri: Uri) {
        val ref = storageReference!!.child(localUri.lastPathSegment!!)
        val uploadTask = ref.putFile(localUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Toast.makeText(activity, "Error when uploading video", Toast.LENGTH_LONG).show()
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            //TODO: rewrite with ternary
            videoUri = if (task.isSuccessful) task.result.toString() else null
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_artist_save -> onSubmitClicked()
            R.id.add_artist_cancel -> onCancelClicked()
            R.id.button_image -> onSelectImage()
            R.id.button_video -> onSelectVideo()
        }
    }

    companion object {
        const val TAG = "AddArtistFragment"
        private const val REQUEST_CODE_VIDEO = 2
        private const val IMAGE_URI = "image_uri"
        private const val VIDEO_URI = "video_uri"
        private const val REQUEST_CODE_IMAGE = 1
    }
}