package tastifai.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collections;

import static tastifai.customer.FacebookLoginActivity.userModel;

public class DeliveryStatus extends AppCompatActivity {
    private static final String TAG = "DeliveryStatus";
    android.os.Handler customHandler;
    DeliveryStatusAPI api;
    private Button placed;
    private Button accepted;
    private Button onTheWay;
    private Button delivered;
    private String guid;
    private int userId;
    private SharedPreferences statusPref;
    private TextView heading;
    private TextView message;
    private TextView placedTimeTextView;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_status);
        heading = findViewById(R.id.heading);
        layout = findViewById(R.id.layout);
        placedTimeTextView = findViewById(R.id.placedTime);

        statusPref = getApplicationContext().getSharedPreferences("StatusPref", MODE_PRIVATE);
        if(!statusPref.getString("guid","").equals("") && statusPref.getInt("userId",-1) != -1){
            String restaurantName = statusPref.getString("restaurantName", "");
            heading.setText("Your " + restaurantName + "'s Order");
            String placedTime = statusPref.getString("placedTime","");
            Log.d(TAG, "onCreate: placedTime: " + placedTime);
            placedTimeTextView.setText(placedTime);


            guid = statusPref.getString("guid", "");
            userId = statusPref.getInt("userId",-1);
            Log.d(TAG, "onCreate: guid, userid: " + guid + " " + userId);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            placed = findViewById(R.id.placed);
            accepted = findViewById(R.id.accepted);
            accepted.setBackgroundColor(getResources().getColor(R.color.dark_grey));
            onTheWay = findViewById(R.id.outForDelivery);
            onTheWay.setBackgroundColor(getResources().getColor(R.color.dark_grey));

            delivered = findViewById(R.id.delivered);
            delivered.setBackgroundColor(getResources().getColor(R.color.dark_grey));
            customHandler = new android.os.Handler();
            customHandler.postDelayed(updateTimerThread, 0);

        }else{
            message = findViewById(R.id.message);

            message.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);

        }


        // add back arrow to toolbar

        //guid = getIntent().getStringExtra("guid");
        Log.d(TAG, "onCreate: " + guid);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            Intent intent = new Intent(DeliveryStatus.this, MainActivity.class);
            startActivity(intent);// close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeliveryStatus.this, MainActivity.class);
        startActivity(intent);
    }

    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {

            new DeliveryStatusAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetUserOrderStatus/" + guid + "/" + userId);


            //write here whaterver you want to repeat
                customHandler.postDelayed(this, 6000);


        }

    };

    private class DeliveryStatusAPI extends AsyncTask<Object,String,String> {
        StringBuilder builder = new StringBuilder();

        @Override
        protected void onPostExecute(String s) {
            if(s != null){
                String newString = s.replace("\"", "");
                Log.d(TAG, "onPostExecute: " + newString);
                if(("Order Accepted").equals(newString)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        accepted.setBackground(getResources().getDrawable(R.drawable.delivery_circle));
                    }
                }else if(("On the Way").equals(newString)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        accepted.setBackground(getResources().getDrawable(R.drawable.delivery_circle));
                        onTheWay.setBackground(getResources().getDrawable(R.drawable.delivery_circle));
                    }
                } else if(("Delivered").equals(newString)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        accepted.setBackground(getResources().getDrawable(R.drawable.delivery_circle));
                        onTheWay.setBackground(getResources().getDrawable(R.drawable.delivery_circle));
                        delivered.setBackground(getResources().getDrawable(R.drawable.delivery_circle));
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
            }catch (SocketTimeoutException e) {


                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(i);
                    e.printStackTrace();

            }catch (IOException e) {

                e.printStackTrace();
            }
            return null;

        }
    }
}
