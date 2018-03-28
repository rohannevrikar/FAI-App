package tastifai.customer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 18-02-2018.
 */

public class SearchRestaurantAdapter extends RecyclerView.Adapter<SearchRestaurantAdapter.ViewHolder> {
    private View view;
    private Context context;
    private ArrayList<WebRestaurantModel> restaurantModelArrayList;
    private final LayoutInflater layoutInflater;
    public static WebRestaurantModel restaurantModel;

    public SearchRestaurantAdapter(Context context, ArrayList<WebRestaurantModel> restaurantModelArrayList) {
        this.context = context;
        this.restaurantModelArrayList = restaurantModelArrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = layoutInflater.inflate(R.layout.restaurant_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.restaurantName.setText(restaurantModelArrayList.get(position).getRestaurantName());
        //holder.cuisine.setText(restaurantModelArrayList.get(position).getCuisine());
        holder.image.setImageResource(R.drawable.foodpic);
        //holder.ratingValue.setText(restaurantModelArrayList.get(position).getRating());
        //holder.timings.setText(restaurantModelArrayList.get(position).getOpenTime() + " - " + restaurantModelArrayList.get(position).getCloseTime());
        //holder.ratingBar.setRating(Float.parseFloat(restaurantModelArrayList.get(position).getRating()));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurantModel = restaurantModelArrayList.get(position);
//                ((MainActivity)context).findViewById(R.id.frame_layout).setVisibility(View.GONE);
//                ((MainActivity)context).findViewById(R.id.restaurantFragment).setVisibility(View.VISIBLE);
                ((MainActivity)context).getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_layout, new MenuFragment()).commit();

                //((MainActivity)context).updateViewPager();
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return restaurantModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView restaurantName;
        private TextView cuisine;
        private TextView timings;
        private RatingBar ratingBar;
        private TextView ratingValue;
        private RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            restaurantName = itemView.findViewById(R.id.name);
            cuisine = itemView.findViewById(R.id.cuisine);
            timings = itemView.findViewById(R.id.timings);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingValue = itemView.findViewById(R.id.ratingValue);
            relativeLayout = itemView.findViewById(R.id.clickableLayout);
        }
    }
}
