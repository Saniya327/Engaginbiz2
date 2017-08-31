    package com.example.fariha.editprofile;


    import android.app.DatePickerDialog;
    import android.app.Dialog;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.graphics.BitmapFactory;
    import android.graphics.Typeface;
    import android.net.Uri;
    import android.support.annotation.NonNull;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.CompoundButton;
    import android.widget.DatePicker;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.RadioButton;
    import android.widget.Switch;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.facebook.AccessToken;
    import com.facebook.CallbackManager;
    import com.facebook.FacebookCallback;
    import com.facebook.FacebookException;
    import com.facebook.FacebookSdk;
    import com.facebook.login.LoginManager;
    import com.facebook.login.LoginResult;
    import com.facebook.login.widget.LoginButton;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthCredential;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FacebookAuthProvider;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;

    import java.io.IOException;
    import java.util.Calendar;
    import java.util.List;

    import retrofit2.Response;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;
    import retrofit2.Call;
    import retrofit2.Callback;


    import static android.R.attr.data;

    public class MainActivity extends AppCompatActivity {

        static String TAG="Logging";


        TextView editProfile,aboutTV,detailsTV,titleTV,accountsTV,genderTV;
        ImageView myImage,connectFb,addPicture;
        ImageButton removeButton,thumb1, thumb2, thumb3, thumb4, thumb5;
        Button  dateChangeButton;
        EditText name, profile, about;
        RadioButton male, female, none;
        LoginButton fbButton;
        private Calendar calendar;
        private TextView dateView;
        private int year, month, day;
        private static final int fb_id=64206;
        private static final int upload_id=0;
        Switch switch1, switch2, switch3, switch4, switch5;
        UploadPicture uploadPicture;    //Uploading the picture is done by an object of type UploadPicture
        private CallbackManager callbackManager;
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mauthListener;



        //Checks if user is already logged in to facebook
        @Override
        public void onStart() {
            super.onStart();

            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null)
                updateUI("Connected");
            else
                updateUI("Not connected");
            //mAuth.addAuthStateListener(mauthListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Description",about.getText().toString());
            editor.putString("Profile",profile.getText().toString());
            editor.apply();
        }

        @Override
        protected void onPostResume() {
            super.onPostResume();
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            about.setText(settings.getString("Description", ""));
            profile.setText(settings.getString("Profile", ""));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FacebookSdk.sdkInitialize(this.getApplicationContext());
            setContentView(R.layout.activity_main);

            Typeface semiBold=Typeface.createFromAsset(getAssets(),"Montserrat-SemiBold.otf");
            Typeface regular = Typeface.createFromAsset(getAssets(),"Montserrat-Regular.otf");
            Typeface medium = Typeface.createFromAsset(getAssets(),"Montserrat-Medium.otf");
            Typeface light = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");

            editProfile=(TextView) findViewById(R.id.editProfile);
            editProfile.setTypeface(semiBold);
            aboutTV=(TextView)findViewById(R.id.about_tv);
            aboutTV.setTypeface(medium);
            detailsTV=(TextView)findViewById(R.id.details_tv);
            detailsTV.setTypeface(medium);
            accountsTV=(TextView)findViewById(R.id.accounts_tv);
            accountsTV.setTypeface(medium);
            genderTV=(TextView)findViewById(R.id.gender_tv);
            genderTV.setTypeface(medium);
            titleTV=(TextView)findViewById(R.id.title_tv);
            titleTV.setTypeface(medium);




            myImage = (ImageView) findViewById(R.id.myImage);
            addPicture = (ImageButton) findViewById(R.id.addButton);
            addPicture.setImageResource(R.drawable.iconpencil);
            profile = (EditText) findViewById(R.id.profile);
            profile.setTypeface(regular);
            about = (EditText) findViewById(R.id.about);
            about.setTypeface(regular);

            male = (RadioButton) findViewById(R.id.male);
            male.setTypeface(regular);

            female = (RadioButton) findViewById(R.id.female);
            female.setTypeface(regular);
            none = (RadioButton) findViewById(R.id.none);
            none.setTypeface(regular);

            dateChangeButton = (Button) findViewById(R.id.dateChangeButton);
            dateView = (TextView) findViewById(R.id.textView3);
            calendar = Calendar.getInstance();
            year = 1996;
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            thumb1 = (ImageButton) findViewById(R.id.Thumb1);
            thumb2 = (ImageButton) findViewById(R.id.Thumb2);
            thumb3 = (ImageButton) findViewById(R.id.Thumb3);
            thumb4 = (ImageButton) findViewById(R.id.Thumb4);
            thumb5 = (ImageButton) findViewById(R.id.Thumb5);

            switch5 = (Switch) findViewById(R.id.switch5);
            switch5.setTypeface(medium);
            switch4 = (Switch) findViewById(R.id.switch4);
            switch4.setTypeface(medium);
            switch3 = (Switch) findViewById(R.id.switch3);
            switch3.setTypeface(medium);
            switch2 = (Switch) findViewById(R.id.switch2);
            switch2.setTypeface(medium);
            switch1 = (Switch) findViewById(R.id.switch1);
            switch1.setTypeface(medium);

            callbackManager = CallbackManager.Factory.create();
            uploadPicture = new UploadPicture(this, R.id.myImage);
            fbButton=(LoginButton) findViewById(R.id.facebookButton);
            fbButton.setReadPermissions("email", "public_profile");
            mAuth = FirebaseAuth.getInstance();
            connectFb=(ImageView)findViewById(R.id.connectedFb);
            removeButton=(ImageButton)findViewById(R.id.removeFb);

            //For storing details entered by user
            //settings = getSharedPreferences("UserInfo", 0);



            //uploading picture object
            addPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPicture.askForPermissions();
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i,upload_id );
                }
            });

            //The switches at the end
            switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        thumb5.setVisibility(View.VISIBLE);
                    else
                        thumb5.setVisibility(View.GONE);
                }
            });

            switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        thumb4.setVisibility(View.VISIBLE);
                    else
                        thumb4.setVisibility(View.GONE);
                }
            });

            switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        thumb3.setVisibility(View.VISIBLE);
                    else
                        thumb3.setVisibility(View.GONE);
                }
            });

            switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        thumb2.setVisibility(View.VISIBLE);
                    else
                        thumb2.setVisibility(View.GONE);
                }
            });

            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        thumb1.setVisibility(View.VISIBLE);
                    }

                    else
                        thumb1.setVisibility(View.GONE);

                }
            });



            //Authenticating with facebook
            fbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
            {
                @Override
                public void onSuccess(LoginResult loginResult)
                {

                    //String userID=loginResult.getAccessToken().getUserId();
                    AccessToken tokenID=loginResult.getAccessToken();
                    handleFacebookAccessToken(tokenID);
                }

                @Override
                public void onCancel()
                {

                }

                @Override
                public void onError(FacebookException exception)
                {
                    Log.v("LoginActivity", exception.getCause().toString());
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    updateUI("Not connected");
                }
            });

        }





        //Exchanged facebook access token for firebase token
        private void handleFacebookAccessToken(AccessToken token) {
            Log.d(TAG, "handleFacebookAccessToken:" + token);

            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI("Connected");
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI("Not connected");
                            }
                        }
                    });
        }

        //Updates the UI based on whether the user is logged in or not(Facebook auth)
        public void updateUI(String status){
            switch(status){
                case "Connected":
                    connectFb.setImageResource(R.drawable.tick);
                    removeButton.setEnabled(true);
                    break;
                case "Not connected":
                    connectFb.setImageDrawable(null);
                    removeButton.setEnabled(false);
                    break;

            }
        }


        //On choosing a display image
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch(requestCode) {
                case upload_id:
                    String picturePath = uploadPicture.ActivityResult(requestCode, resultCode, data);
                    if (!picturePath.equals(""))
                        myImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                case fb_id:
                    Log.v("requestcode",""+requestCode);
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                    break;
            }

        }


        //For choosing date
        @SuppressWarnings("deprecation")
        public void setDate(View view) {
            showDialog(999);
        }

        private DatePickerDialog.OnDateSetListener myDateListener = new
                DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0,
                                          int arg1, int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        // arg1 = year
                        // arg2 = month
                        // arg3 = day
                        showDate(arg1, arg2 + 1, arg3);
                    }
                };

        @Override
        protected Dialog onCreateDialog(int id) {
            // TODO Auto-generated method stub
            if (id == 999) {
                return new DatePickerDialog(this,
                        myDateListener, year, month, day);
            }
            return null;
        }

        private void showDate(int year, int month, int day) {
            dateView.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        }

        //For choosing gender
        public void genderButtonClicked(View view) {

            // Is the button now checked?
            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch (view.getId()) {
                case R.id.male:
                    if (checked)
                        // set gender as male
                        break;
                case R.id.female:
                    if (checked)
                        // set gender as female
                        break;
                case R.id.none:
                    if (checked)
                        // set gender as unspecified
                        break;

            }
        }
    }