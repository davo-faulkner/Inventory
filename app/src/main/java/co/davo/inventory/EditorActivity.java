package co.davo.inventory;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private int quantity;
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
        orderButton.setOnClickListener(orderButtonListener);

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
    public void onBackPressed() {
        if (!itemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.unsaved_changes_alert_message);
        alertBuilder.setPositiveButton(R.string.unsaved_changes_positive_button,
                discardButtonClickListener);
        alertBuilder.setNegativeButton(R.string.unsaved_changes_negative_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentItemUri == null) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            deleteMenuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (saveItem()) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.home:
                if (!itemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardClickListener =
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void placeOrder() {
        quantity = quantity +
                Integer.parseInt(orderQuantityEditText.getEditableText().toString().trim());
        displayQuantity();
        Toast.makeText(this, "Order placed. Save changes to receive items into inventory.",
                Toast.LENGTH_LONG).show();
    }

    private void displayQuantity() {
        quantityTextView.setText(String.valueOf(quantity));
    }

    private View.OnClickListener orderButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            placeOrder();
        }
    };

    //TODO Continue here, Davo

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
