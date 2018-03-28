package tastifai.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Rohan Nevrikar on 17-03-2018.
 */

public class FoodFragment extends Fragment {
    private ViewPager viewPager;
    private RecommendationsFragment recommendationsFragment;
    private SearchRestaurantFragment searchRestaurantFragment;
    private RestaurantFragmentAdapter adapter;

    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.include_viewpager,container,false);
        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        initAction();
        return view;
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new RestaurantFragmentAdapter(((MainActivity)getActivity()).getSupportFragmentManager());
        adapter.clearList();


        recommendationsFragment = new RecommendationsFragment();


        searchRestaurantFragment = new SearchRestaurantFragment();



        adapter.addFragment(recommendationsFragment,  "FAI");
        adapter.addFragment(searchRestaurantFragment, "Near Me");
        //adapter.addFragment(rushFragment, "Rush");

        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(3);
    }
    //        public void updateViewPager() {
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
//            adapter.notifyDataSetChanged();
//        //viewPager.setOffscreenPageLimit(3);
//    }
    private void initAction() {
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("MainActivity", "initAction: called");
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }

}
