package tastifai.customer;

import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 23-02-2018.
 */

public class ItemDetails extends Fragment {
    private TextView itemText;
    private TextView itemDescription;
    private TextView itemQty;
    private TextView itemPrice;
    private static ArrayList<MenuItemModel> cartList = new ArrayList<>();
    private Button add;
    private Button remove;
    private Button addToCart;
    private int itemQuantity = 1;
    private ImageView back;
    private View view;
    private MenuItemModel menuItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.item_details,container,false);
        Bundle bundle = getArguments();
        if(bundle!=null){
            menuItem = (MenuItemModel) bundle.getSerializable("menuItemModel");
        }
        String item = menuItem.getItemName();
        itemText = view.findViewById(R.id.item);
        itemText.setText(item);
        itemPrice = view.findViewById(R.id.item_price);
        itemDescription = view.findViewById(R.id.item_desc);
        itemQty = view.findViewById(R.id.itemQty);
        itemQty.setText("" + itemQuantity);
        add = view.findViewById(R.id.add);
        remove = view.findViewById(R.id.remove);
        String string = "\u20B9";
        byte[] utf8 = null;
        try {
            utf8 = string.getBytes("UTF-8");
            string = new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        itemPrice.setText(string + menuItem.getPrice());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemQuantity = itemQuantity + 1;
                itemQty.setText("" + itemQuantity);
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemQuantity > 0){
                    itemQuantity = itemQuantity - 1;
                    itemQty.setText("" + itemQuantity);

                }

            }
        });
        addToCart = view.findViewById(R.id.btnAddCart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;

    }



}
