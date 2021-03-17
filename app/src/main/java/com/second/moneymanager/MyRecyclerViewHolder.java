package com.second.moneymanager;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
    EditText etPrice, etNotesExpense, etCategoryExpense;
    Button btnAdd, btnCancel, btnDate;
    public MyRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        etCategoryExpense = (EditText) itemView.findViewById(R.id.etCategoryExpense);
        etNotesExpense = (EditText) itemView.findViewById(R.id.etNotesExpense);
        etPrice = (EditText) itemView.findViewById(R.id.etPrice);
    }
}
