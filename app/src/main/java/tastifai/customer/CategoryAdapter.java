package tastifai.customer;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        return new ViewHolder(view);    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.category.setText(categoryModelArrayList.get(position).getCategoryName());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.itemRecyclerView.setLayoutManager(layoutManager);
        Log.d(TAG, "onBindViewHolder: " + categoryModelArrayList.get(position).getCategoryName());
        Log.d(TAG, "onBindViewHolder: " + categoryModelArrayList.get(position).getMenuItemModelArrayList().size());

        FAIItemAdapter adapter = new FAIItemAdapter(context, categoryModelArrayList.get(position).getMenuItemModelArrayList());
            holder.itemRecyclerView.setAdapter(adapter);






        //Set Adapter for items
    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public RecyclerView itemRecyclerView;
        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            itemRecyclerView = itemView.findViewById(R.id.itemRecyclerView);
        }
    }
}
