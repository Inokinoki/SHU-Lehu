package com.veyxstudio.shulehu.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.veyxstudio.shulehu.ArticleActivity;
import com.veyxstudio.shulehu.LoginActivity;
import com.veyxstudio.shulehu.MainActivity;
import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.util.AMarkDataBaseHelper;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.URLHelper;

import java.util.ArrayList;

/**
 * Created by Veyx Shaw on 2016/4/5.
 * Display the articles that marked.
 */
public class MarkFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    private static final String LOG_TAG = "MarkFragment";

    private int aids[];
    private ArrayList<String> titleList = new ArrayList<>();

    private Snackbar snackbar;

    public void closeSnackBar(){
        if (snackbar!=null)
            snackbar.dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Read Database
        updateList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark, container, false);
        ListView listView = (ListView)view.findViewById(R.id.fragment_mark_list);
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, titleList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        return view;
    }

    private void updateList(){
        SQLiteDatabase database =
                new AMarkDataBaseHelper(getActivity(), KeyWordHelper.amDatabase, null,1)
                        .getReadableDatabase();
        android.database.Cursor cursor=
                database.rawQuery("select * from " + AMarkDataBaseHelper.tableName, null);
        if(cursor.getCount()!=0) {
            titleList = new ArrayList<>();
            aids = new int[cursor.getCount()];
            int i = 0;
            cursor.moveToFirst();
            do {
                titleList.add(cursor.getString(cursor.getColumnIndex(AMarkDataBaseHelper.tagBColume)));
                aids[i] = cursor.getInt(cursor.getColumnIndex(AMarkDataBaseHelper.tagAColume));
                i++;
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(getActivity(), R.string.database_no_item,Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        database.close();

    }

    private void deleteItemWhereAid(int index){
        SQLiteDatabase database =
                new AMarkDataBaseHelper(getActivity(), KeyWordHelper.amDatabase, null,1)
                        .getWritableDatabase();
        // TODO: delete not finished
//        database.rawQuery("DELETE FROM " + AMarkDataBaseHelper.tableName +
//                " WHERE " + AMarkDataBaseHelper.tagAColume + " = " + aids[index], null);
//        Log.i(LOG_TAG, "DELETE FROM " + AMarkDataBaseHelper.tableName +
//                " WHERE " + AMarkDataBaseHelper.tagAColume + " = " + aids[index]);
        String argv[] = {aids[index]+""};
        database.delete(AMarkDataBaseHelper.tableName, AMarkDataBaseHelper.tagAColume + "=?", argv);
        database.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = URLHelper.baseArticle+"?aid="+aids[position];
        if (((MainActivity)getActivity()).getLoginState()) {
            Log.i(LOG_TAG, "Logined, start ArticleActivity");
            Intent intent = new Intent(getActivity(), ArticleActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("URL",url);
            intent.putExtras(bundle);
            startActivity(intent);
        }else{
            Log.i(LOG_TAG,"Not login, show snackbar");
            openSnackBar();
        }
    }

    private void openSnackBar(){
        if (getView()!=null) {
            snackbar = Snackbar.make(getView(),
                    R.string.snackbar_log, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.action_login, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity mainActivity = (MainActivity) getActivity();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (snackbar!=null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.database_delete_title)
                .setMessage(R.string.database_delete_text)
                .setPositiveButton(R.string.database_delete_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItemWhereAid(position);
                        Toast.makeText(getActivity(), R.string.database_delete_success, Toast.LENGTH_SHORT).show();
                        updateList();
                        if (getView()!=null) {
                            ListView listView = (ListView) getView().findViewById(R.id.fragment_mark_list);
                            ArrayAdapter<String> arrayAdapter =
                                    new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, titleList);
                            listView.setAdapter(arrayAdapter);
                            listView.setOnItemClickListener(MarkFragment.this);
                            listView.setOnItemLongClickListener(MarkFragment.this);
                        }
                    }
                })
                .setNegativeButton(R.string.database_delete_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(LOG_TAG, "Delete cancel");
                    }
                });
        builder.show();
        return false;
    }
}
