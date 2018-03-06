package com.example.webdevion.booquet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * An {@link BookAdapter} knows how to create a list item layout for each book
 * in the data source (a list of {@link Book} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Constructs a new {@link BookAdapter}.
     *
     * @param context of the app
     * @param books is the list of book, which is the data source of the adapter
     */
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    /**
     * Returns a list item view that displays information about the book at the given position
     * in the list of books.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        // Find the TextView with view ID book_title
        TextView bookTitleView = (TextView) listItemView.findViewById(R.id.book_title);
        // Display the title of the current book in that TextView
        bookTitleView.setText(currentBook.getTitle());

        // Find the TextView with view ID book_subtitle
        TextView bookSubtitleView = (TextView) listItemView.findViewById(R.id.book_subtitle);
        // Display the subtitle of the current book in that TextView
        bookSubtitleView.setText(currentBook.getSubtitle());

        // Find the TextView with view ID book_authors
        TextView bookAuthorsView = (TextView) listItemView.findViewById(R.id.book_authors);
        // Display the authors of the current book in that TextView
        bookAuthorsView.setText(currentBook.getAuthors());

        // Find the TextView with view ID book_average_rating
        TextView averageRatingView = (TextView) listItemView.findViewById(R.id.book_average_rating);
        // Format the average rating to show 1 decimal place
        String formattedAverageRating = formatAverageRating(currentBook.getAverageRating());
        // Display the average rating of the current book in that TextView
        averageRatingView.setText(formattedAverageRating);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted average rating string showing 1 decimal place (i.e. "4.0")
     * from a decimal average rating value.
     */
    private String formatAverageRating(double averageRating) {
        if (averageRating != -1) {
            DecimalFormat averageRatingFormat = new DecimalFormat("0.0");
            return averageRatingFormat.format(averageRating);
        } else {
            return "No rating available.";
        }
    }
}
