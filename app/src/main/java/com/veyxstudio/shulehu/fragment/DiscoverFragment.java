package com.veyxstudio.shulehu.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.veyxstudio.shulehu.DiscoverActivity;
import com.veyxstudio.shulehu.LoginActivity;
import com.veyxstudio.shulehu.MainActivity;
import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.URLHelper;

/**
 * Created by Veyx Shaw on 16-1-9.
 * Handle the action of discover activity.
 */
public class DiscoverFragment extends Fragment implements AdapterView.OnItemClickListener{

    private final String LOG_TAG = "DiscoverFragment";

    private String[] urlList;
    private Snackbar snackbar;

    public void closeSnackBar(){
        if (snackbar!=null)
            snackbar.dismiss();
    }

    public View onCreateView(LayoutInflater layoutInflater ,
                             ViewGroup container ,
                             Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_discover,
                container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        // Init urls from resource file
        urlList = getResources()
                .getStringArray(R.array.list_url_discover);
        // Init titles from resource file
        String[] titles = getResources()
                .getStringArray(R.array.list_title_discover);
        // Find the discover list view
        ListView list = (ListView)view.findViewById(R.id.discover_list);

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,titles);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent;
        Bundle bundle;
        switch(position) {
            case 0:	// Jump to the DiscoverActivity
                if (((MainActivity)getActivity()).getLoginState()) {
                    Log.i(LOG_TAG,"Logined, start DiscoverActivity");
                    intent = new Intent(getActivity()
                            ,DiscoverActivity.class);
                    bundle = new Bundle();
                    bundle.putString("URL",
                            URLHelper.cardTrainingDetail);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Log.i(LOG_TAG,"Not login, show snackbar");
                    openSnackBar();
                }
                break;
            case 1:
                if (((MainActivity)getActivity()).getLoginState()) {
                    Log.i(LOG_TAG,"Logined, start DiscoverActivity");
                    intent = new Intent(getActivity()
                            , DiscoverActivity.class);
                    bundle = new Bundle();
                    bundle.putString("URL",
                            URLHelper.cardTradeDetail);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Log.i(LOG_TAG,"Not login, show snackbar");
                    openSnackBar();
                }
                break;
            case 2:case 3:case 4: // Jump to the browser
                intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(urlList[position]);
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
    }

    private void openSnackBar(){

        snackbar = Snackbar.make(getView(),
                R.string.snackbar_log, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.action_login, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity)getActivity();
                snackbar.setText(R.string.snackbar_logging);
                // Start login activity.
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                startActivity(intent);
                snackbar.dismiss();
                snackbar = null;
            }
        });
        snackbar.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (snackbar!=null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }
}
