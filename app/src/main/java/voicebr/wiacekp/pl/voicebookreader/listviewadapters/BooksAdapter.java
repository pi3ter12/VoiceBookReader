package voicebr.wiacekp.pl.voicebookreader.listviewadapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import voicebr.wiacekp.pl.voicebookreader.R;
import voicebr.wiacekp.pl.voicebookreader.helper.Helper;
import voicebr.wiacekp.pl.voicebookreader.lists.BooksList;

public class BooksAdapter extends BaseAdapter {
    private ArrayList<BooksList> data;
    private Context listContext;
    private Activity act;
    private LayoutInflater layoutInflater;
    private Helper helper;

    public BooksAdapter(Context context, Activity act, ArrayList<BooksList> data) {
        this.listContext = context;
        this.act=act;
        this.data = data;
        helper = new Helper(listContext, act);
        layoutInflater = LayoutInflater.from(listContext);
    }

    public int getCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return 0;
    }

    private class CustomHolder {
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvProcent;
        ProgressBar pbProgress;
        ImageView ivImage;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        CustomHolder viewCache;
        BooksList actualItem = data.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.item_books_list_view, null);

            viewCache = new CustomHolder();

            viewCache.tvTitle = (TextView) convertView
                    .findViewById(R.id.item_books_list_view_title);
            viewCache.tvAuthor = (TextView) convertView
                    .findViewById(R.id.item_books_list_view_author);
            viewCache.tvProcent = (TextView) convertView
                    .findViewById(R.id.item_books_list_view_procent);
            viewCache.pbProgress = (ProgressBar) convertView
                    .findViewById(R.id.item_books_list_view_progress);
            viewCache.ivImage = (ImageView) convertView
                    .findViewById(R.id.item_books_list_view_imageview);

            convertView.setTag(viewCache);
        } else {
            viewCache = (CustomHolder) convertView.getTag();
        }

        viewCache.tvTitle.setText(actualItem.getTitle());
        viewCache.tvAuthor.setText(actualItem.getAuthor());
        viewCache.tvProcent.setText(actualItem.getProcents() + "%");
        viewCache.pbProgress.setProgress(actualItem.getProcents());

        viewCache.pbProgress.setProgressTintList(ColorStateList.valueOf(Color.BLUE));

        try {
            viewCache.ivImage.setImageBitmap(Bitmap.createScaledBitmap(helper.getImage(actualItem.getId()), 120, 180, false));
        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

}
