package com.cressonbluffwinery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cressonbluffwinery.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText userNameEditText, firstNameEditText, lastNameEditText;
    private EditText userPhoneEditText, addressEditText, cityEditText, stateEditText, zipEditText;
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = findViewById(R.id.settings_profile_image);
        userNameEditText = findViewById(R.id.settings_username);
        firstNameEditText = findViewById(R.id.settings_first_name);
        lastNameEditText = findViewById(R.id.settings_last_name);
        userPhoneEditText = findViewById(R.id.settings_phone_number);
        addressEditText = findViewById(R.id.settings_address);
        cityEditText = findViewById(R.id.settings_city);
        stateEditText = findViewById(R.id.settings_state);
        zipEditText = findViewById(R.id.settings_zip);

        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings_btn);
        saveTextButton = findViewById(R.id.update_account_settings_btn);

        userInfoDisplay(profileImageView, userNameEditText, firstNameEditText, lastNameEditText,
                userPhoneEditText, addressEditText, cityEditText, stateEditText, zipEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", userNameEditText.getText().toString());
        userMap. put("firstName", firstNameEditText.getText().toString());
        userMap. put("lastName", lastNameEditText.getText().toString());
        userMap. put("address", addressEditText.getText().toString());
        userMap. put("city", cityEditText.getText().toString());
        userMap. put("state", stateEditText.getText().toString());
        userMap. put("zipCode", zipEditText.getText().toString());
        userMap. put("phone", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getName()).updateChildren(userMap);
        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Please Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }




    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(userNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Username is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(firstNameEditText.getText().toString()))
        {
            Toast.makeText(this, "First Name is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(lastNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Last Name is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Address is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this, "City is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(stateEditText.getText().toString()))
        {
            Toast.makeText(this, "State is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(zipEditText.getText().toString()))
        {
            Toast.makeText(this, "Zip Code is empty.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Phone Number is empty.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentOnlineUser.getName() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap. put("name", userNameEditText.getText().toString());
                        userMap. put("firstName", firstNameEditText.getText().toString());
                        userMap. put("lastName", lastNameEditText.getText().toString());
                        userMap. put("address", addressEditText.getText().toString());
                        userMap. put("city", cityEditText.getText().toString());
                        userMap. put("state", stateEditText.getText().toString());
                        userMap. put("zipCode", zipEditText.getText().toString());
                        userMap. put("phone", userPhoneEditText.getText().toString());
                        userMap. put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getName()).updateChildren(userMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }

    }
    private void userInfoDisplay(
            final CircleImageView profileImageView,  final EditText userNameEditText, final EditText firstNameEditText, final EditText lastNameEditText,
            final EditText userPhoneEditText, final EditText addressEditText, final EditText cityEditText, final EditText stateEditText, final EditText zipEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getName());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String firstName = dataSnapshot.child("firstName").getValue().toString();
                        String lastName = dataSnapshot.child("lastName").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String city = dataSnapshot.child("city").getValue().toString();
                        String state = dataSnapshot.child("state").getValue().toString();
                        String zipCode = dataSnapshot.child("zipCode").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                        userNameEditText.setText(name);
                        firstNameEditText.setText(firstName);
                        lastNameEditText.setText(lastName);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                        cityEditText.setText(city);
                        stateEditText.setText(state);
                        zipEditText.setText(zipCode);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
