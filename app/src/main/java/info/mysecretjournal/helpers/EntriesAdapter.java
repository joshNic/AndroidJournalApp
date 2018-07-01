package info.mysecretjournal.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import info.mysecretjournal.R;
import info.mysecretjournal.model.Entry;

public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.MyViewHolder> {

    private Context context;
    private List<Entry> entriesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView entryTitle;
        public ImageView entryImageView;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            entryTitle = view.findViewById(R.id.entryTitle);
            entryImageView = view.findViewById(R.id.entryImageView);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public EntriesAdapter(Context context, List<Entry> entriesList) {
        this.context = context;
        this.entriesList = entriesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entry_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Entry entry = entriesList.get(position);

        // Displaying entry title
        holder.entryTitle.setText(entry.getEntryTitle());

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(entry.getTimestamp()));

        final String entryTitle = entry.getEntryTitle().toString();
        final String entryBody = entry.getEntryBody().toString();
        
    }

    @Override
    public int getItemCount() {
        return entriesList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21 2018
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}
