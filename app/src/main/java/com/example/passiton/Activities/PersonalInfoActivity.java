package com.example.passiton.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.passiton.Data.User;
import com.example.passiton.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalInfoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseUser fUser;
    private Button profile_BTN_edit,profile_BTN_save;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private User newUser =new User();
    private EditText profile_EDT_name,profile_EDT_email,profile_EDT_city,profile_EDT_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        fUser=FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        setFindViews();
        setMenu();
        setInfoDB();
    }

    private void setFindViews() {
        profile_EDT_name=findViewById(R.id.profile_EDT_name);
        profile_EDT_name.setEnabled(false);
        profile_EDT_email=findViewById(R.id.profile_EDT_email);
        profile_EDT_email.setEnabled(false);
        profile_EDT_city=findViewById(R.id.profile_EDT_city);
        profile_EDT_city.setEnabled(false);
        profile_EDT_phone=findViewById(R.id.profile_EDT_phone);
        profile_EDT_phone.setEnabled(false);
        toolbar=(Toolbar) findViewById(R.id.toolBar);
        profile_BTN_edit=findViewById(R.id.profile_BTN_edit);
        profile_BTN_save=findViewById(R.id.profile_BTN_save);


    }
    private void setMenu() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        if (newUser.notEmpty()) {
            returnIntent.putExtra("message", View.INVISIBLE);
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void setInfoDB(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                boolean found=false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    newUser = snapshot.getValue(User.class);
                    String t=fUser.getUid();
                    if (newUser.getId().equals(fUser.getUid())) {
                        setInfoView();
                        //if edit was clicked
                        setEdit();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ccc", "Failed to read value.", error.toException());
            }
        });
    }

    private void setEdit() {
        profile_BTN_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_BTN_save.setEnabled(true);
                profile_EDT_name.setEnabled(true);
                profile_EDT_email.setEnabled(true);
                profile_EDT_city.setEnabled(true);
                profile_EDT_phone.setEnabled(true);
            }
        });
        setSave();

    }

    private void setSave() {
        profile_BTN_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_BTN_save.setEnabled(false);
                profile_EDT_name.setEnabled(false);
                profile_EDT_email.setEnabled(false);
                profile_EDT_city.setEnabled(false);
                profile_EDT_phone.setEnabled(false);
                newUser.setAllDetails(profile_EDT_name.getText(), profile_EDT_email.getText(), profile_EDT_city.getText(), profile_EDT_phone.getText());
                myRef.child(newUser.getId()).setValue(newUser);
            }
        });
    }

    private void setInfoView() {
        profile_EDT_name.setText(newUser.getName());
        profile_EDT_email.setText(newUser.getEmail());
        profile_EDT_city.setText(newUser.getCity());
        profile_EDT_phone.setText(newUser.getPhoneNum());
    }





}