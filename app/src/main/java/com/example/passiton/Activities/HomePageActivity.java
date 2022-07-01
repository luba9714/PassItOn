package com.example.passiton.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.passiton.Adapters.BookAdapter;
import com.example.passiton.Adapters.RecyclerItemClickListener;
import com.example.passiton.Data.Book;
import com.example.passiton.Data.User;
import com.example.passiton.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomePageActivity extends AppCompatActivity {
    private TextView genre;
    private MaterialTextView home_LBL_list,home_LBL_message,header_LBL_name;
    private SearchView home_SRH_search;
    private ArrayList<Book> listOfBooks=new ArrayList<>();
    private ArrayList<Book> filteredList=new ArrayList<>();
    private Button home_BTN_refresh;
    private String[] genreList = {"Romance", "Fiction","Fantasy", "Action","Biography","Crime","Historical"};
    private ArrayList<Integer> chosen = new ArrayList<>();
    private boolean[] selectedLanguage;
    private Toolbar toolbar;
    private DrawerLayout drawerlayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private RecyclerView home_RCV_books;
    private FirebaseDatabase database;
    private DatabaseReference refBooks,refUsers;
    private FirebaseUser fUser;
    public User user=new User();
    private boolean isExist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        selectedLanguage = new boolean[genreList.length];
        fUser=FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        refBooks = database.getReference("books");
        refUsers = database.getReference("users");
        setFindViews();
        setMenu();
        setUser();
        hardCoded();
        readData();
        setClickOnBookInList();
        setFilterCheckBok();
        home_BTN_refresh.setOnClickListener(view->readData());
        setSearch();
    }


    private void setFindViews() {
        genre = findViewById(R.id.home_LBL_genre);
        home_SRH_search=findViewById(R.id.home_SRH_search);
        toolbar=(Toolbar) findViewById(R.id.toolBar);
        drawerlayout=findViewById(R.id.drawerlayout);
        navigationView=findViewById(R.id.home_NVG_menu);
        home_RCV_books=findViewById(R.id.home_RCV_books);
        home_LBL_list=findViewById(R.id.home_LBL_list);
        home_LBL_message=findViewById(R.id.home_LBL_message);
        header_LBL_name=findViewById(R.id.header_LBL_name);
        home_BTN_refresh=findViewById(R.id.home_BTN_refresh);
        View headerView = navigationView.getHeaderView(0);
        header_LBL_name =  headerView.findViewById(R.id.header_LBL_name);
    }
    private void setMenu() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_man);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePageActivity.this.drawerlayout.openDrawer(GravityCompat.START);
                header_LBL_name.setText(fUser.getDisplayName()+",");
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.info:
                        Intent intent = new Intent(getApplicationContext(), PersonalInfoActivity.class);
                        someActivityResultLauncher.launch(intent);
                        return true;
                    case R.id.viewBooks:
                        intent=new Intent(getApplicationContext(), ViewListOfBooksActivity.class);
                        intent.putExtra("type","mybooks");
                        startActivity(intent);
                        return true;
                    case R.id.Favorites:

                        intent=new Intent(getApplicationContext(), ViewListOfBooksActivity.class);
                        intent.putExtra("type","favorites");
                        startActivity(intent);
                        return true;
                    case R.id.LogOut:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    default:
                        return true;
                }
            }
        });
    }
    private void setUser() {
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User userTemp = snapshot.getValue(User.class);
                    if(userTemp.getId().equals(fUser.getUid())){
                        user=userTemp;
                        isExist=true;
                        break;
                    }
                }
                if(!isExist){
                    user.setId(fUser.getUid());
                    user.setName(fUser.getDisplayName());
                    user.setEmail(fUser.getEmail());
                    user.setPhoneNum("");
                    user.setCity("");
                    refUsers.child(user.getId()).setValue(user);

                }
                if(!user.notEmpty()){
                    popUpMessege();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ccc", "Failed to read value.", error.toException());
            }
        });
    }
    private void hardCoded() {
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User userTemp = snapshot.getValue(User.class);
                    Book book=new Book("Harry Potter","J.K.Rooling","Fantasy",R.drawable.ic_harrypotter,getString(R.string.harry_potter_bio));
                    book.setUser(userTemp);
                    refBooks.child(book.getName()).setValue(book);
                    book=new Book("The Notebook","Nicholas Sparks","Romance",R.drawable.ic_thenotebook,getString(R.string.the_notebook_bio));
                    book.setUser(userTemp);
                    refBooks.child(book.getName()).setValue(book);
                    book=new Book("Memory Man","David Baldacci","Crime",R.drawable.ic_memoryman,getString(R.string.memory_man_bio));
                    book.setUser(userTemp);
                    refBooks.child(book.getName()).setValue(book);
                    book =new Book("Lord of the Rings","J.R.R.Tolkien","Fantasy",R.drawable.ic_lordoftherings,getString(R.string.lord_of_the_rings_bio));
                    book.setUser(userTemp);
                    refBooks.child(book.getName()).setValue(book);
                    break;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }
    private void readData() {
        listOfBooks=new ArrayList<>();
        // Read once from the database
        refBooks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Book book = snapshot.getValue(Book.class);
                    listOfBooks.add(book);
                }
                setAdapterOfListOfBooks(listOfBooks);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ccc", "Failed to read value.", error.toException());
            }
        });
    }
    private void setClickOnBookInList() {
        home_RCV_books.addOnItemTouchListener(new RecyclerItemClickListener(this, home_RCV_books, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Book book=listOfBooks.get(position);
                        book.setUser(user);
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
    private void setAdapterOfListOfBooks(ArrayList<Book> list) {
        BookAdapter bookAdapter = new BookAdapter(this, list);
        home_RCV_books.setLayoutManager(new GridLayoutManager(this,2));
        home_RCV_books.setHasFixedSize(true);
        home_RCV_books.setAdapter(bookAdapter);
    }
    private void setFilterCheckBok() {
        genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
                // set title
                builder.setTitle("Genre");
                // set dialog non cancelable
                builder.setCancelable(false);
                builder.setMultiChoiceItems(genreList, selectedLanguage, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            chosen.add(i);
                            // Sort array list
                            Collections.sort(chosen);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            chosen.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < chosen.size(); j++) {
                            // concat array value
                            stringBuilder.append(genreList[chosen.get(j)]);
                            // check condition
                            if (j != chosen.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        genre.setText(stringBuilder.toString());

                        refBooks.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                boolean t=false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    Book book = snapshot.getValue(Book.class);
                                    for (int j = 0; j < chosen.size(); j++) {
                                        if(book.getGenre().equalsIgnoreCase(genreList[chosen.get(j)])){
                                            filteredList.add(book);
                                            break;
                                        }
                                    }
                                }
                                setAdapterOfListOfBooks(filteredList);
                                filteredList=new ArrayList<>();
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("ccc", "Failed to read value.", error.toException());
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setAdapterOfListOfBooks(listOfBooks);
                        // use for loop
                        for (int j = 0; j < selectedLanguage.length; j++) {
                            // remove all selection
                            selectedLanguage[j] = false;
                            // clear language list
                            chosen.clear();
                            // clear text view value
                            genre.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });

    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if(data.getIntExtra("message",0)==View.VISIBLE){
                            popUpMessege();

                        }
                    }
                }
            });

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    private void setSearch() {
        home_SRH_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Book> newList=new ArrayList<>();
                if(!filteredList.isEmpty()) {
                    for (Book book : filteredList) {
                        if (book.getName().toLowerCase().contains(newText.toLowerCase())) {
                            newList.add(book);
                        }
                    }
                }else{
                    for (Book book : listOfBooks) {
                        if (book.getName().toLowerCase().contains(newText.toLowerCase())) {
                            newList.add(book);
                        }
                    }
                }
                setAdapterOfListOfBooks(newList);
                return false;
            }
        });
    }
    private void popUpMessege(){
        Dialog dialog=new Dialog((this));
        dialog.setContentView(R.layout.activity_popup_message);
        MaterialButton close=dialog.findViewById(R.id.popup_BTN_close);
        close.setOnClickListener(view->dialog.dismiss());
        dialog.show();

    }



}