package com.example.musicartistsapp;

import android.content.res.Resources;
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ArtistItemBinding binding;

        public ViewHolder(ArtistItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnArtistSelectedListener listener) {

            ArtistModel artist = snapshot.toObject(ArtistModel.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(binding.artistImage.getContext())
                    .load(artist.getImagePath())
                    .placeholder(R.drawable.unknown_artist)
                    .into(binding.artistImage);

            binding.artistName.setText(artist.getName());
            binding.artistCountry.setText(artist.getCountry());
            binding.artistDescription.setText(artist.getDescription());
            binding.artistGenres.setText(artist.getGenres().toString());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onArtistSelected(snapshot);
                    }
                }
            });

            //AppConfig.getInstance().addObserver(this);
            //updateFontSize(AppConfig.getInstance().getFontSize());
            //updateFontFamily(AppConfig.getInstance().getFontFamily());
        }
    }
}
