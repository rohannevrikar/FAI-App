package tastifai.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import static tastifai.customer.FacebookLoginActivity.userModel;

public class AddressActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener  {
    private AutoCompleteTextView searchInput;
    private PlaceAutoCompleteAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout relativeLayout;
    private ImageView back;
    private Spinner spinner;
    private RecyclerView addressRecyclerView;
    private static final String TAG = "AddressActivity";
    private LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(40, -168),
            new LatLng(71, 136)
    );
    private PlaceInfo mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        searchInput = findViewById(R.id.searchInput);


        addressRecyclerView = findViewById(R.id.addressRecyclerView);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressActivity.super.onBackPressed();
            }
        });
        Log.d(TAG, "onCreate: user id: " + userModel.getUserId());
        new APIAsyncTask().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetUserAddressDetails/" + userModel.getUserId());
        relativeLayout = findViewById(R.id.currentLocationLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, AddLocationActivity.class);
                startActivity(intent);
            }
        });
        searchInput.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GT-Walsheim.ttf"));
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        adapter = new PlaceAutoCompleteAdapter(this, Places.getGeoDataClient(this, null), LAT_LNG_BOUNDS, null);
        searchInput.setOnItemClickListener(mAutoCompleteClickListener);
        searchInput.setDropDownBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.white)));
        searchInput.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final AutocompletePrediction item = adapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallBack);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallBack = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Toast.makeText(AddressActivity.this, "Place not found", Toast.LENGTH_SHORT).show();
                places.release();
                return;
            }
            final Place place = places.get(0);
            Log.d(TAG, "onResult: " + place.getAddress() + place.getLatLng().latitude + place.getLatLng().latitude);
            mPlace = new PlaceInfo();
            mPlace.setAddress(place.getAddress().toString());
            mPlace.setLatLng(place.getLatLng());
            Intent intent = new Intent(AddressActivity.this, AddLocationActivity.class);
            Bundle args = new Bundle();
            args.putParcelable("latlng", place.getLatLng());
            args.putString("address", place.getAddress().toString());
            intent.putExtra("place", args);
            startActivity(intent);
            places.release();

        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private class APIAsyncTask extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddressActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            ArrayList<AddressPOJO> addressPOJOArrayList = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(s);
                Log.d(TAG, "onPostExecute: array length: " + array.length());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Log.d(TAG, "onPostExecute: object: " + obj.toString());
                    AddressPOJO addressPOJO = new AddressPOJO();
                    addressPOJO.setBuildingName(obj.getString("BuildingName"));
                    addressPOJO.setStreetName(obj.getString("StreetName"));
                    addressPOJO.setCityName(obj.getString("CityName"));
                    addressPOJO.setStateName(obj.getString("StateName"));
                    addressPOJO.setCountryName(obj.getString("Country"));
                    addressPOJO.setPostalCode(obj.getString("PostalCode"));
                    addressPOJO.setIsActive(obj.getString("IsActive"));
                    addressPOJO.setIsDeleted(obj.getString("IsDeleted"));
                    addressPOJO.setAddressType(obj.getString("AddressType"));
                    addressPOJO.setLatitude(obj.getDouble("Latitude"));
                    addressPOJO.setLongitude(obj.getDouble("Longitude"));
                    addressPOJO.setAreaName(obj.getString("AreaName"));

                    addressPOJOArrayList.add(addressPOJO);
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(AddressActivity.this);
                addressRecyclerView.setLayoutManager(layoutManager);
                AddressAdapter adapter = new AddressAdapter(AddressActivity.this, addressPOJOArrayList);
                addressRecyclerView.setAdapter(adapter);


            } catch (JSONException e) {

                e.printStackTrace();
            } catch (NullPointerException e) {
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
                Log.d(TAG, "doInBackground: " + url);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }
}
