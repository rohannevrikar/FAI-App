package tastifai.customer;

import java.io.Serializable;

/**
 * Created by Rohan Nevrikar on 22-02-2018.
 */

public class MenuItemModel implements Serializable {
    private String itemName;
    private String price;
    private int quantity;
    private String itemId;
    private String restaurantId;
    private int itemETA;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getItemETA() {
        return itemETA;
    }

    public void setItemETA(int itemETA) {
        this.itemETA = itemETA;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private boolean isClicked;

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
