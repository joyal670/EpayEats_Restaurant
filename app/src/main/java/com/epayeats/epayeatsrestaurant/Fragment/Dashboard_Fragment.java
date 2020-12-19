package com.epayeats.epayeatsrestaurant.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsrestaurant.Activity.Menu_Activity;
import com.epayeats.epayeatsrestaurant.Activity.PendingOrders_Activity;
import com.epayeats.epayeatsrestaurant.Activity.resaurantReportDetails_Activity;
import com.epayeats.epayeatsrestaurant.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Dashboard_Fragment extends Fragment
{
    Button fragment_dashboard_menu_btn;
    SwitchMaterial switchStatus;
    SharedPreferences sharedPreferences;
    String a1;

    TextView total_sale, deliverd_items, pending_items, cancelled_items, neworders_items;

    int count0 = 0;
    int count1 = 0;
    int count2 = 0;
    int count3 = 0;
    int countnew = 0;

    String status0 = "0";
    String status1 = "1";
    String status2 = "2";
    String status3 = "3";

    DatabaseReference orderReference;

    public ProgressDialog progressDialog;

    Button dashboard_report_btn, dashboard_pending_order_btn;

    public Dashboard_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_, container, false);

        sharedPreferences = getContext().getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");

        total_sale = view.findViewById(R.id.total_sale);
        deliverd_items = view.findViewById(R.id.deliverd_items);
        pending_items = view.findViewById(R.id.pending_items);
        cancelled_items = view.findViewById(R.id.cancelled_items);
        dashboard_report_btn = view.findViewById(R.id.dashboard_report_btn);
        neworders_items = view.findViewById(R.id.neworders_items);
        dashboard_pending_order_btn = view.findViewById(R.id.dashboard_pending_order_btn);

        orderReference = FirebaseDatabase.getInstance().getReference("order_data");

        switchStatus= view.findViewById(R.id.switchStatus);
        fragment_dashboard_menu_btn = view.findViewById(R.id.fragment_dashboard_menu_btn);
        fragment_dashboard_menu_btn.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), Menu_Activity.class);
            startActivity(intent);
        });

        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                changeStatus(isChecked);
            }
        });

        dashboard_report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), resaurantReportDetails_Activity.class);
                startActivity(intent);
            }
        });

        dashboard_pending_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), PendingOrders_Activity.class);
                startActivity(intent);
            }
        });

        loadStatus();
        loadAllOrders();
        loadDeliverdOrders();
        loadPendingOrders();
        loadCancelledOrders();
        loanewOrders();

        return view;
    }



    private void changeStatus(boolean isChecked)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(a1);

        if(isChecked)
        {
            switchStatus.setText("Opened");
            ref.child("isShopClosed").setValue("open");
        }
        else
        {
            switchStatus.setText("Closed");
            ref.child("isShopClosed").setValue("closed");
        }
    }

    private void loadStatus()
    {
        progressDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("restaurants").child(a1);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                progressDialog.dismiss();
                String sta = snapshot.child("isShopClosed").getValue().toString();
                String on = "open";
                if(on.equals(sta))
                {
                    switchStatus.setChecked(true);
                    switchStatus.setText("opened");
                }
                else
                {
                    switchStatus.setChecked(false);
                    switchStatus.setText("closed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                progressDialog.dismiss();
                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllOrders()
    {
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if(a1.equals(dataSnapshot1.child("restID").getValue().toString()))
                    {
                        try {
                            count0 = count0 + 1;
                            total_sale.setText(count0 + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDeliverdOrders()
    {
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if(status2.equals(dataSnapshot1.child("orderStatus").getValue().toString()))
                    {
                        if(a1.equals(dataSnapshot1.child("restID").getValue().toString()))
                        {
                            try {
                                count2 = count2 + 1;
                                deliverd_items.setText(count2 + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadPendingOrders()
    {
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if(status1.equals(dataSnapshot1.child("orderStatus").getValue().toString()))
                    {
                        if(a1.equals(dataSnapshot1.child("restID").getValue().toString()))
                        {
                            try {
                                count1 = count1 + 1;
                                pending_items.setText(count1 + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadCancelledOrders()
    {
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if(status3.equals(dataSnapshot1.child("orderStatus").getValue().toString()))
                    {
                        if(a1.equals(dataSnapshot1.child("restID").getValue().toString()))
                        {
                            try {
                                count3 = count3 + 1;
                                cancelled_items.setText(count3 + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loanewOrders()
    {
        progressDialog.show();
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if (a1.equals(dataSnapshot1.child("restID").getValue().toString()))
                    {
                        if (status0.equals(dataSnapshot1.child("orderStatus").getValue().toString()))
                        {
                            try {
                                countnew = countnew + 1;
                                neworders_items.setText(countnew + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}