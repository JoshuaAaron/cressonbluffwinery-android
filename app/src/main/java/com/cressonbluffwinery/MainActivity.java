package com.cressonbluffwinery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cressonbluffwinery.Model.Users;
import com.cressonbluffwinery.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = (Button) findViewById(R.id.join_now_btn);
        loginButton = (Button)findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String UserNameKey = Paper.book().read(Prevalent.UserNameKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);


        if (UserNameKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserNameKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserNameKey, UserPasswordKey);

                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }


    }

    private void AllowAccess(final String name, final String password)
    {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child("Users").child(name).exists())
                {
                    Users usersData = snapshot.child("Users").child(name).getValue(Users.class);

                    if (usersData.getName().equals(name))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Account with  " + name + " does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Please create a new account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}