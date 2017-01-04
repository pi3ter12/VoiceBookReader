package voicebr.wiacekp.pl.voicebookreader.helper;

import android.os.AsyncTask;

import voicebr.wiacekp.pl.voicebookreader.lists.SendParamToBookReaderHelper;

/**
 * Created by Piotrek on 05.05.2016.
 */
public class InitializeBookReaderHelper extends AsyncTask<SendParamToBookReaderHelper, Void, BookReaderHelper> {
    @Override
    protected BookReaderHelper doInBackground(SendParamToBookReaderHelper... params) {
        BookReaderHelper bookReaderHelper = new BookReaderHelper(params[0].getAct(), params[0].getCtx(), params[0].getBooksList(), params[0].getBook());
        return bookReaderHelper;
    }
}
