package tastifai.customer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rohan Nevrikar on 25-03-2018.
 */

public class JourneyFragment extends Fragment {
    private View view;
    private ArrayList<Order> orderHistoryList = new ArrayList<>();
    private RecyclerView orderHistoryRecyclerView;
    private JourneyAdapter adapter;
    private static final String TAG = "JourneyFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_history,container,false);
        orderHistoryRecyclerView = view.findViewById(R.id.order_history_recyclerview);
        new JourneyAPI().execute("http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetSearchOrdersHistory/7");

        return view;
    }
    private class JourneyAPI extends AsyncTask<Object, String, String> {
        StringBuilder builder = new StringBuilder();
        String text;
        String dateTime, deliverAt, itemName, itemPrice, quantity;
        ArrayList<String> guidList = new ArrayList<>();
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
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
                for(int i=0;i<orderArray.length();i++){
                    JSONObject orderObj = orderArray.getJSONObject(i);
                    guidList.add(orderObj.getString("GUID"));
                }
                double totalPrice = 0;

                Set<String> guidUnique = new HashSet<>(guidList);
                for(String guid : guidUnique){
                    ArrayList<Item> itemList = new ArrayList<>();
                    Order order = new Order();
                    totalPrice = 0;


                    for(int i=0;i<orderArray.length(); i++){
                        JSONObject orderObj = orderArray.getJSONObject(i);

                        if(orderObj.getString("GUID").equals(guid)){
                            Item item = new Item();
                            item.setItem(orderObj.getString("ItemName"));
                            totalPrice = totalPrice + (Double.parseDouble(orderObj.getString("ItemPrice")) * Double.parseDouble(orderObj.getString("Quantity")));

                            item.setPrice(orderObj.getString("ItemPrice"));
                            item.setQty(orderObj.getString("Quantity"));
                            itemList.add(item);
                            order.setRestaurantName(orderObj.getString("RestaurantName"));
                            order.setCustomerName(orderObj.getString("UserFirstName"));
                            order.setGuid(orderObj.getString("GUID"));
                            order.setDateTime(((MainActivity)getActivity()).convertDateTime(orderObj.getString("DateTime")));
                            order.setDeliveryAddress(orderObj.getString("DeliverAt"));
                            order.setItemList(itemList);
                        }

                    }
                    order.setTotalPrice(String.valueOf(totalPrice));

                    orderHistoryList.add(order);

                }
                Log.d(TAG, "onPostExecute: " + orderHistoryList.size());

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                orderHistoryRecyclerView.setLayoutManager(layoutManager);
                adapter = new JourneyAdapter(getActivity(), orderHistoryList);
                orderHistoryRecyclerView.setAdapter(adapter);



            } catch (JSONException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                if(getActivity()!=null){
//                    if(!getActivity(){
//                        Toast.makeText(getActivity(), "Trying to connect to the internet..", Toast.LENGTH_SHORT).show();
//
//                    }
                }

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
                InputStream istream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("OrderHistory", "doInBackground: " + line);
                    builder.append(line);



                }
                int responseCode = connection.getResponseCode();
                Log.d("OrderHistory", "Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("OrderHistory", "doInBackground: " + responseCode + " " + builder.toString());
                    String[] myArray = builder.toString().split(",");
                    Log.d("OrderHistory", "onPostExecute: " + myArray[0]);
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
