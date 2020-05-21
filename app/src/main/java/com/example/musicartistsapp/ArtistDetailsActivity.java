package com.example.musicartistsapp;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.example.musicartistsapp.databinding.ActivityArtistDetailsBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class ArtistDetailsActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "ArtistDetails";

    public static final String ARTIST_ID = "musicartistsapp_artist_id";

    private ActivityArtistDetailsBinding activityArtistDetailsBinding;
    private DocumentReference artistReference;
    private ListenerRegistration artistListenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityArtistDetailsBinding = ActivityArtistDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityArtistDetailsBinding.getRoot());

        String artistId = getIntent().getExtras().getString(ARTIST_ID);
        artistReference = FirebaseFirestore.getInstance().collection("artists").document(artistId);
    }

    @Override
    public void onStart() {
        super.onStart();
        artistListenerRegistration = artistReference.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (artistListenerRegistration != null) {
            artistListenerRegistration.remove();
            artistListenerRegistration = null;
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, e);
            return;
        }

        ArtistModel artist = documentSnapshot.toObject(ArtistModel.class);

        Glide.with(activityArtistDetailsBinding.artistImage.getContext())
                .load(artist.getImagePath())
                .into(activityArtistDetailsBinding.artistImage);

        activityArtistDetailsBinding.artistName.setText(artist.getName());
        activityArtistDetailsBinding.artistCountry.setText(artist.getCountry());
        activityArtistDetailsBinding.artistDescription.setText(artist.getDescription());
        activityArtistDetailsBinding.artistGenres.setText(artist.getGenres().toString());
    }
}
