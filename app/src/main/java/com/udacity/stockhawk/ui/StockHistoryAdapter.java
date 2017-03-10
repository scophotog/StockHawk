package com.udacity.stockhawk.ui;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;

class StockHistoryAdapter extends RecyclerView.Adapter<StockHistoryAdapter.StockHistoryViewHolder> {

    private final Context context;
    private Cursor cursor;

    StockHistoryAdapter(Context context) {
        this.context = context;
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public StockHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.stock_item, parent, false);
        return new StockHistoryViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockHistoryViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.stock_history_string.setText(cursor.getString(Contract.Quote.POSITION_HISTORY));
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }

    class StockHistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.stock_history_string)
        TextView stock_history_string;

        StockHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
