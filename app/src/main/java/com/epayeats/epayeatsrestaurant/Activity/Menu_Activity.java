package com.epayeats.epayeatsrestaurant.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.epayeats.epayeatsrestaurant.Adapter.MenuAdapter;
import com.epayeats.epayeatsrestaurant.Model.MenuModel;
import com.epayeats.epayeatsrestaurant.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Menu_Activity extends AppCompatActivity
{
    SearchView activity_menu_searchView;
    SwipeRefreshLayout refresh_menu;
    ListView activity_menu_listview;
    FloatingActionsMenu menuActivityFloatingMenu;
    FloatingActionButton fab_menuActivityAdd;

    SharedPreferences sharedPreferences;
    String a1;

    DatabaseReference mMenuDatabaseReference;
    List<MenuModel> mMenuModel;
    MenuAdapter mMenuAdapter;


    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_);

        activity_menu_searchView = findViewById(R.id.activity_menu_searchView);
        refresh_menu = findViewById(R.id.refresh_menu);
        activity_menu_listview = findViewById(R.id.activity_menu_listview);
        menuActivityFloatingMenu = findViewById(R.id.menuActivityFloatingMenu);
        fab_menuActivityAdd = findViewById(R.id.fab_menuActivityAdd);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        sharedPreferences = getSharedPreferences("data", 0);
        a1 = sharedPreferences.getString("userid", "");

        mMenuDatabaseReference = FirebaseDatabase.getInstance().getReference("menu");

        loadData();

        refresh_menu.setRefreshing(true);
        refresh_menu.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        activity_menu_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchMenu(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchMenu(newText);
                return false;
            }
        });

        fab_menuActivityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuActivityFloatingMenu.collapse();
                addMenu();
            }
        });
    }

    private void addMenu()
    {
        Intent intent = new Intent(Menu_Activity.this, AddMenu_Activity.class);
        startActivity(intent);
    }

    private void searchMenu(String query)
    {
        ArrayList<MenuModel> myList = new ArrayList<>();
        for (MenuModel obj : mMenuModel)
        {
            if (obj.getMenuName().toLowerCase().contains(query.toLowerCase())) {
                myList.add(obj);
            }
        }
        MenuAdapter localadminAdapter = new MenuAdapter(this, myList);
        activity_menu_listview.setAdapter(localadminAdapter);
        activity_menu_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void loadData()
    {
        mMenuDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                refresh_menu.setRefreshing(false);
                mMenuModel.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    if(a1.equals(snapshot1.child("restID").getValue().toString()))
                    {
                        MenuModel model = snapshot1.getValue(MenuModel.class);
                        mMenuModel.add(model);
                    }
                }
                mMenuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(Menu_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mMenuModel = new ArrayList<>();
        mMenuAdapter = new MenuAdapter(this, mMenuModel);
        activity_menu_listview.setAdapter(mMenuAdapter);
        activity_menu_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                editMenu(position);
            }
        });
    }

    private void editMenu(int position)
    {
        String[] items = {"Edit", "Delete"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Options");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(which == 0)
                {
                    LayoutInflater li = LayoutInflater.from(Menu_Activity.this);
                    View menu = li.inflate(R.layout.addmenu_catagory_layout, null);
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Menu_Activity.this);
                    alertBuilder.setView(menu);

                    final EditText add_menu_name, add_menu_description;
                    final EditText add_menu_opentime, add_menu_close_time, add_menu_unit_kg;
                    final Spinner add_menu_main_catagory_spinner, add_menu_sub_catagory_spinner;
                    final Button add_menu_save_btn;
                    final SwitchCompat add_menu_on_off;

                    add_menu_name = menu.findViewById(R.id.add_menu_name);
                    add_menu_description = menu.findViewById(R.id.add_menu_description);

                    add_menu_opentime = menu.findViewById(R.id.add_menu_opentime);
                    add_menu_close_time = menu.findViewById(R.id.add_menu_close_time);
                    add_menu_unit_kg = menu.findViewById(R.id.add_menu_unit_kg);
                    add_menu_on_off = menu.findViewById(R.id.add_menu_on_off);
                    add_menu_save_btn = menu.findViewById(R.id.add_menu_save_btn);

                    add_menu_main_catagory_spinner = menu.findViewById(R.id.add_menu_main_catagory_spinner);
                    add_menu_sub_catagory_spinner = menu.findViewById(R.id.add_menu_sub_catagory_spinner);

                    add_menu_description.setText(mMenuModel.get(position).getMenuDescription());
                    add_menu_unit_kg.setText(mMenuModel.get(position).getMenuUnit());

                    add_menu_name.setText(mMenuModel.get(position).getMenuName());
                    add_menu_opentime.setText(mMenuModel.get(position).getMenuOpenTime());
                    add_menu_close_time.setText(mMenuModel.get(position).getMenuCloseTime());

                    String tem = mMenuModel.get(position).getMenuOnorOff();
                    if(tem.equals("on"))
                    {
                        add_menu_on_off.setChecked(true);
                    }
                    else
                    {
                        add_menu_on_off.setChecked(false);
                    }

                    // main catagory spinner
                    DatabaseReference businessReference = FirebaseDatabase.getInstance().getReference("main_catagory");
                    ArrayList<String> spinnerDatalist;
                    ArrayAdapter<String> adapter;
                    ValueEventListener listener;
                    String msg = "Select --  ";

                    progressDialog.show();
                    spinnerDatalist = new ArrayList<>();
                    adapter = new ArrayAdapter<String>(Menu_Activity.this, android.R.layout.simple_spinner_dropdown_item, spinnerDatalist);
                    listener = businessReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            spinnerDatalist.clear();
                            spinnerDatalist.add(mMenuModel.get(position).getMenuMainCatagorey());
                            spinnerDatalist.add(msg);
                            progressDialog.dismiss();
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren())
                            {
                                spinnerDatalist.add(dataSnapshot1.child("foodCatagoreyType").getValue().toString());
                                add_menu_main_catagory_spinner.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(Menu_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    // sub catagory spinner
                    DatabaseReference catagoryRef = FirebaseDatabase.getInstance().getReference("sub_category");
                    ArrayList<String> spinnerDatalistcat;
                    ArrayAdapter<String> adaptercat;
                    ValueEventListener listenercat;

                    progressDialog.show();
                    spinnerDatalistcat = new ArrayList<>();
                    adaptercat = new ArrayAdapter<String>(Menu_Activity.this, android.R.layout.simple_spinner_dropdown_item, spinnerDatalistcat);
                    listenercat = catagoryRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            spinnerDatalistcat.clear();
                            spinnerDatalistcat.add(mMenuModel.get(position).getMenuSubCatagorey());
                            spinnerDatalistcat.add(msg);
                            progressDialog.dismiss();
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren())
                            {
                                spinnerDatalistcat.add(dataSnapshot1.child("subCatagoryName").getValue().toString());
                                add_menu_sub_catagory_spinner.setAdapter(adaptercat);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(Menu_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                    final AlertDialog alertDialog = alertBuilder.create();

                    add_menu_save_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            String name, open, close, decription, unit, main, sub;

                            name = add_menu_name.getText().toString();
                            open = add_menu_opentime.getText().toString();
                            close = add_menu_close_time.getText().toString();
                            decription = add_menu_description.getText().toString();
                            unit = add_menu_unit_kg.getText().toString();
                            main = add_menu_main_catagory_spinner.getSelectedItem().toString();
                            sub = add_menu_sub_catagory_spinner.getSelectedItem().toString();

                            if(name.isEmpty() || open.isEmpty() || close.isEmpty() || decription.isEmpty() || unit.isEmpty())
                            {
                                if(name.isEmpty())
                                {
                                    add_menu_name.setError("Required");
                                }
                                if(open.isEmpty())
                                {
                                    add_menu_opentime.setError("Required");
                                }
                                if(close.isEmpty())
                                {
                                    add_menu_close_time.setError("Required");
                                }
                                if(decription.isEmpty())
                                {
                                    add_menu_description.setError("Required");
                                }
                                if(unit.isEmpty())
                                {
                                    add_menu_unit_kg.setError("Required");
                                }
                            }
                            else
                            {
                                if(main.equals(msg) || sub.equals(msg))
                                {
                                    if(main.equals(msg))
                                    {
                                        Toast.makeText(Menu_Activity.this, "Select Main Catagory", Toast.LENGTH_SHORT).show();
                                    }
                                    if(sub.equals(msg))
                                    {
                                        Toast.makeText(Menu_Activity.this, "Select Sub Catagory", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    String crtval;
                                    if(add_menu_on_off.isChecked())
                                    {
                                        crtval = "on";
                                    }
                                    else
                                    {
                                        crtval = "off";
                                    }

                                    progressDialog.show();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("menu").child(mMenuModel.get(position).getMenuID());
                                    ref.child("menuName").setValue(name);
                                    ref.child("menuOpenTime").setValue(open);
                                    ref.child("menuCloseTime").setValue(close);
                                    ref.child("menuOnorOff").setValue(crtval);
                                    ref.child("menuDescription").setValue(decription);
                                    ref.child("menuUnit").setValue(unit);

                                    progressDialog.dismiss();
                                    Toast.makeText(Menu_Activity.this, "Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                    alertDialog.cancel();
                                }

                            }
                        }
                    });
                    alertDialog.show();
                }
                if(which == 1)
                {
                    SweetAlertDialog dialog1 = new SweetAlertDialog(Menu_Activity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Won't be able to recover!")
                            .setConfirmText("Yes, delete it!")
                            .setCancelText("No, cancel please")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog)
                                {
                                    sweetAlertDialog.setTitleText("Deleted")
                                            .setContentText("Data has been deleted")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    database.getReference("menu").child(mMenuModel.get(position).getMenuID()).removeValue();
                                }
                            });
                    dialog1.show();
                }
            }
        });
        dialog.create().show();
    }

}