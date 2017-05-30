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
import org.secfirst.umbrella.models.FormValue;
import org.secfirst.umbrella.util.Global;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class FilledOutFormListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FormValue> forms = new ArrayList<>();
    private Context context;
    private Global global;

    static class FilledOutFormViewHolder extends RecyclerView.ViewHolder {
        TextView formTitle;
        TextView formLastModified;
        CardView textHolder;
        FilledOutFormViewHolder(View itemView) {
            super(itemView);
            this.formTitle = (TextView) itemView.findViewById(R.id.form_title);
            this.formLastModified = (TextView) itemView.findViewById(R.id.form_last_modified);
            this.textHolder = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    public FilledOutFormListAdapter(Context context) {
        this.context = context;
        this.global = (Global) context.getApplicationContext();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilledOutFormViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filled_out_form_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        FilledOutFormViewHolder formViewHolder = (FilledOutFormViewHolder) holder;
        final Form current = forms.get(holder.getAdapterPosition()).getFormItem(global).getFormScreen(global).getForm(global);
        Timber.d("form %s", current);
        formViewHolder.formTitle.setText(current.getTitle());
        formViewHolder.formLastModified.setText(forms.get(holder.getAdapterPosition()).getLastModified().toString());
        formViewHolder.textHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FormActivity.class);
                intent.putExtra("form_id", current.get_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public void updateData(List<FormValue> forms) {
        this.forms = forms;
        notifyDataSetChanged();
    }

}
