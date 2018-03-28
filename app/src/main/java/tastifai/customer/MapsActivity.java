package tastifai.customer;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private static final int ERROR_DIALOG_REQUEST = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Button setAddress;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng finalLocation;
    public static final String locationPref = "LocationPref";
    private SharedPreferences locationSharedPref;
    private LatLng saveLocation;
    private ImageView currentLocationImage;
    private String subLocality;
    private String fullAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationSharedPref = getSharedPreferences(locationPref, Context.MODE_PRIVATE);
        PlaceInfo placeInfo = null;
        init();


        if (isServicesOK()) {
            getLocationPermission();
        } else {

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        Intent intent = getIntent();

        if (mLocationPermissionsGranted) {
            if (intent != null) {
                Bundle bundle = getIntent().getBundleExtra("place");
                if (bundle != null) {
                    LatLng latlng = (LatLng) bundle.getParcelable("latlng");
                    String address = (String) bundle.getString("address");
                    Log.d(TAG, "onCreate: " + address + latlng.latitude);
                    moveCamera(latlng, 15f);
                } else
                    getDeviceLocation();
            } else
                getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                        @Override
                        public void onCameraIdle() {
                            finalLocation = mMap.getCameraPosition().target;
                            Log.d(TAG, "onCameraIdle: " + finalLocation.latitude + " " + finalLocation.longitude);
//                  Location addressLocation = new Location(LocationManager.GPS_PROVIDER);
//                  addressLocation.setLatitude(finalLocation.latitude);
//                  addressLocation.setLongitude(finalLocation.longitude);
                            getAddress(finalLocation);
                            Toast.makeText(MapsActivity.this, "New Lat : " + finalLocation.latitude + " New Lng : " + finalLocation.longitude, Toast.LENGTH_SHORT).show();
                        }
                    });
                    // mMap.setMyLocationEnabled(true);
                }
            });
        }

    }
    private void getAddress(LatLng latlng){
        Log.d(TAG, "getAddress: " + latlng.latitude + " " + latlng.longitude);
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
            subLocality = addresses.get(0).getSubLocality();
            fullAddress = addresses.get(0).getAddressLine(0);
            Toast.makeText(MapsActivity.this, addresses.get(0).getSubLocality() + " " + addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
            setAddress.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void searchAddress(){

    }
    private void init(){

        setAddress = findViewById(R.id.setAddress);
        setAddress.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GT-Walsheim.ttf"));
        setAddress.setVisibility(View.GONE);
        setAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = locationSharedPref.edit();
                editor.putString("subLocality",subLocality);
                editor.putString("address", fullAddress);
                editor.apply();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        currentLocationImage = findViewById(R.id.currentLocation);
        currentLocationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });


    }
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }
    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);
        if(available == ConnectionResult.SUCCESS){
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else
            Toast.makeText(this, "You can't make map requests",Toast.LENGTH_SHORT).show();
        return false;
    }
    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionsGranted = true;
                initMap();

            }else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        }else
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }
    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location)task.getResult();
                            Log.d(TAG, "onComplete: " + currentLocation.getLongitude() + " " + currentLocation.getLatitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);
                            finalLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            getAddress(finalLocation);

                        }
                        else
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: " + e.getMessage());
        }

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private void moveCamera(LatLng latlng, float zoom){
        Log.d(TAG, "moveCamera: " + latlng.latitude + " " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
//        MarkerOptions options = new MarkerOptions().position(latlng).draggable(false).title("You are here").icon(bitmapDescriptorFromVector(this, R.mipmap.marker));
//        mMap.addMarker(options);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE : {
                if(grantResults.length > 0){
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }


}
