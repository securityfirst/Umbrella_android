package org.secfirst.umbrella.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.stmt.DeleteBuilder;

import org.secfirst.umbrella.FormActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.fragments.TabbedFormsFragment;
import org.secfirst.umbrella.models.Form;
import org.secfirst.umbrella.models.FormValue;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class FilledOutFormListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FormValue> forms = new ArrayList<>();
    private Context context;
    private OnSessionHTMLCreate sessionHTMLCreate;
    private FilledOutFormListen filledOutFormListen;

    static class FilledOutFormViewHolder extends RecyclerView.ViewHolder {
        TextView formTitle;
        TextView formLastModified;
        LinearLayout textHolder;
        ImageButton btnShare;
        ImageButton btnEdit;
        ImageButton btnDelete;

        FilledOutFormViewHolder(View itemView) {
            super(itemView);
            this.formTitle = itemView.findViewById(R.id.form_title);
            this.formLastModified = itemView.findViewById(R.id.form_last_modified);
            this.textHolder = itemView.findViewById(R.id.text_holder);
            this.btnShare = itemView.findViewById(R.id.btn_share);
            this.btnEdit = itemView.findViewById(R.id.btn_edit);
            this.btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public FilledOutFormListAdapter(Context context, TabbedFormsFragment fragment) {
        this.context = context;
        this.sessionHTMLCreate = fragment;
        this.filledOutFormListen = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilledOutFormViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filled_out_form_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        FilledOutFormViewHolder formViewHolder = (FilledOutFormViewHolder) holder;
        final FormValue currentValue = forms.get(holder.getAdapterPosition());
        final Form current = forms.get(holder.getAdapterPosition()).getFormItem().getFormScreen().getForm();
        formViewHolder.formTitle.setText(current.getTitle());
        formViewHolder.formLastModified.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(currentValue.getLastModified()));
        formViewHolder.btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.select_action));
            builder.setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            builder.setPositiveButton(context.getString(R.string.delete), (dialog, which) -> {
                try {
                    DeleteBuilder<FormValue, String> deleteBuilder = Global.INSTANCE.getDaoFormValue().deleteBuilder();
                    deleteBuilder.where().eq(FormValue.FIELD_SESSION, currentValue.getSessionID());
                    deleteBuilder.delete();
                    forms.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                    if (forms.size() == 0) filledOutFormListen.onDeleteFilledOutForm();
                } catch (SQLException e) {
                    Timber.e(e);
                }
            });
            builder.show();
        });
        formViewHolder.btnShare.setOnClickListener(v -> sessionHTMLCreate.onSessionHTMLCreated(currentValue.getSessionID()));

        formViewHolder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, FormActivity.class);
            intent.putExtra("form_id", current.get_id());
            intent.putExtra("session_id", forms.get(holder.getAdapterPosition()).getSessionID());
            context.startActivity(intent);
        });
        formViewHolder.textHolder.setOnClickListener(v -> {
            Intent intent = new Intent(context, FormActivity.class);
            intent.putExtra("form_id", current.get_id());
            intent.putExtra("session_id", forms.get(holder.getAdapterPosition()).getSessionID());
            context.startActivity(intent);
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

    public interface OnSessionHTMLCreate {
        void onSessionHTMLCreated(long sessionId);
    }

    public interface FilledOutFormListen {
        void onDeleteFilledOutForm();
    }
}
