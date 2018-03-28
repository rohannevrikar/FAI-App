package tastifai.customer;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 22-02-2018.
 */

class CategoryModel {
    private String categoryName;
    private ArrayList<MenuItemModel> menuItemModelArrayList;

    public ArrayList<MenuItemModel> getMenuItemModelArrayList() {
        return menuItemModelArrayList;
    }

    public void setMenuItemModelArrayList(ArrayList<MenuItemModel> menuItemModelArrayList) {
        this.menuItemModelArrayList = menuItemModelArrayList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


}
