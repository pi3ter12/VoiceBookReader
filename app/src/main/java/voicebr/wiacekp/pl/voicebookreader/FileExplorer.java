package voicebr.wiacekp.pl.voicebookreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import voicebr.wiacekp.pl.voicebookreader.helper.ExtSdHelper;
import voicebr.wiacekp.pl.voicebookreader.helper.Helper;
import voicebr.wiacekp.pl.voicebookreader.lists.FileList;
import voicebr.wiacekp.pl.voicebookreader.listviewadapters.FileListAdapter;

public class FileExplorer extends AppCompatActivity {
    private ListView listView;
    private FileListAdapter fileListAdapter;
    private ExtSdHelper extSdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        listView = (ListView)findViewById(R.id.activity_file_explorer_listview);
        extSdHelper = new ExtSdHelper(this);

        Helper helper = new Helper(this, this);
        try {
            File file = new File(helper.getBooksLoaction());
            extSdHelper.setLastPath(file);
            extSdHelper.updateFiles();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        fileListAdapter = new FileListAdapter(this, extSdHelper.getFilesList());
        listView.setAdapter(fileListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateListV(position);

            }
        });
    }

    private void updateListV(int position){
        extSdHelper.createDirs(position);
        fileListAdapter = new FileListAdapter(this, extSdHelper.getFilesList());
        listView.setAdapter(fileListAdapter);
        listView.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fileexplorer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            Helper helper = new Helper(this, this);

            try {
                helper.saveBooksLoaction(extSdHelper.getLastPath().getAbsolutePath());
                updateBooksStorage();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }else if (id== R.id.action_fileback){
            updateListV(-2);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateBooksStorage(){
            try {
                Helper helper  = new Helper(this, this);
                ArrayList<FileList> books =  extSdHelper.searchAllBooks(helper.getBooksLoaction());
                helper.saveBooksList(books);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    }
}
