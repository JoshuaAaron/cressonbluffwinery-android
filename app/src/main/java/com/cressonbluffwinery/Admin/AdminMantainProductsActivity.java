package com.cressonbluffwinery.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cressonbluffwinery.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMantainProductsActivity extends AppCompatActivity {
    private Button applyChangesBtn,deleteBtn;
    private EditText price,description,name;
    private ImageView imageView;
    private String productID="";
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mantain_products);

        applyChangesBtn= findViewById(R.id.apply_changes_btn);
        price= findViewById(R.id.product_price_mantain);
        description= findViewById(R.id.product_description_mantain);
        name= findViewById(R.id.product_name_mantain);
        imageView= findViewById(R.id.product_image_mantain);
        deleteBtn= findViewById(R.id.delete_pdt_btn);

        productID=getIntent().getStringExtra("pid");
        productRef= FirebaseDatabase.getInstance().getReference().child("Products")
                .child(productID);

        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }
        });

    }

    private void deleteThisProduct() {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminMantainProductsActivity.this,"Product deleted successfully",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminMantainProductsActivity.this, AdminCategoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void applyChanges() {
        String pName =name.getText().toString();
        String pPrice =price.getText().toString();
        String pDesc =description.getText().toString();
        if(pName.equals("")){
            Toast.makeText(this,"Enter Product Name",Toast.LENGTH_LONG).show();

        }

        if(pPrice.equals("")){
            Toast.makeText(this,"Enter Product Price",Toast.LENGTH_LONG).show();

        }
        if(pDesc.equals("")){
            Toast.makeText(this,"Enter Product Description",Toast.LENGTH_LONG).show();

        }
        else {
            final HashMap<String,Object> prodMap=new HashMap<>();
            prodMap.put("pid",productID);
            prodMap.put("pname",pName);
            prodMap.put("price",pPrice);
            prodMap.put("description",pDesc);
            productRef.updateChildren(prodMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AdminMantainProductsActivity.this,"Changes applied successfully",Toast.LENGTH_LONG).show();
                        Intent intent =new Intent(AdminMantainProductsActivity.this,AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }




    }

    private void displaySpecificProductInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String pName= dataSnapshot.child("pname").getValue().toString();
                    String pPrice= dataSnapshot.child("price").getValue().toString();
                    String pDescription= dataSnapshot.child("description").getValue().toString();
                    String pImage= dataSnapshot.child("image").getValue().toString();

                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}