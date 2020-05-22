package com.example.musicartistsapp;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.musicartistsapp.databinding.ArtistItemBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FirestoreArtistsRecyclerAdapter extends RecyclerView.Adapter<FirestoreArtistsRecyclerAdapter.ViewHolder> implements EventListener<QuerySnapshot> {
    private static final String TAG = "FirestoreArtistsRecyclerAdapter";

    public interface OnArtistSelectedListener {
        void onArtistSelected(DocumentSnapshot artist);

    }
    private OnArtistSelectedListener onArtistSelectedListener;
    private ArrayList<DocumentSnapshot> documentSnapshots = new ArrayList<>();

    private Query query;
    private ListenerRegistration listenerRegistration;



    public FirestoreArtistsRecyclerAdapter(Query query, OnArtistSelectedListener listener) {
        super();
        this.query = query;
        onArtistSelectedListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ArtistItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), onArtistSelectedListener);
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, e);
            return;
        }

        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
            }
        }
    }

    public void startListening() {
        if (query != null && listenerRegistration == null) {
            listenerRegistration = query.addSnapshotListener(this);
        }
    }

    private void onDocumentAdded(DocumentChange change) {
        documentSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    private void onDocumentRemoved(DocumentChange change) {
        documentSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    private void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            documentSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            documentSnapshots.remove(change.getOldIndex());
            documentSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }

        documentSnapshots.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query) {
        stopListening();
        documentSnapshots.clear();
        notifyDataSetChanged();
        this.query = query;
        startListening();
    }

    @Override
    public int getItemCount() {
        return documentSnapshots.size();
    }

    private DocumentSnapshot getSnapshot(int index) {
        return documentSnapshots.get(index);
    }



    static class ViewHolder extends RecyclerView.ViewHolder implements ConfigObserver {

        private ArtistItemBinding artistItemBinding;

        public ViewHolder(ArtistItemBinding artistItemBinding) {
            super(artistItemBinding.getRoot());
            this.artistItemBinding = artistItemBinding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnArtistSelectedListener listener) {

            ArtistModel artist = snapshot.toObject(ArtistModel.class);
            Resources resources = itemView.getResources();

            Glide.with(artistItemBinding.artistImage.getContext())
                    .load(artist.imagePath)
                    .placeholder(R.drawable.unknown_artist)
                    .into(artistItemBinding.artistImage);

            artistItemBinding.artistName.setText(artist.name);
            artistItemBinding.artistCountry.setText(artist.country);
            artistItemBinding.artistDescription.setText(artist.description);
            artistItemBinding.artistGenres.setText(artist.genres.toString());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onArtistSelected(snapshot);
                    }
                }
            });

            GlobalConfig.getInstance().addObserver(this);
        }

        @Override
        public void updateConfig(String fontFamily, int fontSize, String backgroundColor) {
            artistItemBinding.artistName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize + 2);
            artistItemBinding.artistCountry.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            artistItemBinding.artistGenres.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize - 2);
            artistItemBinding.artistDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

            artistItemBinding.artistName.setTypeface(Typeface.create(fontFamily, Typeface.BOLD));
            artistItemBinding.artistCountry.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));
            artistItemBinding.artistGenres.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));
            artistItemBinding.artistDescription.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));
        }
    }
}
