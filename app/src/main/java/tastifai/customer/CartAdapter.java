package tastifai.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import static tastifai.customer.Cart.total;
import static tastifai.customer.MainActivity.cartItems;

/**
 * Created by Rohan Nevrikar on 22-03-2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private final SharedPreferences cartSharedPref;
    private View view;
    private Context context;
    private ArrayList<MenuItemModel> checkoutList;
    private LayoutInflater layoutInflater;
    //public static int checkoutItemsTotal=0;
    public TextView itemTotal;
    public TextView checkoutTotal;

    public CartAdapter(Context context, ArrayList<MenuItemModel> checkoutList, TextView itemTotal, TextView checkoutTotal) {
        this.context = context;
        this.checkoutList = checkoutList;
        layoutInflater = LayoutInflater.from(context);
        this.itemTotal = itemTotal;
        this.checkoutTotal = checkoutTotal;
        cartSharedPref = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = layoutInflater.inflate(R.layout.cart_item_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CartAdapter.ViewHolder holder, final int position) {
        holder.itemName.setText(checkoutList.get(position).getItemName());
        holder.itemQty.setText(Integer.toString(checkoutList.get(position).getQuantity()));
        holder.itemPrice.setText(Integer.toString(Integer.parseInt(checkoutList.get(position).getPrice()) * checkoutList.get(position).getQuantity()));

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutList.get(position).setQuantity(checkoutList.get(position).getQuantity() + 1);
                holder.itemQty.setText("" + checkoutList.get(position).getQuantity());
                holder.itemPrice.setText(Integer.toString(Integer.parseInt(checkoutList.get(position).getPrice()) * checkoutList.get(position).getQuantity()));

                total = total + Integer.parseInt(checkoutList.get(position).getPrice());
                itemTotal.setText("" + total);
                checkoutTotal.setText("" + total);
                SharedPreferences.Editor editor = cartSharedPref.edit();
                Gson gson = new Gson();
                String jsonCart = gson.toJson(checkoutList);
                editor.putString("cart", jsonCart);
                editor.apply();


            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total = total - Integer.parseInt(checkoutList.get(position).getPrice());
                if(checkoutList.get(position).getQuantity() > 1) {
                    checkoutList.get(position).setQuantity(checkoutList.get(position).getQuantity() - 1);
                    holder.itemQty.setText("" + checkoutList.get(position));
                    holder.itemPrice.setText(Integer.toString(Integer.parseInt(checkoutList.get(position).getPrice()) * checkoutList.get(position).getQuantity()));
                }
                else
                    checkoutList.remove(position);

                if(checkoutList.size() == 0){
                    Intent intent = new Intent(context, MainActivity.class);
                    (context).startActivity(intent);
                }
                SharedPreferences.Editor editor = cartSharedPref.edit();
                Gson gson = new Gson();
                String jsonCart = gson.toJson(checkoutList);
                editor.putString("cart", jsonCart);
                editor.apply();

                notifyDataSetChanged();

                    itemTotal.setText("" + total);
                    checkoutTotal.setText("" + total);


            }
        });

    }

    @Override
    public int getItemCount() {
        return checkoutList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private Button itemQty;
        private TextView itemPrice;
        private Button add;
        private Button remove;
        private TextView itemTotal;
        private TextView checkoutTotal;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQty = (Button)itemView.findViewById(R.id.itemQty);
            itemPrice = itemView.findViewById(R.id.price);
            add = itemView.findViewById(R.id.add);
            remove = itemView.findViewById(R.id.remove);


        }
    }
}
