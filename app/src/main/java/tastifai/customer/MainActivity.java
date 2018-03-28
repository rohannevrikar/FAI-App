package tastifai.customer;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import static tastifai.customer.MapsActivity.locationPref;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse {

    public static Toolbar toolbar;
    private EditText searchText;
    private AppBarLayout appBarLayout;
    private NavigationView view;
    private TextView title;
    private TextView toolbarAddress;
    private ImageView dropDown;
    private TextView cartQty;
    private ImageView profilePic;
    private ImageView locationImage;
    private RelativeLayout cartLayout;
    private TextView welcomeText;
    private ViewPager viewPager;
    private RecommendationsFragment recommendationsFragment;
    private NearbyRestaurantsFragment nearbyRestaurantsFragment;
    private RushFragment rushFragment;
    private RestaurantFragmentAdapter adapter;
    private OverviewFragment f_overview;
    private MenuFragment f_menu;
    private ReviewsFragment f_reviews;
    public JSONArray friendsArray;
    private ArrayList friendsArrayList;
    private SharedPreferences locationSharedPref;
    public DatabaseHelper databaseHelper;
    public static ArrayList<MenuItemModel> cartItems = new ArrayList<>();
    public static ArrayList<WebRestaurantModel> restaurantModelArrayList = new ArrayList<>();
    private SharedPreferences cartSharedPref;
    String id;
    URL fb_url = null;//small | noraml | large

    HttpsURLConnection conn1 = null;
    private SharedPreferences locationSharedPreferences;

    FacebookAsyncTask asyncTask = new FacebookAsyncTask();
    private String subLocality;
    private String address;
    private  static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    ArrayList<WebRestaurantModel> webRestaurantList;
    String url = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetAllRestaurant";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationSharedPreferences = getSharedPreferences(locationPref, Context.MODE_PRIVATE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        restaurantModelArrayList.clear();
        webRestaurantList = new ArrayList<>();
        if(locationSharedPreferences!=null){
            subLocality = locationSharedPreferences.getString("subLocality",null);
            address = locationSharedPreferences.getString("address", null);
            Toast.makeText(this, "Showing results from " + subLocality, Toast.LENGTH_LONG).show();
        }
        cartSharedPref = getSharedPreferences("Cart", Context.MODE_PRIVATE);
        if(cartSharedPref.getString("cart", null) != null){
            Gson gson = new Gson();
            String json = cartSharedPref.getString("cart", null);
            Log.d(TAG, "onCreate: cartsharedpref not null" +json);

            Type type = new TypeToken<ArrayList<MenuItemModel>>(){}.getType();
            cartItems = gson.fromJson(json, type);
        }
        //getApplicationContext().deleteDatabase("restaurants.db");
        //databaseHelper = new DatabaseHelper(this);
        Log.d(TAG, "onCreate: " + UUID.randomUUID().toString());
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.food);
        new APIAsyncTask().execute(url);

        dropDown = findViewById(R.id.drop_down);
        dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddressActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        sharedPreferences = getApplicationContext().getSharedPreferences("FacebookPref", MODE_PRIVATE);
        if(sharedPreferences!=null){
            try {
                Toast.makeText(MainActivity.this, sharedPreferences.getString("friends", null),Toast.LENGTH_LONG).show();
                JSONObject object = new JSONObject(sharedPreferences.getString("friends", null));
                friendsArray = object.getJSONArray("data");
                Log.d(TAG, "onCreate: " + friendsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            id = sharedPreferences.getString("fbUserId", null);
            Log.d(TAG, "onCreate: USer id" + id);
            //profilePic.setProfileId(id);
        }
        Log.d("MainActivity", "onCreate: " + AccessToken.getCurrentAccessToken().toString());
        asyncTask.delegate = (AsyncResponse) this;
            asyncTask.execute("https://graph.facebook.com/" + id + "/picture?type=large");

//        GraphRequest request = GraphRequest.newGraphPathRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/me/friends",
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        try {
//                            Log.d("MainActivity", "onCompleted: " + response.toString());
//                            friendsArray = response.getJSONObject().getJSONArray("data");
//                            Log.d("MainActivity", "onCompleted: " + friendsArray.toString());
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//        Bundle parameters = new Bundle();
//
//        request.executeAsync();
        //Log.d("FriendsArray", "onCreate: " + friendsArray.toString());

//        Bitmap bitmap = null;
//        try {
//            bitmap = BitmapFactory.decodeStream(new URL(sharedPreferences.getString("picture", null)).openConnection().getInputStream());
//            profilePic.setImageBitmap(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Log.d("MainActivity", "onCreate: " + sharedPreferences.getString("fbUserId", null) + " " + sharedPreferences.getString("first_name", null));
        title = findViewById(R.id.title);
//        if(databaseHelper.getRestaurantData().getCount() == 0){
//        }
//        toolbarAddress = findViewById(R.id.address);
//        toolbarAddress.setText(address);
        title.setText(subLocality);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GT-Walsheim.ttf"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        view = (NavigationView) findViewById(R.id.nav_view);
        View hView = view.getHeaderView(0);
        profilePic = hView.findViewById(R.id.profilePic);

        view.setNavigationItemSelectedListener(MainActivity.this);
        cartLayout = toolbar.findViewById(R.id.cartLayout);
        cartQty = cartLayout.findViewById(R.id.cartQty);
        distFrom();
        if(cartSharedPref != null){
            if(cartItems.size() == 0){
                cartLayout.setVisibility(View.GONE);
            }
            else
                cartLayout.setVisibility(View.VISIBLE);
            cartQty.setText("" + cartItems.size());
        }

        cartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Cart.class);
                startActivity(intent);

            }
        });

                bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.food:
                                Log.d("SearchRestaurantFrag", "onNavigationItemSelected: food clicked");
