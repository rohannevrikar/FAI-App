package tastifai.customer;

import android.graphics.Bitmap;

/**
 * Created by Rohan Nevrikar on 16-03-2018.
 */

public class FriendsModel {
    private Bitmap profilePicture;
    private String name;

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
