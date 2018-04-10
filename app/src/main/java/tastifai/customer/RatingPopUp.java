package tastifai.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static tastifai.customer.FacebookLoginActivity.userModel;
import static tastifai.customer.MainActivity.cartItems;
import static tastifai.customer.RatingItemAdapter.ratingPOJOArrayList;

public class RatingPopUp extends AppCompatActivity {
    ProgressDialog progressDialog;
    private ArrayList<Order> ratingOrderList;
    private RecyclerView orderRatingRecyclerView;
    private ArrayList<Order> orderRatingList;

    private String url = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetSearchOrdersHistory/" + userModel.getUserId();
    private static final String TAG = "RatingPopUp";
    private Button ratingButton;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_pop_up);
        orderRatingRecyclerView = findViewById(R.id.ratingRecyclerView);
        ratingButton = findViewById(R.id.rateButton);
        ratingButton.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(RatingPopUp.this);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
        if(args != null)
            orderRatingList = (ArrayList<Order>) args.getSerializable("orderRatingList");
        else{
            Toast.makeText(this, "Oops, something went wrong, trying again", Toast.LENGTH_SHORT).show();
            Intent faultIntent = new Intent(RatingPopUp.this, FacebookLoginActivity.class);
            startActivity(faultIntent);
        }
        orderRatingRecyclerView.setLayoutManager(layoutManager);
        RatingAdapter adapter = new RatingAdapter(RatingPopUp.this, orderRatingList);
        orderRatingRecyclerView.setAdapter(adapter);
        ratingButton.setVisibility(View.VISIBLE);
        Log.d(TAG, "onCreate: user id: " + userModel.getUserId());
        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray ratingsOrdersArray = new JSONArray();
                JSONObject postData;
//                if (ratingPOJOArrayList.size() == 0) {
//                    Toast.makeText(RatingPopUp.this, "Please rate all the items", Toast.LENGTH_LONG).show();
//
//                } else {
                    for (int i = 0; i < orderRatingList.size(); i++) {
                        for(Item item : orderRatingList.get(i).getItemList()){
                            postData = new JSONObject();

                            //ArrayList<Item> itemArrayList = (ArrayList<Item>) ratingItemList;

                            Log.d(TAG, "onClick: " + item.getUserId() + " " + item.getRatingValue() + " " + item.getOrderId());
                            try {
                                postData.put("UserID", item.getUserId());
                                postData.put("RatingValue", item.getRatingValue());
                                postData.put("OrderID", item.getOrderId());
                                ratingsOrdersArray.put(postData);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

//                        try {
//                            if (ratingPOJOArrayList.get(i).getRatingValue() == 0) {
//                                Toast.makeText(RatingPopUp.this, "Please rate all the items", Toast.LENGTH_LONG).show();
//
//                            } else {
//                                Log.d(TAG, "onClick: " + ratingPOJOArrayList.get(i).getUserId() + " " + ratingPOJOArrayList.get(i).getRatingValue() + " " + ratingPOJOArrayList.get(i).getOrderId());
//                                postData.put("UserID", ratingPOJOArrayList.get(i).getUserId());
//                                postData.put("RatingValue", ratingPOJOArrayList.get(i).getRatingValue());
//                                postData.put("OrderID", ratingPOJOArrayList.get(i).getOrderId());
//                                ratingsOrdersArray.put(postData);
//
//                                JSONObject orderObj = new JSONObject();
//                                try {
//                                    orderObj.put("Ratings", ratingsOrdersArray);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                new PostRatingAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/PostOrderRatings", ratingsOrdersArray.toString());
//
//                            }
//
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }

//                }
                new PostRatingAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/PostOrderRatings", ratingsOrdersArray.toString());


            }
        });


    }





    private class PostRatingAPI extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            Intent intent = new Intent(RatingPopUp.this, MainActivity.class);
            startActivity(intent);

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
//                connection.setDoInput(true);
//                connection.setRequestProperty("Content-Type", "application/json");
//                connection.setRequestProperty("Accept", "application/json");
//                connection.setR(7000);
//                connection.setConnectTimeout(7000);
                connection.connect();
                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(data);
// json data
                writer.close();
//                out = new BufferedOutputStream(connection.getOutputStream());
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                writer.write(object);
//                writer.flush();
//
//                writer.close();

//                out.close();


                InputStream istream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "doInBackground: " + line);
                    builder.append(line);


                }
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
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
