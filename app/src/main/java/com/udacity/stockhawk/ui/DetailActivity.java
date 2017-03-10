package com.udacity.stockhawk.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STOCK_HISTORY_LOADER = 0;
    @BindView(R.id.chart)
    LineChart mChart;

    @BindView(R.id.stock_symbol)
    TextView mTextView;

    private StockHistoryAdapter mStockHistoryAdapter;
    private String mStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mStockHistoryAdapter = new StockHistoryAdapter(this);
        mStock = getIntent().getStringExtra("stock_name");
        mTextView.setText(mStock);

        getSupportLoaderManager().initLoader(STOCK_HISTORY_LOADER,null,this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.makeUriForStock(mStock),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Entry> entries;
        final ArrayList<String> labels;
        if (data != null && data.moveToFirst()) {
            entries = new ArrayList<>();
            labels = new ArrayList<>();

            String[] historyString = data.getString(Contract.Quote.POSITION_HISTORY).split("\n");
            Timber.i("Total Records: " + historyString.length);

            for (int i = 0; i < historyString.length; i++) {
                String[] str = historyString[i].split(",");
                String dateString = str[0];
                String price = String.format(Locale.US, "%.2f", Float.parseFloat(str[1]));
                labels.add(dateString);
                Timber.i("Stock info: " + dateString + " " + price);
                entries.add(new Entry(i, Float.parseFloat(price)));
            }

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
                    return format.format(Long.parseLong(labels.get((int) value)));
                }
            };

            LineDataSet dataSet = new LineDataSet(entries, mStock);
            LineData lineData = new LineData(dataSet);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(5f);
            xAxis.setValueFormatter(formatter);
            xAxis.setLabelRotationAngle(-45f);

            mChart.getAxisRight().setEnabled(false);
            mChart.getLegend().setEnabled(false);
            mChart.setDrawGridBackground(true);
            mChart.setData(lineData);
            mChart.setTouchEnabled(false);
            mChart.setBackgroundColor(Color.WHITE);
            mChart.setDescription(null);
            mChart.notifyDataSetChanged();
            mChart.setContentDescription(getString(R.string.stock_chart, mStock));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mChart.announceForAccessibility(getString(R.string.stock_chart, mStock));
            }
            mChart.invalidate();

        } else {
            Timber.e("Cursor: Null");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockHistoryAdapter.setCursor(null);
    }

}
