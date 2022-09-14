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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.datafrominternet.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Create an EditText variable called mSearchBoxEditText
    EditText mSearchBoxEditText;
    // Create a TextView variable called mUrlDisplayTextView
    TextView mUrlDisplayTextView;
    // Create a TextView variable called mSearchResultsTextView
    TextView mSearchResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use findViewById to get a reference to mSearchBoxEditText
        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);
        // Use findViewById to get a reference to mUrlDisplayTextView
        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        // Use findViewById to get a reference to mSearchResultsTextView
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results);
    }

    // Create a method called makeGithubSearchQuery
    void makeGithubSearchQuery() {
        // Get Input from Search Box
        String githubQuery = mSearchBoxEditText.getText().toString();
        // Call the buildUrl method of NetworkUtils to create the URL
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        // Show the created Url in the TextView
        mUrlDisplayTextView.setText(githubSearchUrl.toString());
        // Call getResponseFromHttpUrl and display the results in mSearchResultsTextView
        String githubSearchResults = null;
        // Surround the call to getResponseFromHttpUrl with a try / catch block to catch an IOException
        try {
            githubSearchResults = NetworkUtils.getResponseFromHttpUrl(githubSearchUrl);
            mSearchResultsTextView.setText(githubSearchResults);
        } catch (IOException e) {
            e.printStackTrace();
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
}
