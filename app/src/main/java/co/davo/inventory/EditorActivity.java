package co.davo.inventory;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Davo on 9/18/2017.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_ITEM_LOADER = 0;
    private Uri currentItemUri;
    private EditText nameEditText;
    private EditText priceEditText;
    private Button quantityMinusButton;
    private TextView quantityTextView;
    private Button quantityPlusButton;
    private EditText orderQuantityEditText;
    private Button orderButton;
    private boolean itemHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentItemUri = intent.getData();

        nameEditText = (EditText) findViewById(R.id.item_name_editText);
        priceEditText = (EditText) findViewById(R.id.item_price_editText);
        quantityMinusButton = (Button) findViewById(R.id.quantity_minus_button);
        quantityTextView = (TextView) findViewById(R.id.quantity_textView);
        quantityPlusButton = (Button) findViewById(R.id.quantity_plus_button);
        orderQuantityEditText = (EditText) findViewById(R.id.order_quantity_editText);
        orderButton = (Button) findViewById(R.id.order_button);

        if (currentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityMinusButton.setOnTouchListener(touchListener);
        quantityPlusButton.setOnTouchListener(touchListener);
        orderQuantityEditText.setOnTouchListener(touchListener);
        orderButton.setOnTouchListener(touchListener);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
