package com.intellibins.intellibinsandroid;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intellibins.intellibinsandroid.data.fragments.list.BinListDataFragment;

import butterknife.BindView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG_ACTIVITY = "maps_activity";
    private static final String TAG_DATA_FRAGMENT_BINS = "data_fragment_bins";
    private static final String TAG_REQUEST_GET_BINS = TAG_ACTIVITY + "_get_bins";
    @BindView(R.id.layout_container)
    CoordinatorLayout mLayoutContainer;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mLayoutSwipeRefresh;
    private BinListDataFragment mListDataFragmentBins;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(savedInstanceState == null) {
            mListDataFragmentBins = BinListDataFragment.newInstance(TAG_REQUEST_GET_BINS);
            fragmentManager.beginTransaction()
                    .add(mListDataFragmentBins, TAG_DATA_FRAGMENT_BINS)
                    .commit();
            mListDataFragmentBins.refreshItems(false);
        } else {
            mListDataFragmentBins = (BinListDataFragment) fragmentManager.findFragmentByTag(TAG_DATA_FRAGMENT_BINS);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
