package com.example.passiton.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passiton.Activities.AddBookActivity;
import com.example.passiton.Activities.BookDetailsActivity;
import com.example.passiton.Adapters.BookAdapter;
import com.example.passiton.Adapters.RecyclerItemClickListener;
import com.example.passiton.Data.Book;
import com.example.passiton.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewListOfBooksActivity extends AppCompatActivity {
    private ArrayList<Book> listOfBooks =new ArrayList<>();
    private TextView view_LBL_title,view_LBL_text;
    private EditText view_EDT_bName;
    private RecyclerView view_RCV_list;
    private Button view_BTN_add,view_BTN_bookDelete,view_BTN_delete;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Toolbar toolbar;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        String type = getIntent().getStringExtra("type");
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("books");
        setFindViews();
        setMenu();

        if(type.equals("mybooks")){
            view_LBL_title.setText("My Books");
            readMyBooks();
            addBook();
            deleteValue();

        }else{
            view_LBL_title.setText("My Favorites");
            readFavorites();
            view_BTN_add.setVisibility(View.INVISIBLE);
            view_BTN_bookDelete.setVisibility(View.INVISIBLE);
        }
        setClickOnBookInList();
    }

    private void setFindViews() {
        view_LBL_title=findViewById(R.id.view_LBL_title);
        view_RCV_list =findViewById(R.id.view_RCV_list);
        view_BTN_add =findViewById(R.id.view_BTN_add);
        view_BTN_bookDelete=findViewById(R.id.view_BTN_bookDelete);
        view_BTN_delete=findViewById(R.id.view_BTN_delete);
        toolbar=(Toolbar) findViewById(R.id.toolBar);
        view_EDT_bName=findViewById(R.id.view_EDT_bName);
        view_LBL_text=findViewById(R.id.view_LBL_text);



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

    private void deleteValue() {
        view_BTN_bookDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Book book=snapshot.getValue(Book.class);
                            if(book.getUser().getId().equalsIgnoreCase(fUser.getUid())){
                                view_BTN_bookDelete.setEnabled(false);
                                view_LBL_text.setVisibility(View.VISIBLE);
                                view_EDT_bName.setVisibility(View.VISIBLE);
                                view_BTN_delete.setVisibility((View.VISIBLE));
                                view_BTN_delete.setOnClickListener(view->delete(book));
                                break;
                            }else{
                                Toast.makeText(getApplicationContext(), "You have no books to delete", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    private void delete(Book book){
        if(!view_EDT_bName.getText().toString().equals("")) {
            if (book.getName().equalsIgnoreCase(view_EDT_bName.getText().toString())) {
                myRef.child(book.getName()).getRef().removeValue();
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "Invalid name, please try again", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Empty field name-can't delete", Toast.LENGTH_LONG).show();
        }
    }



    private void setClickOnBookInList() {
        view_RCV_list.addOnItemTouchListener(new RecyclerItemClickListener(this, view_RCV_list, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Book book= listOfBooks.get(position);
                        Intent intent=new Intent(getApplicationContext(), BookDetailsActivity.class);
                        intent.putExtra("bName", book.getName());
                        startActivity(intent);
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );
    }


    private void readMyBooks() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Book book = snapshot.getValue(Book.class);
                    if(book.getUser().getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        listOfBooks.add(book);
                    }
                }
               setListOfBooks();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ccc", "Failed to read value.", error.toException());
            }
        });
    }


    private void readFavorites() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Book book = snapshot.getValue(Book.class);
                    if(book.isSelected()) {
                        listOfBooks.add(book);
                    }
                }
                setListOfBooks();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ccc", "Failed to read value.", error.toException());
            }
        });
    }



    private void addBook() {
        view_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddBookActivity.class));
                finish();
            }
        });
    }


    private void setListOfBooks() {
        BookAdapter bookAdapter = new BookAdapter(this, listOfBooks);
        view_RCV_list.setLayoutManager(new GridLayoutManager(this,2));
        view_RCV_list.setHasFixedSize(true);
        view_RCV_list.setAdapter(bookAdapter);

    }
}