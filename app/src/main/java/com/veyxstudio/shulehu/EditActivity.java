package com.veyxstudio.shulehu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.view.BackOnClickListener;

/**
 * Created by Veyx Shaw on 2016/4/3.
 * Edit Article or Comment.
 */
public class EditActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        toolbar.setNavigationOnClickListener(new BackOnClickListener(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cancel:
                finish();
                break;
            case R.id.action_send:
                String content = ((EditText)findViewById(R.id.edit_text)).getText().toString();
                if (!content.equals("")) {
                    Intent intent = new Intent(this,ArticleActivity.class);
                    intent.putExtra(KeyWordHelper.replyContent, content);
                    setResult(0x126, intent);
                    finish();
                } else {
                    Toast.makeText(this, R.string.edit_no_empty, Toast.LENGTH_SHORT).show();
                }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
}
