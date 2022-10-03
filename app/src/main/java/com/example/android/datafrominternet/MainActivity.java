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
import androidx.loader.content.Loader;

import com.example.android.datafrominternet.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

// TODO (1) implement LoaderManager.LoaderCallbacks<String> on MainActivity
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    // T05b.01 COMPLETED (1) Create a static final key to store the query's URL
    public static final String SEARCH_QUERY_URL_EXTRA = "query";
    // T05b.01 COMPLETED (2) Create a static final key to store the search's raw JSON
    public static final String SEARCH_RESULTS_RAW_JSON = "results";

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
            String rawJsonSearchResults = savedInstanceState.getString(SEARCH_RESULTS_RAW_JSON);

            mUrlDisplayTextView.setText(queryUrl);
            mSearchResultsTextView.setText(rawJsonSearchResults);
        }

    }

    // Create a method called makeGithubSearchQuery
    void makeGithubSearchQuery() {
        // Get Input from Search Box
        String githubQuery = mSearchBoxEditText.getText().toString();
        // Call the buildUrl method of NetworkUtils to create the URL
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        // Show the created Url in the TextView
        mUrlDisplayTextView.setText(githubSearchUrl.toString());
        // Create a new GithubQueryTask and call its execute method, passing in the url to query
        new GithubQueryTask().execute(githubSearchUrl);
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

    // Create a class called GithubQueryTask that extends AsyncTask<URL, Void, String>
    public class GithubQueryTask extends AsyncTask<URL, Void, String>{

        //Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        //Override the doInBackground method to perform the query. Return the results.
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }
        // Override onPostExecute to display the results in the TextView
        @Override
        protected void onPostExecute(String githubSearchResults) {
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                mSearchResultsTextView.setText(githubSearchResults);
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
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
        String rawJsonSearchResult = mSearchResultsTextView.getText().toString();
        // T05b.01 COMPLETED (8) Using the key for the raw JSON search results, put the search results into the outState Bundle
        outState.putString(SEARCH_RESULTS_RAW_JSON, rawJsonSearchResult);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }



}
