package tastifai.customer;

import android.content.Context;
import android.database.Cursor;
import android.support.transition.TransitionManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 22-02-2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private View view;
    private Context context;
    private ArrayList<CategoryModel> categoryModelArrayList;
    private final LayoutInflater layoutInflater;
    private int layout;
    ArrayList<MenuItemModel> menuItemModelArrayList = new ArrayList<>();
    private static final String TAG = "CategoryAdapter";
    private int mExpandedPosition = -1;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModelArrayList) {
        this.context = context;
        this.categoryModelArrayList = categoryModelArrayList;
        layoutInflater = LayoutInflater.from(context);
        Log.d(TAG, "CategoryAdapter: " + categoryModelArrayList.size());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //MenuItemModel menuItemModel = new MenuItemModel();
//        Cursor cursor = ((MainActivity)context).databaseHelper.getMenuData();
//        while(cursor.moveToNext()){
//            menuItemModel.setItemName(cursor.getString(1));
//            menuItemModel.setPrice();
//
//        }
//        menuItemModel.setItemName("Chole Bhature");
//        menuItemModel.setPrice("50");
//        menuItemModelArrayList.add(menuItemModel);
//        MenuItemModel menuItemModel1 = new MenuItemModel();
//        menuItemModel1.setItemName("Chicken Cheese Grilled Sandwich");
//        menuItemModel1.setPrice("70");
//        menuItemModelArrayList.add(menuItemModel1);
//        MenuItemModel menuItemModel2 = new MenuItemModel();
//        menuItemModel2.setItemName("Veg Hakka Noodles");
//        menuItemModel2.setPrice("75");
//        menuItemModelArrayList.add(menuItemModel2);

        view = layoutInflater.inflate(R.layout.menu_category_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.category.setText(categoryModelArrayList.get(position).getCategoryName());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.itemRecyclerView.setLayoutManager(layoutManager);
        Log.d(TAG, "onBindViewHolder: " + categoryModelArrayList.get(position).getCategoryName());
        Log.d(TAG, "onBindViewHolder: " + categoryModelArrayList.get(position).getMenuItemModelArrayList().size());
        FAIItemAdapter adapter = new FAIItemAdapter(context, categoryModelArrayList.get(position).getMenuItemModelArrayList());
        holder.itemRecyclerView.setAdapter(adapter);

        final boolean isExpanded = position == mExpandedPosition;
        holder.itemRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemRecyclerView.setActivated(isExpanded);
        if (isExpanded){
            mExpandedPosition = position;
            holder.down.setVisibility(View.GONE);
            holder.up.setVisibility(View.VISIBLE);

        }
        else {
            holder.down.setVisibility(View.VISIBLE);
            holder.up.setVisibility(View.GONE);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                if(!isExpanded){
//
//                }
//                else{
//
//                }
                //Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                mExpandedPosition = isExpanded ? -1 : position;
                Log.d(TAG, "onBindViewHolder: " + isExpanded);

                notifyItemChanged(mExpandedPosition);
                notifyItemChanged(position);

            }
        });


        //Set Adapter for items
    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public RecyclerView itemRecyclerView;
        private CardView cardView;
        private ImageView down;
        private ImageView up;
        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            itemRecyclerView = itemView.findViewById(R.id.itemRecyclerView);
            cardView = itemView.findViewById(R.id.categoryCard);
            down = itemView.findViewById(R.id.down);
            up = itemView.findViewById(R.id.up);
        }
    }
}
