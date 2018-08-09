package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.secfirst.umbrella.FormActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.Form;

import java.util.ArrayList;
import java.util.List;

public class FormListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Form> forms = new ArrayList<>();
    private Context context;

    static class FormViewHolder extends RecyclerView.ViewHolder {
        TextView formTitle;
        CardView textHolder;
        FormViewHolder(View itemView) {
            super(itemView);
            this.formTitle = (TextView) itemView.findViewById(R.id.form_title);
            this.textHolder = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    public FormListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FormViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.form_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        FormViewHolder formViewHolder = (FormViewHolder) holder;
        formViewHolder.formTitle.setText(forms.get(holder.getAdapterPosition()).getTitle());
        formViewHolder.textHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FormActivity.class);
                intent.putExtra("form_id", forms.get(holder.getAdapterPosition()).get_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public void updateData(List<Form> forms) {
        this.forms = forms;
        notifyDataSetChanged();
    }

}
