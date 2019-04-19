package com.example.sayarsamanta.socialintegrationwithfirebase;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    private static final String AUTH_TYPE = "rerequest";
    private CallbackManager mCallbackManager;
    private static final String TAG = "AndroidClarified";
    private SignInButton googleSignInButton;
    private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.mainActivityTopBar));
        PackageInfo info;
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton mLoginButton = findViewById(R.id.login_button);
        googleSignInButton = findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        // Set the initial permissions to request from the user while logging in
        mLoginButton.setReadPermissions(Arrays.asList(EMAIL));

        mLoginButton.setAuthType(AUTH_TYPE);
        //call back method for facebook log in
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setResult(RESULT_OK);
                Profile profile=Profile.getCurrentProfile();
                String name=profile.getFirstName()+" "+profile.getLastName();
                Intent intent=new Intent(MainActivity.this,FormActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);

            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                Intent intent=new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(FacebookException e) {
                // Handle exception
            }
        });
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });
    }


    //Catching the result of successful log in attempt for facebook and google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
                if (requestCode == 101) {
                    try {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                }
                else{
                    mCallbackManager.onActivityResult(requestCode, resultCode, data);
                }
                }
    //Going to form activity with the details

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.GOOGLE_ACCOUNT, googleSignInAccount);
        startActivity(intent);
        finish();
    }

    //Checking if the user is already logged in or not

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (alreadyloggedAccount != null) {
            onLoggedIn(alreadyloggedAccount);
        } else {
            Log.d(TAG, "Not logged in");
        }
    }
}
