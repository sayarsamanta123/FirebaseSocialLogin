package com.example.sayarsamanta.socialintegrationwithfirebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class FormActivity extends AppCompatActivity {
    public static final String GOOGLE_ACCOUNT = "google_account";
    private FusedLocationProviderClient client;
    private static final String TAG = MainActivity.class.getSimpleName();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;
    Button button,buttonGetLocation;
    ImageView imageView;
    EditText editTextEmail,editTextPhone,editTextLocation;
    TextView textViewTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Window window = FormActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(FormActivity.this, R.color.mainActivityTopBar));
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node

        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");
        button=findViewById(R.id.buttonSubmit);
        imageView=findViewById(R.id.imageViewBack);
        editTextEmail=findViewById(R.id.email_edittext);
        editTextPhone=findViewById(R.id.mobile_edittext);
        textViewTitle=findViewById(R.id.titlesave);
        buttonGetLocation=findViewById(R.id.getLocation);
        editTextLocation=findViewById(R.id.location_edittext);
        requestPermission();


        try {
            String name = getIntent().getStringExtra("name");
            textViewTitle.setText(name);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        client = LocationServices.getFusedLocationProviderClient(this);
        try {
            textViewTitle.setText(googleSignInAccount.getDisplayName());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=editTextEmail.getText().toString();
                String mobile=editTextPhone.getText().toString();
                String location=editTextLocation.getText().toString();

                if (email.equals("")||mobile.equals("")||location.equals("")){
                    Toast.makeText(FormActivity.this, R.string.all_details, Toast.LENGTH_SHORT).show();
                    return;
                }
                createUser(email,mobile,location);
                }
        });

        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                // update toolbar title
                textViewTitle.setText(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });
    }
    //creating user and inserting into firebase
    private void createUser(String email, String phone,String location) {
        mFirebaseDatabase = mFirebaseInstance.getReference().child("users").push();
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }

        FormDetails user = new FormDetails(email, phone,location);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }

    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FormDetails user = dataSnapshot.getValue(FormDetails.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.email + ", " + user.phone + ", "+user.location);

                // Display newly updated name and email
                //txtDetails.setText(user.name + ", " + user.email);

                // clear edit text
                editTextEmail.setText("");
                editTextPhone.setText("");
                editTextLocation.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    editTextLocation.setVisibility(View.VISIBLE);
                    editTextLocation.setText(location.getLatitude()+","+location.getLongitude());
                    Log.d("location",location.toString());
                }

            }
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }
}
