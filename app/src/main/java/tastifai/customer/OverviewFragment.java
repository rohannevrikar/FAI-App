package tastifai.customer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import static tastifai.customer.MainActivity.cartItems;
import static tastifai.customer.MainActivity.restaurantModel;

/**
 * Created by Rohan Nevrikar on 19-02-2018.
 */

public class OverviewFragment extends Fragment {
    private View view;
    private TextView restaurantName;
    private TextView cuisine;
    private TextView title;
    private ImageView backImage;
    private HorizontalScrollMenuView horizontalScrollMenuView;
    private android.support.v7.widget.Toolbar toolbar;
    private RelativeLayout cartLayout;
    private TextView cartQty;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_overview,container,false);
        horizontalScrollMenuView = view.findViewById(R.id.horizontalMenu);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
//        toolbar = view.findViewById(R.id.viewPagerToolbar);
        toolbar.setBackgroundResource(R.drawable.toolbar_bg);
//        backImage = toolbar.findViewById(R.id.back);
//        backImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
//        backImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().onBackPressed();
//            }
//        });
        cartLayout = toolbar.findViewById(R.id.cartLayout);
        cartQty = cartLayout.findViewById(R.id.cartQty);

        if(cartItems.size() == 0){
            cartLayout.setVisibility(View.GONE);
        }
        else
            cartLayout.setVisibility(View.VISIBLE);
        cartQty.setText("" + cartItems.size());

        restaurantName = view.findViewById(R.id.name);
        restaurantName.setText(restaurantModel.getRestaurantName());
        cuisine = view.findViewById(R.id.cuisine);
        //cuisine.setText(restaurantModel.getCuisine());
        //initMenu();
        return view;
    }
//    private void initMenu() {
//        horizontalScrollMenuView.addItem(restaurantModel.getOpenTime() + " - " + restaurantModel.getCloseTime(), R.drawable.ic_clock);
//        horizontalScrollMenuView.addItem("1.5km", R.drawable.ic_action_name);
//        horizontalScrollMenuView.addItem("DELIVERY", R.drawable.ic_delivery);
//        horizontalScrollMenuView.setOnHSMenuClickListener(new HorizontalScrollMenuView.OnHSMenuClickListener() {
//            @Override
//            public void onHSMClick(tastifai.customer.MenuItem menuItem, int position) {
//            }
//
//
//        });
//
//    }

}
