package co.davo.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import co.davo.inventory.data.InventoryContract.InventoryEntry;

import static android.R.attr.data;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = CatalogActivity.class.getName();
    private static final int ITEM_LOADER = 0;

    private Cursor items;

    private RecyclerView recyclerView;
    private RecyclerAdapter itemRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        emptyStateTextView.setText(R.string.no_items_found);
        emptyStateTextView.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_QUANTITY
        };
        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            int dataCount = data.getCount();
            recyclerView.setVisibility(View.VISIBLE);
            this.items = data;
            int itemsCount = items.getCount();
            itemRecyclerAdapter = new RecyclerAdapter(items);
        } else {
            emptyStateTextView.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (items.getCount() > 0 && items != null) {
            recyclerView.setVisibility(View.VISIBLE);
            itemRecyclerAdapter = new RecyclerAdapter(items);
        } else {
            emptyStateTextView.setVisibility(View.VISIBLE);
        }
    }
}
