package com.example.musicartistsapp

import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicartistsapp.GlobalConfig.Companion.GlobalConfigInstance
import com.example.musicartistsapp.databinding.ArtistItemBinding
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.lang.IllegalStateException
import java.util.*

class FirestoreArtistsRecyclerAdapter(private var query: Query?, private val onArtistSelectedListener: OnArtistSelectedListener) : RecyclerView.Adapter<FirestoreArtistsRecyclerAdapter.ViewHolder>(), EventListener<QuerySnapshot> {
    interface OnArtistSelectedListener {
        fun onArtistSelected(artist: DocumentSnapshot?)
    }

    private val documentSnapshots = ArrayList<DocumentSnapshot>()
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ArtistItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), onArtistSelectedListener)
    }

    override fun onEvent(documentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        if (e != null || documentSnapshots == null) {
            Log.w(TAG, e)
            return
        }
        for (change in documentSnapshots.documentChanges) {
            when (change.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(change)
                DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
                DocumentChange.Type.MODIFIED -> onDocumentModified(change)
            }
        }
    }

    fun startListening() {
        val query = query
        if (query != null && listenerRegistration == null) {
            listenerRegistration = query.addSnapshotListener(this)
        }
    }

    private fun onDocumentAdded(change: DocumentChange) {
        documentSnapshots.add(change.newIndex, change.document)
        notifyItemInserted(change.newIndex)
    }

    private fun onDocumentRemoved(change: DocumentChange) {
        documentSnapshots.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    private fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            documentSnapshots[change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else {
            documentSnapshots.removeAt(change.oldIndex)
            documentSnapshots.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    fun stopListening() {
        val listenerRegistration = listenerRegistration
        if (listenerRegistration != null) {
            listenerRegistration.remove()
            this.listenerRegistration = null
        }
        documentSnapshots.clear()
        notifyDataSetChanged()
    }

    fun setQuery(query: Query?) {
        stopListening()
        documentSnapshots.clear()
        notifyDataSetChanged()
        this.query = query
        startListening()
    }

    override fun getItemCount(): Int {
        return documentSnapshots.size
    }

    private fun getSnapshot(index: Int): DocumentSnapshot {
        return documentSnapshots[index]
    }

    class ViewHolder : RecyclerView.ViewHolder, ConfigObserver {
        private lateinit var artistItemBinding: ArtistItemBinding

        constructor(artistItemBinding: ArtistItemBinding) : super(artistItemBinding.root) {
            this.artistItemBinding = artistItemBinding
        }

        constructor(itemView: View) : super(itemView) {}

        fun bind(snapshot: DocumentSnapshot, listener: OnArtistSelectedListener?) {
            val artist = snapshot.toObject(ArtistModel::class.java) ?:
                    throw IllegalStateException("Cannot convert snapshot to artist model.")
            val resources = itemView.resources
            Glide.with(artistItemBinding.artistImage.context)
                    .load(artist.imagePath)
                    .placeholder(R.drawable.unknown_artist)
                    .into(artistItemBinding.artistImage)
            artistItemBinding.artistName.text = artist.name
            artistItemBinding.artistCountry.text = artist.country
            artistItemBinding.artistDescription.text = artist.description
            artistItemBinding.artistGenres.text = artist.genres.toString()
            itemView.setOnClickListener { listener?.onArtistSelected(snapshot) }
            GlobalConfigInstance.addObserver(this)
        }

        override fun updateConfig(fontFamily: String, fontSize: Int, backgroundColor: String) {
            artistItemBinding.artistName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize + 2.toFloat())
            artistItemBinding.artistCountry.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
            artistItemBinding.artistGenres.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize - 2.toFloat())
            artistItemBinding.artistDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
            artistItemBinding.artistName.typeface = Typeface.create(fontFamily, Typeface.BOLD)
            artistItemBinding.artistCountry.typeface = Typeface.create(fontFamily, Typeface.NORMAL)
            artistItemBinding.artistGenres.typeface = Typeface.create(fontFamily, Typeface.NORMAL)
            artistItemBinding.artistDescription.typeface = Typeface.create(fontFamily, Typeface.NORMAL)
        }
    }

    //TODO: constants
    companion object {
        private const val TAG = "FirestoreArtistsRecyclerAdapter"
    }

}