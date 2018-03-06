package com.example.webdevion.booquet;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving books data from the google API.
 */
public final class Utils {

    /** Tag for the log messages */
    public static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name Utils (and an object instance of Utils is not needed).
     */
    private Utils() {
    }

    /**
     * Query the google API dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link List<Book>} object by parsing out information
     * about the first book from the input bookJSON string.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> books = new ArrayList<>();
        // Try to parse the JSON RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Convert JSON RESPONSE String into a JSONObject
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            if (baseJsonResponse.has("items")) {
                // Extract the JSONArray associated with the key called "items",
                // which represents a list of items (or books).
                JSONArray booksArray = baseJsonResponse.getJSONArray("items");

                // For each book in the booksArray, create an {@link Book} object
                for (int i = 0; i < booksArray.length(); i++) {
                    // Get a single book at position i within the list of books
                    JSONObject currentBook = booksArray.getJSONObject(i);
                    // For a given book, extract the JSONObject associated with the
                    // key called "volumeInfo", which represents a list of all properties
                    // for that book.
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                    // Extract the value for the key called "title"
                    String title = volumeInfo.getString("title");
                    // Extract the value for the key called "subtitle"
                    String subtitle = "";
                    if (volumeInfo.has("subtitle")) {
                        subtitle = volumeInfo.getString("subtitle");
                    } else {
                        subtitle = "No subtitle available.";
                    }
                    // Extract the name of the authors from the JSONArray with the
                    // key called "authors"
                    // Here we initialise the authors String to an empty string.
                    String authors = "";
                    // We check to see if the JSONObject has any authors, if it has none we set
                    // the authors String to the "Unknown author".
                    if (volumeInfo.has("authors")) {
                        // Store the array in authorsArray variable.
                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        // Each string in the authors array we store it in the authors String variable.
                        for (int j = 0; j < authorsArray.length(); j++) {
                            // Here we check to see if the string is the last in the array.
                            // Because we want to display the authors one under the other, we
                            // will insert an "\n" character at the end of each name, but we need to check
                            // if the name is the last in the array so we do not add the "\n" character
                            // after the last name.
                            if (j < authorsArray.length()-1) {
                                authors = authors + volumeInfo.getJSONArray("authors").get(j).toString() + "\n";
                            } else {
                                authors = authors + volumeInfo.getJSONArray("authors").get(j).toString();
                            }
                        }
                    } else {
                        // This value is used if there are no authors.
                        authors = "Unknown author";
                    }
                    // Extract the value for the key called "averageRating"
                    double averageRating;
                    if (volumeInfo.has("averageRating")) {
                        averageRating = volumeInfo.getDouble("averageRating");
                    } else {
                        averageRating = -1;
                    }
                    // Extract the value for the key called "infoLink"
                    String url = volumeInfo.getString("infoLink");

                    // Create a new {@link Book} object with the title, subtitle,
                    // and url from the JSON response.
                    Book book = new Book(title, subtitle, authors, averageRating, url);
                    // Add book to list of books
                    books.add(book);
                }

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }
}