//                                setupViewPager(viewPager);
//                                findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
//                                findViewById(R.id.app_bar_layout).setVisibility(View.VISIBLE);
                                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_layout, new FoodFragment()).commit();

                                break;

                            case R.id.fun:
                                Log.d("SearchRestaurantFrag", "onNavigationItemSelected: fun clicked");
                                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_layout, new FunFragment()).commit();

                                break;

                            case R.id.friends:
                                Log.d("SearchRestaurantFrag", "onNavigationItemSelected: Friends clicked");
                                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_layout, new FriendsFragment()).commit();

                                break;
                            case R.id.me:
                                Log.d("SearchRestaurantFrag", "onNavigationItemSelected: me clicked");
                                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_layout, new MeFragment()).commit();

                                break;

                        }
                        return true;

                    }


                });

    }



    @Override
    public void onBackPressed() {
       Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public ImageView getProfilePic(){
        return profilePic;
    }
    private void previousFragment() {
        super.onBackPressed();

    }

    public void itemDetailsData(MenuItemModel menuItemModel){
        Bundle args = new Bundle();
        args.putSerializable("menuItemModel", menuItemModel);
        ItemDetails fragment = new ItemDetails();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_layout, fragment).commit();



    }
    private void setupViewPager(ViewPager viewPager) {
        adapter = new RestaurantFragmentAdapter(getSupportFragmentManager());
        adapter.clearList();


        recommendationsFragment = new RecommendationsFragment();


        nearbyRestaurantsFragment = new NearbyRestaurantsFragment();
        rushFragment = new RushFragment();



        adapter.addFragment(recommendationsFragment,  "FAI");
        adapter.addFragment(nearbyRestaurantsFragment, "Near Me");
        //adapter.addFragment(rushFragment, "Rush");

        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(3);
    }
