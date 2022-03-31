package com.example.booklisting;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Book>> {

    private static final int BOOK_LOADER_ID = 1;
    private static final String TAG = "BookActivity";
    private BookAdapter mAdapter;
    private TextView mEmptyState;
    private EditText querySearchTerms;
    private int NightMode;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        NightMode = sharedPreferences.getInt("NightModeInt", 1);
        AppCompatDelegate.setDefaultNightMode(NightMode);

        querySearchTerms = findViewById(R.id.editTextView);

        ListView bookListView = (ListView) findViewById(R.id.listView);
        mEmptyState = findViewById(R.id.emptyState);
        bookListView.setEmptyView(mEmptyState);

        mAdapter = new BookAdapter(getBaseContext(), new ArrayList<>());
        bookListView.setAdapter(mAdapter);

        /*
        * onclickListener for each list Item and redirects them to their respective url
        * */
        bookListView.setOnItemClickListener((adapterView, view, position, l) -> {
            // Find the current earthquake that was clicked on
            Book currentBook = mAdapter.getItem(position);

            // Convert the String URL into a URI object (to pass into the Intent constructor)
            Uri bookUri = Uri.parse(currentBook.getUrl());

            // Create a new intent to view the earthquake URI
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

            // Send the intent to launch a new activity
            startActivity(websiteIntent);
        });

        /*
          onClickListener for handling the touch of
          the search icon in the bottom of the keyboard
          */
        querySearchTerms.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
                return true;
            }
            return false;
        });

    }

    /**
     * function to get input from the editText and call loaderManager Class to make a network request
     * */
    public void search(){
        // Get the search string from the input field.
        String queryString = querySearchTerms.getText().toString();

        ProgressBar showProgress = findViewById(R.id.progress_indicator);
        showProgress.setVisibility(View.VISIBLE);

        // Hide the keyboard when the button is pushed.
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        // Check the status of the network connection.
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            Bundle bundle = new Bundle();
            bundle.putString("Query", queryString);


            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.restartLoader(BOOK_LOADER_ID,bundle,this);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            showProgress.setVisibility(View.GONE);
            // Update empty state with no connection error message

            mEmptyState.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        // Create a new loader for the given URL
        ProgressBar showProgressLF = findViewById(R.id.progress_indicator);
        showProgressLF.setVisibility(View.VISIBLE);
        return new BookLoader(BookActivity.this, bundle.getString("Query"));
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Book>> loader, List<Book> data) {
        // hide the start image
        ImageView initImage = findViewById(R.id.init_image);
        initImage.setVisibility(View.GONE);
        // hide the start message
        TextView initMsg = findViewById(R.id.init_msg);
        initMsg.setVisibility(View.GONE);
        // stop the loading that is still running
        ProgressBar showProgressLF = findViewById(R.id.progress_indicator);
        showProgressLF.setVisibility(View.GONE);

        mEmptyState.setText(R.string.emptyBooklist);
        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    /**
     * method for ImageClickListener for displaying the PopUp Menu for the Dark and Light theme
     * */
    public void settingBtnClick(View view){
        // Create a new PopUp Menu and assigning it the current context and the View in which it is applied
        PopupMenu popup = new PopupMenu(BookActivity.this, view);

        /* MenuInflater is the file that is used to instantiate the items of the
         * menu_option.xml file into the list of Menu objects
         */
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_option, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(item -> {
            final int lightMode = R.id.lightMode;
            final int darkMode = R.id.darkMode;
            final int sysDefault = R.id.SystemTheme;

            switch (item.getItemId()) {
                case lightMode:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    return true;
                case darkMode:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    return true;
                case sysDefault:
                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);


                    return false;
            }
        });
    }
    /**
     * method overridden for saving the current theme and state of the app
     * */

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        NightMode = AppCompatDelegate.getDefaultNightMode();

        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("NightModeInt", NightMode);
        editor.apply();
    }

}
