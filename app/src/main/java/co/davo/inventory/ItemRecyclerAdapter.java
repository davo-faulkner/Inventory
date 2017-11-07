package co.davo.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import co.davo.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Davo on 11/1/2017.
 */

public class ItemRecyclerAdapter extends CursorRecyclerAdapter<ItemRecyclerAdapter.ViewHolder> {
    long id;
    Cursor cursor;

    public ItemRecyclerAdapter(Cursor c) {
        super(c);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(InventoryEntry._ID));
        this.cursor = cursor;
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        final int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);

        String nameString = cursor.getString(nameColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        int itemPriceInt = cursor.getInt(priceColumnIndex);
        float itemPriceFloat = itemPriceInt;
        itemPriceFloat = itemPriceFloat / 100;
        String itemPrice = String.format("%.02f", itemPriceFloat);

        holder.nameTextView.setText(nameString);
        holder.priceTextView.setText("$" + itemPrice);
        holder.quantityTextView.setText("" + quantity);
        holder.saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cursor.moveToPosition(holder.getAdapterPosition());
                long itemId = cursor.getLong(cursor.getColumnIndex(InventoryEntry._ID));
                int itemQuantity = cursor.getInt(quantityColumnIndex);
                if (itemQuantity > 0) {
                    Uri currentItemUri =
                            ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, itemId);
                    ContentValues values = new ContentValues();
                    itemQuantity--;
                    values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, itemQuantity);
                    v.getContext().getContentResolver().update(
                            currentItemUri,
                            values,
                            null,
                            null);
                } else {
                    Toast.makeText(v.getContext(), "You are out of this item.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_layout, parent, false);
        return new ViewHolder(inflatedView);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView nameTextView;
        private TextView priceTextView;
        private TextView quantityTextView;
        private Button saleButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.nameTextView = (TextView) itemView.findViewById(R.id.name_textView);
            this.priceTextView = (TextView) itemView. findViewById(R.id.price_textView);
            this.quantityTextView = (TextView) itemView.findViewById(R.id.quantity_textView);
            this.saleButton = (Button) itemView.findViewById(R.id.sale_button);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            int itemPosition = getAdapterPosition();
            cursor.moveToPosition(itemPosition);
            id = cursor.getLong(cursor.getColumnIndex(InventoryEntry._ID));
            Intent intent = new Intent(context, EditorActivity.class);
            Uri currentItemUri =
                    ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
            intent.setData(currentItemUri);
            context.startActivity(intent);
        }
    }
}
