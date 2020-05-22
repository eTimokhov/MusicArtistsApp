package com.example.musicartistsapp;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.musicartistsapp.databinding.ArtistItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class FirestoreArtistsRecyclerAdapter extends FirestoreAdapter<FirestoreArtistsRecyclerAdapter.ViewHolder> {
    public interface OnArtistSelectedListener {
        void onArtistSelected(DocumentSnapshot artist);
    }

    private OnArtistSelectedListener onArtistSelectedListener;

    public FirestoreArtistsRecyclerAdapter(Query query, OnArtistSelectedListener listener) {
        super(query);
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
                    .load(artist.getImagePath())
                    .placeholder(R.drawable.unknown_artist)
                    .into(artistItemBinding.artistImage);

            artistItemBinding.artistName.setText(artist.getName());
            artistItemBinding.artistCountry.setText(artist.getCountry());
            artistItemBinding.artistDescription.setText(artist.getDescription());
            artistItemBinding.artistGenres.setText(artist.getGenres().toString());

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
