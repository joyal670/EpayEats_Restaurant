package com.epayeats.epayeatsrestaurant.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.epayeats.epayeatsrestaurant.Model.MenuModel;
import com.epayeats.epayeatsrestaurant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddMenu_Activity extends AppCompatActivity
{
    EditText new_menu_name, new_menu_description, new_menu_actual_price;
    EditText new_menu_opentime, new_menu_close_time, new_menu_unit_kg;
    Spinner new_menu_main_catagory_spinner, new_menu_sub_catagory_spinner;
    Button new_menu_save_btn;
    SwitchCompat new_menu_on_off;
    ImageView new_menu_image1;

    private Uri image;
    private StorageTask mUploadTask;
    private static int image_pic_request = 1;

    SharedPreferences sharedPreferences;
    String a1;
    String Resname;
    String adminName;
    String adminID;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_);

        sharedPreferences = getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        Query query = FirebaseDatabase.getInstance().getReference("restaurants").child(a1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Resname = snapshot.child("resName").getValue().toString();
                adminID = snapshot.child("resLocalAdminID").getValue().toString();
                adminName = snapshot.child("resLocalAdminName").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        progressDialog = new ProgressDialog(AddMenu_Activity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        new_menu_name =  findViewById(R.id.new_menu_name);
            new_menu_description = findViewById(R.id.new_menu_description);
        new_menu_actual_price = findViewById(R.id.new_menu_actual_price);

        new_menu_opentime = findViewById(R.id.new_menu_opentime);
        new_menu_close_time = findViewById(R.id.new_menu_close_time);
        new_menu_unit_kg = findViewById(R.id.new_menu_unit_kg);
        new_menu_on_off = findViewById(R.id.new_menu_on_off);

        new_menu_sub_catagory_spinner = findViewById(R.id.new_menu_sub_catagory_spinner);
        new_menu_main_catagory_spinner = findViewById(R.id.new_menu_main_catagory_spinner);

        new_menu_image1 = findViewById(R.id.new_menu_image1);
        new_menu_save_btn = findViewById(R.id.new_menu_save_btn);

        new_menu_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // main catagory spinner
        DatabaseReference businessReference = FirebaseDatabase.getInstance().getReference("main_catagory");
        ArrayList<String> spinnerDatalist;
        ArrayAdapter<String> adapter;
        ValueEventListener listener;
        String msg = "Select --  ";

        progressDialog.show();
        spinnerDatalist = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerDatalist);
        listener = businessReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                spinnerDatalist.clear();
                spinnerDatalist.add(msg);
                progressDialog.dismiss();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren())
                {
                    spinnerDatalist.add(dataSnapshot1.child("foodCatagoreyType").getValue().toString());
                    new_menu_main_catagory_spinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                progressDialog.dismiss();
                Toast.makeText(AddMenu_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // sub catagory spinner
        DatabaseReference catagoryRef = FirebaseDatabase.getInstance().getReference("sub_category");
        ArrayList<String> spinnerDatalistcat;
        ArrayAdapter<String> adaptercat;
        ValueEventListener listenercat;

        progressDialog.show();
        spinnerDatalistcat = new ArrayList<>();
        adaptercat = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerDatalistcat);
        listenercat = catagoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                spinnerDatalistcat.clear();
                spinnerDatalistcat.add(msg);
                progressDialog.dismiss();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren())
                {
                    spinnerDatalistcat.add(dataSnapshot1.child("subCatagoryName").getValue().toString());
                    new_menu_sub_catagory_spinner.setAdapter(adaptercat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                progressDialog.dismiss();
                Toast.makeText(AddMenu_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        new_menu_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String st;
                if(new_menu_on_off.isChecked())
                {
                    st = "on";
                }
                else
                {
                    st = "off";
                }

                String name, description, actual, open, close, unitKg, approval, mainCat, subCat, img1;

                name = new_menu_name.getText().toString();
                description = new_menu_description.getText().toString();
                actual = new_menu_actual_price.getText().toString();

                open = new_menu_opentime.getText().toString();
                close = new_menu_close_time.getText().toString();

                unitKg = new_menu_unit_kg.getText().toString();

                mainCat = new_menu_main_catagory_spinner.getSelectedItem().toString();
                subCat = new_menu_sub_catagory_spinner.getSelectedItem().toString();

                if(name.isEmpty() || description.isEmpty() || actual.isEmpty() || open.isEmpty() || close.isEmpty() || unitKg.isEmpty() || image == null )
                {
                    if(name.isEmpty())
                    {
                        new_menu_name.setError("Required");
                    }
                    if(description.isEmpty())
                    {
                        new_menu_description.setError("Required");
                    }
                    if(actual.isEmpty())
                    {
                        new_menu_actual_price.setError("Required");
                    }
                    if(open.isEmpty())
                    {
                        new_menu_opentime.setError("Required");
                    }
                    if(close.isEmpty())
                    {
                        new_menu_close_time.setError("Required");
                    }
                    if(unitKg.isEmpty())
                    {
                        new_menu_unit_kg.setError("Required");
                    }

                    if(image == null)
                    {
                        Toast.makeText(AddMenu_Activity.this, "Select image", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    if(mainCat.equals(msg) || subCat.equals(msg))
                    {
                        if(mainCat.equals(msg))
                        {
                            Toast.makeText(AddMenu_Activity.this, "Select Main Catagory", Toast.LENGTH_SHORT).show();
                        }
                        if(subCat.equals(msg))
                        {
                            Toast.makeText(AddMenu_Activity.this, "Select Sub Catagory", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        progressDialog.show();
                        Query query1 = FirebaseDatabase.getInstance().getReference("sub_category");
                        query1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                {
                                    if(subCat.equals(dataSnapshot.child("subCatagoryName").getValue().toString()))
                                    {
                                        String subCatID = dataSnapshot.child("subCatagoryID").getValue().toString();
                                        Query query2 = FirebaseDatabase.getInstance().getReference("main_catagory");
                                        query2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren())
                                                {
                                                    if(mainCat.equals(dataSnapshot1.child("foodCatagoreyType").getValue().toString()))
                                                    {
                                                        String mainCatId = dataSnapshot1.child("foodCatagoreyID").getValue().toString();

                                                        StorageReference mstorageReference;
                                                        mstorageReference = FirebaseStorage.getInstance().getReference("menu/image");

                                                        if (mUploadTask != null && mUploadTask.isInProgress())
                                                        {
                                                            Toast.makeText(AddMenu_Activity.this, "Uploading in Progress", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("menu");
                                                            String pushKey = ref.push().getKey();

                                                            MenuModel model;
                                                            model = new MenuModel();

                                                            if(image != null)
                                                            {
                                                                StorageReference fileRef = mstorageReference.child(System.currentTimeMillis() + "." + getFileExtention(image));
                                                                mUploadTask =  fileRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                                                    {
                                                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                                        while (!uriTask.isSuccessful());

                                                                        Uri downloadUrl = uriTask.getResult();

                                                                        model.setMenuID(pushKey);
                                                                        model.setMenuName(name);
                                                                        model.setImage1(downloadUrl.toString());

                                                                        model.setMenuMainCatagorey(mainCat);
                                                                        model.setMenuMainCatagoryID(mainCatId);

                                                                        model.setMenuSubCatagorey(subCat);
                                                                        model.setMenuSubCatagoreyID(subCatID);

                                                                        model.setMenuLocalAdminID(adminID);

                                                                        model.setMenuDescription(description);

                                                                        model.setMenuActualPrice(actual);
                                                                        model.setMenuSellingPrice("");
                                                                        model.setMenuOfferPrice("");

                                                                        model.setMenuOpenTime(open);
                                                                        model.setMenuCloseTime(close);

                                                                        model.setMenuOnorOff(st);
                                                                        model.setMenuUnit(unitKg);
                                                                        model.setMenuApprovel("Not Approved");
                                                                        model.setGstno("");
                                                                        model.setTemp1("");
                                                                        model.setTemp2("");
                                                                        model.setTemp3("");

                                                                        model.setRestID(a1);
                                                                        model.setRestName(Resname);

                                                                        image = null;
                                                                        new_menu_image1.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);

                                                                        progressDialog.dismiss();

                                                                        new_menu_name.setText("");
                                                                        new_menu_description.setText("");
                                                                        new_menu_actual_price.setText("");

                                                                        new_menu_opentime.setText("");
                                                                        new_menu_close_time.setText("");

                                                                        new_menu_unit_kg.setText("");

                                                                        ref.child(pushKey).setValue(model);
                                                                        Toast.makeText(AddMenu_Activity.this, "New Menu Added", Toast.LENGTH_SHORT).show();


                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e)
                                                                    {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(AddMenu_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                            }
                                                            else
                                                            {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(AddMenu_Activity.this, "No files selected", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error)
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddMenu_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {
                                progressDialog.dismiss();
                                Toast.makeText(AddMenu_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            }
        });


    }

    private String getFileExtention(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_pic_request && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            image = data.getData();
            Picasso.get().load(image).into(new_menu_image1);
        }

    }

    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, image_pic_request);
    }

}