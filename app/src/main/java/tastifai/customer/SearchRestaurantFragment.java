package tastifai.customer;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static tastifai.customer.MainActivity.restaurantModelArrayList;
import static tastifai.customer.MainActivity.toolbar;

/**
 * Created by Rohan Nevrikar on 18-02-2018.
 */

public class SearchRestaurantFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private Cursor cursor;
    private Toolbar toolbar;
    TextView title;
    private static final String TAG = "SearchRestaurantFragmen";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.restaurants_list,container,false);
        ((MainActivity)getActivity()).findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).findViewById(R.id.app_bar_layout).setVisibility(View.VISIBLE);
//        title = toolbar.findViewById(R.id.title);
//        title.setText("Menu");
        recyclerView = view.findViewById(R.id.restaurantRecyclerView);
//        cursor = ((MainActivity)getActivity()).databaseHelper.getRestaurantData();
//        if(cursor.getCount() == 0){
//            Toast.makeText(getActivity(), "No online restaurants", Toast.LENGTH_SHORT).show();
//
//        }
//        else
//        {
//
//            Log.d(TAG, "onCreateView: " + cursor.getCount());
//            while (cursor.moveToNext()){
//                Log.d(TAG, "onCreateView: " + cursor.getPosition());
//                WebRestaurantModel model = new WebRestaurantModel();
//                model.setId(cursor.getString(0));
//                model.setRestaurantName(cursor.getString(1));
//                restaurantModelArrayList.add(model);
//
//            }
//        }
//        WebRestaurantModel restaurantModel = new WebRestaurantModel();
//        restaurantModel.setRestaurantName("Cafe Tanstaafl");
////        restaurantModel.setCloseTime("3:00");
////        restaurantModel.setOpenTime("6:00");
////        restaurantModel.setCuisine("Fast food");
////        restaurantModel.setRating("4.8");
//        restaurantModelArrayList.add(restaurantModel);
//        WebRestaurantModel restaurantModel2 = new WebRestaurantModel();
//        restaurantModel2.setRestaurantName("Radhika");
////        restaurantModel2.setCloseTime("3:00");
////        restaurantModel2.setOpenTime("6:00");
////        restaurantModel2.setCuisine("South Indian");
////        restaurantModel2.setRating("4.5");
//        restaurantModelArrayList.add(restaurantModel2);
//        WebRestaurantModel restaurantModel3 = new WebRestaurantModel();
//        restaurantModel3.setRestaurantName("Tapri");
////        restaurantModel3.setCloseTime("3:00");
////        restaurantModel3.setOpenTime("6:00");
////        restaurantModel3.setCuisine("Coffee/Tea stop");
////        restaurantModel3.setRating("3.5");
//        restaurantModelArrayList.add(restaurantModel3);
//        WebRestaurantModel restaurantModel4 = new WebRestaurantModel();
//        restaurantModel4.setRestaurantName("Tea post");
////        restaurantModel4.setCloseTime("3:00");
////        restaurantModel4.setOpenTime("6:00");
////        restaurantModel4.setCuisine("Coffee/Tea stop");
////        restaurantModel4.setRating("5.0");
//        restaurantModelArrayList.add(restaurantModel4);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        SearchRestaurantAdapter adapter = new SearchRestaurantAdapter(getActivity(), restaurantModelArrayList);
        recyclerView.setAdapter(adapter);
//        Spinner spinner = toolbar.findViewById(R.id.spinner);
//        ArrayList<String> dropdown = new ArrayList<>();
//        dropdown.add("");
//        dropdown.add("Sort by");
//        dropdown.add("Filter by");
//        dropdown.add("Currency");
//        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, dropdown);
//        spinner.setAdapter(arrayAdapter);


        return view;
    }


}
