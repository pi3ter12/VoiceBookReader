package voicebr.wiacekp.pl.voicebookreader.helper;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import voicebr.wiacekp.pl.voicebookreader.lists.SendParam;

/**
 * Created by Piotrek on 03.05.2016.
 */
public class LoadBook extends AsyncTask<SendParam, Void, Book> {
    @Override
    protected Book doInBackground(SendParam... params) {
        File file = new File(params[0].data.get(0));
        Book book;
        InputStream inStr = null;
        try {
            inStr = new FileInputStream(file);
            book = (new nl.siegmann.epublib.epub.EpubReader()).readEpub(inStr);
            return book;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
