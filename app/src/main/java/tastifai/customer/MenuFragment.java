package tastifai.customer;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.app.Activity.RESULT_OK;
import static tastifai.customer.MainActivity.cartItems;
import static tastifai.customer.MainActivity.progressDialog;
import static tastifai.customer.SearchRestaurantAdapter.restaurantModel;

/**
 * Created by Rohan Nevrikar on 19-02-2018.
 */
public class MenuFragment extends Fragment {
    private View view;
    private ImageView backImage;
    private RecyclerView categoryRecyclerView;
    private ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<>();
    private RelativeLayout cartLayout;
    private HorizontalScrollMenuView horizontalScrollMenuView;
    private Toolbar toolbar;
    private TextView viewCart;
    private TextView name;
    private TextView cartQty;
    private static final String TAG = "MenuFragment";
    private ImageView menuImage;
    public static Set<String> categories;
    RecyclerView.SmoothScroller smoothScroller;
    LinearLayoutManager layoutManager;
    String url;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_menu,container,false);
//        NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
//        scrollView.setNestedScrollingEnabled(false);
        name = view.findViewById(R.id.name);
        name.setText(restaurantModel.getRestaurantName());
        horizontalScrollMenuView = view.findViewById(R.id.horizontalMenu);
//        menuImage = view.findViewById(R.id.menu);
//
//
//        menuImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(getActivity(), MenuPopUp.class);
////                startActivity(intent);
//                startActivityForResult(new Intent(getActivity(), MenuPopUp.class), 999);
//
//            }
//        });

        Log.d(TAG, "onCreateView: " + restaurantModel.getId());
        url = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantMenuItems/" + restaurantModel.getId();
        final  AppCompatActivity act = (AppCompatActivity) getActivity();
        ((MainActivity)getActivity()).getSupportActionBar();
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(((MainActivity)getActivity()).isConnectedToInternet())
            new APIAsyncTask().execute(url);
        else
            ((MainActivity)getActivity()).setUpAlert();
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setNestedScrollingEnabled(false);


        initMenu();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 999 && resultCode == RESULT_OK){
//           smoothScroller.setTargetPosition(data.getIntExtra("position", -1));
//            layoutManager.startSmoothScroll(smoothScroller);
            Toast.makeText(getActivity(), "" + data.getIntExtra("position", -1), Toast.LENGTH_SHORT).show();
//            categoryRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
//
            //layoutManager.smoothScrollToPosition(categoryRecyclerView, RecyclerView.State, data.getIntExtra("position", -1));
            //categoryRecyclerView.smoothScrollToPosition(data.getIntExtra("position", -1));
            //categoryRecyclerView.getLayoutManager().scrollToPosition(data.getIntExtra("position", -1));
//            layoutManager.scrollToPositionWithOffset(data.getIntExtra("position", -1), 0);
            categoryRecyclerView.scrollToPosition(data.getIntExtra("position", -1));
            //categoryRecyclerView.smoothScrollToPosition(data.getIntExtra("position", -1));


        }
    }

        private void initMenu(){

            String string = "\u20B9";
            byte[] utf8 = null;
            try {
                utf8 = string.getBytes("UTF-8");
                string = new String(utf8, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "initMenu: " + restaurantModel.getTiming());
            if(((!restaurantModel.getTiming().equals("null")) || (!restaurantModel.getTiming().equals("")))){
                horizontalScrollMenuView.addItem(restaurantModel.getTiming().trim(), R.mipmap.clock);

            }else
                horizontalScrollMenuView.addItem("10:00 AM - 10:00 PM", R.mipmap.clock);
            double value = restaurantModel.getDistance();
            double rounded = (double) Math.round(value * 10) / 10;
            horizontalScrollMenuView.addItem(String.valueOf(rounded) + "km" ,R.mipmap.distancetores);
            horizontalScrollMenuView.addItem(string + "100 FOR ONE", R.mipmap.person);
    }
private class APIAsyncTask extends AsyncTask<Object,String,String> {
    StringBuilder builder = new StringBuilder();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                JSONArray array = new JSONArray(s);
                ArrayList<String> categoryList = new ArrayList<>();
                for(int i=0;i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);


                    categoryList.add(obj.getString("ItemType"));
                }
                categories = new HashSet<>(categoryList);
                for(String category : categories){
                    Log.d(TAG, "onPostExecute: " + category);
                    ArrayList<MenuItemModel> menuList = new ArrayList<>();
                    CategoryModel model = new CategoryModel();
                    model.setCategoryName(category);
                    for(int i=0;i<array.length();i++){
                        JSONObject obj = array.getJSONObject(i);

                        if(obj.getString("ItemType").equals(category)){
                            MenuItemModel menuItem = new MenuItemModel();
                            menuItem.setItemName(obj.getString("ItemName"));
                            menuItem.setPrice(obj.getString("ItemPrice"));
                            menuItem.setItemETA(obj.getInt("ItemETA"));
                            menuItem.setItemId(obj.getString("ItemId"));
                            menuItem.setCategory(obj.getString("ItemType"));
                            menuItem.setRestaurantId(obj.getString("RestaurantID"));
                            menuItem.setRestaurantName(restaurantModel.getRestaurantName());
                            menuItem.setDeliveryCharges(restaurantModel.getDeliveryCharges());
                            menuItem.setQuantity(1);
                            menuItem.setClicked(false);
                            menuList.add(menuItem);

                            model.setMenuItemModelArrayList(menuList);

                        }
                    }

                    categoryModelArrayList.add(model);

                }
                Log.d(TAG, "onPostExecute: " + categoryModelArrayList.size());
                 layoutManager = new LinearLayoutManager(getActivity());

                categoryRecyclerView.setLayoutManager(layoutManager);
                CategoryAdapter adapter = new CategoryAdapter(getActivity(), categoryModelArrayList);
                categoryRecyclerView.setAdapter(adapter);

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }catch (NullPointerException e){
            int tracker = 0;

            while (tracker == 0) {
                Toast.makeText(getActivity(), "No internet connection, trying to connect...", Toast.LENGTH_SHORT).show();
                new APIAsyncTask().execute(url);
                if (((MainActivity) getActivity()).isConnectedToInternet())
                    tracker = 1;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
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
