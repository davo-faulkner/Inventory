package co.davo.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    int itemQuantity;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent, false);
    }
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name_textView);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_textView);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_textView);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);

        String itemName = cursor.getString(nameColumnIndex);
        int itemPriceInt = cursor.getInt(priceColumnIndex);
        float itemPriceFloat = itemPriceInt;
        itemPriceFloat = itemPriceFloat / 100;
        String itemPrice = "$ " + itemPriceFloat;
        itemQuantity = cursor.getInt((quantityColumnIndex));
        String itemQuantityString = "" + itemQuantity;

        nameTextView.setText(itemName);
        priceTextView.setText(itemPrice);
        quantityTextView.setText(itemQuantityString);
        saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("saleButton", "Sale Button clicked!");
                itemQuantity--;

                ContentValues values = new ContentValues();
                values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, itemQuantity);
//TODO Davo, Finish the following commented code
//                int rowsAffected = context.getContentResolver().update(currentItemUri, values,
//                        null,null);
//
//                if (rowsAffected == 0) {
//                    Toast.makeText(this, "Error with saving item",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }
}
