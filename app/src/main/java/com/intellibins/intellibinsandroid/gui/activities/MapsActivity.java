package com.intellibins.intellibinsandroid.gui.activities;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intellibins.intellibinsandroid.ErrorRetry;
import com.intellibins.intellibinsandroid.MiscHelper;
import com.intellibins.intellibinsandroid.R;
import com.intellibins.intellibinsandroid.data.fragments.BinsDataFragment;
import com.intellibins.intellibinsandroid.gui.OnBinsRetrieved;
import com.intellibins.intellibinsandroid.gui.OnClosestBinRetrieved;
import com.intellibins.intellibinsandroid.restdata.BinInformation;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements LocationListener,OnMapReadyCallback, OnBinsRetrieved, OnClosestBinRetrieved {
    private static final String TAG_ACTIVITY = "maps_activity";
    private static final String TAG_DATA_FRAGMENT_BINS = "data_fragment_bins";
    private static final String TAG_DATA_FRAGMENT_CLOSEST = "data_fragment_closest";
    private static final String TAG_REQUEST_GET_BINS = TAG_ACTIVITY + "_get_bins";
    private static final String TAG_REQUEST_CLOSEST_BIN = TAG_ACTIVITY + "_closest_bin";
    private static final String ARG_SORT_TYPE = "sort_type";
    private static final int CODE_GET_GPS_PERMISSION = 0;
    private static final int CODE_REFRESH = 1;
    private int mSortType = 0;
    @BindView(R.id.nav)
    NavigationView mNavigationView;
    @BindView(R.id.layout_container)
    CoordinatorLayout mLayoutContainer;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.layout_drawer)
    DrawerLayout mLayoutDrawer;
    View RAWR_ID;
    View test;
    TextView mTextSortBy;
    ImageView mImagePin;
    TextView mTextHeaderTitle;
    TextView mTextHeaderMessage;
    private BinsDataFragment mDataFragmentBins;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Timer mTimer;
    public void changeSort(BinInformation[] bins) {
        mSortType = (mSortType + 1) % 3;
        sortBins(bins);
    }
    public String sortByTitle() {
        switch (mSortType) {
            case 0:
                return "Fullness";
            case 1:
                return "ID";
            case 2:
                return "Distance";
            case 3:
                return "Magic";
        }
        return "Error";
    }
    public void sortBins(BinInformation[] bins) {
        switch (mSortType) {
            case 0:
                Arrays.sort(bins, (binInformation, t1) ->  t1.full - binInformation.full);
                break;
            case 1:
                Arrays.sort(bins, (binInformation, t1) ->  t1.id - binInformation.id);
                break;
            case 2:
                Arrays.sort(bins, (binInforamtion, t1) -> {
                    float distance1 = binInforamtion.getLocation().distanceTo(getCurrentLocation());
                    float distance2 = t1.getLocation().distanceTo(getCurrentLocation());
                    return Math.round(distance1 - distance2);
                });
                break;
        }
        mTextSortBy.setText("Sort by: " + sortByTitle());
        repopulateNavigation(bins);
    }

    public void recalculateClosest(Location location) {
        BinInformation[] bins = mDataFragmentBins.getServerData();
        if(bins != null) {
            BinInformation smallestBin = bins[0];
            Location currentLocation = getCurrentLocation();
            float smallestDistance = smallestBin.getLocation().distanceTo(getCurrentLocation());
            for(BinInformation bin : bins) {
                float distance = bin.getLocation().distanceTo(currentLocation);
                if(smallestDistance > distance && bin.full >= 75) {
                    smallestDistance = distance;
                    smallestBin = bin;
                }
            }
            if(smallestBin.full >= 75) {
                Drawable drawable = AppCompatResources.getDrawable(this, R.drawable.ic_marker);
                drawable.setColorFilter(getBlendedColour(smallestBin.full), PorterDuff.Mode.SRC_IN);
                mImagePin.setImageDrawable(drawable);
                mTextHeaderTitle.setText(MessageFormat.format("Bin {1} is {2}% full",
                        Integer.toString(smallestBin.getId()),
                        Integer.toString(smallestBin.full)
                ));
                mTextHeaderMessage.setText("Click to navigate to this bin");
                BinInformation finalSmallestBin = smallestBin;
                RAWR_ID.setOnClickListener(v-> {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(finalSmallestBin.coord[0], finalSmallestBin.coord[1]));
                    if(mMap != null) {
                        mMap.moveCamera(cameraUpdate);
                        mLayoutDrawer.closeDrawer(GravityCompat.START);
                    }
                });
            } else {
                RAWR_ID.setOnClickListener(null);
                mImagePin.setImageDrawable(null);
                mTextHeaderTitle.setText("No near-full bins!");
                mTextHeaderMessage.setText("No bins urgently need cleaning");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        mNavigationView.setItemIconTintList(null);
        View navigationHeader = mNavigationView.getHeaderView(0);
        RAWR_ID = navigationHeader.findViewById(R.id.RAWR_ID);
        test = navigationHeader.findViewById(R.id.test);
        mTextSortBy = (TextView) navigationHeader.findViewById(R.id.text_sort_by);
        mTextHeaderMessage = (TextView) navigationHeader.findViewById(R.id.text_header_message);
        mTextHeaderTitle = (TextView) navigationHeader.findViewById(R.id.text_header_title);
        mImagePin = (ImageView) navigationHeader.findViewById(R.id.image_closest_pin);
        this.setSupportActionBar(mToolbar);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (savedInstanceState == null) {
            mDataFragmentBins = BinsDataFragment.newInstance(TAG_REQUEST_GET_BINS);
            fragmentManager.beginTransaction()
                    .add(mDataFragmentBins, TAG_DATA_FRAGMENT_BINS)
                    .commit();
            mDataFragmentBins.refreshData(false);
        } else {
            mSortType = savedInstanceState.getInt(ARG_SORT_TYPE);
            mDataFragmentBins = (BinsDataFragment) fragmentManager.findFragmentByTag(TAG_DATA_FRAGMENT_BINS);
        }

        if (mLayoutDrawer != null) {
            ActionBarDrawerToggle toggle =
                    new ActionBarDrawerToggle(
                            this,
                            mLayoutDrawer,
                            mToolbar,
                            R.string.navigation_drawer_open,
                            R.string.navigation_drawer_close
                    );
            mLayoutDrawer.addDrawerListener(toggle);
            toggle.syncState();
        }
        mDataFragmentBins.setRefreshErrorHandler(this::handleRetriableError);

    }

    public void repopulateNavigation(BinInformation[] bins) {
        Menu menu = mNavigationView.getMenu();
        menu.clear();
        mNavigationView.inflateMenu(R.menu.menu_activity_main_bins);
        for(BinInformation bin : bins) {
            MenuItem menuItem = menu.add(
                    R.id.group_bins,
                    bin.getId(),
                    0,
                    MessageFormat.format(
                            "Bin {0}: {1}%",
                            Integer.toString(bin.getId()),
                            Integer.toString(bin.full)
                    )
            );
            Drawable drawable = AppCompatResources.getDrawable(this, R.drawable.ic_marker);
            drawable = drawable.mutate();
            drawable.setColorFilter(getBlendedColour(bin.full), PorterDuff.Mode.SRC_IN);
            menuItem.setIcon(drawable);
        }
    }

    public void handleRetriableError(String errorMessage, String actionMessage, ErrorRetry errorRetry) {
        Snackbar.make(mLayoutContainer, errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionMessage, v -> errorRetry.errorRetry())
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(ARG_SORT_TYPE, mSortType);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public Location getCurrentLocation() {
        return mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), true));
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
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        if (MiscHelper.isPermitted(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
            MiscHelper.isPermitted(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mMap.setMyLocationEnabled(true); // Ignore this
            Location location = getCurrentLocation();
            if(location != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20);
                mMap.moveCamera(cameraUpdate);
                onLocationChanged(location);
            }
        } else{
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    CODE_GET_GPS_PERMISSION
            );
        }
    }

    public int getBlendedColour(int fullness) {
        float hsv[] = new float[3];
        Color.colorToHSV(Color.parseColor("#F44336"), hsv);
        hsv[0] += 90 * (1.0 - Math.max(0, Math.min(1, ((double)fullness)/100.0)));
        return Color.HSVToColor(hsv);
    }

    @Override
    public void onBinsRetrieved(BinInformation[] bins) {
        if(mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        mDataFragmentBins.refreshData(true);
                    } catch(Exception ex) {
                        Log.e("EASTEREGGGS?", "YAY FOR HACKS", ex);
                    }
                }
            }, 10000, 10000);
        }
        recalculateClosest(getCurrentLocation());
        View navigationHeader = mNavigationView.getHeaderView(0);
        test.setOnClickListener(v -> changeSort(bins));
        sortBins(bins);
        DecimalFormat longLatFormat = new DecimalFormat("#.###");
        if(mMap != null) {
            mMap.clear();
        }
        for(BinInformation bin : bins){
            MarkerOptions markerOptions =
                    new MarkerOptions()
                    .position(new LatLng(bin.coord[0], bin.coord[1]))
                    .title(
                            MessageFormat.format(
                                    "Accept job ({0}% full)",
                                    Integer.toString(bin.full)
                                    )
                    );
            if(mMap != null) {
                VectorDrawableCompat markerDrawable = (VectorDrawableCompat) AppCompatResources.getDrawable(this, R.drawable.ic_marker);
                markerDrawable = (VectorDrawableCompat) markerDrawable.mutate();
                markerDrawable.setColorFilter(getBlendedColour(bin.full), PorterDuff.Mode.SRC_IN);
                Bitmap bitmapMarker = Bitmap.createBitmap(
                        markerDrawable.getIntrinsicWidth(),
                        markerDrawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapMarker);
                markerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                markerDrawable.draw(canvas);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmapMarker));
                mMap.addMarker(markerOptions);
            }
        }
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        mLayoutDrawer.closeDrawer(GravityCompat.START);
                        int binId = item.getItemId();
                        for(BinInformation bin : bins) {
                            if(bin.getId() == binId) {
                                CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(bin.coord[0], bin.coord[1]));
                                mMap.moveCamera(update);
                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
    }


    @Override
    public void onClosestBinIdRetrieved(BinInformation id) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mSortType == 2) {
            sortBins(mDataFragmentBins.getServerData());
        }
        recalculateClosest(location);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer = null;
    }
}
