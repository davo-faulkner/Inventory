package co.davo.inventory;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
        return null;
    }
    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

    }
    @Override
    public int getItemCount() {
        return 0;
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
            int itemPosition = items.getPosition();
        }
    }

}
