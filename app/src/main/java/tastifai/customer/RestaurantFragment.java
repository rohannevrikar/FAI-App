package tastifai.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Rohan Nevrikar on 15-03-2018.
 */

public class RestaurantFragment extends Fragment {
    private View view;
    private ViewPager viewPager;
    private RestaurantFragmentAdapter adapter;
    private OverviewFragment f_overview;
    private MenuFragment f_menu;
    private ReviewsFragment f_reviews;
    private TextView title;

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.include_viewpager,container,false);
//        viewPager = view.findViewById(R.id.viewpager);
//        setupViewPager(viewPager);
//        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        title = toolbar.findViewById(R.id.title);
//
//        initAction();
//        Bundle bundle = getArguments();
//        if(bundle!=null){
//            restaurantModel = (RestaurantModel) bundle.getSerializable("restaurantData");
//            Log.d("RestaurantFragment", "onCreateView: restaurantModel init");
//            title.setText(restaurantModel.getRestaurantName());
//        }
//
//        return view;
//    }
//    private void initAction() {
//        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
//        Log.d("MainActivity", "initAction: called");
//        viewPager.setCurrentItem(1);
//    }
//
//
//
//    private void setupViewPager(ViewPager viewPager) {
//        adapter = new RestaurantFragmentAdapter(((MainActivity)getActivity()).getSupportFragmentManager());
//        adapter.clearList();
//
//
//        f_overview = new OverviewFragment();
//
//
//        f_menu = new MenuFragment();
//
//
//        f_reviews = new ReviewsFragment();
//
//
//        adapter.addFragment(f_overview,  "Overview");
//        adapter.addFragment(f_menu, "Menu");
//        adapter.addFragment(f_reviews, "Reviews");
//
//        viewPager.setAdapter(adapter);
//        //viewPager.setOffscreenPageLimit(3);
//    }

}

