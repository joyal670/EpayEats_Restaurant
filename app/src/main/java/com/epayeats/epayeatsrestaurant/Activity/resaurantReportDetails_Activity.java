package com.epayeats.epayeatsrestaurant.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsrestaurant.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class resaurantReportDetails_Activity extends AppCompatActivity
{

    TextView selectedDate_From_admn, selectedDate_To_admn;
    Button search_generate_coadmin_report;


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date fromD = new Date();
    Date toD = new Date();


    TextView coadmin_report_totaldeliverd, coadmin_report_earned;
    int report_totaldeliverd = 0;

    String status2 = "2";

    int delivCharge;
    int delivtotal = 0;

    int totalearned = 0;

    public ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    String myid;

    Button rest_report_from_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resaurant_report_details_);

        sharedPreferences = getSharedPreferences("data", 0);
        myid = sharedPreferences.getString("userid", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");


        selectedDate_From_admn = findViewById(R.id.selectedDate_From_rest);
        selectedDate_To_admn = findViewById(R.id.selectedDate_To_rest);
        search_generate_coadmin_report = findViewById(R.id.search_generate_rest_report);

        coadmin_report_totaldeliverd = findViewById(R.id.rest_report_totaldeliverd);
        coadmin_report_earned = findViewById(R.id.rest_report_earned);
        rest_report_from_date = findViewById(R.id.rest_report_from_date);


        search_generate_coadmin_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a1 = selectedDate_From_admn.getText().toString();
                String a2 = selectedDate_To_admn.getText().toString();
                if (a1.isEmpty() || a2.isEmpty()) {
                    Toast.makeText(resaurantReportDetails_Activity.this, "Select Date", Toast.LENGTH_SHORT).show();
                } else {
                    generateReport(a1, a2);
                }
            }
        });

        CalendarConstraints.Builder conBuilder = new CalendarConstraints.Builder();
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date");
        builder.setCalendarConstraints(conBuilder.build());
        final MaterialDatePicker materialDatePicker = builder.build();
        rest_report_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection)
            {
                Pair selectedDates = (Pair) materialDatePicker.getSelection();

                final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));

                Date startDate = rangeDate.first;
                Date endDate = rangeDate.second;

                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                selectedDate_From_admn.setText(simpleFormat.format(startDate));
                selectedDate_To_admn.setText(simpleFormat.format(endDate));
            }
        });


    }

    private void generateReport(String a1, String a2)
    {
        try {
            String tem1 = a1;
            String tem2 = a2;


            try {
                fromD = dateFormat.parse(a1);
                toD = dateFormat.parse(a2);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            report_totaldeliverd = 0;
            coadmin_report_totaldeliverd.setText("0");
            coadmin_report_earned.setText("0");

            delivtotal = 0;
            delivCharge = 0;

            totalearned = 0;

            progressDialog.show();
            Query query = FirebaseDatabase.getInstance().getReference("order_data");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        if (myid.equals(snapshot1.child("restID").getValue().toString())) {
                            String d1 = snapshot1.child("orderDate").getValue().toString();

                            try {
                                Date cd = dateFormat.parse(d1);

                                if (fromD.before(cd) && cd.before(toD)) {
                                    if (status2.equals(snapshot1.child("orderStatus").getValue().toString())) {

                                        report_totaldeliverd = report_totaldeliverd + 1;
                                        coadmin_report_totaldeliverd.setText(report_totaldeliverd + "");

                                        int qty = 0;
                                        int price = 0;
                                        qty = qty + Integer.parseInt(snapshot1.child("qty").getValue().toString());
                                        price = price + Integer.parseInt(snapshot1.child("actualPrice").getValue().toString());
                                        int temp = qty * price;

                                        totalearned = totalearned + temp;
                                        coadmin_report_earned.setText(totalearned + "");

                                    }
                                }


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(resaurantReportDetails_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}