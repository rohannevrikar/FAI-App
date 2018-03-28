package tastifai.customer;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rohan Nevrikar on 14-03-2018.
 */

public class FriendsFragment extends Fragment{
    private View view;

    private FriendsFragmentAdapter adapter;
    private BestMatchFragment bestMatchFragment;
    private NearMeFragment nearMeFragment;
    private ChatFragment chatFragment;
    private ViewPager viewPager;
    private SharedPreferences sharedPreferences;

    private static final String TAG = "FriendsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.include_viewpager,container,false);
        viewPager = view.findViewById(R.id.viewpager);
        sharedPreferences = getActivity().getSharedPreferences("FacebookPref", MODE_PRIVATE);


        setupViewPager(viewPager);
        initAction();

        return view;
    }
    private void setupViewPager(ViewPager viewPager) {
        adapter = new FriendsFragmentAdapter(((MainActivity)getActivity()).getSupportFragmentManager());
        adapter.clearList();


        bestMatchFragment = new BestMatchFragment();
        nearMeFragment = new NearMeFragment();
        chatFragment = new ChatFragment();

        adapter.addFragment(bestMatchFragment,  "Best Match");
        adapter.addFragment(nearMeFragment, "Near Me");
        adapter.addFragment(chatFragment, "Chat");

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

