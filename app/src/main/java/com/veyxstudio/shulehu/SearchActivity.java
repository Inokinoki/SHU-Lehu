package com.veyxstudio.shulehu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.veyxstudio.shulehu.view.BackOnClickListener;

/**
 * Created by Veyx Shaw on 2016/4/3.
 * Search input activity.
 */
public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initComp();
    }

    private void initComp(){
        // Init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_action_arrow_left);
        toolbar.setNavigationOnClickListener(new BackOnClickListener(this));
        // Init those about search
        Button button = (Button)findViewById(R.id.search_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = ((EditText)findViewById(R.id.search_input)).getText().toString();
                if(!search.equals("")){
                    Intent intent  = new Intent(SearchActivity.this,TagArticleListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("key",search);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(SearchActivity.this, R.string.search_no_empty,Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*
            Todo: Store search history in the database and read into the ListView.
         */
    }
}
