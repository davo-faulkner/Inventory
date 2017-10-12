package co.davo.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.davo.inventory.data.InventoryContract;
import co.davo.inventory.data.InventoryContract.InventoryEntry;

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
    private int originalQuantity;
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
        quantityMinusButton.setOnClickListener(minusButtonListener);
        quantityTextView = (TextView) findViewById(R.id.item_quantity_textView);
        quantityPlusButton = (Button) findViewById(R.id.quantity_plus_button);
        quantityPlusButton.setOnClickListener(plusButtonListener);
        orderQuantityEditText = (EditText) findViewById(R.id.order_quantity_editText);
        orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(orderButtonListener);

        if (currentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
            originalQuantity = 0;
            quantity = 0;
            displayQuantity();
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
                saveItem();
                finish();
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

    private void saveItem() {
        String nameString = nameEditText.getText().toString().trim();
        int priceInt = (int) Float.parseFloat(priceEditText.getText().toString()) * 100;

        if (currentItemUri == null && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(priceEditText.getText().toString()) &&
                originalQuantity == quantity) {
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            //TODO Add String resources, Davo
            Toast.makeText(this, "Item name required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (priceInt == 0) {
            Toast.makeText(this, "Item price required", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_NAME, nameString);
        values.put(InventoryEntry.COLUMN_ITEM_PRICE, priceInt);
        values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, quantity);

        if (currentItemUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentItemUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            }
        }
        return;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (currentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error with deleting item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
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

    private void incrementQuantity() {
        quantity++;
        displayQuantity();
    }

    private void decrementQuantity() {
        if (quantity < 0) {
            quantity--;
            displayQuantity();
        }
    }

    private View.OnClickListener plusButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            incrementQuantity();
        }
    };

    private View.OnClickListener minusButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            decrementQuantity();
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryEntry.COLUMN_ITEM_PRICE
        };
        return new CursorLoader(this,
                currentItemUri,
                projection,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);

            String name = cursor.getString(nameColumnIndex);
            originalQuantity = cursor.getInt(quantityColumnIndex);
            int priceInt = cursor.getInt(priceColumnIndex);
            float priceFloatInflated = (float) priceInt;
            float priceFloat = priceFloatInflated / 100;

            nameEditText.setText(name);
            quantityTextView.setText(Integer.toString(originalQuantity));
            priceEditText.setText(Float.toString(priceFloat));
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        quantityTextView.setText("");
        orderQuantityEditText.setText("");
        priceEditText.setText("");
    }
}
