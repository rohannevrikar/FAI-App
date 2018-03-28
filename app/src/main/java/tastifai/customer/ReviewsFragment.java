package tastifai.customer;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static tastifai.customer.MainActivity.cartItems;


/**
 * Created by Rohan Nevrikar on 19-02-2018.
 */

public class ReviewsFragment extends Fragment {
    private View view;
    private ImageView backImage;
    private TextView title;
    private Toolbar toolbar;
    private RelativeLayout cartLayout;
    private TextView cartQty;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_reviews,container,false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        toolbar = view.findViewById(R.id.viewPagerToolbar);
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
//        backImage = toolbar.findViewById(R.id.back);
//        backImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
        cartLayout = toolbar.findViewById(R.id.cartLayout);
        cartQty = cartLayout.findViewById(R.id.cartQty);

        if(cartItems.size() == 0){
            cartLayout.setVisibility(View.GONE);
        }
        else
            cartLayout.setVisibility(View.VISIBLE);
        cartQty.setText("" + cartItems.size());

        return view;
    }
}
