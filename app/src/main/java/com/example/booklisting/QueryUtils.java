package com.example.booklisting;

import android.net.Uri;
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
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving Book data from Google Book Server;
 */
public final class QueryUtils {

    /**
     * Sample JSON response for a Google Book query
     * */
    private static final String JSON_BOOK_RESPONSE = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     *  Create a new URL object from the given String and returns it
     */
    private static URL createUrl(String queryString){
        URL url = null;

        // Attempt to query the Books API.
        try {
            final String BOOK_BASE_URL =  "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q"; // Parameter for the search string.
            final String MAX_RESULTS = "maxResults"; // Parameter to show max results
            final String PRINT_TYPE = "printType"; // Parameter to filter by print type.
            final String KEY = "key";
            final String ORDER_BY= "orderBy";
            final String PROJECTION="projection";
            // Build up your query URI, limiting results to 10 items and printed books.
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, "40")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .appendQueryParameter(ORDER_BY,"relevance")
                    .appendQueryParameter(PROJECTION,"lite")
                    .build();

            url = new URL(builtURI.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Make an Http Request to the given URL and returns a String as response
     */
    private static String makeHttpConnnection(URL url) throws IOException {
        String jsonResponse="";

        // if url is empty then return early ...
        if(url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream =null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*miliseconds*/);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            }
            else{
                Log.e(LOG_TAG, "makeHttpConnnection: "+ urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "problem in retriving Json Response ",e );
        }finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while(line!=null){
            response.append(line);
            line = reader.readLine();
        }
        return response.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Book> extractBooks(String jsonResponse) {

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject bookObj = new JSONObject(jsonResponse);

            JSONArray items = bookObj.getJSONArray("items");
            for(int i = 0;i<items.length();i++){
                JSONObject itemsObj = items.getJSONObject(i);

                JSONObject volume = itemsObj.getJSONObject("volumeInfo");
                String title = volume.getString("title");

                JSONArray author = volume.getJSONArray("authors");
                StringBuilder authorStr= new StringBuilder();
                for (int j = 0; j < author.length(); j++) {
                    authorStr.append(author.getString(j));
                    if(j < author.length()-1)
                        authorStr.append(", ");
                }
                String authors = authorStr.toString();

                String time = volume.getString("publishedDate");
                StringBuilder tempDate = new StringBuilder();
                for (int j = 0; j < time.length(); j++) {

                    if(time.charAt(j)!='-'){
                        tempDate.append(time.charAt(j));
                    }
                    else break;
                }
                time = tempDate.toString();

                String url = volume.getString("infoLink");
                JSONObject imageLink = volume.getJSONObject("imageLinks");
                String image = imageLink.getString("smallThumbnail");
                books.add(new Book(title,authors,time,url,image));
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return books;
    }
    /**
     * Query the USGS dataset and return an ArrayList of {@link Book} object
     * to represent a list of earthquakes.
     */
    public static List<Book> fetchBookData( String query) {
        // Create URL object
        URL responseUrl = createUrl(query);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        try{
            jsonResponse = makeHttpConnnection(responseUrl);
        }
        catch(IOException e){
            Log.e(LOG_TAG, "Error in making httpRequest ", e);
        }
        // Extract relevant fields from the JSON response and create an {@link Earthquake} object
        List<Book> availableBooks;
        availableBooks = extractBooks(jsonResponse);

        Log.d(LOG_TAG, "fetchBookData: ");
        // Return the {@link Earthquake}
        return availableBooks;
    }
}