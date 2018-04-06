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

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 14-03-2018.
 */

public class MeFragment extends Fragment {
    private View view;
    private ViewPager viewPager;
    private MeFragmentAdapter adapter;
    private CartFragment cartFragment;
    private AccountFragment accountFragment;
    private JourneyFragment journeyFragment;
    private static final String TAG = "MeFragment";
    public static ArrayList<String> settingsOptions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.include_viewpager,container,false);
        viewPager = view.findViewById(R.id.viewpager);
        if(settingsOptions.size() != 0){
            settingsOptions.clear();
        }
//        settingsOptions.add("Edit Bio");
//        settingsOptions.add("Manage Addresses");
//        settingsOptions.add("Referrals");
//        settingsOptions.add("Offers");
        settingsOptions.add("Logout");

        setupViewPager(viewPager);
        initAction();

        return view;
    }
    private void setupViewPager(ViewPager viewPager) {
        adapter = new MeFragmentAdapter(((MainActivity)getActivity()).getSupportFragmentManager());
        adapter.clearList();


        cartFragment = new CartFragment();
        journeyFragment = new JourneyFragment();
        accountFragment = new AccountFragment();

        //adapter.addFragment(cartFragment,  "Cart");
        adapter.addFragment(journeyFragment, "Journey");
        adapter.addFragment(accountFragment, "Account");

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(3);
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
        viewPager.setCurrentItem(0);
    }

}
