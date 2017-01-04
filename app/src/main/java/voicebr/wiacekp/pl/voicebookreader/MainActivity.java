package voicebr.wiacekp.pl.voicebookreader;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import voicebr.wiacekp.pl.voicebookreader.helper.ExtSdHelper;
import voicebr.wiacekp.pl.voicebookreader.helper.Helper;
import voicebr.wiacekp.pl.voicebookreader.helper.UpdateBooksInfo;
import voicebr.wiacekp.pl.voicebookreader.lists.BooksList;
import voicebr.wiacekp.pl.voicebookreader.lists.FileList;
import voicebr.wiacekp.pl.voicebookreader.lists.SendParam;
import voicebr.wiacekp.pl.voicebookreader.listviewadapters.BooksAdapter;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 122;
    private ListView lv;
    private BooksAdapter booksAdapter;
    private Helper helper;
    private ArrayList<BooksList> booksLists = new ArrayList<>();
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv=(ListView)findViewById(R.id.activity_main_listv);
        helper=new Helper(this, this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startRead(position);
            }
        });

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            setListView();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        setListView();
    }

    private void setListView(){
        try {
            booksLists = helper.getbooksListFromJson(helper.getJsonInfo());
            booksAdapter = new BooksAdapter(this, this, booksLists);
            lv.setAdapter(booksAdapter);
            lv.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void progressMethod(boolean start){
        if(start){
            progress = ProgressDialog.show(this, this.getResources().getString(R.string.refresh_library),
                    this.getResources().getString(R.string.please_wait), true);
        }else{
            progress.dismiss();
        }
    }

    private void startRead(int position){
        Intent intent = new Intent(this, ReadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", booksLists.get(position).getId());
        bundle.putString("title", booksLists.get(position).getTitle());
        bundle.putString("author", booksLists.get(position).getAuthor());
        bundle.putString("books", booksLists.get(position).getBooks().getAbsolutePath());
        bundle.putInt("chapters", booksLists.get(position).getChapters());
        bundle.putInt("size", booksLists.get(position).getSize());
        bundle.putInt("progress", booksLists.get(position).getProgress());
        bundle.putInt("procents", booksLists.get(position).getProcents());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_folder) {
            Intent intentFE = new Intent(this, FileExplorer.class);
            startActivity(intentFE);
            return true;
        }else if(id == R.id.action_refresh){
            updateBooksInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateBooksInfo(){
        Helper helper  = new Helper(this, this);
        progressMethod(true);
        try {
            ExtSdHelper extSdHelper = new ExtSdHelper(this);
            ArrayList<FileList> books =  extSdHelper.searchAllBooks(helper.getBooksLoaction());
            helper.saveBooksList(books);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            new UpdateBooksInfo(){
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    progressMethod(false);
                    setListView();
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);

                }
            }.execute(new SendParam(this, this, helper.getBooksList()));
        } catch (FileNotFoundException e) {
            progressMethod(false);
            e.printStackTrace();
        }
    }

}
