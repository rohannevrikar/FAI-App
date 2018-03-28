package tastifai.customer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Rohan Nevrikar on 19-03-2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "restaurants.db";
    private static final String RESTAURANTS_TABLE = "restaurantstable";
    private static final String MENU_TABLE = "menu";
    private static final String USERS_TABLE = "users";

    private static final String COL_1 = "restaurant_id";
    private static final String COL_2= "restautant_name";
    private  static final String TAG = "DatabaseHelper";


    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL("CREATE TABLE " + RESTAURANTS_TABLE  + " ("
//                INTEGER PRIMARY KEY, " + COL_2 + " TEXT NOT NULL);");
//
         sqLiteDatabase.execSQL("create table " + RESTAURANTS_TABLE + " (restaurantid INTEGER PRIMARY KEY, restaurantname TEXT NOT NULL);");
         sqLiteDatabase.execSQL("create table " + MENU_TABLE + " (itemid INTEGER PRIMARY KEY, itemname TEXT NOT NULL, itemdesc TEXT, itemprice TEXT, itemtype TEXT);");
//        sqLiteDatabase.execSQL("create table " + USERS_TABLE + " (restaurantid INTEGER PRIMARY KEY, restaurantname TEXT NOT NULL");


        Log.d(TAG, "onCreate: db created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + RESTAURANTS_TABLE);
        onCreate(sqLiteDatabase);
    }
    public boolean insertRestaurantData(int id, String restaurantName){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = getRestaurantData();

            ContentValues contentValues = new ContentValues();
            contentValues.put("restaurantid", id);
            contentValues.put("restaurantname", restaurantName);
            long result = sqLiteDatabase.insert(RESTAURANTS_TABLE, null, contentValues);
            if(result == -1)
                return false;
            else
                return true;



    }
    public boolean insertMenuData(int id, String itemName, String itemDesc, String itemType, String price){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = getMenuData();

        ContentValues contentValues = new ContentValues();
        contentValues.put("itemid", id);
        contentValues.put("itemname", itemName);
        contentValues.put("itemdesc", itemDesc);
        contentValues.put("itemprice", price);
        contentValues.put("itemtype", itemType);

        long result = sqLiteDatabase.insert(MENU_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getRestaurantData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + RESTAURANTS_TABLE, null);
//        if(cursor != null)
//            cursor.moveToFirst();
        return cursor;
    }
    public Cursor getMenuData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + MENU_TABLE, null);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

}
