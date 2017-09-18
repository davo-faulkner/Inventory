package co.davo.inventory;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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

        public ItemHolder(View itemView) {
            super(itemView);
        }
        @Override
        public void onClick(View view) {

        }
    }

}
