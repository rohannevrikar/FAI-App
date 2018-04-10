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

import static tastifai.customer.MainActivity.cartItems;
import static tastifai.customer.MainActivity.mostOrderedRestaurantList;
import static tastifai.customer.MainActivity.recommendationsList;
import static tastifai.customer.MainActivity.restaurantModelArrayList;

/**
 * Created by Rohan Nevrikar on 15-03-2018.
 */

public class RecommendationsFragment extends Fragment {
    private View view;
    private RecyclerView restaurantsRecommendationRecyclerView;
    private RecyclerView usersRecommendationRecyclerView;

    private ArrayList<CategoryModel> faiCategoryModelArrayList;
    private static final String TAG = "RecommendationsFragment";
    private ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fai_layout,container,false);
        faiCategoryModelArrayList = new ArrayList<>();
        restaurantsRecommendationRecyclerView = view.findViewById(R.id.restaurantsRecommendationRecyclerView);
        usersRecommendationRecyclerView = view.findViewById(R.id.usersRecommendationRecyclerView);
        Log.d(TAG, "onCreateView: ");
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());

        restaurantsRecommendationRecyclerView.setLayoutManager(layoutManager);
        usersRecommendationRecyclerView.setLayoutManager(layoutManager2);
        RestaurantRecommendationAdapter restaurantRecommendationAdapter= new RestaurantRecommendationAdapter(getActivity(), mostOrderedRestaurantList, restaurantModelArrayList);
        UserRecommendationAdapter userRecommendationAdapteradapter = new UserRecommendationAdapter(getActivity(), recommendationsList);

        restaurantsRecommendationRecyclerView.setAdapter(restaurantRecommendationAdapter);
        usersRecommendationRecyclerView.setAdapter(userRecommendationAdapteradapter);

        return view;
    }

}
