package voicebr.wiacekp.pl.voicebookreader.lists;

import java.io.File;

/**
 * Created by Piotrek on 02.05.2016.
 */
public class BooksList {
    private int id;
    private String title;
    private String author;
    private File books;
    private int chapters;
    private int size;
    private int progress;
    private int procents;//current chapter

    public BooksList(int id, String title, String author, File books, int chapters, int size, int progress, int procents) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.books = books;
        this.chapters = chapters;
        this.size = size;
        this.progress = progress;
        this.procents = procents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public File getBooks() {
        return books;
    }

    public void setBooks(File books) {
        this.books = books;
    }

    public int getChapters() {
        return chapters;
    }

    public void setChapters(int chapters) {
        this.chapters = chapters;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProcents() {
        return procents;
    }

    public void setProcents(int procents) {
        this.procents = procents;
    }
}
