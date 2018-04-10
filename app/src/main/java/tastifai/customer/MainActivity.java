package tastifai.customer;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import static tastifai.customer.FacebookLoginActivity.userModel;
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
    public ImageView profilePic;
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
    public static Bitmap pictureBitmap;
    String id;
    public static String restaurantId;
    public static RecommendationsPOJO mostOrderedDishesRestaurant;
    public static RecommendationsPOJO mostFrequentDishUser;
    public static RecommendationsPOJO mostRecentDishUser;
    public static ArrayList<RecommendationsPOJO> recommendationsList = new ArrayList<>();
    URL fb_url = null;//small | noraml | large

    HttpsURLConnection conn1 = null;
    private SharedPreferences locationSharedPreferences;
    private ArrayList<Order> orderRatingList = new ArrayList<>();
    private RecyclerView orderRatingRecyclerView;
    public static WebRestaurantModel restaurantModel;

    FacebookAsyncTask asyncTask = new FacebookAsyncTask(MainActivity.this);
    private String subLocality;
    private String address;
    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    ArrayList<WebRestaurantModel> webRestaurantList;
    String url = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetAllRestaurant";
    private double userLat;
    private double userLng;
    public static ProgressDialog progressDialog;
    private RelativeLayout toolbarLayout;
    public static ArrayList<MenuItemModel> mostOrderedRestaurantList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationSharedPreferences = getSharedPreferences(locationPref, Context.MODE_PRIVATE);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) toolbar.findViewById(R.id.title);
        toolbarLayout = findViewById(R.id.toolbarLayout);
        Log.d(TAG, "onCreate: timeStamp: " + timeStamp);
        restaurantModelArrayList.clear();
        if (cartItems.size() != 0)
            cartItems.clear();
        webRestaurantList = new ArrayList<>();

        if (userModel != null) {
            subLocality = userModel.getSubLocality();
            address = userModel.getFullAddress();
            userLat = userModel.getLatitude();
            userLng = userModel.getLongitude();
            Log.d(TAG, "onCreate: userModel: " + subLocality + " " + address + " " + userLat + " " + userLng);
        }

        progressDialog = new ProgressDialog(this);
        Log.d(TAG, "onCreate: " + UUID.randomUUID().toString());
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.food);
        Log.d(TAG, "onCreate: " + isConnectedToInternet());
        if (isConnectedToInternet()) {
            new GetAllRestaurantsAPI().execute(url);
            new MostFrequentDishesAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRecommendMostOrderedItemByUser/" + userModel.getUserId());
            new MostRecentDishesAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRecommendMostOrderedItemByDate/" + userModel.getUserId());
        } else
            setUpAlert();

        toolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddressActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        sharedPreferences = getApplicationContext().getSharedPreferences("FacebookPref", MODE_PRIVATE);
        if (sharedPreferences != null) {
            try {
                JSONObject object = new JSONObject(sharedPreferences.getString("friends", ""));
                friendsArray = object.getJSONArray("data");
                Log.d(TAG, "onCreate: " + friendsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            id = sharedPreferences.getString("fbUserId", null);
            Log.d(TAG, "onCreate: USer id" + id);
        }
        title.setText("(" + userModel.getAddressType() + ")" + userModel.getBuildingName() + " " + userModel.getStreetName());
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GT-Walsheim.ttf"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        asyncTask.delegate = (AsyncResponse) this;
        asyncTask.execute("https://graph.facebook.com/" + id + "/picture?type=large");

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
        if (cartSharedPref != null) {
            if (cartItems.size() == 0) {
                cartLayout.setVisibility(View.GONE);
            } else
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
        if (isConnectedToInternet())

            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.food:
                                    Log.d("SearchRestaurantFrag", "onNavigationItemSelected: food clicked");
                                    getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_layout, new FoodFragment()).commit();

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

    public void setUpAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your phone is not connected to the internet. Please close the app and try again")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void previousFragment() {
        super.onBackPressed();

    }

    public void itemDetailsData(MenuItemModel menuItemModel) {
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


        adapter.addFragment(recommendationsFragment, "FAI");
        adapter.addFragment(nearbyRestaurantsFragment, "Near Me");

        viewPager.setAdapter(adapter);
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


    private void initAction() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("MainActivity", "initAction: called");
        viewPager.setCurrentItem(0);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.myorder) {
            Intent intent = new Intent(MainActivity.this, DeliveryStatus.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.home) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);


        }
