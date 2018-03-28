package tastifai.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.params.Face;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookLoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    String profilePicUrl;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        sharedPreferences = getApplicationContext().getSharedPreferences("FacebookPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(isLoggedIn()){

//                String fbUserId = sharedPreferences.getString("fbUserId", null);
//                String accessToken = sharedPreferences.getString("accessToken", null);
//                String first_name = sharedPreferences.getString("first_name", null);
//                String last_name = sharedPreferences.getString("last_name", null);
//                String email = sharedPreferences.getString("email", null);
//                String picture = sharedPreferences.getString("picture", null);
//                String friendsArray = sharedPreferences.getString("friendsArray", null);
//                Log.d("FacebookLoginActivity", "onCreate: " + friendsArray);
                Intent intent = new Intent(this, MainActivity.class);
//                intent.putExtra("fbUserId", fbUserId);
//                intent.putExtra("first_name", first_name);
//                intent.putExtra("last_name", last_name);
//                intent.putExtra("email", email);
//                intent.putExtra("accessToken", accessToken);
//                intent.putExtra("friendsArray", friendsArray);
//                intent.putExtra("picture", picture);
                startActivity(intent);
            }


        loginButton = findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        loginButton.setVisibility(View.GONE);

                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        Log.v("LoginActivity", response.toString());
                                        // Application code
                                        try {
                                            if(object.has("picture")){
                                                profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                                Log.d("FacebookActivity", "onCompleted: " + profilePicUrl);
                                            }
                                            String email = object.getString("email");
                                            String birthday = object.getString("birthday");
                                            String first_name = object.getString("first_name");
                                            String last_name = object.getString("last_name");
                                            String id = object.getString("id");
                                            String friends = object.getString("friends");
                                            Log.d("FacebookLogin", "onCompleted: " + friends);
                                            editor = sharedPreferences.edit();
                                            editor.putString("fbUserId", id);
                                            editor.putString("accessToken", loginResult.getAccessToken().toString());
                                            editor.putString("first_name", first_name);
                                            editor.putString("last_name", last_name);
                                            editor.putString("email", email);
                                            editor.putString("picture", profilePicUrl);
                                            editor.putString("friends", friends);
                                            editor.apply();
                                            Log.d("facebookActivity", "displayUserInfo: " + email + " " + first_name + profilePicUrl);
//                                            intent. putExtra("first_name",first_name);
//                                            intent. putExtra("last_name",last_name);
//                                            intent. putExtra("email",email);
//                                            intent. putExtra("fbUserId",id);
//                                            intent.putExtra("picture", profilePicUrl);

                                            // 01/31/1980 format

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name, last_name, email,gender,birthday,friends");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }
                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        TextView error = findViewById(R.id.error);
                        error.setText(exception.getMessage() + " \n" + exception.toString());
                        Toast.makeText(FacebookLoginActivity.this, exception.toString(),Toast.LENGTH_LONG).show();
                        // App code
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        final Intent intent = new Intent(FacebookLoginActivity.this, MapsActivity.class);
        startActivity(intent);

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}
