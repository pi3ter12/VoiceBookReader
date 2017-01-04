package voicebr.wiacekp.pl.voicebookreader.lists;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Piotrek on 02.05.2016.
 */
public class SendParam {
    public Context ctx;
    public Activity act;
    public ArrayList<String> data;

    public SendParam(Context ctx, Activity act, ArrayList<String> data) {
        this.ctx = ctx;
        this.act = act;
        this.data = data;
    }
}
