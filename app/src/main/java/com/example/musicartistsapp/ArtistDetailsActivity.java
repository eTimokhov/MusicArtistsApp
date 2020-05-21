package com.example.musicartistsapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
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

import java.io.IOException;

public class ArtistDetailsActivity extends AppCompatActivity implements EventListener<DocumentSnapshot>, View.OnClickListener {

    private static final String TAG = "ArtistDetails";

    public static final String ARTIST_ID = "musicartistsapp_artist_id";

    private ActivityArtistDetailsBinding activityArtistDetailsBinding;
    private DocumentReference artistReference;
    private ArtistModel artist;
    private ListenerRegistration artistListenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityArtistDetailsBinding = ActivityArtistDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityArtistDetailsBinding.getRoot());

        String artistId = getIntent().getExtras().getString(ARTIST_ID);
        artistReference = FirebaseFirestore.getInstance().collection(AppDataset.getInstance().getDataset()).document(artistId);

        activityArtistDetailsBinding.buttonPlayVideo.setOnClickListener(this);
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

        artist = documentSnapshot.toObject(ArtistModel.class);

        Glide.with(activityArtistDetailsBinding.artistImage.getContext())
                .load(artist.getImagePath())
                .into(activityArtistDetailsBinding.artistImage);

        activityArtistDetailsBinding.artistName.setText(artist.getName());
        activityArtistDetailsBinding.artistCountry.setText(artist.getCountry());
        activityArtistDetailsBinding.artistDescription.setText(artist.getDescription());
        activityArtistDetailsBinding.artistGenres.setText(artist.getGenres().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_play_video:
                onPlayVideoClicked();
                break;
        }
    }

    private void onPlayVideoClicked() {
        if (artist.getVideoPath() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(artist.getVideoPath()), "video/mp4");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Video not found", Toast.LENGTH_LONG).show();
        }
    }
}
