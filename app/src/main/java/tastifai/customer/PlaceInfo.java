package tastifai.customer;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Rohan Nevrikar on 21-03-2018.
 */

public class PlaceInfo implements Serializable {
    private String address;
    private transient LatLng latLng;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
