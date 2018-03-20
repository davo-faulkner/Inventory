package co.davo.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import co.davo.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Davo on 11/5/2017.
 */

public class ItemCursorAdapter extends CursorAdapter {
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent, false);
    }
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final int[] itemQuantity = new int[1];

        TextView nameTextView = (TextView) view.findViewById(R.id.name_textView);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_textView);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_textView);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);

        final int itemId = cursor.getInt(idColumnIndex);
        String itemName = cursor.getString(nameColumnIndex);
        int itemPriceInt = cursor.getInt(priceColumnIndex);
        float itemPriceFloat = itemPriceInt;
        itemPriceFloat = itemPriceFloat / 100;
        itemQuantity[0] = cursor.getInt((quantityColumnIndex));
        String itemQuantityString = "" + itemQuantity[0];

        nameTextView.setText(itemName);
        priceTextView.setText("$" + String.format("%.02f", itemPriceFloat));
        quantityTextView.setText(itemQuantityString);
        saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("saleButton", "Sale Button clicked!");
                if (itemQuantity[0] > 0) {
                    itemQuantity[0]--;

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, itemQuantity[0]);

                    Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI,
                            itemId);
                    context.getContentResolver().update(currentItemUri, values,
                            null, null);
                    if (itemQuantity[0] == 0) {
                        showSaleButtonZeroQuantityToast();
                    }
                } else {
                    showSaleButtonZeroQuantityToast();
                }
            }
            private void showSaleButtonZeroQuantityToast() {
                Toast.makeText(context, "Tap Item to Change Quantity & Order More",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
