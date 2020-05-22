package com.example.musicartistsapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.musicartistsapp.databinding.ActivityMainBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

public class MainActivity extends AppCompatActivity implements FirestoreArtistsRecyclerAdapter.OnArtistSelectedListener, FilterFragment.FilterListener, View.OnClickListener, AddArtistFragment.AddArtistListener, ConfigObserver {

    private static final String TAG = "MainActivity";


    private ActivityMainBinding activityMainBinding;
    private FirestoreArtistsRecyclerAdapter firestoreArtistsRecyclerAdapter;
    private FilterFragment filterFragment;
    private AddArtistFragment addArtistFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        GlobalConfig.getInstance().setDataset(getResources().getString(R.string.dataset));

        filterFragment = new FilterFragment();
        addArtistFragment = new AddArtistFragment();
        settingsFragment = new SettingsFragment();

        activityMainBinding.artistsRecycler.setLayoutManager(new LinearLayoutManager(this));
        activityMainBinding.buttonFilter.setOnClickListener(this);
        activityMainBinding.buttonRemoveFilter.setOnClickListener(this);
        activityMainBinding.buttonAddArtist.setOnClickListener(this);
        activityMainBinding.buttonSettings.setOnClickListener(this);

        FirebaseFirestore.setLoggingEnabled(true);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection(GlobalConfig.getInstance().getDataset());

        firestoreArtistsRecyclerAdapter = new FirestoreArtistsRecyclerAdapter(query, this);

        activityMainBinding.artistsRecycler.setAdapter(firestoreArtistsRecyclerAdapter);


        GlobalConfig.getInstance().addObserver(this);
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

    @Override
    public void onFilter(FilterModel filter) {
        Query query = FirebaseFirestore.getInstance().collection(GlobalConfig.getInstance().getDataset());

        if (filter.getCountry() != null) {
            query = query.whereEqualTo("country", filter.getCountry());
        }

        if (filter.getGenre() != null) {
            query = query.whereArrayContains("genres", filter.getGenre());
        }

        firestoreArtistsRecyclerAdapter.setQuery(query);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_filter:
                onFilterClicked();
                break;
            case R.id.button_remove_filter:
                onRemoveFilterClicked();
                break;
            case R.id.button_add_artist:
                onAddArtistClicked();
                break;
            case R.id.button_settings:
                onSettingsClicked();
                break;
        }
    }

    private void onSettingsClicked() {
        settingsFragment.show(getSupportFragmentManager(), TAG);
    }

    private void onAddArtistClicked() {
        addArtistFragment.show(getSupportFragmentManager(), TAG);
    }

    private void onRemoveFilterClicked() {
        Query query = FirebaseFirestore.getInstance().collection(GlobalConfig.getInstance().getDataset());
        firestoreArtistsRecyclerAdapter.setQuery(query);
        filterFragment.setDefaultSelection();
    }

    private void onFilterClicked() {
        filterFragment.show(getSupportFragmentManager(), TAG);
    }

    @Override
    public void onAddArtist(ArtistModel artistModel) {
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        DocumentReference artistDocumentReference = FirebaseFirestore.getInstance().collection(GlobalConfig.getInstance().getDataset()).document();
        batch.set(artistDocumentReference, artistModel);

        batch.commit();
    }

    @Override
    public void updateConfig(String fontFamily, int fontSize, String backgroundColor) {
        activityMainBinding.mainScreen.setBackgroundColor(Color.parseColor(backgroundColor.toLowerCase()));
    }
}