//
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ArrayList<WebRestaurantModel> getList() {
        return webRestaurantList;
    }

    @Override
    public void processFinish(Bitmap output) {

        if (output != null) {
            pictureBitmap = output;
            profilePic.setImageBitmap(output);

        } else
            Toast.makeText(MainActivity.this, "Problem loading profile picture", Toast.LENGTH_SHORT).show();
    }

    public JSONArray friendsArrayFromFacebook() {
        return friendsArray;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public double findDistance(double restaurantLat, double restaurantLng, double userLat, double userLng) {
        Log.d(TAG, "findDistance: " + restaurantLat + " " + restaurantLng + " " + userLat + " " + userLng);
        double earthRadius = 6371; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(userLat - restaurantLat);
        double dLng = Math.toRadians(userLng - restaurantLng);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(restaurantLat)) * Math.cos(Math.toRadians(userLat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        Log.d(TAG, "distFrom: " + distance);
        return distance;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    public String convertDateTime(String dateTime) {
        String start_dt = dateTime.replace("T", " ");

        DateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = (Date) parser.parse(start_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat formatter = new SimpleDateFormat("dd MMM, yyyy hh:mm a");
        return formatter.format(date);
    }

    private class GetAllRestaurantsAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!MainActivity.this.isFinishing()) {
                //progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    restaurantModelArrayList.clear();
                    JSONArray array = new JSONArray(s);
                    for (int i = 0; i < array.length(); i++) {
                        Log.d(TAG, "onPostExecute: " + array.length());
                        JSONObject obj = array.getJSONObject(i);
                        WebRestaurantModel model = new WebRestaurantModel();
                        model.setId(obj.getString("RestaurantID"));
                        model.setRestaurantName(obj.getString("RestaurantName"));
                        model.setTiming(obj.getString("Timings"));
                        model.setDeliveryCharges(obj.getInt("DeliveryCharges"));
                        if (!obj.getString("Latitude").equals("null")) {
                            model.setLatitude(Double.parseDouble(obj.getString("Latitude")));
                            model.setLongitude(Double.parseDouble(obj.getString("Longitude")));
                        }
                        restaurantModelArrayList.add(model);
                    }
                    for (WebRestaurantModel model : restaurantModelArrayList) {
                        model.setDistance(findDistance(model.getLatitude(), model.getLongitude(), userLat, userLng));

                    }
                    Log.d(TAG, "onPostExecute: list size: " + restaurantModelArrayList.size());
                    Iterator<WebRestaurantModel> iterator = restaurantModelArrayList.iterator();
//                    while (iterator.hasNext()) {
//                        WebRestaurantModel model = iterator.next();
//                        Log.d(TAG, "onPostExecute: distance radius: " + model.getDistance());
//
//                        if (model.getDistance() >= 5) {
//                            Log.d(TAG, "onPostExecute: removing: " + model.getRestaurantName());
//                            iterator.remove();                        }
//                    }
                    Collections.sort(restaurantModelArrayList);
                    for (WebRestaurantModel model : restaurantModelArrayList)
                        new MostOrderedItemRestaurantAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRecommendMostOrderedItemByRestaurant/" + model.getId());

//                    new MostOrderedItemRestaurantAPI().execute("");
//                    new MostFrequentDishesAPI().execute("");
//                    new MostRecentDishesAPI().execute("");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                int tracker = 0;
                while (tracker == 0) {
                    Toast.makeText(MainActivity.this, "No internet connection, trying to connect...", Toast.LENGTH_SHORT).show();
                    new GetAllRestaurantsAPI().execute(url);
                    if (isConnectedToInternet())
                        tracker = 1;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }

        @Override
        protected String doInBackground(Object[] objects) {
            try {
                URL url = new URL((String) objects[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                //connection.setReadTimeout(7000);
                connection.setConnectTimeout(30000);
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

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                progressDialog.dismiss();
                startActivity(i);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    private class MostOrderedItemRestaurantAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!MainActivity.this.isFinishing()) {
                //progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                mostOrderedRestaurantList.clear();
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < 1; i++) {
                    JSONObject object = array.getJSONObject(i);
//                    MenuItemModel model = new MenuItemModel();
//                    model.setItemName(object.getString("MenuItemName"));
//                    model.setItemId(object.getString("MenuItemID"));
//                    model.setPrice(object.getString("MenuItemPrice"));
//                    model.setDeliveryCharges(15);
//                    model.setCategory("");
//                    menuItemList.add(model);
//                    MostOrderedDishesRestaurantPOJO mostOrderedDish = new MostOrderedDishesRestaurantPOJO();
//                    mostOrderedDish.setRestaurantName(obj);
                    MenuItemModel menuItem = new MenuItemModel();
                    menuItem.setRestaurantName(object.getString("RestaurantName"));
                    menuItem.setDeliveryCharges(object.getInt("DeliveryCharges"));
                    String item = object.getString("MenuItemName");
                    String newString = item.substring(item.indexOf(')') + 1);
//                    if (newString.indexOf(')') != -1) {
//                        String newString2 = newString.substring(item.indexOf(')') + 1);
//                        menuItem.setItemName(newString2);
//
//                    } else {
                        menuItem.setItemName(newString);

                   // }
                    Log.d(TAG, "onPostExecute: substring" + newString);
//                    StringBuilder builder = new StringBuilder(item);
//                    int index=0;
//                    while(builder.charAt(index) != ')'){
//                        Log.d(TAG, "onPostExecute: " + builder.toString() + " " + builder.charAt(index));
//                        builder.deleteCharAt(index);
//                        index = index + 1;
//
//                    }
//                    Log.d(TAG, "onPostExecute: stringbuilder: " + builder.toString());
                    menuItem.setItemId(object.getString("MenuItemID"));
                    menuItem.setPrice(object.getString("MenuItemPrice"));
                    menuItem.setQuantity(1);
                    menuItem.setCategory(object.getString("ItemCategory"));
                    menuItem.setRestaurantId(object.getString("RestaurantID"));
                    mostOrderedRestaurantList.add(menuItem);


                }
                Log.d(TAG, "onPostExecute: mostOrderedRestaurantList: " + mostOrderedRestaurantList.size());
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_layout, new FoodFragment()).commitAllowingStateLoss();


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
                    Log.d(TAG, "most ordered: doInBackground: " + line);
                    builder.append(line);


                }
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "most ordered: doInBackground: " + responseCode + " " + builder.toString());
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

    private class MostFrequentDishesAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!MainActivity.this.isFinishing()) {
                //progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                mostFrequentDishUser = new RecommendationsPOJO();
                ArrayList<MenuItemModel> menuItemList = new ArrayList<>();
                JSONArray array = new JSONArray(s);
                if (array.length() == 0) {
                    Toast.makeText(MainActivity.this, "You don't have order history", Toast.LENGTH_SHORT).show();
                } else if (array.length() <= 5) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        MenuItemModel model = new MenuItemModel();
                        String item = object.getString("MenuItemName");
                        String newString = item.substring(item.indexOf(')') + 1);
//                        if (newString.indexOf(')') != -1) {
//                            String newString2 = newString.substring(item.indexOf(')') + 1);
//                            model.setItemName(newString2);
//
//                        } else {
                            model.setItemName(newString);

                      //  }
                        model.setItemId(object.getString("MenuItemID"));
                        model.setPrice(object.getString("MenuItemPrice"));
                        model.setRestaurantName(object.getString("RestaurantName"));
                        model.setDeliveryCharges(object.getInt("DeliveryCharges"));
                        model.setCategory(object.getString("ItemCategory"));
                        model.setQuantity(1);
                        model.setRestaurantId(object.getString("RestaurantID"));
                        menuItemList.add(model);
                    }
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        MenuItemModel model = new MenuItemModel();
                        String item = object.getString("MenuItemName");
                        String newString = item.substring(item.indexOf(')') + 1);
//                        if (newString.indexOf(')') != -1) {
//                            String newString2 = newString.substring(item.indexOf(')') + 1);
//                            model.setItemName(newString2);
//
//                        } else {
                            model.setItemName(newString);

                      //  }
                        model.setItemId(object.getString("MenuItemID"));
                        model.setPrice(object.getString("MenuItemPrice"));
                        model.setRestaurantName(object.getString("RestaurantName"));
                        model.setDeliveryCharges(object.getInt("DeliveryCharges"));
                        model.setQuantity(1);
                        model.setRestaurantId(object.getString("RestaurantID"));
                        model.setCategory(object.getString("ItemCategory"));
                        menuItemList.add(model);
                    }
                }
                mostFrequentDishUser.setRecommendationType("Your favorite dishes");
                mostFrequentDishUser.setRecommendationItemList(menuItemList);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }


        }

        @Override
        protected String doInBackground(Object[] objects) {
            try {
                URL url = new URL((String) objects[0]);
                Log.d(TAG, "most frequent: doInBackground: " + url.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setReadTimeout(7000);
                connection.setConnectTimeout(7000);
                InputStream istream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, " most frequently ordered: doInBackground: " + line);
                    builder.append(line);


                }
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "most frequently ordered: doInBackground: " + responseCode + " " + builder.toString());
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

    private class MostRecentDishesAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!MainActivity.this.isFinishing()) {
                //progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            recommendationsList.clear();
            progressDialog.dismiss();
            try {
                mostRecentDishUser = new RecommendationsPOJO();
                ArrayList<MenuItemModel> menuItemList = new ArrayList<>();
                JSONArray array = new JSONArray(s);
                if (array.length() == 0) {
                    Toast.makeText(MainActivity.this, "You don't have order history", Toast.LENGTH_SHORT).show();
                } else if (array.length() <= 5) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        MenuItemModel model = new MenuItemModel();
                        String item = object.getString("MenuItemName");
                        String newString = item.substring(item.indexOf(')') + 1);
//                        if (newString.indexOf(')') != -1) {
//                            String newString2 = newString.substring(item.indexOf(')') + 1);
//                            model.setItemName(newString2);
//
//                        } else {
                            model.setItemName(newString);

                       // }
                        model.setItemId(object.getString("MenuItemID"));
                        model.setPrice(object.getString("MenuItemPrice"));
                        model.setRestaurantName(object.getString("RestaurantName"));
                        model.setDeliveryCharges(object.getInt("DeliveryCharges"));
                        model.setCategory(object.getString("ItemCategory"));
                        model.setQuantity(1);
                        model.setRestaurantId(object.getString("RestaurantID"));
                        menuItemList.add(model);
                    }
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        MenuItemModel model = new MenuItemModel();
                        String item = object.getString("MenuItemName");
                        String newString = item.substring(item.indexOf(')') + 1);
//                        if (newString.indexOf(')') != -1) {
//                            String newString2 = newString.substring(item.indexOf(')') + 1);
//                            model.setItemName(newString2);
//
//                        } else {
                            model.setItemName(newString);

                        //}
                        model.setItemId(object.getString("MenuItemID"));
                        model.setPrice(object.getString("MenuItemPrice"));
                        model.setDeliveryCharges(object.getInt("DeliveryCharges"));
                        model.setRestaurantName(object.getString("RestaurantName"));
                        model.setQuantity(1);
                        model.setRestaurantId(object.getString("RestaurantID"));
                        model.setCategory(object.getString("ItemCategory"));
                        menuItemList.add(model);
                    }
                }
                mostRecentDishUser.setRecommendationType("Most recent dishes ");
                mostRecentDishUser.setRecommendationItemList(menuItemList);

                recommendationsList.add(mostFrequentDishUser);
                recommendationsList.add(mostRecentDishUser);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Object[] objects) {
            try {
                URL url = new URL((String) objects[0]);
                Log.d(TAG, "most recent: doInBackground: " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setReadTimeout(7000);
                connection.setConnectTimeout(7000);
                InputStream istream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "most recently ordered: doInBackground: " + line);
                    builder.append(line);


                }
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "most recently ordered: doInBackground: " + responseCode + " " + builder.toString());
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
