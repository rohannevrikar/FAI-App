package tastifai.customer;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static tastifai.customer.FacebookLoginActivity.userModel;
import static tastifai.customer.MainActivity.cartItems;
import static tastifai.customer.MainActivity.progressDialog;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener  {
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
    private String locality;
    private String country;
    private TextView area;
    String[] addressType = { "Home", "Work", "Other",  };
    private Spinner spinner;

    private String fullAddress;
    private EditText flatNumber;
    private EditText streetName;
    private EditText phoneNumber;
    private String zipCode;
    private PrefManagerMaps prefManagerMaps;
    private String addressTypeSelected;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManagerMaps = new PrefManagerMaps(this);
//        if (!prefManagerMaps.isFirstTimeLaunch()) {
//            launchHomeScreen();
//            finish();
//        }
        setContentView(R.layout.activity_maps);
        locationSharedPref = getSharedPreferences(locationPref, Context.MODE_PRIVATE);
        sharedPreferences = getApplicationContext().getSharedPreferences("FacebookPref", MODE_PRIVATE);
        PlaceInfo placeInfo = null;
        init();
        Toast.makeText(MapsActivity.this, "Drag the map to the delivery location", Toast.LENGTH_SHORT).show();


        if (isServicesOK()) {
            getLocationPermission();
        } else {

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        Intent intent = getIntent();

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
//            if (intent != null) {
//                Bundle bundle = getIntent().getBundleExtra("place");
//                if (bundle != null) {
//                    final LatLng latlng = (LatLng) bundle.getParcelable("latlng");
//                    userModel.setLatitude(latlng.latitude);
//                    userModel.setLongitude(latlng.longitude);
//                    String address = (String) bundle.getString("address");
//                    Log.d(TAG, "onCreate: " + address + latlng.latitude);
//                    moveCamera(latlng, 15f);
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

                        @Override
                        public void onMapLoaded() {
                            Log.d(TAG, "onMapLoaded: ");
                            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                                @Override
                                public void onCameraIdle() {
                                    finalLocation = mMap.getCameraPosition().target;
                                    Log.d(TAG, "onCameraIdle: " + finalLocation.latitude + " " + finalLocation.longitude);
//                  Location addressLocation = new Location(LocationManager.GPS_PROVIDER);
//                  addressLocation.setLatitude(finalLocation.latitude);
//                  addressLocation.setLongitude(finalLocation.longitude);
                                    getAddress(finalLocation);
                                }
                            });
//                            // mMap.setMyLocationEnabled(true);
                        }
                    });
