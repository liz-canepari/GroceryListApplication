package com.example.grocerylistapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    private final ArrayList<String> groceryList;

    public GroceryAdapter(ArrayList<String> groceryList) {
        this.groceryList = groceryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = groceryList.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public void addItem(String item) {
        groceryList.add(item);
        notifyItemInserted(groceryList.size() - 1);
    }

    public void updateList(ArrayList<String> newList) {
        groceryList.clear();
        groceryList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