//        public void updateViewPager() {
//        adapter.clearList();
//
//
//        f_overview = new OverviewFragment();
//
//
//        f_menu = new MenuFragment();
//
//
//        f_reviews = new ReviewsFragment();
//
//
//        adapter.addFragment(f_overview,  "Overview");
//        adapter.addFragment(f_menu, "Menu");
//        adapter.addFragment(f_reviews, "Reviews");
//
//        viewPager.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//        //viewPager.setOffscreenPageLimit(3);
//    }
    private void initAction() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("MainActivity", "initAction: called");
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            if (AccessToken.getCurrentAccessToken() == null) {
                return true; // already logged out
            }
            this.getSharedPreferences("FacebookPref", 0).edit().clear().apply();
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {

                    LoginManager.getInstance().logOut();
                    Toast.makeText(MainActivity.this, "You are successfully logged out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
                    startActivity(intent);

                }
            }).executeAsync();
        } else if(item.getItemId() == R.id.myaccount){
            Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            findViewById(R.id.home).setVisibility(View.GONE);
            title.setText("My Account");
            findViewById(R.id.restaurantFragment).setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.restaurantFragment, new MyAccount()).commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public ArrayList<WebRestaurantModel> getList(){
        return webRestaurantList;
    }

    @Override
    public void processFinish(Bitmap output) {

        if(output!=null){
            profilePic.setImageBitmap(output);

        }
        else
            Toast.makeText(MainActivity.this, "Problem loading profile picture", Toast.LENGTH_SHORT).show();
    }
    public JSONArray friendsArrayFromFacebook(){
        return friendsArray;
    }
//    private void findDistance(){
//        double earthRadius = 6371;
//
//        double latDiff = Math.abs(convertToRadian(lat2 - lat1));
//        Log.d(TAG, "findDistance: differece lat: " + latDiff);
//        double lngDiff = Math.abs(convertToRadian(lng2 - lng1));
//        Log.d(TAG, "findDistance: difference lng: " + lngDiff);
//        double a = Math.pow(Math.sin(latDiff/2),2) + (Math.cos(convertToRadian(lat1) * Math.cos(convertToRadian(lat2) * Math.pow(Math.sin(lngDiff/2),2))));
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        Log.d(TAG, "findDistance: c: " + c + " a : " + a + " math.tan2 : " + Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
//        double distance = earthRadius * c;
//        Log.d(TAG, "findDistance: " + distance);
//    }
//    private double convertToRadian(double degree){
//        return degree * (Math.PI/180);
//    }
@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
}public void distFrom() {
        double lat1 = 23.034393; //CIIE
        double lng1 = 72.532507; //CIIE
        double lat2 = 23.019207; //Satyam
        double lng2 = 72.514001; //Satyam
        double earthRadius = 6371; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        Log.d(TAG, "distFrom: " + dist);

    }
    private class APIAsyncTask extends AsyncTask<Object,String,String> {
        StringBuilder builder = new StringBuilder();

        @Override
        protected void onPostExecute(String s) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    JSONArray array = new JSONArray(s);
                    for(int i=0;i<array.length(); i++){
                        Log.d(TAG, "onPostExecute: " + array.length());
                        JSONObject obj = array.getJSONObject(i);
                        WebRestaurantModel model = new WebRestaurantModel();
                        model.setId(obj.getString("RestaurantID"));
                        model.setRestaurantName(obj.getString("RestaurantName"));
                        restaurantModelArrayList.add(model);
                        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_layout, new FoodFragment()).commitAllowingStateLoss();

//                        databaseHelper.insertRestaurantData(Integer.parseInt(obj.getString("RestaurantID")), obj.getString("RestaurantName"));
//                            Log.d(TAG, "onPostExecute: inserting " + i);
//                            Toast.makeText(MainActivity.this, "data inserted", Toast.LENGTH_SHORT).show();


//                            Toast.makeText(MainActivity.this, "data not inserted", Toast.LENGTH_SHORT).show();

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Object[] objects) {
            try {
                URL url = new URL((String) objects[0]);
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