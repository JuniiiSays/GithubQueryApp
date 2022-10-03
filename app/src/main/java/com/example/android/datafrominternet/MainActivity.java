/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.datafrominternet;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.example.android.datafrominternet.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

// T05b.02 COMPLETED (1) implement LoaderManager.LoaderCallbacks<String> on MainActivity
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    // T05b.01 COMPLETED (1) Create a static final key to store the query's URL
    public static final String SEARCH_QUERY_URL_EXTRA = "query";
    // T05b.01 COMPLETED (2) Create a static final key to store the search's raw JSON
    public static final String SEARCH_RESULTS_RAW_JSON = "results";

    // T05b.02 COMPLETED (2) Create a constant int to uniquely identify your loader. Call it GITHUB_SEARCH_LOADER
    public static final int GITHUB_SEARCH_LOADER = 22;

    // Create an EditText variable called mSearchBoxEditText
    private EditText mSearchBoxEditText;
    // Create a TextView variable called mUrlDisplayTextView
    private TextView mUrlDisplayTextView;
    // Create a TextView variable called mSearchResultsTextView
    private TextView mSearchResultsTextView;
    // Create a variable to store a reference to the error message TextView
    private TextView mErrorMessageDisplay;
    // Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use findViewById to get a reference to mSearchBoxEditText
        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);
        // Use findViewById to get a reference to mUrlDisplayTextView
        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        // Use findViewById to get a reference to mSearchResultsTextView
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results_json);
        //
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        //
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // T05b.01 COMPLETED (9) If the savedInstanceState bundle is not null, set the text of the URL and search results TextView respectively
        if (savedInstanceState != null){
            String queryUrl = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);

        }

        getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER, null, this);

    }

    // Create a method called makeGithubSearchQuery
    void makeGithubSearchQuery() {
        // Get Input from Search Box
        String githubQuery = mSearchBoxEditText.getText().toString();
        // Call the buildUrl method of NetworkUtils to create the URL

        if (TextUtils.isEmpty(githubQuery)) {
            mUrlDisplayTextView.setText("No query entered, nothing to search for.");
            return;
        }

        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        // Show the created Url in the TextView
        mUrlDisplayTextView.setText(githubSearchUrl.toString());
        // Create a new GithubQueryTask and call its execute method, passing in the url to query
        // T05b.02 COMPLETED (18) Remove the call to execute the AsyncTask

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, githubSearchUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> githubSearchLoader = loaderManager.getLoader(GITHUB_SEARCH_LOADER);

        if (githubSearchLoader == null){
            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        }
    }

    // Create a method called showJsonDataView to show the data and hide the error
    private void showJsonDataView(){
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        // First, hide the currently visible data
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }



    // Override onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // use getMenuInflater().inflate to inflate the menu
        getMenuInflater().inflate(R.menu.main, menu);
        // Return true to display your menu
        return true;
    }

    // Override onOptionsItemSelected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Get the ID of the item that was selected
        int menuItemThatWasSelected = item.getItemId();
        // If the item's ID is R.id.action_search, show a Toast and return true to tell Android that you've handled this menu click
        if (menuItemThatWasSelected == R.id.action_search){
            makeGithubSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // T05b.01 COMPLETED (3) Override onSaveInstanceState to persist data across Activity recreation
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Do the following steps within onSaveInstanceState
        // T05b.01 COMPLETED (4) Make sure super.onSaveInstanceState is called before doing anything else
        super.onSaveInstanceState(outState);

        // T05b.01 COMPLETED (5) Put the contents of the TextView that contains our URL into a variable
        String queryUrl = mUrlDisplayTextView.getText().toString();
        // T05b.01 COMPLETED (6) Using the key for the query URL, put the string in the outState Bundle
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl);
        // T05b.01 COMPLETED (7) Put the contents of the TextView that contains our raw JSON search results into a variable

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null){
                    return;
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);
                // COMPLETED (8) Force a load
                forceLoad();
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                if (TextUtils.isEmpty(searchQueryUrlString)){
                    return null;
                }
                try {
                    URL githubUrl = new URL(searchQueryUrlString);
                    return NetworkUtils.getResponseFromHttpUrl(githubUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

        // As soon as the loading is complete, hide the loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null && !data.equals("")) {
            // Call showJsonDataView if we have valid, non-null results
            showJsonDataView();
            mSearchResultsTextView.setText(data);
        } else {
            // Call showErrorMessage if the result is null in onPostExecute
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }



}
