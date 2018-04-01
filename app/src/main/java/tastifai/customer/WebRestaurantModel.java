package tastifai.customer;

import android.support.annotation.NonNull;

/**
 * Created by Rohan Nevrikar on 20-03-2018.
 */

public class WebRestaurantModel implements Comparable<WebRestaurantModel> {
    private String restaurantName;
    private String id;
    private double latitude;
    private double longitude;
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(@NonNull WebRestaurantModel other) {
        if(this.getDistance() > other.getDistance())
            return 1;
        else if (this.getDistance() == other.getDistance())
            return 0 ;
        return -1 ;

    }
}
