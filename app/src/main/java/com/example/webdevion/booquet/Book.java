package com.example.webdevion.booquet;

public class Book {

    // Variable for storing the title of the book.
    private String mTitle;

    // Variable for storing the subtitle of the book.
    private String mSubtitle;

    // Variable for storing the authors of the book.
    private String mAuthors;

    // Variable for storing the average rating of the book.
    private double mAverageRating;

    /** Website URL of the book */
    private String mUrl;

    /**
     * Constructs a new {@link Book} object.
     *
     * @param title is the title of the book
     * @param subtitle is the subtitle of the book
     * @param authors are the authors of the book
     * @param averageRating is the average rating of the book
     * @param url is the website URL to find more details about the book
     */
    public Book(String title, String subtitle, String authors, double averageRating, String url) {
        mTitle = title;
        mSubtitle = subtitle;
        mAuthors = authors;
        mAverageRating = averageRating;
        mUrl = url;
    }

    // Get the title of the book.
    public String getTitle(){
        return mTitle;
    }

    // Get the subtitle of the book.
    public String getSubtitle(){
        return mSubtitle;
    }

    // Get the authors of the book.
    public String getAuthors(){
        return mAuthors;
    }

    // Get the subtitle of the book.
    public double getAverageRating(){
        return mAverageRating;
    }

    // Returns the website URL to find more information about the book.
    public String getUrl() {
        return mUrl;
    }

}