package com.example.passiton.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.passiton.Data.Book;
import com.example.passiton.Data.User;
import com.example.passiton.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddBookActivity extends AppCompatActivity {
    private TextInputEditText add_EDT_name,add_EDT_author,add_EDT_genre,add_EDT_image;
    private Button add_BTN_save,add_BTN_browse,add_BTN_cancel;
    private EditText add_LBL_bio;
    private FirebaseDatabase database;
    private DatabaseReference refBooks,refUsers;
    private String imageName="";
    private StorageReference sRef;
    private FirebaseStorage storage;
    private FirebaseUser fUser;
    private Book book;
    private User user;
    private Uri uri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        book=new Book();
        user=new User();
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        storage=FirebaseStorage.getInstance();
        sRef=storage.getReference();
        database = FirebaseDatabase.getInstance();
        refBooks = database.getReference("books");
        refUsers = database.getReference("users");
        setFineViews();
        setImage();
        setCancel();
        setAdd();

    }

    private void setFineViews() {
        add_EDT_name=findViewById(R.id.add_EDT_name);
        add_EDT_author=findViewById(R.id.add_EDT_author);
        add_EDT_genre=findViewById(R.id.add_EDT_genre);
        add_EDT_image=findViewById(R.id.add_EDT_image);
        add_LBL_bio=findViewById(R.id.add_LBL_bio);
        add_BTN_save=findViewById(R.id.add_BTN_save);
        add_BTN_browse=findViewById(R.id.add_BTN_browse);
        add_BTN_cancel=findViewById(R.id.add_BTN_cancel);
    }
    private void setImage() {
        add_BTN_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent,1000);
            }
        });
    }
    private void uploadImage() {
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Uploading...");
        dialog.show();
        StorageReference ref = sRef.child("images/"+UUID.randomUUID().toString());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                imageName = taskSnapshot.getStorage().getName();
                book.setImageUri(imageName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error, Image not uploaded
                dialog.dismiss();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                dialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }
    private void setCancel() {
        add_BTN_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                finish();
            }
        });
    }
    private void setAdd() {
        add_BTN_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDetailsEmpty()) {
                    book.setName(add_EDT_name.getText().toString());
                    book.setAuthor(add_EDT_author.getText().toString());
                    book.setGenre(add_EDT_genre.getText().toString());
                    book.setBio(add_LBL_bio.getText().toString());
                    refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                if (user.getId().equals(fUser.getUid())) {
                                    book.setUser(user);
                                    refBooks.child(book.getName()).setValue(book);
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
                    Intent intent = new Intent(getApplicationContext(), ViewListOfBooksActivity.class);
                    intent.putExtra("type", "mybooks");
                    startActivity(intent);
                    finish();
                } else{
                    popUpMessege();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==1000){
                uri=data.getData();
                uploadImage();
                add_EDT_image.setText(data.getData().getPath());
            }
        }
    }

    private boolean isDetailsEmpty() {
        if(add_LBL_bio.getText().equals("") ||
           add_EDT_image.getText().toString().equals("") ||
            add_EDT_author.getText().toString().equals("")||
            add_EDT_name.getText().toString().equals("")||
            add_EDT_genre.getText().toString().equals("")){
            return true;
        }
        return false;
    }
    private void popUpMessege(){
        Dialog dialog=new Dialog((this));
        dialog.setContentView(R.layout.activity_popup_message);
        MaterialButton close=dialog.findViewById(R.id.popup_BTN_close);
        MaterialTextView text=dialog.findViewById(R.id.popup_LBL_message);
        text.setText("Make sure there are\n no empty fields left");
        close.setOnClickListener(view->dialog.dismiss());
        dialog.show();
    }


}