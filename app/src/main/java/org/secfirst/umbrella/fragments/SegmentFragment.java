package org.secfirst.umbrella.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.secfirst.umbrella.R;

public class SegmentFragment extends Fragment {

    public SegmentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_segment, container, false);
        WebView content = (WebView) rootView.findViewById(R.id.segment_content);
        String html =  getArguments().getString("segment");
        if (html!=null) {
            content.loadDataWithBaseURL("file:///android_res/drawable/", "<style>img{width:100%}h1{color:#33b5e5}h2{color:#9ABE2E}</style>" + html, "text/html; charset=UTF-8", null, null);
        }
        return rootView;
    }


}