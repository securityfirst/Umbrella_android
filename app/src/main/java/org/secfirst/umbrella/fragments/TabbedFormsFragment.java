package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canelmas.let.AskPermission;
import com.canelmas.let.DeniedPermission;
import com.canelmas.let.RuntimePermissionListener;
import com.canelmas.let.RuntimePermissionRequest;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.j256.ormlite.stmt.PreparedQuery;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.FilledOutFormListAdapter;
import org.secfirst.umbrella.adapters.FormListAdapter;
import org.secfirst.umbrella.models.Form;
import org.secfirst.umbrella.models.FormItem;
import org.secfirst.umbrella.models.FormScreen;
import org.secfirst.umbrella.models.FormValue;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TabbedFormsFragment extends Fragment implements RuntimePermissionListener, SwipeRefreshLayout.OnRefreshListener, FilledOutFormListAdapter.OnSessionPdfCreate {

    private FormListAdapter formListAdapter;
    private FilledOutFormListAdapter filledOutAdapter;
    Global global;
    List<Form> allForms = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefresh;
    LinearLayout filledOutHolder;
    private ProgressDialog progressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        global = (Global) context.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_forms,
                container, false);

        filledOutHolder = (LinearLayout) rootView.findViewById(R.id.filled_out_holder);

        RecyclerView formListView = (RecyclerView) rootView.findViewById(R.id.form_list);
        RecyclerView filledOutList = (RecyclerView) rootView.findViewById(R.id.filled_out_list);

        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setOnRefreshListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        formListView.setLayoutManager(llm);

        formListAdapter = new FormListAdapter(getContext());

        formListView.setAdapter(formListAdapter);

        LinearLayoutManager llm1 = new LinearLayoutManager(getContext());
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        formListView.setLayoutManager(llm1);

        filledOutAdapter = new FilledOutFormListAdapter(getContext(), this);
        filledOutList.setAdapter(filledOutAdapter);
        LinearLayoutManager llm2 = new LinearLayoutManager(getContext());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        filledOutList.setLayoutManager(llm2);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefreshed();
    }

    @Override
    public void onStop() {
        if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
        super.onStop();
    }

    public void onRefreshed() {
        try {
            allForms = global.getDaoForm().queryForAll();
            PreparedQuery<FormValue> queryBuilder = global.getDaoFormValue().queryBuilder().groupBy(FormValue.FIELD_SESSION).prepare();
            List<FormValue> fValues = global.getDaoFormValue().query(queryBuilder);
            filledOutHolder.setVisibility((fValues==null || fValues.size()<1) ? View.GONE : View.VISIBLE);
            formListAdapter.updateData(allForms);
            filledOutAdapter.updateData(fValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    @AskPermission(WRITE_EXTERNAL_STORAGE)
    public void onRefresh() {
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onShowPermissionRationale(List<String> permissionList, final RuntimePermissionRequest permissionRequest) {
        new MaterialDialog.Builder(getContext())
                .title("Write")
                .content("Blah")
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        permissionRequest.retry();
                    }
                })
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onPermissionDenied(List<DeniedPermission> deniedPermissionList) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSessionsPdfCreated(long sessionId) {
        progressDialog = UmbrellaUtil.launchRingDialogWithText((Activity) getContext(), "");
        List<FormValue> formValues = null;
        Form form = null;
        try {
            PreparedQuery<FormValue> query = global.getDaoFormValue().queryBuilder().where().eq(FormValue.FIELD_SESSION, sessionId).prepare();
            formValues = global.getDaoFormValue().query(query);
            if (formValues!=null && formValues.size()>0) {
                int formId = formValues.get(0).getFormItem(global).getFormScreen(global).getForm(null).get_id();
                form = global.getDaoForm().queryForId(String.valueOf(formId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (form!=null && formValues.size()>0) {
            String fileName = form.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
            if (fileName.length() > 50) fileName = fileName.substring(0, 50);
            Document document = new Document();
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName+".pdf");
                FileOutputStream fos = new FileOutputStream(file);
                PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
                document.open();
                for (FormScreen screen : form.getScreens()) {
                    document.add(new Paragraph(screen.getTitle()));
                    for (FormItem formItem : screen.getItems()) {
                        document.add(new Paragraph(formItem.getTitle()));

                        switch (formItem.getType()) {
                            case "text_input":
//                                PdfAnnotation shape2 = PdfAnnotation.createLine(pdfWriter, new Rectangle(200f, 250f, 300f, 350f), "this is a line", 200, 250, 300, 350);
//                                PdfFormField f2 = PdfFormField.createPushButton(pdfWriter);
//                                pdfWriter.add(f2);
//                                document.
//                                pdfWriter.addAnnotation(shape2);
//                                PdfFormField field = PdfFormField.createTextField(pdfWriter, false, false, 1000);
//                                PdfAcroForm pForm = new PdfAcroForm(pdfWriter);
//                                pForm.addFormField(field);
                            case "text_area":
                            case "multiple_choice":
                            case "single_choice":
                            case "toggle_button":
                        }
                    }
                }
                document.close();

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                if(file.exists()) {
                    intentShareFile.setType("application/pdf");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            getString(R.string.share_file));
                    startActivity(Intent.createChooser(intentShareFile, getString(R.string.share_file)));
//                    startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(file), "application/pdf"));
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                }
                file.delete();
            } catch (DocumentException | FileNotFoundException e) {
                Timber.e(e);
            }
        }
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }
}
