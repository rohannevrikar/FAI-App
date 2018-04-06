package tastifai.customer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static tastifai.customer.FacebookLoginActivity.userModel;
import static tastifai.customer.MainActivity.pictureBitmap;

/**
 * Created by Rohan Nevrikar on 25-03-2018.
 */

public class AccountFragment extends Fragment {
    private View view;
    private ImageView userImage;
    private SharedPreferences sharedPreferences;
    private TextView name;
    private TextView email;
    private TextView phoneNumber;

    private RecyclerView settingsRecyclerView;
    private LinearLayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.me_layout, container, false);
        userImage = view.findViewById(R.id.profilePic);
        userImage.setImageBitmap(pictureBitmap);
        sharedPreferences = getApplicationContext().getSharedPreferences("FacebookPref", MODE_PRIVATE);
        settingsRecyclerView = view.findViewById(R.id.settingsRecyclerView);
        name = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        if(sharedPreferences != null){
            name.setText(userModel.getFirst_name() + " " + userModel.getLast_name());
            email.setText(userModel.getEmail());
            phoneNumber.setText(userModel.getContactNumber());
        }
        layoutManager = new LinearLayoutManager(getActivity());

        settingsRecyclerView.setLayoutManager(layoutManager);
        SettingsAdapter adapter = new SettingsAdapter(getActivity(), MeFragment.settingsOptions);
        settingsRecyclerView.setAdapter(adapter);
        return view;

    }
}
