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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rohan Nevrikar on 14-03-2018.
 */

public class FriendsFragment extends Fragment implements AsyncResponse {

    //    private FriendsFragmentAdapter adapter;
//    private BestMatchFragment bestMatchFragment;
//    private NearMeFragment nearMeFragment;
//    private ChatFragment chatFragment;
//    private ViewPager viewPager;
    private SharedPreferences sharedPreferences;
    private View view;
    private ArrayList<FriendsModel> friendsArrayList;
    JSONArray friendList = null;
    String name;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private int count = 0;
    private TextView message;
    private static final String TAG = "FriendsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("FacebookPref", MODE_PRIVATE);

        view = inflater.inflate(R.layout.friends_layout, container, false);
        recyclerView = view.findViewById(R.id.friendsRecyclerView);
        message = view.findViewById(R.id.noFriendsMessage);
        friendsArrayList = new ArrayList();
        try {
            friendList = ((MainActivity) getActivity()).friendsArrayFromFacebook();
            if(friendList.length() == 0){
                message.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else{
                for (int i = 0; i < friendList.length(); i++) {
                    FacebookAsyncTask asyncTask = new FacebookAsyncTask(getActivity());
                    asyncTask.delegate = (AsyncResponse) this;
                    asyncTask.execute("https://graph.facebook.com/" + friendList.getJSONObject(i).getString("id") + "/picture?type=large");
                    Log.d("FriendsFragment", "onCreateView: " + friendList.getJSONObject(i).getString("name"));

                }
            }

        } catch (JSONException e) {
            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e){
            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void processFinish(Bitmap output) {
        FriendsModel model = new FriendsModel();
        try {
            name = friendList.getJSONObject(count).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        model.setName(name);
        model.setProfilePicture(output);
        Log.d("FriendsFragment", "processFinish: Adding.." + name);
        friendsArrayList.add(model);
        if (friendsArrayList.size() == friendList.length()) {
            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            Log.d("FriendsFragment", "processFinish: " + friendsArrayList.size());
            FriendsAdapter adapter = new FriendsAdapter(getActivity(), friendsArrayList);
            recyclerView.setAdapter(adapter);

        }
        count = count + 1;

    }

}
    //setupViewPager(viewPager);
    //initAction();


//
//    private void setupViewPager(ViewPager viewPager) {
//        adapter = new FriendsFragmentAdapter(((MainActivity) getActivity()).getSupportFragmentManager());
//        adapter.clearList();
//
//
//        bestMatchFragment = new BestMatchFragment();
//        nearMeFragment = new NearMeFragment();
//        chatFragment = new ChatFragment();
//
//        adapter.addFragment(bestMatchFragment, "Best Match");
//        adapter.addFragment(nearMeFragment, "Near Me");
//        adapter.addFragment(chatFragment, "Chat");
//
//        viewPager.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        viewPager.setOffscreenPageLimit(3);
//    }
//
//    //        public void updateViewPager() {
////        adapter.clearList();
////
////
////        f_overview = new OverviewFragment();
////
////
////        f_menu = new MenuFragment();
////
////
////        f_reviews = new ReviewsFragment();
////
////
////        adapter.addFragment(f_overview,  "Overview");
////        adapter.addFragment(f_menu, "Menu");
////        adapter.addFragment(f_reviews, "Reviews");
////
////        viewPager.setAdapter(adapter);
////            adapter.notifyDataSetChanged();
////        //viewPager.setOffscreenPageLimit(3);
////    }
//    private void initAction() {
//        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
//        Log.d("MainActivity", "initAction: called");
//        viewPager.setCurrentItem(0);
//    }




