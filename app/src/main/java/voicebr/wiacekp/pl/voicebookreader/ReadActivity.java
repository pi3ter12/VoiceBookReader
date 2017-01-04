package voicebr.wiacekp.pl.voicebookreader;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import nl.siegmann.epublib.domain.Book;
import voicebr.wiacekp.pl.voicebookreader.helper.BookReaderHelper;
import voicebr.wiacekp.pl.voicebookreader.helper.Helper;
import voicebr.wiacekp.pl.voicebookreader.helper.InitializeBookReaderHelper;
import voicebr.wiacekp.pl.voicebookreader.helper.LoadBook;
import voicebr.wiacekp.pl.voicebookreader.lists.BooksList;
import voicebr.wiacekp.pl.voicebookreader.lists.SendParam;
import voicebr.wiacekp.pl.voicebookreader.lists.SendParamToBookReaderHelper;

public class ReadActivity extends AppCompatActivity {
    private BooksList bookItem;
    private Helper helper;

    private ImageButton ibplay, ibnext, ibprev, ibforward, ibbackward, ibplusspeech, ibminusspeech;
    private ImageView imageViewCover;
    private TextView tvAuthor, tvTitle, tvChapters, tvSpeech;
    private ProgressBar progressBar;

    boolean play = true;

    private TextToSpeech tts;

    private Book book = null;

    private HashMap<String, String> map = new HashMap<String, String>();
    private SendParam sendParam = null;

    private BookReaderHelper bookReaderHelper = null;
    private boolean loaded=false;

    private ProgressDialog progress;

    private int chapterShow=1;

    private int speechRateToShow = 10;

    private String lastLine="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        ibplay=(ImageButton)findViewById(R.id.activity_read_ib_play);
        ibforward=(ImageButton)findViewById(R.id.activity_read_fforward);
        ibbackward=(ImageButton)findViewById(R.id.activity_read_fbackward);
        ibprev=(ImageButton)findViewById(R.id.activity_read_backchapters);
        ibnext=(ImageButton)findViewById(R.id.activity_read_nextchapters);

        ibplusspeech=(ImageButton)findViewById(R.id.activity_read_ib_plus_rate);
        ibminusspeech=(ImageButton)findViewById(R.id.activity_read_ib_minus_rate);

        imageViewCover=(ImageView)findViewById(R.id.activity_read_imageView);

        tvAuthor=(TextView)findViewById(R.id.activity_read__author);
        tvTitle=(TextView)findViewById(R.id.activity_read_title);
        tvChapters=(TextView)findViewById(R.id.activity_read_tvaboveprogressbar);

        tvSpeech=(TextView)findViewById(R.id.activity_read_tv_rate);

        progressBar=(ProgressBar)findViewById(R.id.activity_read_progressbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        }else{
            progressBar.getProgressDrawable().setColorFilter(
                    Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        helper = new Helper(this, this);
        Bundle bundle = getIntent().getExtras();
        bookItem = new BooksList(
                bundle.getInt("id"),
                bundle.getString("title"),
                bundle.getString("author"),
                new File(bundle.getString("books")),
                bundle.getInt("chapters"),
                bundle.getInt("size"),
                bundle.getInt("progress"),
                bundle.getInt("procents")
                );

        tvAuthor.setText(bookItem.getAuthor());
        tvTitle.setText(bookItem.getTitle());

        chapterShow=1;
        tvChapters.setText(this.getResources().getString(R.string.chapter) + chapterShow + "/" + bookItem.getChapters());

        ibplay.setOnClickListener(new ImageButtonClickListener());
        ibnext.setOnClickListener(new ImageButtonClickListener());
        ibprev.setOnClickListener(new ImageButtonClickListener());
        ibbackward.setOnClickListener(new ImageButtonClickListener());
        ibforward.setOnClickListener(new ImageButtonClickListener());
        ibminusspeech.setOnClickListener(new ImageButtonClickListener());
        ibplusspeech.setOnClickListener(new ImageButtonClickListener());

        imageViewCover.setImageBitmap(Bitmap.createScaledBitmap(helper.getImage(bookItem.getId()), 160, 240, false));



        ArrayList<String> toLoad = new ArrayList<>();
        toLoad.add(bookItem.getBooks().getAbsolutePath());
        sendParam = new SendParam(this, this, toLoad);

        progress = ProgressDialog.show(this, this.getResources().getString(R.string.load_book),
                this.getResources().getString(R.string.please_wait), true);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(new Locale("pl"));
//                tts.setSpeechRate(2.0f);
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "BookSpeek");
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (!play) {
                            addText(bookReaderHelper.nextLine());
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        if (!play) {
                            clickPlay();
                            Toast.makeText(sendParam.ctx, getApplicationContext().getResources().getString(R.string.tts_error_message), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                new LoadBook(){
                    @Override
                    protected void onPostExecute(Book bookDone) {
                        super.onPostExecute(bookDone);
                        book=bookDone;
                        initializeBookReaderHelper();
                    }
                }.execute(sendParam);

            }
        });
    }

