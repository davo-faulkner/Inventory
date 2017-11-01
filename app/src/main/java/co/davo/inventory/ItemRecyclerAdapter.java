package co.davo.inventory;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Davo on 11/1/2017.
 */

public class ItemRecyclerAdapter extends CursorRecyclerAdapter<ItemRecyclerAdapter.ViewHolder> {

    public ItemRecyclerAdapter(Cursor c) {
        super(c);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {

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
        }
        @Override
        public void onClick(View v) {
            //TODO Davo, Continue here after researching
        }
    }
}
