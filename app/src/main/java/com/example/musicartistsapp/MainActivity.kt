package com.example.musicartistsapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicartistsapp.AddArtistFragment.AddArtistListener
import com.example.musicartistsapp.FirestoreArtistsRecyclerAdapter.OnArtistSelectedListener
import com.example.musicartistsapp.GlobalConfig.Companion.GlobalConfigInstance
import com.example.musicartistsapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity(), OnArtistSelectedListener, FilterFragment.FilterListener, View.OnClickListener, AddArtistListener, ConfigObserver {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var firestoreArtistsRecyclerAdapter: FirestoreArtistsRecyclerAdapter
    private lateinit var filterFragment: FilterFragment
    private lateinit var addArtistFragment: AddArtistFragment
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalConfigInstance.dataset = resources.getString(R.string.dataset)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        activityMainBinding.artistsRecycler.layoutManager = LinearLayoutManager(this)
        activityMainBinding.buttonFilter.setOnClickListener(this)
        activityMainBinding.buttonRemoveFilter.setOnClickListener(this)
        activityMainBinding.buttonAddArtist.setOnClickListener(this)
        activityMainBinding.buttonSettings.setOnClickListener(this)
        setContentView(activityMainBinding.root)

        filterFragment = FilterFragment()
        addArtistFragment = AddArtistFragment()
        settingsFragment = SettingsFragment()

        FirebaseFirestore.setLoggingEnabled(true)
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val query: Query = firebaseFirestore.collection(GlobalConfigInstance.dataset)
        firestoreArtistsRecyclerAdapter = FirestoreArtistsRecyclerAdapter(query, this)
        activityMainBinding.artistsRecycler.adapter = firestoreArtistsRecyclerAdapter

        GlobalConfigInstance.addObserver(this)
    }

    public override fun onStart() {
        super.onStart()
        firestoreArtistsRecyclerAdapter.startListening()
    }

    public override fun onStop() {
        super.onStop()
        firestoreArtistsRecyclerAdapter.stopListening()
    }

    override fun onArtistSelected(artist: DocumentSnapshot?) {
        val intent = Intent(this, ArtistDetailsActivity::class.java)
        intent.putExtra(ArtistDetailsActivity.ARTIST_ID, artist!!.id)
        startActivity(intent)
    }

    override fun onFilter(filter: FilterModel?) {
        var query: Query = FirebaseFirestore.getInstance().collection(GlobalConfigInstance.dataset)
        if (filter!!.country != null) {
            query = query.whereEqualTo("country", filter.country)
        }
        if (filter.genre != null) {
            query = query.whereArrayContains("genres", filter.genre!!)
        }
        firestoreArtistsRecyclerAdapter.setQuery(query)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_filter -> onFilterClicked()
            R.id.button_remove_filter -> onRemoveFilterClicked()
            R.id.button_add_artist -> onAddArtistClicked()
            R.id.button_settings -> onSettingsClicked()
        }
    }

    private fun onSettingsClicked() {
        settingsFragment.show(supportFragmentManager, TAG)
    }

    private fun onAddArtistClicked() {
        addArtistFragment.show(supportFragmentManager, TAG)
    }

    private fun onRemoveFilterClicked() {
        val query: Query = FirebaseFirestore.getInstance().collection(GlobalConfigInstance.dataset)
        firestoreArtistsRecyclerAdapter.setQuery(query)
        filterFragment.setDefaultSelection()
    }

    private fun onFilterClicked() {
        filterFragment.show(supportFragmentManager, TAG)
    }

    override fun onAddArtist(artistModel: ArtistModel?) {
        val batch = FirebaseFirestore.getInstance().batch()
        val artistDocumentReference = FirebaseFirestore.getInstance().collection(GlobalConfigInstance!!.dataset!!).document()
        batch[artistDocumentReference] = artistModel!!
        batch.commit()
    }

    override fun updateConfig(fontFamily: String?, fontSize: Int, backgroundColor: String?) {
        activityMainBinding.mainScreen.setBackgroundColor(Color.parseColor(backgroundColor!!.toLowerCase()))
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}