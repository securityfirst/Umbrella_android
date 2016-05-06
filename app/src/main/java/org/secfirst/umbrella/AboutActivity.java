package org.secfirst.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class AboutActivity extends BaseActivity {

    private WebView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        content = (WebView) findViewById(R.id.about_content);

        String html;
        Intent i  = getIntent();
        String topic = i.getStringExtra("topic");
        if (topic==null || topic.equals("licences")) {
            setTitle(getString(R.string.licences));
            html = getString(R.string.licences_html)
                ;
        } else {
            setTitle(getString(R.string.thank_you));
            html = getString(R.string.thank_you_html);
        }
        final String finalHtml = html;
        content.postDelayed(new Runnable() {
            @Override
            public void run() {
                content.loadDataWithBaseURL("file:///android_res/drawable/", "<style>body{color:#444444}img{width:100%}h1{color:#33b5e5; font-weight:normal;}h2{color:#9ABE2E; font-weight:normal;}a{color:#33b5e5}.button,.button:link{display:block;text-decoration:none;color:white;border:none;width:100%;text-align:center;border-radius:3px;padding-top:10px;padding-bottom:10px;}.green{background:#9ABE2E}.purple{background:#b83656}.yellow{background:#f3bc2b}</style>" + finalHtml, "text/html", "UTF-8", "UTF-8");
            }
        }, 100);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_about;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
