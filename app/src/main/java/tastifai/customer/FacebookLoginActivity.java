package tastifai.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.params.Face;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static tastifai.customer.MainActivity.progressDialog;
import static tastifai.customer.SearchRestaurantAdapter.restaurantModel;

public class FacebookLoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    String profilePicUrl;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static UserModel userModel = new UserModel();
    private static final String TAG = "FacebookLoginActivity";
    private ProgressDialog progressDialog;
    private ArrayList<Order> orderRatingList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        if (!isConnectedToInternet())
            setUpAlert();

        sharedPreferences = getApplicationContext().getSharedPreferences("FacebookPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (isLoggedIn()) {
            Log.d(TAG, "onCreate: user id: " + sharedPreferences.getString("fbUserId", ""));
            new APIAsyncTask().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetUserDetailsByFacebookID/" + sharedPreferences.getString("fbUserId", ""));

//                String fbUserId = sharedPreferences.getString("fbUserId", null);
//                String accessToken = sharedPreferences.getString("accessToken", null);
//                String first_name = sharedPreferences.getString("first_name", null);
//                String last_name = sharedPreferences.getString("last_name", null);
//                String email = sharedPreferences.getString("email", null);
//                String picture = sharedPreferences.getString("picture", null);
//                String friendsArray = sharedPreferences.getString("friendsArray", null);
//                Log.d("FacebookLoginActivity", "onCreate: " + friendsArray);

        }


        loginButton = findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_friends"));
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        loginButton.setVisibility(View.GONE);

                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        Log.v("LoginActivity", response.toString());
                                        // Application code
                                        try {
                                            if (object.has("picture")) {
                                                profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                                Log.d("FacebookActivity", "onCompleted: " + profilePicUrl);
                                            }
                                            String email = object.getString("email");
                                            //String birthday = object.getString("birthday");
                                            String first_name = object.getString("first_name");
                                            String last_name = object.getString("last_name");
                                            String id = object.getString("id");
                                            Log.d(TAG, "onCompleted: fb user id 1:" + id);
                                            String friends = object.getString("friends");
                                            userModel.setFirst_name(first_name);
                                            userModel.setLast_name(last_name);
                                            userModel.setFbUserId(id);
                                            userModel.setEmail(email);
                                            Log.d(TAG, "onCompleted: fbuserid" + userModel.getUserId());
                                            Log.d("FacebookLogin", "onCompleted: " + friends);
                                            editor = sharedPreferences.edit();
                                            editor.putString("fbUserId", id);
                                            editor.putString("accessToken", loginResult.getAccessToken().toString());
                                            editor.putString("first_name", first_name);
                                            editor.putString("last_name", last_name);
                                            editor.putString("email", email);
                                            editor.putString("picture", profilePicUrl);
                                            editor.putString("friends", friends);
                                            editor.apply();
                                            Log.d("facebookActivity", "displayUserInfo: " + email + " " + first_name + profilePicUrl);
                                            //new APIAsyncTask().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetUserDetailsByFacebookID/" + id);
                                            final Intent intent = new Intent(FacebookLoginActivity.this, MapsActivity.class);
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name, last_name, email,gender,friends");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        Intent intent = new Intent(FacebookLoginActivity.this, FacebookLoginActivity.class);
                        startActivity(intent);
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        TextView error = findViewById(R.id.error);
                        error.setText(exception.getMessage() + " \n" + exception.toString());
                        Toast.makeText(FacebookLoginActivity.this, exception.toString(), Toast.LENGTH_LONG).show();
                        // App code
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


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

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private class APIAsyncTask extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FacebookLoginActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
//                JSONArray array = new JSONArray(s);
//                for(int i=0;i<array.length();i++) {
                JSONObject obj = new JSONObject(s);
                userModel.setFirst_name(obj.getString("UserFName"));
                userModel.setLast_name(obj.getString("UserLName"));
                Log.d(TAG, "onPostExecute: phone number: " + obj.getString("PhoneNumber"));
                userModel.setContactNumber(String.valueOf(obj.getDouble("PhoneNumber")));
                userModel.setEmail(obj.getString("UserEmail"));
                userModel.setUserId(obj.getInt("UserId"));
                Log.d(TAG, "onPostExecute: " + userModel.getUserId());

//                Intent intent = new Intent(FacebookLoginActivity.this, RatingPopUp.class);
////                intent.putExtra("fbUserId", fbUserId);
////                intent.putExtra("first_name", first_name);
////                intent.putExtra("last_name", last_name);
////                intent.putExtra("email", email);
////                intent.putExtra("accessToken", accessToken);
////                intent.putExtra("friendsArray", friendsArray);
////                intent.putExtra("picture", picture);
//                startActivity(intent);
////                }
                new GetRatingAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetSearchOrdersHistory/" + userModel.getUserId());


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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    private class GetRatingAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FacebookLoginActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

//            if(!mainActivity.isFinishing()){
//
//            }

        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                JSONArray orderArray = new JSONArray(s);
                for (int i = 0; i < orderArray.length(); i++) {
                    JSONObject orderObj = orderArray.getJSONObject(i);
                    guidList.add(orderObj.getString("GUID"));
                }
                double totalPrice = 0;

                Set<String> guidUnique = new HashSet<>(guidList);
                for (String guid : guidUnique) {
                    ArrayList<Item> itemRatingList = new ArrayList<>();
                    Order order = new Order();
                    totalPrice = 0;

                    int c = 0;

                    for (int i = 0; i < orderArray.length(); i++) {
                        JSONObject orderObj = orderArray.getJSONObject(i);

                        if (orderObj.getString("GUID").equals(guid)) {
                            if (orderObj.getInt("RatingValue") == 0) {
                                c = 1;
                                Item item = new Item();
                                item.setItem(orderObj.getString("ItemName"));
                                totalPrice = totalPrice + (Double.parseDouble(orderObj.getString("ItemPrice")) * Double.parseDouble(orderObj.getString("Quantity")));

                                item.setPrice(orderObj.getString("ItemPrice"));
                                item.setOrderId(orderObj.getString("OrderId"));
                                item.setQty(orderObj.getString("Quantity"));
                                itemRatingList.add(item);
                                order.setRestaurantName(orderObj.getString("RestaurantName"));
//                                order.setCustomerName(orderObj.getString("UserFirstName"));
//                                order.setGuid(orderObj.getString("GUID"));
                                //order.setDateTime(mainActivity.convertDateTime(orderObj.getString("DateTime")));
//                                order.setDeliveryAddress(orderObj.getString("DeliverAt"));
                                order.setItemList(itemRatingList);


                            }

                        }
//                        order.setTotalPrice(String.valueOf(totalPrice));

                    }
                    if (c == 1) {
                        orderRatingList.add(order);
                        Log.d(TAG, "onPostExecute: adding to orderRatingList");
                    }


                }
                if (orderRatingList.size() == 0) {
                    Intent intent = new Intent(FacebookLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(FacebookLoginActivity.this, RatingPopUp.class);
                    Bundle args = new Bundle();
                    args.putSerializable("orderRatingList",orderRatingList);
                    intent.putExtra("bundle",args);
                    startActivity(intent);
                }


                //Log.d(TAG, "onPostExecute: " + orderHistofryList.size());


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                int tracker = 0;

//                while (tracker == 0) {
//                    Toast.makeText(RatingPopUp.this, "No internet connection, trying to connect...", Toast.LENGTH_SHORT).show();
//                    new GetRatingAPI().execute(url);
//                    if (mainActivity.isConnectedToInternet())
//                        tracker = 1;
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
//                }
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Object... objects) {

            try {
                URL url = new URL((String) objects[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setReadTimeout(7000);
                connection.setConnectTimeout(7000);
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                InputStream istream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "doInBackground: " + line);
                    builder.append(line);


                }
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
