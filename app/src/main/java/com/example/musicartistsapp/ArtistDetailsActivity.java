package com.example.musicartistsapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
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

public class ArtistDetailsActivity extends AppCompatActivity implements EventListener<DocumentSnapshot>, View.OnClickListener, ConfigObserver {

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
        artistReference = FirebaseFirestore.getInstance().collection(GlobalConfig.getInstance().getDataset()).document(artistId);

        activityArtistDetailsBinding.buttonPlayVideo.setOnClickListener(this);

        GlobalConfig.getInstance().addObserver(this);
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
                .load(artist.imagePath)
                .placeholder(R.drawable.unknown_artist)
                .into(activityArtistDetailsBinding.artistImage);

        activityArtistDetailsBinding.artistName.setText(artist.name);
        activityArtistDetailsBinding.artistCountry.setText(artist.country);
        activityArtistDetailsBinding.artistDescription.setText(artist.description);
        activityArtistDetailsBinding.artistGenres.setText(artist.genres.toString());
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
        if (artist.videoPath != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(artist.videoPath), "video/mp4");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Video not found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void updateConfig(String fontFamily, int fontSize, String backgroundColor) {
        activityArtistDetailsBinding.artistDetailsBody.setBackgroundColor(Color.parseColor(backgroundColor.toLowerCase()));

        activityArtistDetailsBinding.artistName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize + 2);
        activityArtistDetailsBinding.artistCountry.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        activityArtistDetailsBinding.artistGenres.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize - 2);
        activityArtistDetailsBinding.artistDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        activityArtistDetailsBinding.artistName.setTypeface(Typeface.create(fontFamily, Typeface.BOLD));
        activityArtistDetailsBinding.artistCountry.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));
        activityArtistDetailsBinding.artistGenres.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));
        activityArtistDetailsBinding.artistDescription.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));
    }
}
