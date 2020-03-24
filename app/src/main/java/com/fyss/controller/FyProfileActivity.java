package com.fyss.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.fyss.R;
import com.fyss.model.FyUser;
import com.fyss.model.SyUser;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.service.Cache;
import com.fyss.session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FyProfileActivity extends AppCompatActivity {
    private TextView name, email, num, group, bio;
    private String id;
    private FyUser user;
    private ImageButton homeBtn, settingsBtn;
    private SessionManager sm;
    private Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
    private final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    public static final int GET_FROM_GALLERY = 3;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri selectedImage, userImage;
    private Bitmap bitmap;
    private de.hdodenhof.circleimageview.CircleImageView profPic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_DarkTheme_NoActionBar);
        }else{
            setTheme(R.style.AppTheme_LightTheme_NoActionBar);
        }
        setContentView(R.layout.activity_profile);
        id = (String) getIntent().getSerializableExtra("FyId");
        sm = new SessionManager(getApplicationContext());

        name = findViewById(R.id.nameText);
        email = findViewById(R.id.emailText);
        num = findViewById(R.id.numText);
        group = findViewById(R.id.groupText);
        homeBtn = findViewById(R.id.homeBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profPic = findViewById(R.id.profile);
        bio = findViewById(R.id.bioText);

        setUser(id);
    }

    /**This method calls the API to set the user based on the Id passed to it.
     * @param id - This is the id of the user whos page it is.
     * */
    private void setUser(String id) {
        Call<FyUser> call = jsonPlaceHolderApi.findFyUserById(Integer.parseInt(id));

        call.enqueue(new Callback<FyUser>() {
            @Override
            public void onResponse(Call<FyUser> call, Response<FyUser> response) {
                if (!response.isSuccessful()) {
                    String result = "Cod: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } else {
                    user = response.body();
                    setInfo();
                    checkUserOnPage();

                }
            }
            @Override
            public void onFailure(Call<FyUser> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    /**Sets the on click listeners for the UI elements.
     * @param type - this is a string which describes the type of user who is viewing the page.
     * */
    private void setOnClickListeners(final String type) {
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FyEditProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                //finish();
            }
        });
    }

    /**Sets the profile page info.
     * */
    private void setInfo() {
        name.setText(user.getFirstname() + " " + user.getSurname());
        email.setText(user.getEmail());
        num.setText(user.getPhoneNum());
        group.setText("1st Year - Member of group " + user.getGid().getGid());
        bio.setText(user.getFirstname() + " has not set a bio yet.");

        if(user.getProfileImg() != null) {
            bitmap = getBitmapFromMemCache(user.getProfileImg());
            if (bitmap != null) {
                profPic.setImageBitmap(bitmap);
            } else {
                setPic();
            }
        }
    }

    /** Gets the image from the Firebase storage, adds it to the cache and sets the profile picture.
     * */
    private void setPic(){
        StorageReference imgRef = storageReference.child("images/" + user.getProfileImg());
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            imgRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    addBitmapToMemoryCache(user.getProfileImg(), bitmap);
                    profPic.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }

    }


   /** Determines the type of user who is viewing the page.
            * */
    private void checkUserOnPage() {
        HashMap<String, String> loggedInUser = sm.getUserDetails();
        if (loggedInUser.get(SessionManager.KEY_USER_ID) != null) {
            if (user.getFyid().toString().matches(loggedInUser.get(SessionManager.KEY_USER_ID)) &&
                    loggedInUser.get(SessionManager.KEY_USER_TYPE).matches("FY")) {
                setOnClickListeners("FY");
            }
            else if(loggedInUser.get(SessionManager.KEY_USER_TYPE).matches("FY")){
                settingsBtn.setVisibility(View.GONE);
                setOnClickListeners("FY");
                profPic.setClickable(false);
            }
            else{
                settingsBtn.setVisibility(View.GONE);
                setOnClickListeners("SY");
                profPic.setClickable(false);
            }
        }
    }



    /** Provides the user with a view of their local storage so that they can select an image to add.
     * */
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent d) {
        super.onActivityResult(reqCode, resCode, d);

        if(reqCode==GET_FROM_GALLERY && resCode == Activity.RESULT_OK) {
            selectedImage = d.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                deleteExistingPhoto();
                uploadImage();

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Removes existing photo from firebase, used when the user is updating their picture.
    private void deleteExistingPhoto() {
        StorageReference ref = storageReference.child("images/" + user.getProfileImg());
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d("Deleted", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d("Not Deleted", "onFailure: did not delete file");
            }
        });
    }


    //Uploads image to firebase
    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading picture..");
        pd.show();
        final String random = UUID.randomUUID().toString();

        StorageReference ref = storageReference.child("images/" + random);
        ref.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                        updateDB(random);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        pd.setMessage("Uploading: " + (int) progress + "%");
                    }
                });
    }

    //Updates the db with a reference to the image file
    private void updateDB(String random) {
        user.setProfileImg(random);
        Call<Void> call = jsonPlaceHolderApi.editFyUser(user.getFyid(), user);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    String result = "Cde: " + response.code();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                setInfo();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //this is called when the profile picture button is clicked.
    public void setProfilePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //startActivity(intent);
        startActivityForResult(intent, GET_FROM_GALLERY);
    }

    //adds the picture to cache
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        Cache.getInstance().getLru().put(key, bitmap);
    }

    //retrieves picture from memory
    public Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) Cache.getInstance().getLru().get(key);
    }
}
