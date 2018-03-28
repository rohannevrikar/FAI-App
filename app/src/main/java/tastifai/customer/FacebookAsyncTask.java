package tastifai.customer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;



import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Rohan Nevrikar on 16-03-2018.
 */

class FacebookAsyncTask extends AsyncTask<String, Object, Bitmap> {
    Bitmap fb_img = null;
    public AsyncResponse delegate = null;
    private static final String TAG = "FacebookAsyncTask";


    @Override
    protected Bitmap doInBackground(String... strings) {
        //Object fbId = strings[0];
        URL fb_url = null;//small | noraml | large
        try {


            fb_url = new URL(strings[0]);

            HttpsURLConnection connection = null;

            connection = (HttpsURLConnection) fb_url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setReadTimeout(7000);
            connection.setConnectTimeout(7000);
            connection.setInstanceFollowRedirects(true);
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "doInBackground: " + responseCode);
//            InputStream is = connection.getInputStream();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;


            fb_img = BitmapFactory.decodeStream(connection.getInputStream());

            connection.disconnect();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return fb_img;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        delegate.processFinish(bitmap);
    }
}

