package mz.co.insystems.trackingservice.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.model.CommandResponse;
import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.util.AppConfig;
import mz.co.insystems.trackingservice.util.GPSTracker;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private static final String TAG = MapActivity.class.getSimpleName();
    private GPSTracker gps;
    private Vehicle vehicle;
    private CommandResponse commandResponse;
    private Location vehicleLocation;
    private Location userLocation;
    private LatLng vehicleLatLng;
    private LatLng userLatLng;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setupToolbar();

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);
        try {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        toolbar.bringToFront();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setIcon(R.mipmap.ic_launcher);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (AppConfig.requestPermission(requestCode, permissions, grantResults)){
            case AppConfig.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                break;
            default:
                Toast.makeText(this, getString(R.string.fine_location_permission_denied), Toast.LENGTH_LONG).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        try {
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.getUiSettings().setRotateGesturesEnabled(true);
            map.getUiSettings().setZoomGesturesEnabled(true);


            if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                map.setMyLocationEnabled(true);
            }else {
                AppConfig.requestMissingPermition(Manifest.permission.ACCESS_FINE_LOCATION,AppConfig.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION, this);
            }

            map.setTrafficEnabled(true);
            map.setIndoorEnabled(true);
            map.setBuildingsEnabled(true);

            vehicle = new Vehicle();
            vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");

            commandResponse = new CommandResponse();
            commandResponse = (CommandResponse) getIntent().getSerializableExtra("vehicleResponse");

            map.setOnMarkerClickListener(this);

            vehicleLatLng = this.addMarker(commandResponse.getLatitude() , commandResponse.getLongitude(), vehicle.getNrPlate(), map).getPosition();
            vehicleLocation = new Location("Vehicle Location");
            vehicleLocation.setLatitude(vehicleLatLng.latitude);
            vehicleLocation.setLongitude(vehicleLatLng.longitude);

            gps = new GPSTracker(getApplicationContext(), MapActivity.this);
            if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (gps.canGetLocation()) {
                    userLatLng = this.addMarker(gps.getLatitude(), gps.getLongitude(), getString(R.string.my_location), map).getPosition();
                    userLocation = new Location("User Location");
                    userLocation.setLatitude(userLatLng.latitude);
                    userLocation.setLongitude(userLatLng.longitude);
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    //gps.showSettingsAlert();
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private MarkerOptions addMarker(double latitude, double longitude, String title, GoogleMap map){
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title);

        // ROSE color icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        // Changing marker icon
        // marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));

        map.addMarker(marker);
        if (marker.getTitle().equals(vehicle.getNrPlate()))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));
        return marker;
    }

    private double getDistanceToTheVehicle(Location startLocation, Location endLocation){
        return startLocation.distanceTo(endLocation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gps.stopUsingGPS();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        /*if (marker.getTitle().equals(vehicle.getNrPlate())){
            Toast.makeText(this, "O veiculo encontra-se a " +
                    String.valueOf(getDistanceToTheVehicle(vehicleLocation, userLocation))+ " Km!", Toast.LENGTH_LONG).show();
        }*/
        return false;
    }
}