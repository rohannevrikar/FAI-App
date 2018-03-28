package tastifai.customer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import static tastifai.customer.SearchRestaurantAdapter.restaurantModel;

/**
 * Created by Rohan Nevrikar on 15-03-2018.
 */

public class RecommendationsFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<CategoryModel> faiCategoryModelArrayList;
    private static final String TAG = "RecommendationsFragment";
    private ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fai_layout,container,false);
        faiCategoryModelArrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.faiRecyclerView);
//        String url = "http://foodspecwebapi.us-east-1.elasticbeanstalk.com/api/FoodSpec/GetRestaurantMenuItems/" + restaurantModel.getId();
//        new APIAsyncTask().execute(url);

//        CategoryModel categoryModel = new CategoryModel();
//        categoryModel.setCategoryName("Sandwiches and Burgers");
//        faiCategoryModelArrayList.add(categoryModel);
//        CategoryModel categoryModel1 = new CategoryModel();
//        categoryModel1.setCategoryName("Chinese");
//        faiCategoryModelArrayList.add(categoryModel1);
//        CategoryModel categoryModel2 = new CategoryModel();
//        categoryModel2.setCategoryName("Dessert");
//        faiCategoryModelArrayList.add(categoryModel2);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//        CategoryAdapter adapter = new CategoryAdapter(getActivity(), faiCategoryModelArrayList);
//        recyclerView.setAdapter(adapter);

        return view;
    }
    private class APIAsyncTask extends AsyncTask<Object,String,String> {
        StringBuilder builder = new StringBuilder();

        @Override
        protected void onPostExecute(String s) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    JSONArray array = new JSONArray(s);
                    ArrayList<String> categoryList = new ArrayList<>();
                    for(int i=0;i<array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
//                    if(!((MainActivity)getActivity()).databaseHelper.insertMenuData(Integer.parseInt(obj.getString("ItemId")), obj.getString("ItemName"), obj.getString("ItemDescription"), obj.getString("ItemType"), obj.getString("ItemPrice"))){
//                        Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                        Toast.makeText(getActivity(), "Menu added", Toast.LENGTH_SHORT).show();

                        categoryList.add(obj.getString("ItemType"));
                    }
                    Set<String> categories = new HashSet<>(categoryList);
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
                                menuItem.setQuantity(1);
                                menuItem.setClicked(false);
                                menuList.add(menuItem);

                                model.setMenuItemModelArrayList(menuList);

                            }
                        }

                        categoryModelArrayList.add(model);

                    }
                    Log.d(TAG, "onPostExecute: " + categoryModelArrayList.size());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    CategoryAdapter adapter = new CategoryAdapter(getActivity(), categoryModelArrayList);
                    recyclerView.setAdapter(adapter);

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
