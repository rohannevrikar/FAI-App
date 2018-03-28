package tastifai.customer;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

public class AddressActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private AutoCompleteTextView searchInput;
    private PlaceAutoCompleteAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout relativeLayout;
    private  static final String TAG = "AddressActivity";
    private LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(40, -168),
            new LatLng(71,136)
    );
    private PlaceInfo mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        searchInput = findViewById(R.id.searchInput);
        relativeLayout = findViewById(R.id.currentLocationLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, MapsActivity.class);
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
        adapter = new PlaceAutoCompleteAdapter(this, Places.getGeoDataClient(this, null), LAT_LNG_BOUNDS,null);
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
            if(!places.getStatus().isSuccess()){
                Toast.makeText(AddressActivity.this, "Place not found", Toast.LENGTH_SHORT).show();
                places.release();
                return;
            }
            final Place place = places.get(0);
            Log.d(TAG, "onResult: " + place.getAddress() + place.getLatLng().latitude + place.getLatLng().latitude);
            mPlace = new PlaceInfo();
            mPlace.setAddress(place.getAddress().toString());
            mPlace.setLatLng(place.getLatLng());
            Intent intent = new Intent(AddressActivity.this, MapsActivity.class);
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
}
