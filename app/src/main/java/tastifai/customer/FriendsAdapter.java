package tastifai.customer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 16-03-2018.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private View view;
    private Context context;
    private ArrayList<FriendsModel> friendsModelList;
    private final LayoutInflater layoutInflater;

    public FriendsAdapter(Context context, ArrayList<FriendsModel> friendsModelList) {
        this.context = context;
        this.friendsModelList = friendsModelList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = layoutInflater.inflate(R.layout.friends_row, parent, false);
        return new FriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.friendName.setText(friendsModelList.get(position).getName());
        holder.friendImage.setImageBitmap(friendsModelList.get(position).getProfilePicture());

    }

    @Override
    public int getItemCount() {
        return friendsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView friendName;
        private ImageView friendImage;
        public ViewHolder(View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);
            friendImage = itemView.findViewById(R.id.friendsProfilePic);
        }
    }

}
