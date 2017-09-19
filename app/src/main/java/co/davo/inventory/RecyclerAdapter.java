package co.davo.inventory;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import co.davo.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Davo on 9/18/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemHolder> {
    private static Cursor items;

    public RecyclerAdapter(Cursor items) {
        this.items = items;
    }
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_layout, parent, false);
        return new ItemHolder(inflatedView);
    }
    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        items.moveToPosition(position);

        int nameColumnIndex = items.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = items.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = items.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);

        holder.nameTextView.setText(items.getString(nameColumnIndex));
        holder.priceTextView.setText(items.getString(priceColumnIndex));
        holder.quantityTextView.setText(items.getString(quantityColumnIndex));
    }
    @Override
    public int getItemCount() {
        return items.getCount();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView;
        private TextView priceTextView;
        private TextView quantityTextView;
        private Button saleButton;

        public ItemHolder(View itemView) {
            super(itemView);

            this.nameTextView = (TextView) itemView.findViewById(R.id.name_textView);
            this.priceTextView = (TextView) itemView. findViewById(R.id.price_textView);
            this.quantityTextView = (TextView) itemView.findViewById(R.id.quantity_textView);
            this.saleButton = (Button) itemView.findViewById(R.id.sale_button);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            Log.d("RecyclerView", "CLICK!");
            Context context = view.getContext();
            int itemPosition = getAdapterPosition();
            Intent intent = new Intent(context, EditorActivity.class);
            Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI,
                    itemPosition);
            intent.setData(currentItemUri);
            context.startActivity(intent);
        }
    }

}
