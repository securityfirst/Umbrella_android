package org.secfirst.umbrella.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.secfirst.umbrella.BuildConfig;
import org.secfirst.umbrella.R;

public class SegmentFragment extends Fragment {

    public SegmentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_segment, container, false);
        TextView content = (TextView) rootView.findViewById(R.id.segment_content);
        String html =  getArguments().getString("segment");
        if (html!=null) {
            html = html.replaceAll("\\<h1\\>", "<p><font color=\"#33b5e5\"><big><big>");
            html = html.replaceAll("\\</h1\\>", "</big></big></font></p>");
            html = html.replaceAll("\\<h2\\>", "<p><font color=\"#9ABE2E\"><big>");
            html = html.replaceAll("\\</h2\\>", "</big></font></p>");
            content.setText(Html.fromHtml(html, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    Drawable d = getActivity().getResources().getDrawable(getActivity().getResources().getIdentifier(source, "drawable", BuildConfig.APPLICATION_ID));
                    d.setBounds(0, 0, getActivity().getWindowManager().getDefaultDisplay().getWidth(), d.getIntrinsicHeight() * getActivity().getWindowManager().getDefaultDisplay().getWidth() / d.getIntrinsicWidth());
                    return d;
                }
            }, null));
        }
        return rootView;
    }


}