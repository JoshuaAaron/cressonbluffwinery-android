package com.cressonbluffwinery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cressonbluffwinery.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.EditText;

public class LoginActivity extends AppCompatActivity 
{
    private EditText InputUsername, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    
    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputUsername = (EditText) findViewById(R.id.login_username_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);
        
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                LoginUser();                
            }
        });
    }
    
    

    private void LoginUser() 
    {
        String username = InputUsername.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please make sure to type your username correctly.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password and Username do not match.", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, validating login credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(username, password);

        }
}

    private void AllowAccessToAccount(String username, String password) 
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) 
            {
                if (snapshot.child(parentDbName).child(username).exists())
                {
                    Users usersData = snapshot.child(parentDbName).child(username).getValue(Users.class);

                    if (usersData.getName().equals(username))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Toast.makeText(LoginActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);

                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account with  " + username + "does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Please create a new account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}