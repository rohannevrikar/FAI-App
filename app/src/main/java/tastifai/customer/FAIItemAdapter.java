package tastifai.customer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static tastifai.customer.MainActivity.cartItems;

/**
 * Created by Rohan Nevrikar on 22-02-2018.
 */

public class FAIItemAdapter extends RecyclerView.Adapter<FAIItemAdapter.ViewHolder> {
    private View view;
    private Context context;
    private ArrayList<MenuItemModel> menuItemModelArrayList;
    private final LayoutInflater layoutInflater;
    private Typeface typeface;
    private RelativeLayout cartLayout;
    private TextView cartQty;
    private int itemQuantity;
    private RelativeLayout bottomCartLayout;
    private TextView bottomCartQty;
    private Layout layout;
    private View menuView;
    private static final String TAG = "FAIItemAdapter";
    public SharedPreferences cartSharedPref;
    private ArrayList<MenuItemModel> checkoutCart;
    private int pos=0;

    public FAIItemAdapter(Context context, ArrayList<MenuItemModel> menuItemModelArrayList) {
        this.context = context;
        this.menuItemModelArrayList = menuItemModelArrayList;
        Log.d(TAG, "FAIItemAdapter: " + menuItemModelArrayList.size());
        layoutInflater = LayoutInflater.from(context);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/GT-Walsheim.ttf");
        cartLayout = ((MainActivity)context).toolbar.findViewById(R.id.cartLayout);
        cartQty = cartLayout.findViewById(R.id.cartQty);
        cartSharedPref = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = layoutInflater.inflate(R.layout.menu_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: isClicked: " + menuItemModelArrayList.get(position).isClicked());

        String string = "\u20B9";
        byte[] utf8 = null;
        try {
            utf8 = string.getBytes("UTF-8");
            string = new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.itemName.setText(menuItemModelArrayList.get(position).getItemName());
        holder.price.setText(string + menuItemModelArrayList.get(position).getPrice());

        holder.btnInit.setTypeface(typeface);
        holder.btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartItems.size() == 15){
                    Toast.makeText(context, "You have reached maximum cart limit", Toast.LENGTH_SHORT).show();
                }else{
                    holder.btnInit.setVisibility(View.GONE);
                    holder.setQty.setVisibility(View.VISIBLE);
                    menuItemModelArrayList.get(position).setQuantity(1);
                    cartItems.add(menuItemModelArrayList.get(position));
                    cartLayout.setVisibility(View.VISIBLE);
                    cartQty.setText("" + cartItems.size());
                    Log.d(TAG, "onClick: getQuantity: " + menuItemModelArrayList.get(position).getQuantity());
                    holder.btnItemQty.setText("" + menuItemModelArrayList.get(position).getQuantity());
                }


            }
        });
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuItemModelArrayList.get(position).setQuantity(menuItemModelArrayList.get(position).getQuantity() + 1);
                Log.d(TAG, "onClick: getQuantity on add: " +  menuItemModelArrayList.get(position).getQuantity());
                    holder.btnItemQty.setText("" + menuItemModelArrayList.get(position).getQuantity());
            }
        });
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuItemModelArrayList.get(position).getQuantity() >= 1) {
                    menuItemModelArrayList.get(position).setQuantity(menuItemModelArrayList.get(position).getQuantity() - 1);
                }
                    if(menuItemModelArrayList.get(position).getQuantity() == 0){
                        holder.btnInit.setVisibility(View.VISIBLE);
                        holder.setQty.setVisibility(View.GONE);
                        cartItems.remove(menuItemModelArrayList.get(position));
                        cartQty.setText("" + cartItems.size());
                    }

                holder.btnItemQty.setText("" + menuItemModelArrayList.get(position).getQuantity());
                if(cartItems.size() == 0){
                    cartLayout.setVisibility(View.GONE);
                    cartSharedPref.edit().clear().apply();
                }
            }
        });

        Log.d(TAG, "onBindViewHolder: isClicked end of code: " + menuItemModelArrayList.get(position).isClicked());



    }

    @Override
    public int getItemCount() {
        return menuItemModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public Button btnInit;
        public Button btnRemove;
        public Button btnAdd;
        public Button btnItemQty;
        public TableLayout setQty;

        public TextView price;
        public RelativeLayout menuItemLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.price);
            btnInit = itemView.findViewById(R.id.btnInit);
            setQty = itemView.findViewById(R.id.setQty);
            btnRemove = itemView.findViewById(R.id.remove);
            btnAdd = itemView.findViewById(R.id.add);
            btnItemQty = itemView.findViewById(R.id.itemQty);
            menuItemLayout = itemView.findViewById(R.id.menu_item_layout);
        }
    }
}
