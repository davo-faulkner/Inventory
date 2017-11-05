package co.davo.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import co.davo.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Davo on 11/5/2017.
 */

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent, false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name_textView);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_textView);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_textView);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        
    }
}
