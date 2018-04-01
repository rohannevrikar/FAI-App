package tastifai.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static tastifai.customer.MainActivity.cartItems;
import static tastifai.customer.MainActivity.guid;

public class Cart extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private ArrayList<MenuItemModel> checkoutItems = cartItems;
    private LinearLayoutManager linearLayoutManager;
    private ImageView back;
    public TextView itemTotal;
    public TextView checkoutTotal;
    public static int total;
    private SharedPreferences cartSharedPref;
    private ArrayList<MenuItemModel> checkoutCart;
    private static final String TAG = "Cart";
    private Button btnPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartSharedPref = getSharedPreferences("Cart", Context.MODE_PRIVATE);
        if(cartSharedPref != null){
            Gson gson = new Gson();
            String json = cartSharedPref.getString("cart", null);
            Type type = new TypeToken<ArrayList<MenuItemModel>>(){}.getType();
            checkoutCart = gson.fromJson(json, type);
            Log.d(TAG, "onCreate: checkoutcart created" + checkoutCart.size() + json);
        }

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        itemTotal = findViewById(R.id.itemTotal);
        checkoutTotal = findViewById(R.id.checkoutTotal);

        linearLayoutManager = new LinearLayoutManager(this);
        cartRecyclerView.setLayoutManager(linearLayoutManager);
        for(int i=0;i<checkoutCart.size(); i++){
            total = total + checkoutCart.get(i).getQuantity() * Integer.parseInt(checkoutCart.get(i).getPrice());
        }
        itemTotal.setText(Integer.toString(total));
        checkoutTotal.setText(Integer.toString(total));
        CartAdapter adapter = new CartAdapter(this, checkoutCart, itemTotal, checkoutTotal);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cart.this, MainActivity.class);
                startActivity(intent);
            }
        });
        cartRecyclerView.setAdapter(adapter);
        btnPlaceOrder = findViewById(R.id.placeOrder);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Cart.this, "Placing your order...", Toast.LENGTH_SHORT).show();
                JSONArray ordersArray = new JSONArray();
                JSONObject postData;
//                for(int i=0;i<cartItems.size();i++){
                    postData = new JSONObject();
                    try {
                        postData.put("RestaurantId", cartItems.get(0).getRestaurantId());
                        postData.put("ItemId",  cartItems.get(0).getItemId());
                        postData.put("Quantity",  cartItems.get(0).getQuantity());
                        postData.put("StatusId", 7);
                        postData.put("UserId", 7);
                        postData.put("UserFirstName", "Subhradip");
                        postData.put("UserContactNumber", "9292929292");
                        postData.put("ItemName", cartItems.get(0).getItemName() );
                        postData.put("ItemPrice", cartItems.get(0).getPrice());
                        postData.put("ItemETA", cartItems.get(0).getItemETA());
                        postData.put("DateTime", "2017-08-10T00:00:00");
                        postData.put("DeliverAt", "MSH1302");
                        postData.put("GUID", guid);
                        //ordersArray.put(postData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                }
                JSONObject orderObj = new JSONObject();
                try {
                    orderObj.put("Orders", ordersArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onClick: " + postData);
                new PlaceOrderApi().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/PostOrder", postData.toString());


            }
        });
    }
    private class PlaceOrderApi extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(Cart.this, "Order placed " + s, Toast.LENGTH_SHORT).show();

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
