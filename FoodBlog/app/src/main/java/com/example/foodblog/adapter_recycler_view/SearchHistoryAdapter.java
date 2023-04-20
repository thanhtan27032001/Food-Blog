package com.example.foodblog.adapter_recycler_view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodblog.R;
import com.example.foodblog.fragment.SearchFragment;

import java.util.ArrayList;

public class SearchHistoryAdapter extends ArrayAdapter<String> {
    private SearchFragment context;
    private int resLayout;
    private ArrayList<String> histories;
    private ArrayList<String> filteredHistories;

    private Filter myFilter;

    public SearchHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects, SearchFragment fragmentContext) {
        super(context, resource, objects);
        this.resLayout = resource;
        this.histories = objects;
        this.context = fragmentContext;
        this.filteredHistories = new ArrayList<>(histories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(resLayout, null);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (MyViewHolder) convertView.getTag();
        }
        String value = filteredHistories.get(position);
        holder.itemSearchHistory.setOnClickListener(view -> {
//            context.setHistoryValue(value);
        });
        holder.tvHistory.setText(value);
        return convertView;
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return filteredHistories.size();
    }

    @Override
    public String getItem(int position) {
        return filteredHistories.get(position);
    }
    @NonNull
    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new CustomFilter();
        }
        return myFilter;
    }

    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                ArrayList<String> list = new ArrayList<>(histories);
                results.values = list;
                results.count = list.size();
            } else {
                ArrayList<String> newValues = new ArrayList<>();
                for(int i = 0; i < histories.size(); i++) {
                    String item = histories.get(i);
                    if(item.contains(charSequence)) {
                        newValues.add(item);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredHistories = (ArrayList<String>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout itemSearchHistory;
        TextView tvHistory;
        ImageButton btnDelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemSearchHistory = itemView.findViewById(R.id.itemSearchHistory);
            tvHistory = itemView.findViewById(R.id.tvHistory);
            btnDelete = itemView.findViewById(R.id.btnDeleteHistory);
        }
    }
}
