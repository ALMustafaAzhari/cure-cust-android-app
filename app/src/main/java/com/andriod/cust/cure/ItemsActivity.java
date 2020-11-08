package com.andriod.cust.cure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import com.andriod.cust.cure.Adapter.ItemsAdapter;
import com.andriod.cust.cure.bean.Item;
import com.andriod.cust.cure.service.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity {

    DatabaseService databaseService ;
    List<Item> items ;
    ItemsAdapter mAdapter;
    RecyclerView itemRecyclerView;

    public static final String EXTRA_ITEM_ID = "com.andriod.cust.cure.ITEM_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        itemRecyclerView = findViewById(R.id.items_recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(mLayoutManager);
        itemRecyclerView.setItemAnimator(new DefaultItemAnimator());

        databaseService = new DatabaseService(this);
        items = databaseService.findAllItems();
        mAdapter = new ItemsAdapter(items , this);
        itemRecyclerView.setAdapter(mAdapter);
        handleIntent(getIntent());
        getSupportActionBar().setSubtitle(getString(R.string.items_activity_subtitle , items.size()));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =  (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    mAdapter.notifyDataSetChanged();
                }

                else {
                    mAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mAdapter.getFilter().filter(query);
        }
    }
}
