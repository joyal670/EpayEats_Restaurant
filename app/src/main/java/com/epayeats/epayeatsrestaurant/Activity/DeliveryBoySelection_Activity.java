package com.epayeats.epayeatsrestaurant.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epayeats.epayeatsrestaurant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DeliveryBoySelection_Activity extends AppCompatActivity
{
    String orderID;
    String menuID;
    String menuName;
    String menuImage;

    String mainCatagoryID;
    String mainCatagoryName;
    String subCatagoryID;
    String subCatagoryName;

    String localAdminID;

    String offerPrice;
    String sellingPrice;
    String actualPrice;

    String orderDate;
    String orderTime;
    String qty;
    String totalPrice;

    String house;
    String area;
    String city;
    String pincode;
    String cName;
    String cPhone;
    String cAltPhone;

    String userID;
    String userLocation;

    String status;

    ImageView current_image;
    TextView current_name, current_price, current_qty, current_house, current_area, current_pincode, current_cname, current_cnumber, current_status;


    DatabaseReference reference;

    TextView sample_menu_total;
    int total = 0;

    TextView current_ordredTime, current_deliveredTime, current_ordredDate, current_deliveredDate;
    String deliverytime, deliverydate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_selection_);

         orderID = getIntent().getExtras().getString("orderID");
         menuID = getIntent().getExtras().getString("menuID");
         menuName = getIntent().getExtras().getString("menuName");
         menuImage = getIntent().getExtras().getString("menuImage");

         mainCatagoryID = getIntent().getExtras().getString("mainCatagoryID");
         mainCatagoryName = getIntent().getExtras().getString("mainCatagoryName");
         subCatagoryID = getIntent().getExtras().getString("subCatagoryID");
         subCatagoryName = getIntent().getExtras().getString("subCatagoryName");

         localAdminID = getIntent().getExtras().getString("localAdminID");

         offerPrice = getIntent().getExtras().getString("offerPrice");
         sellingPrice = getIntent().getExtras().getString("sellingPrice");
         actualPrice = getIntent().getExtras().getString("actualPrice");

         orderDate = getIntent().getExtras().getString("orderDate");
         orderTime = getIntent().getExtras().getString("orderTime");
         qty = getIntent().getExtras().getString("qty");
         totalPrice = getIntent().getExtras().getString("totalPrice");

         house = getIntent().getExtras().getString("house");
         area = getIntent().getExtras().getString("area");
         city = getIntent().getExtras().getString("city");
         pincode = getIntent().getExtras().getString("pincode");
         cName = getIntent().getExtras().getString("cName");
         cPhone = getIntent().getExtras().getString("cPhone");
         cAltPhone = getIntent().getExtras().getString("cAltPhone");

         userID = getIntent().getExtras().getString("userID");
         userLocation = getIntent().getExtras().getString("userLocation");

        status = getIntent().getExtras().getString("status");
        deliverytime = getIntent().getExtras().getString("deliverytime");
        deliverydate = getIntent().getExtras().getString("deliverydate");

        current_image = findViewById(R.id.current_image);
        current_name = findViewById(R.id.current_name);
        current_price = findViewById(R.id.current_price);
        current_qty = findViewById(R.id.current_qty);
        current_house = findViewById(R.id.current_house);
        current_area = findViewById(R.id.current_area);
        current_pincode = findViewById(R.id.current_pincode);
        current_cname = findViewById(R.id.current_cname);
        current_cnumber = findViewById(R.id.current_cnumber);
        current_status = findViewById(R.id.current_status);
        current_ordredTime = findViewById(R.id.current_ordredTime);
        current_deliveredTime = findViewById(R.id.current_deliveredTime);
        current_ordredDate = findViewById(R.id.current_ordredDate);
        current_deliveredDate = findViewById(R.id.current_deliveredDate);

        sample_menu_total = findViewById(R.id.sample_menu_total);


        Picasso.get().load(menuImage).into(current_image);

        current_name.setText(menuName);
        current_price.setText(actualPrice);
        current_qty.setText(qty);
        current_house.setText(house);
        current_area.setText(area);
        current_pincode.setText(pincode);
        current_cname.setText(cName);
        current_cnumber.setText(cPhone);
        current_ordredTime.setText(orderTime);
        current_deliveredTime.setText(deliverytime);
        current_ordredDate.setText(orderDate);
        current_deliveredDate.setText(deliverydate);

        if (status.equals("0"))
        {
            current_status.setText("Pending, Not yet Delivered");
        }
        else if (status.equals("1"))
        {
            current_status.setText("Order is Picked up by the Delivery Agent");

        }
        else if (status.equals("2"))
        {
            current_status.setText("Delivered");

        }
        else
            {
            current_status.setText("Cancelled");

        }

        int qty = 0;
        int price = 0;
        qty = qty + Integer.parseInt(current_qty.getText().toString());
        price = price + Integer.parseInt(actualPrice);
        int temp = qty * price;
        total = total + temp;
        sample_menu_total.setText(total + "");



    }
}