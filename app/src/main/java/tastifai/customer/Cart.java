package tastifai.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static tastifai.customer.MainActivity.cartItems;

public class Cart extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private ArrayList<MenuItemModel> checkoutItems = cartItems;
    private LinearLayoutManager linearLayoutManager;
    private ImageView back;
    public TextView itemTotal;
    public TextView checkoutTotal;
    public static int total;
    private SharedPreferences cartSharedPref;
    private ArrayList<MenuItemModel> checkoutCart;
    private static final String TAG = "Cart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartSharedPref = getSharedPreferences("Cart", Context.MODE_PRIVATE);
        if(cartSharedPref != null){
            Gson gson = new Gson();
            String json = cartSharedPref.getString("cart", null);
            Type type = new TypeToken<ArrayList<MenuItemModel>>(){}.getType();
            checkoutCart = gson.fromJson(json, type);
            Log.d(TAG, "onCreate: checkoutcart created" + checkoutCart.size() + json);
        }

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        itemTotal = findViewById(R.id.itemTotal);
        checkoutTotal = findViewById(R.id.checkoutTotal);

        linearLayoutManager = new LinearLayoutManager(this);
        cartRecyclerView.setLayoutManager(linearLayoutManager);
        for(int i=0;i<checkoutCart.size(); i++){
            total = total + checkoutCart.get(i).getQuantity() * Integer.parseInt(checkoutCart.get(i).getPrice());
        }
        itemTotal.setText(Integer.toString(total));
        checkoutTotal.setText(Integer.toString(total));
        CartAdapter adapter = new CartAdapter(this, checkoutCart, itemTotal, checkoutTotal);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cart.this, MainActivity.class);
                startActivity(intent);
            }
        });
        cartRecyclerView.setAdapter(adapter);
    }
}
