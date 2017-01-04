package voicebr.wiacekp.pl.voicebookreader.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import nl.siegmann.epublib.domain.Book;
import voicebr.wiacekp.pl.voicebookreader.lists.BooksList;
import voicebr.wiacekp.pl.voicebookreader.lists.SendParam;

/**
 * Created by Piotrek on 02.05.2016.
 */
public class UpdateBooksInfo extends AsyncTask<SendParam, Integer, Void>{
    @Override
    protected Void doInBackground(SendParam... params) {
        String DEBUG_TAG = "UpdateBooksInfo";

        Helper helper = new Helper(params[0].ctx ,params[0].act);

        int sID, sSize, sProgress, sProcents, sChapters;
        String sTitle, sAuthor;
        sProcents=0;
        sProgress=0;

        JSONArray jsonArray = new JSONArray();

        ArrayList<BooksList> oldBookList = new ArrayList<>();
        try {
            oldBookList = helper.getbooksListFromJson(helper.getJsonInfo());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0; i<params[0].data.size(); i++){
            publishProgress(i);
            File file = new File(params[0].data.get(i));
            JSONObject jsonObject = new JSONObject();

            try {
                Book book;
                InputStream inStr = new FileInputStream(file);
                book = (new nl.siegmann.epublib.epub.EpubReader()).readEpub(inStr);

                sID = i;
                sTitle=book.getTitle();
                sAuthor=book.getMetadata().getAuthors().get(0).getFirstname()+" "+book.getMetadata().getAuthors().get(0).getLastname();
                sChapters = book.getSpine().getSpineReferences().size();
                sSize=0;

                for(int j=0; j<book.getSpine().getSpineReferences().size(); j++){
                    InputStream is = book.getSpine().getSpineReferences().get(j).getResource().getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line=null;
                    while((line = reader.readLine())!=null){
                        if(!line.trim().equals("")){
                            sSize++;
                        }
                    }
                }
                Log.d(DEBUG_TAG, book.getTitle());

                try {
                    Bitmap bmp = BitmapFactory.decodeByteArray(book.getCoverImage().getData(), 0, book.getCoverImage().getData().length);
                    helper.saveImage(bmp, "cover" + i);
                }catch (Exception e){
                    e.printStackTrace();
                }

                int tempOldPostion = searchOldPosition(oldBookList, book);
                if(tempOldPostion!=-1){
                    sProcents = oldBookList.get(tempOldPostion).getProcents();
                    sProgress = oldBookList.get(tempOldPostion).getProgress();
                }
                jsonObject.put("id", sID);
                jsonObject.put("titile", sTitle);
                jsonObject.put("author", sAuthor);
                jsonObject.put("books", file.getAbsolutePath().toString());
                jsonObject.put("size", sSize);
                jsonObject.put("progress", sProgress);
                jsonObject.put("procents", sProcents);
                jsonObject.put("chapters", sChapters);
                jsonArray.put(jsonObject);
                sProcents=0;
                sProgress=0;

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        try {
            helper.saveJsonInfo(jsonArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int searchOldPosition(ArrayList<BooksList> oldBookList, Book book){
        String nTitle=book.getTitle();
        String nAuthor=book.getMetadata().getAuthors().get(0).getFirstname()+" "+book.getMetadata().getAuthors().get(0).getLastname();
        int nChapters = book.getSpine().getSpineReferences().size();

        for(int i=0; i<oldBookList.size(); i++){
            if((nTitle.equals(oldBookList.get(i).getTitle()))&&
                    (nAuthor.equals(oldBookList.get(i).getAuthor()))&&
                    (nChapters==oldBookList.get(i).getChapters())){
                return i;
            }
        }

        return -1;
    }
}
