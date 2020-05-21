package com.example.musicartistsapp;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements FirestoreArtistsRecyclerAdapter.OnArtistSelectedListener, FilterFragment.FilterListener, View.OnClickListener, AddArtistFragment.AddArtistListener {

    private static final String TAG = "MainActivity";


    private ActivityMainBinding activityMainBinding;
    private FirestoreArtistsRecyclerAdapter firestoreArtistsRecyclerAdapter;
    private FilterFragment filterFragment;
    private AddArtistFragment addArtistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        //
        AppDataset.getInstance().setDataset("artists");
        //

        filterFragment = new FilterFragment();
        addArtistFragment = new AddArtistFragment();

        activityMainBinding.artistsRecycler.setLayoutManager(new LinearLayoutManager(this));
        activityMainBinding.buttonFilter.setOnClickListener(this);
        activityMainBinding.buttonRemoveFilter.setOnClickListener(this);
        activityMainBinding.buttonAddArtist.setOnClickListener(this);

        FirebaseFirestore.setLoggingEnabled(true);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection(AppDataset.getInstance().getDataset());

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

    @Override
    public void onFilter(FilterModel filter) {
        Query query = FirebaseFirestore.getInstance().collection(AppDataset.getInstance().getDataset());

        if (filter.getCountry() != null) {
            query = query.whereEqualTo(ArtistModel.COUNTRY, filter.getCountry());
        }

        if (filter.getGenre() != null) {
            query = query.whereArrayContains(ArtistModel.GENRES, filter.getGenre());
        }

        firestoreArtistsRecyclerAdapter.setQuery(query);

//        mViewModel.setFilterUtil(filterUtil);
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
        }
    }

    private void onAddArtistClicked() {
        addArtistFragment.show(getSupportFragmentManager(), TAG);
    }

    private void onRemoveFilterClicked() {
        Query query = FirebaseFirestore.getInstance().collection(AppDataset.getInstance().getDataset());
        firestoreArtistsRecyclerAdapter.setQuery(query);
        filterFragment.setDefaultSelection();
    }

    private void onFilterClicked() {
        filterFragment.show(getSupportFragmentManager(), TAG);
    }

    @Override
    public void onAddArtist(ArtistModel artistModel) {
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        DocumentReference artistDocumentReference = FirebaseFirestore.getInstance().collection(AppDataset.getInstance().getDataset()).document();
        batch.set(artistDocumentReference, artistModel);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //TODO: rewrite logging - cover the tracks :)
                if (task.isSuccessful()) {
                    Log.d(TAG, "Write batch succeeded.");
                } else {
                    Log.w(TAG, "write batch failed.", task.getException());
                }
            }
        });
    }
}