    private void initializeBookReaderHelper(){
        SendParamToBookReaderHelper sendParamToBookReaderHelper = new SendParamToBookReaderHelper(sendParam.act, sendParam.ctx, bookItem, book);
        new InitializeBookReaderHelper(){
            @Override
            protected void onPostExecute(BookReaderHelper bookReaderHelper1) {
                super.onPostExecute(bookReaderHelper1);
                play = true;

                bookReaderHelper =bookReaderHelper1;
                loaded=true;
                progress.dismiss();
                checkChapterInfo();
                invalidateProcentsInChapters();
            }
        }.execute(sendParamToBookReaderHelper);
    }

    private void invalidateProcentsInChapters(){
        int value = (bookReaderHelper.getReadedLine()*100)/bookReaderHelper.getMaxInChapters();
        progressBar.setProgress(value);
    }

    private void tellText(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
        if(!text.equals("")){
            lastLine=text;
        }
    }

    private void addText(String text){
        tts.speak(text, TextToSpeech.QUEUE_ADD, map);
        checkChapterInfo();
        invalidateProcentsInChapters();
        lastLine=text;
    }

    private void clickPlay(){
        if(loaded) {
            if (play) {
                ibplay.setImageResource(R.drawable.ic_pause_circle_filled_black_48dp);
            } else {
                ibplay.setImageResource(R.drawable.ic_play_circle_filled_black_48dp);
            }
            play = !play;
            if(!play){
                tellText("");
                addText(bookReaderHelper.nextLine());
            }else{
                tellText("");
                bookReaderHelper.stop();
            }
        }
    }

    private void checkChapterInfo(){
        if(chapterShow-1!=bookReaderHelper.getReadedChapter()){
            chapterShow=bookReaderHelper.getReadedChapter()+1;
            tvChapters.setText(this.getResources().getString(R.string.chapter) + chapterShow + "/" + bookItem.getChapters());
        }
    }

    private void clickForward(){
        if(loaded){
            bookReaderHelper.forward();
            resumeRead();
        }
    }

    private void clickBackward(){
        if(loaded){
            bookReaderHelper.backward();
            resumeRead();
        }
    }

    private void clickNextChapter(){
        if(loaded){
            bookReaderHelper.nextChapter();
            resumeRead();
        }
    }

    private void resumeRead(){
        if(!play){
            clickPlay();
            clickPlay();
        }else{
            clickPlay();
        }
    }


    private void clickPrevChapter(){
        if(loaded){
            bookReaderHelper.prevChapter();
            resumeRead();
        }
    }

    private void clickChangeSpeechRate(boolean plus){
        if(loaded){
            if(plus){
                if(speechRateToShow<20){
                    speechRateToShow++;
                }
            }else{
                if(speechRateToShow>1){
                    speechRateToShow--;
                }
            }
            float speechRate = speechRateToShow*0.1f;
            tts.setSpeechRate(speechRate);
            tvSpeech.setText("Prędkość : "+speechRateToShow);
            tellText(lastLine);
        }
    }


    public class ImageButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getId()==ibplay.getId()){
                clickPlay();
            }else if(v.getId()==ibbackward.getId()){
                clickBackward();
            }else if(v.getId()==ibforward.getId()){
                clickForward();
            }else if(v.getId()==ibnext.getId()){
                clickNextChapter();
            }else if(v.getId()==ibprev.getId()){
                clickPrevChapter();
            }else if(v.getId()==ibplusspeech.getId()){
                clickChangeSpeechRate(true);
            }else if(v.getId()==ibminusspeech.getId()){
                clickChangeSpeechRate(false);
            }
        }
    }
}
