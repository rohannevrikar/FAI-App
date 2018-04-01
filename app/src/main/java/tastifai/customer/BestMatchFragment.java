package tastifai.customer;


import android.graphics.Bitmap;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 17-03-2018.
 */

public class BestMatchFragment extends Fragment implements AsyncResponse {
    private View view;
    private ArrayList<FriendsModel> friendsArrayList;
    JSONArray friendList = null;
    String name;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.friends_layout, container, false);
        recyclerView = view.findViewById(R.id.friendsRecyclerView);
        friendsArrayList = new ArrayList();
        try {
            friendList = ((MainActivity) getActivity()).friendsArrayFromFacebook();

            for (int i = 0; i < friendList.length(); i++) {
                FacebookAsyncTask asyncTask = new FacebookAsyncTask(getActivity());
                name = friendList.getJSONObject(i).getString("name");
                asyncTask.delegate = (AsyncResponse) this;
                asyncTask.execute("https://graph.facebook.com/" + friendList.getJSONObject(i).getString("id") + "/picture?type=large");
                Log.d("FriendsFragment", "onCreateView: " + friendList.getJSONObject(i).getString("name"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void processFinish(Bitmap output) {
        FriendsModel model = new FriendsModel();
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

    }
}
