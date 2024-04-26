package com.zin.dvac;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder> {

    private final List<Password> passwordList;
    private final OnItemClickListener onItemClickListener;

    public PasswordAdapter(List<Password> passwordList, OnItemClickListener onItemClickListener) {
        this.passwordList = passwordList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_password, parent, false);
        return new PasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordViewHolder holder, int position) {
        Password password = passwordList.get(position);
        holder.bind(password, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return passwordList.size();
    }

    static class PasswordViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtUsername;
        private final Button btnDelete;

        PasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Password password, OnItemClickListener onItemClickListener) {
            txtUsername.setText(password.getUsername());
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onDeleteClick(password.getId());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(password.getId());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(long passwordId);

        void onDeleteClick(long passwordId);
    }
}
