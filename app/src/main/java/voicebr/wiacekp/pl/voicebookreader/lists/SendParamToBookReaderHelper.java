package voicebr.wiacekp.pl.voicebookreader.lists;

import android.app.Activity;
import android.content.Context;

import nl.siegmann.epublib.domain.Book;

/**
 * Created by Piotrek on 05.05.2016.
 */
public class SendParamToBookReaderHelper {
    private Activity act;
    private Context ctx;
    private BooksList booksList;
    private Book book;

    public SendParamToBookReaderHelper(Activity act, Context ctx, BooksList booksList, Book book) {
        this.act = act;
        this.ctx = ctx;
        this.booksList = booksList;
        this.book = book;
    }

    public Activity getAct() {
        return act;
    }

    public void setAct(Activity act) {
        this.act = act;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public BooksList getBooksList() {
        return booksList;
    }

    public void setBooksList(BooksList booksList) {
        this.booksList = booksList;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
