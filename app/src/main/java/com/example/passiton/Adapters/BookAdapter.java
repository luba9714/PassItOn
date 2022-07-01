package com.example.passiton.Adapters;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.passiton.Data.Book;
import com.example.passiton.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Activity activity;
    private ArrayList<Book> listOfBooks=new ArrayList<Book>();
    private StorageReference photoRef;
    private FirebaseStorage storage;

    public BookAdapter(Activity activity , ArrayList<Book> listOfBooks) {
        this.activity=activity;
        this.listOfBooks=listOfBooks;
        storage=FirebaseStorage.getInstance();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_books, parent, false);
        BookHolder BookHolder = new BookHolder(view);
        return BookHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final BookHolder holder = (BookHolder) viewHolder;
        Book book = listOfBooks.get(position);
        if(book.getImage()!=0) {
            holder.image.setImageResource(book.getImage());
        }else{
            photoRef = storage.getReference("images/"+book.getImageUri());
            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    Glide.with(activity).load(downloadUrl)
                            .into(holder.image);
                }
            });
        }
        holder.genre.setText(book.getGenre());
        holder.name.setText(book.getName());
        holder.author.setText(book.getAuthor());
    }





    @Override
    public int getItemCount() {
        return listOfBooks.size();
    }



    class BookHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private MaterialTextView name;
        private MaterialTextView author;
        private MaterialTextView genre;

        public BookHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            author=itemView.findViewById(R.id.author);
            genre = itemView.findViewById(R.id.genre);
        }

    }
}
