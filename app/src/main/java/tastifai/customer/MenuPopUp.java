package tastifai.customer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import java.util.ArrayList;

import static tastifai.customer.MenuFragment.categories;

public class MenuPopUp extends AppCompatActivity {
    private RecyclerView categoriesListRecyclerView;
    public int categoryPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pop_up);
        categoriesListRecyclerView = findViewById(R.id.categoriesListRecyclerView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*0.7), (int)(height*0.7));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        categoriesListRecyclerView.setLayoutManager(layoutManager);
        ArrayList<String> categoriesList = new ArrayList<>();
        categoriesList.addAll(categories);
        MenuPopUpAdapter adapter = new MenuPopUpAdapter(this, categoriesList);
        categoriesListRecyclerView.setAdapter(adapter);
    }
}
