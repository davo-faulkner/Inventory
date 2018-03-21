package co.davo.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.NumberFormat;

import co.davo.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Davo on 9/18/2017.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;
    private static final String INSTANCE_KEY_QUANTITY = "quantity";
    private Uri currentItemUri;
    private EditText nameEditText;
    private EditText priceEditText;
    private Button quantityMinusButton;
    private TextView quantityTextView;
    private int originalQuantity;
    private int quantity;
    private Button quantityPlusButton;
    private TextView placeOrderLabelTextView;
    private EditText orderQuantityEditText;
    private Button orderButton;
    private boolean itemHasChanged = false;
    private String nameString;
    private String priceString;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemHasChanged = true;
            return false;
        }
    };
    private View.OnClickListener orderButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            placeOrder();
        }
    };
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
        placeOrderLabelTextView = (TextView) findViewById(R.id.place_order_label_textview);
        orderQuantityEditText = (EditText) findViewById(R.id.order_quantity_editText);
        orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(orderButtonListener);

        if (currentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
            originalQuantity = 0;
            if (savedInstanceState != null) {
                quantity = savedInstanceState.getInt(INSTANCE_KEY_QUANTITY);
            } else {
                quantity = 0;
            }
            displayQuantity();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
            displayQuantity();
            placeOrderLabelTextView.setVisibility(View.VISIBLE);
            orderQuantityEditText.setVisibility(View.VISIBLE);
            orderButton.setVisibility(View.VISIBLE);
        }

        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityMinusButton.setOnTouchListener(touchListener);
        quantityPlusButton.setOnTouchListener(touchListener);
        orderQuantityEditText.setOnTouchListener(touchListener);
        orderButton.setOnTouchListener(touchListener);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_KEY_QUANTITY, quantity);
        super.onSaveInstanceState(outState);
    }
    private void displayQuantity() {
        String quantityString = String.valueOf(NumberFormat.getIntegerInstance().format(quantity));
        quantityTextView.setText(quantityString);
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
        super.onPrepareOptionsMenu(menu);
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
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
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
        if (currentItemUri == null &&
                TextUtils.isEmpty(nameEditText.getText().toString()) &&
                TextUtils.isEmpty(priceEditText.getText().toString()) &&
                originalQuantity == quantity) {
            Log.d("saveItem: ", "Item completely empty");
            finish();
        } else if (currentItemUri == null &&
                TextUtils.isEmpty(nameEditText.getText().toString()) &&
                !TextUtils.isEmpty(priceEditText.getText().toString())) {
            Log.d("saveItem: ", "Item Name empty");
            Toast.makeText(this, "Item Name is required", Toast.LENGTH_SHORT).show();
            nameEditText.requestFocus();
        } else if (currentItemUri == null &&
                !TextUtils.isEmpty(nameEditText.getText().toString()) &&
                TextUtils.isEmpty(priceEditText.getText().toString())) {
            Log.d("saveItem: ", "Item Price empty");
            Toast.makeText(this, "Item Price is required", Toast.LENGTH_SHORT).show();
            priceEditText.requestFocus();
        } else {
            nameString = nameEditText.getText().toString().trim();
            priceString = priceEditText.getText().toString();
            Float priceFloat = Float.parseFloat(priceString);
            int priceInt = (int) (priceFloat * 100);


            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_ITEM_NAME, nameString);
            values.put(InventoryEntry.COLUMN_ITEM_PRICE, priceInt);
            values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, quantity);

            if (currentItemUri == null) {
                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, "Error with saving item",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(currentItemUri, values, null,
                        null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, "Error with saving item",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
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
            int rowsDeleted = getContentResolver().delete(currentItemUri, null,
                    null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error with deleting item",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    private void placeOrder() {
        int orderQuantity =
                Integer.parseInt(orderQuantityEditText.getEditableText().toString().trim());
        String emailBody = "John,\n" +
                "\n" +
                "Can you please process an order for " + orderQuantity + " " +
                nameEditText.getEditableText().toString().trim() + "s. I believe they are " +
                "currently priced at $" +
                priceEditText.getEditableText().toString().trim() + ".\n" +
                "\n" +
                "Thanks!";
        orderQuantityEditText.setText("");
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "New Order");
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //TODO Davo, fix Dialog for up(?) button 10/12/17
    private void incrementQuantity() {
        quantity++;
        displayQuantity();
    }
    private void decrementQuantity() {
        if (quantity > 0) {
            quantity--;
            displayQuantity();
        }
    }
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
            priceEditText.setText(String.format("%.02f", priceFloat));
            quantity = originalQuantity;
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
