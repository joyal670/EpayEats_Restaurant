package com.epayeats.epayeatsrestaurant.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsrestaurant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class Account_Fragment extends Fragment {

    TextView account_resname, account_email, account_id, account_phone, account_location;
    TextView account_open_time, account_close_time, account_local_admin, account_licenceno;
    TextView account_gstno;
    ImageView account_photo;

    SharedPreferences sharedPreferences;
    String a1;
    public ProgressDialog progressDialog;
    DatabaseReference reference;

    public Account_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_, container, false);


        sharedPreferences = getContext().getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        account_resname = view.findViewById(R.id.account_resname);
        account_email = view.findViewById(R.id.account_email);
        account_id = view.findViewById(R.id.account_id);
        account_phone = view.findViewById(R.id.account_phone);
        account_location = view.findViewById(R.id.account_location);
        account_open_time = view.findViewById(R.id.account_open_time);
        account_close_time = view.findViewById(R.id.account_close_time);
        account_local_admin = view.findViewById(R.id.account_local_admin);
        account_licenceno = view.findViewById(R.id.account_licenceno);
        account_gstno = view.findViewById(R.id.account_gstno);
        account_photo = view.findViewById(R.id.account_photo);

        progressDialog.show();
        reference = FirebaseDatabase.getInstance().getReference("restaurants").child(a1);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                progressDialog.dismiss();
                account_resname.setText(snapshot.child("resName").getValue().toString());
                account_email.setText(snapshot.child("resEmail").getValue().toString());
                account_id.setText(snapshot.child("resID").getValue().toString());
                account_phone.setText(snapshot.child("resPhone").getValue().toString());
                account_location.setText(snapshot.child("resLocation").getValue().toString());
                account_open_time.setText(snapshot.child("resOpenTime").getValue().toString());
                account_close_time.setText(snapshot.child("resCloseTime").getValue().toString());
                account_local_admin.setText(snapshot.child("resLocalAdminName").getValue().toString());
                account_licenceno.setText(snapshot.child("resLicenceNo").getValue().toString());
                account_gstno.setText(snapshot.child("resGstNo").getValue().toString());

                Picasso.get().load(snapshot.child("resPhoto").getValue().toString()).into(account_photo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                progressDialog.dismiss();
                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}