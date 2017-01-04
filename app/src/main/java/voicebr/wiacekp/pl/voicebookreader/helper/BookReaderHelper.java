package voicebr.wiacekp.pl.voicebookreader.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SpineReference;
import voicebr.wiacekp.pl.voicebookreader.R;
import voicebr.wiacekp.pl.voicebookreader.lists.BooksList;

/**
 * Created by Piotrek on 04.05.2016.
 */
public class BookReaderHelper {
    private Activity act;
    private Context ctx;
    private BooksList booksList;
    private Book book;
    private ArrayList<ArrayList<String>> bookUnziped = new ArrayList<>();

    private int readedChapter=0;
    private int readedLine=0;

    private Helper helper;

    private int maxInChapters=0;

    private int autoSave=0;

    public BookReaderHelper(Activity act, Context ctx, BooksList booksList, Book book) {
        this.act = act;
        this.ctx = ctx;
        this.booksList = booksList;
        this.book = book;
        helper = new Helper(ctx, act);
        initBook();
    }

    private void initBook(){
        unzipFunction();
        int progress = booksList.getProgress();
        for(int i=0; i<bookUnziped.size(); i++){
            if(progress-bookUnziped.get(i).size()<0){
                readedChapter=i;

                readedLine=progress;
                break;
            }else {
                progress-=bookUnziped.get(i).size();
            }
        }

        invalidateMaxInChapters();
    }

    public void forward(){  //+10 line
        if(readedLine+10>=bookUnziped.get(readedChapter).size()){
            if(readedChapter!=bookUnziped.size()-1){
                int toNextChapter=10-(bookUnziped.get(readedChapter).size()-readedLine);
                readedChapter++;
                readedLine=toNextChapter;
            }
        }else{
            readedLine+=10;
        }
    }

    public void backward(){
        if(readedLine-10<0){
            if(readedChapter>0){
                readedChapter--;
                readedLine=10-readedLine;
            }
        }else {
            readedLine-=10;
        }
    }

    public void nextChapter(){
        if(readedChapter!=bookUnziped.size()-1){
            readedChapter++;
            readedLine=0;
            invalidateMaxInChapters();
        }
    }

    public void prevChapter(){
        if(readedChapter>0){
            readedChapter--;
            readedLine=0;
            invalidateMaxInChapters();
        }
    }

    public void stop(){
        save();
    }

    private void save(){
        int progress=0;
        for(int i=0; i<readedChapter; i++){
            progress+=bookUnziped.get(i).size();
        }
        progress+=readedLine-2;

        try {
            helper.saveNewValues(booksList, progress);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String nextLine(){
        String out= bookUnziped.get(readedChapter).get(readedLine);

        if(readedLine+1>=bookUnziped.get(readedChapter).size()){
            if(readedChapter==bookUnziped.size()-1){
                out+=ctx.getResources().getString(R.string.end);
            }else{
                readedChapter++;
                invalidateMaxInChapters();
                readedLine=0;
            }
        }else{
            readedLine++;
        }

        autoSave++;
        if(autoSave==10){
            save();
            autoSave=0;
        }

        return out;
    }

    private void invalidateMaxInChapters(){
        maxInChapters=bookUnziped.get(readedChapter).size();
    }

    private void unzipFunction() {
        try {
            List<SpineReference> spineReferences = book.getSpine().getSpineReferences();
            InputStream is = null;

            for(int j=0; j<spineReferences.size(); j++){
                is = spineReferences.get(j).getResource().getInputStream();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(is));


                bookUnziped.add(new ArrayList<String>());
                String line = null;
                while ((line = reader.readLine()) != null) {
                    bookUnziped.get(j).add(deleteHtmlTag(line));
                }
                bookUnziped.set(j, removeEmpty(bookUnziped.get(j)));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String deleteHtmlTag(String text){
        String out ="";
        Boolean skipThis=false;
        for(int i=0; i<text.length(); i++){
            if(text.charAt(i)=='<'){
                skipThis=true;
            }
            if(!skipThis){
                out+=text.charAt(i);
            }
            if(text.charAt(i)=='>'){
                skipThis=false;
            }
        }
        return out;
    }

    private ArrayList<String> removeEmpty(ArrayList<String>input){
        ArrayList<String> output = input;
        for(int i=0; i<output.size(); i++){
            if(output.get(i).trim().equals("")){
                output.remove(i);
                i--;
            }
        }
        return output;
    }

    public int getReadedChapter(){
        return readedChapter;
    }

    public int getReadedLine(){
        return readedLine;
    }

    public int getMaxInChapters() {
        return maxInChapters;
    }
}
