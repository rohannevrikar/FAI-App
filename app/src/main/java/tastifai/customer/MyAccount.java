package tastifai.customer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Rohan Nevrikar on 28-02-2018.
 */

public class MyAccount extends Fragment {
    private View view;
    private ImageView profilePic;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile,container,false);
        profilePic = view.findViewById(R.id.profilepic);
//        Bitmap bitmap = ((BitmapDrawable)((MainActivity)getActivity()).getProfilePic().getDrawable()).getBitmap();
//        profilePic.setImageBitmap(bitmap);
//


        return view;
    }
}
