package com.example.passiton.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.passiton.Data.Book;
import com.example.passiton.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BookDetailsActivity extends AppCompatActivity {
    private MaterialTextView details_LBL_bName,details_LBL_aName,details_LBL_genre,details_LBL_bio;
    private MaterialTextView details_LBL_cName,details_LBL_cCity,details_LBL_cPhone;
    private ImageView details_IMG_image,detail_IMG_heart,detail_IMG_heart2;
    private Toolbar toolbar;
    private FirebaseDatabase database;
    private DatabaseReference refBook;
    private StorageReference photoRef;
    private FirebaseStorage storage;
    private boolean hasAction =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        database = FirebaseDatabase.getInstance();
        refBook = database.getReference("books");
        storage=FirebaseStorage.getInstance();
        setFindViews();
        setMenu();
        setLikeAction();
        setBookDetail();
    }

    private void setFindViews() {
        details_LBL_bName=findViewById(R.id.details_LBL_bName);
        details_LBL_aName=findViewById(R.id.details_LBL_aName);
        details_LBL_genre=findViewById(R.id.details_LBL_genre);
        details_IMG_image=findViewById(R.id.details_IMG_image);
        details_LBL_bio=findViewById(R.id.details_LBL_bio);
        toolbar=(Toolbar) findViewById(R.id.toolBar);
        details_LBL_cName=findViewById(R.id.details_LBL_cName);
        details_LBL_cCity=findViewById(R.id.details_LBL_cCity);
        details_LBL_cPhone=findViewById(R.id.details_LBL_cPhone);
        detail_IMG_heart=findViewById(R.id.detail_IMG_heart);
        detail_IMG_heart2=findViewById(R.id.detail_IMG_heart2);
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
    private void setLikeAction() {
        detail_IMG_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail_IMG_heart2.setVisibility(View.VISIBLE);
                detail_IMG_heart2.setClickable(true);
                detail_IMG_heart.setVisibility(View.INVISIBLE);
                detail_IMG_heart.setClickable(false);
                hasAction =true;
                setLike(true, hasAction);
            }
        });

        detail_IMG_heart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail_IMG_heart.setVisibility(View.VISIBLE);
                detail_IMG_heart.setClickable(true);
                detail_IMG_heart2.setVisibility(View.INVISIBLE);
                detail_IMG_heart2.setClickable(false);
                hasAction =true;
                setLike(false, hasAction);
            }
        });
        setLike(false, hasAction);
    }
    private void setLike(boolean selected,boolean act) {
        details_LBL_bName.setText(getIntent().getStringExtra("bName"));
        refBook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if(book.getName().equals(details_LBL_bName.getText())){
                        if(book.isSelected()==true && selected==true){
                            book.setSelected(true);
                        }else if(book.isSelected()==true &&selected==false && act==false) {
                            book.setSelected(true);
                        }else if(book.isSelected()==true &&selected==false && act==true){
                            book.setSelected(false);
                        }else if(book.isSelected()==false &&selected==true){
                            book.setSelected(true);

                        }else {
                            book.setSelected(false);
                        }
                        refBook.child(book.getName()).setValue(book);
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
    private void setBookDetail() {
        refBook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if(book.getName().equals(details_LBL_bName.getText())){
                        details_LBL_genre.setText(book.getGenre());
                        details_LBL_aName.setText(book.getAuthor());
                        details_LBL_bio.setText(book.getBio());
                        if (book.getImage() != 0) {
                            //photo from local space
                            details_IMG_image.setImageResource(book.getImage());
                        }else{
                            photoRef = storage.getReference("images/"+book.getImageUri());
                            photoFromFirebase();
                        }
                        details_LBL_cName.setText(book.getUser().getName());
                        details_LBL_cCity.setText(book.getUser().getCity());
                        details_LBL_cPhone.setText(book.getUser().getPhoneNum());
                        if(book.isSelected()){
                            detail_IMG_heart2.setVisibility(View.VISIBLE);
                            detail_IMG_heart2.setClickable(true);
                            detail_IMG_heart.setVisibility(View.INVISIBLE);
                            detail_IMG_heart.setClickable(false);
                        }
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
    private void photoFromFirebase() {
        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Glide.with(BookDetailsActivity.this).load(downloadUrl)
                        .into(details_IMG_image);
            }
        });
    }
}