//                } else
//                    getDeviceLocation();
//            } else
//                getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

        }

    }
    private void getAddress(LatLng latlng){
        Log.d(TAG, "getAddress: " + latlng.latitude + " " + latlng.longitude);
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
            subLocality = addresses.get(0).getSubLocality();
            area.setText(subLocality);
            fullAddress = addresses.get(0).getAddressLine(0);
            locality = addresses.get(0).getLocality();
            country = addresses.get(0).getCountryName();
            zipCode = addresses.get(0).getPostalCode();
            Log.d(TAG, "getAddress: " + locality);
            //Toast.makeText(MapsActivity.this, addresses.get(0).getSubLocality() + " " + addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
            setAddress.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void launchHomeScreen() {
        //prefManagerMaps.setFirstTimeLaunch(false);
        startActivity(new Intent(MapsActivity.this, MainActivity.class));
        finish();
    }
    private void searchAddress(){

    }
    private void init(){
        area = findViewById(R.id.area);
        setAddress = findViewById(R.id.setAddress);
        setAddress.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GT-Walsheim.ttf"));
        setAddress.setVisibility(View.GONE);
        flatNumber = findViewById(R.id.building_name);
        streetName = findViewById(R.id.street_name);
        phoneNumber = findViewById(R.id.contact_number);
        spinner = findViewById(R.id.spinner2);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, addressType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        setAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(flatNumber.getText()) || TextUtils.isEmpty(phoneNumber.getText())){
                    if(TextUtils.isEmpty(flatNumber.getText())){
                        flatNumber.setError("Please enter flat number");
                    }

                    if(TextUtils.isEmpty(phoneNumber.getText())){
                        phoneNumber.setError("Please enter phone number");
                    }
                }else if(phoneNumber.getText().toString().trim().length() <10){
                    phoneNumber.setError("Phone number should be of 10 characters");

                } else{

                    userModel.setContactNumber(phoneNumber.getText().toString());
                    userModel.setBuildingName(flatNumber.getText().toString());
                    userModel.setStreetName(streetName.getText().toString());
                    userModel.setCity(locality);
                    userModel.setSubLocality(subLocality);
                    userModel.setCountry(country);
                    userModel.setZipCode(zipCode);
                    userModel.setAddressType(addressTypeSelected);
                    userModel.setLatitude(finalLocation.latitude);
                    userModel.setLongitude(finalLocation.longitude);

                    JSONObject postData;
//                for(int i=0;i<cartItems.size();i++){

//                        if(!validateAddress(MapsActivity.this, userModel.getStreetName() + " " + userModel.getCity())){
//                            Log.d(TAG, "onClick: not validated");
//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
//                            builder1.setMessage("Your address couldn't be identified, please enter a valid address");
//                            builder1.setCancelable(false);
//
//                            builder1.setPositiveButton(
//                                    "OK",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//                            AlertDialog alert11 = builder1.create();
//                            alert11.show();
//                        }else{
                            postData = new JSONObject();
                            try {
                                postData.put("BuildingName", userModel.getBuildingName());
                                postData.put("PhoneNumber", userModel.getContactNumber());
                                postData.put("StreetName", userModel.getStreetName());
                                postData.put("Country", userModel.getCountry());
                                postData.put("UserEmail", userModel.getEmail());
                                postData.put("UserFName", userModel.getFirst_name());
                                postData.put("UserLName", userModel.getLast_name());
                                postData.put("CityName", userModel.getCity());
                                postData.put("IsActive", 1);
                                postData.put("IsDeleted", 0);
                                postData.put("DefaultAddress", userModel.getCity());
                                postData.put("PostalCode", userModel.getZipCode());
                                postData.put("StateName", "Gujarat");
                                postData.put("UserPassword", "facebook123");
                                postData.put("FacebookID", userModel.getFbUserId());
                                postData.put("CreatedDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                                postData.put("AddressType", userModel.getAddressType());
                                postData.put("Latitude", Math.round(userModel.getLatitude() * 100000000)/100000000);
                                postData.put("Longitude", Math.round(userModel.getLongitude() * 100000000)/100000000);
                                postData.put("AreaName", userModel.getSubLocality());
                                Log.d(TAG, "onClick: lat lng sublocal: " + userModel.getLatitude() + " " + userModel.getLongitude() + " " + userModel.getSubLocality());


                                //postData.put()





                                //ordersArray.put(postData);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                }

                            Log.d(TAG, "onClick: " + postData);
                            if(isConnectedToInternet())
                                new PostUserDetailsAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/PostUserDetails", postData.toString());
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                builder.setMessage("Your phone is not connected to the internet. Please check your connection")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }



                       // Log.d(TAG, "onClick: " + cartItems.get(0).getItemName());



//                    Intent intent = new Intent(MapsActivity.this, MainActivity.class);
//                    startActivity(intent);
                }


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
    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }
    public boolean validateAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        Log.d(TAG, "getLocationFromAddress: " + strAddress);

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            Log.d(TAG, "validateAddress: " + address.size());
            if (address == null) {
                return false;
            }
            if(address.size() == 0){
                Log.d(TAG, "validateAddress: returning false");
               return false;
            }

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return true;
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
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location)task.getResult();
                            Log.d(TAG, "onComplete: " + currentLocation.getLongitude() + " " + currentLocation.getLatitude());
                            userModel.setLatitude(currentLocation.getLatitude());
                            userModel.setLongitude(currentLocation.getLongitude());
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

    @Override
    public void onBackPressed() {

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        addressTypeSelected = addressType[i];

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class PostUserDetailsAPI extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "onPostExecute: FB User id: " + sharedPreferences.getString("fbUserId",""));
            new APIAsyncTask().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetUserDetailsByFacebookID/" + sharedPreferences.getString("fbUserId",""));

        }

        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];
            Log.d(TAG, "doInBackground: " + urlString);
            String data = strings[1];

            Log.d(TAG, "doInBackground: " + data);
            OutputStream out = null;
            StringBuilder builder = new StringBuilder();
            try {

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                connection.connect();
                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(data);
// json data
                writer.close();



//                InputStream istream = connection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    Log.d(TAG, "doInBackground: " + line);
//                    builder.append(line);
//
//
//
//                }
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
//                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
//                    Log.d(TAG, "doInBackground: " + responseCode + " " + builder.toString());
//                    String[] myArray = builder.toString().split(",");
//                    Log.d(TAG, "onPostExecute: " + myArray[0]);
//                    return builder.toString();
//                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (SocketTimeoutException e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(i);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class APIAsyncTask extends AsyncTask<Object,String,String> {
        StringBuilder builder = new StringBuilder();
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!MapsActivity.this.isFinishing()){
                progressDialog = new ProgressDialog(MapsActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try{
//                JSONArray array = new JSONArray(s);
//                for(int i=0;i<array.length();i++) {
                JSONObject obj = new JSONObject(s);
                userModel.setFirst_name(obj.getString("UserFName"));
                userModel.setLast_name(obj.getString("UserLName"));
                userModel.setContactNumber(obj.getString("PhoneNumber"));
                userModel.setEmail(obj.getString("UserEmail"));
                userModel.setUserId(obj.getInt("UserId"));
                Log.d(TAG, "onPostExecute: " + userModel.getUserId());
                launchHomeScreen();

//                Intent intent = new Intent(MapsActivity.this, RatingPopUp.class);
////                intent.putExtra("fbUserId", fbUserId);
////                intent.putExtra("first_name", first_name);
////                intent.putExtra("last_name", last_name);
////                intent.putExtra("email", email);
////                intent.putExtra("accessToken", accessToken);
////                intent.putExtra("friendsArray", friendsArray);
////                intent.putExtra("picture", picture);
//                startActivity(intent);
//                }


            } catch (JSONException e) {

                e.printStackTrace();
            }catch (NullPointerException e){
//            int tracker = 0;
//
//            while(tracker == 0){
//                Toast.makeText(getActivity(), "No internet connection, trying to connect...", Toast.LENGTH_SHORT).show();
//                new APIAsyncTask().execute(url);
//                if(((MainActivity)getActivity()).isConnectedToInternet())
//                    tracker = 1;
//            }
                e.printStackTrace();

            }
        }

        @Override
        protected String doInBackground(Object[] objects) {
            try {
                URL url = new URL((String) objects[0]);
                Log.d(TAG, "doInBackground: " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setReadTimeout(7000);
                connection.setConnectTimeout(7000);
                InputStream istream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "doInBackground: " + line);
                    builder.append(line);



                }
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "doInBackground: " + responseCode + " " + builder.toString());
                    String[] myArray = builder.toString().split(",");
                    Log.d(TAG, "onPostExecute: " + myArray[0]);
                    return builder.toString();
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                try {


                    Thread.sleep(5000);
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    progressDialog.dismiss();
                    startActivity(i);
                    e.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }
}
