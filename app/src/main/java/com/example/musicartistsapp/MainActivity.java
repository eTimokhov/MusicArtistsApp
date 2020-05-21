package com.example.musicartistsapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.musicartistsapp.databinding.ActivityMainBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity implements FirestoreArtistsRecyclerAdapter.OnArtistSelectedListener {

    private ActivityMainBinding activityMainBinding;
    private FirestoreArtistsRecyclerAdapter firestoreArtistsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.artistsRecycler.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore.setLoggingEnabled(true);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("artists");

        FirestoreRecyclerOptions<ArtistModel> options = new FirestoreRecyclerOptions.Builder<ArtistModel>().setQuery(query, ArtistModel.class).build();

        firestoreArtistsRecyclerAdapter = new FirestoreArtistsRecyclerAdapter(query, this);

        activityMainBinding.artistsRecycler.setAdapter(firestoreArtistsRecyclerAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreArtistsRecyclerAdapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        firestoreArtistsRecyclerAdapter.stopListening();
    }

    @Override
    public void onArtistSelected(DocumentSnapshot artist) {
        Intent intent = new Intent(this, ArtistDetailsActivity.class);
        intent.putExtra(ArtistDetailsActivity.ARTIST_ID, artist.getId());
        startActivity(intent);
    }
}
