package tastifai.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 24-03-2018.
 */

public class MenuPopUpAdapter extends RecyclerView.Adapter<MenuPopUpAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<String> categoryModels;
    private LayoutInflater layoutInflater;
    private View view;

    public MenuPopUpAdapter(Context context, ArrayList<String> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
        layoutInflater = LayoutInflater.from(context);


    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = layoutInflater.inflate(R.layout.menu_popup_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.category.setText(categoryModels.get(position));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("position", holder.getAdapterPosition());
                ((MenuPopUp)context).setResult(Activity.RESULT_OK, intent);
                ((MenuPopUp)context).finish();

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView category;
        private RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            relativeLayout = itemView.findViewById(R.id.categoryLayout);
        }
    }
}
