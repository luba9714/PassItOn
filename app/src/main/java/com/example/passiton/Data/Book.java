package com.example.passiton.Data;

public class Book {
    private String name;
    private String author;
    private String genre;
    private int image=0;
    private String imageUri="";
    private String bio;
    private User user;
    private boolean isSelcted=false;

    public Book() {
        user=new User();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Book(String name, String author, String genre, int image, String bio) {
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.image = image;
        this.bio=bio;
        user=new User();
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getImage() {
        return image;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setImage(int image) {
        this.image = image;
    }



    public void setSelected(boolean selcted) {
        isSelcted = selcted;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", image=" + image +
                '}';
    }

    public boolean isSelected() {
        return isSelcted;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


}
