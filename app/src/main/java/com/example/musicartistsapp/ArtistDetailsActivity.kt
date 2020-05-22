package com.example.musicartistsapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.musicartistsapp.GlobalConfig.Companion.instance
import com.example.musicartistsapp.databinding.ActivityArtistDetailsBinding
import com.google.firebase.firestore.*

class ArtistDetailsActivity : AppCompatActivity(), EventListener<DocumentSnapshot?>, View.OnClickListener, ConfigObserver {
    private var activityArtistDetailsBinding: ActivityArtistDetailsBinding? = null
    private var artistReference: DocumentReference? = null
    private var artist: ArtistModel? = null
    private var artistListenerRegistration: ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityArtistDetailsBinding = ActivityArtistDetailsBinding.inflate(layoutInflater)
        setContentView(activityArtistDetailsBinding?.getRoot())
        val artistId = intent.extras!!.getString(ARTIST_ID)
        artistReference = FirebaseFirestore.getInstance().collection(instance!!.dataset!!).document(artistId!!)
        activityArtistDetailsBinding?.buttonPlayVideo?.setOnClickListener(this)
        instance!!.addObserver(this)
    }

    public override fun onStart() {
        super.onStart()
        artistListenerRegistration = artistReference!!.addSnapshotListener(this)
    }

    public override fun onStop() {
        super.onStop()
        if (artistListenerRegistration != null) {
            artistListenerRegistration!!.remove()
            artistListenerRegistration = null
        }
    }

    override fun onEvent(documentSnapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(TAG, e)
            return
        }
        artist = documentSnapshot!!.toObject(ArtistModel::class.java)
        Glide.with(activityArtistDetailsBinding!!.artistImage.context)
                .load(artist!!.imagePath)
                .placeholder(R.drawable.unknown_artist)
                .into(activityArtistDetailsBinding!!.artistImage)
        activityArtistDetailsBinding!!.artistName.text = artist!!.name
        activityArtistDetailsBinding!!.artistCountry.text = artist!!.country
        activityArtistDetailsBinding!!.artistDescription.text = artist!!.description
        activityArtistDetailsBinding!!.artistGenres.text = artist!!.genres.toString()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_play_video -> onPlayVideoClicked()
        }
    }

    private fun onPlayVideoClicked() {
        if (artist!!.videoPath != null) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(artist!!.videoPath), "video/mp4")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Video not found", Toast.LENGTH_LONG).show()
        }
    }

    override fun updateConfig(fontFamily: String?, fontSize: Int, backgroundColor: String?) {
        activityArtistDetailsBinding!!.artistDetailsBody.setBackgroundColor(Color.parseColor(backgroundColor!!.toLowerCase()))
        activityArtistDetailsBinding!!.artistName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize + 2.toFloat())
        activityArtistDetailsBinding!!.artistCountry.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        activityArtistDetailsBinding!!.artistGenres.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize - 2.toFloat())
        activityArtistDetailsBinding!!.artistDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        activityArtistDetailsBinding!!.artistName.typeface = Typeface.create(fontFamily, Typeface.BOLD)
        activityArtistDetailsBinding!!.artistCountry.typeface = Typeface.create(fontFamily, Typeface.NORMAL)
        activityArtistDetailsBinding!!.artistGenres.typeface = Typeface.create(fontFamily, Typeface.NORMAL)
        activityArtistDetailsBinding!!.artistDescription.typeface = Typeface.create(fontFamily, Typeface.NORMAL)
    }

    companion object {
        private const val TAG = "ArtistDetails"
        const val ARTIST_ID = "musicartistsapp_artist_id"
    }
}