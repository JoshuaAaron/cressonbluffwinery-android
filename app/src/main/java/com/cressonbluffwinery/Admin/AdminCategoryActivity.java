package com.cressonbluffwinery.Admin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cressonbluffwinery.HomeActivity;
import com.cressonbluffwinery.MainActivity;
import com.cressonbluffwinery.R;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView wine, wearables, accessories, glassware;
    private Button LogoutBtn,CheckOrdersBtn,maintainProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        permissions();
        LogoutBtn=findViewById(R.id.admin_logout_btn);
        CheckOrdersBtn=findViewById(R.id.check_orders_btn);
        maintainProductsBtn=findViewById(R.id.maintain_btn);


        wine = (ImageView) findViewById(R.id.wine_selection);
        wearables = (ImageView) findViewById(R.id.wearables_selection);
        accessories = (ImageView) findViewById(R.id.accessories_selection);
        glassware = (ImageView) findViewById(R.id.glassware_selection);

        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminCategoryActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);

            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        CheckOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminCategoryActivity.this, AdminNewOrderActivity.class);
                startActivity(intent);

            }
        });

        wine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "wine");
                startActivity(intent);

            }
        });

        wearables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "wearables");
                startActivity(intent);

            }
        });

        accessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "accessories");
                startActivity(intent);

            }
        });

        glassware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "glassware");
                startActivity(intent);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }

        }
    }
    public void permissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(AdminCategoryActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();

        }
        else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is required for the app to function perfectly")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(AdminCategoryActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }

    }
}