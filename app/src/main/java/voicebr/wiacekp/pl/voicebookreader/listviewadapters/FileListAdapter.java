package voicebr.wiacekp.pl.voicebookreader.listviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import voicebr.wiacekp.pl.voicebookreader.R;
import voicebr.wiacekp.pl.voicebookreader.lists.FileList;

/**
 * Created by Piotrek on 02.05.2016.
 */
public class FileListAdapter extends BaseAdapter {
    private ArrayList<FileList> data;
    private Context listContext;
    private LayoutInflater layoutInflater;

    public FileListAdapter(Context context, ArrayList<FileList> data) {
        this.listContext = context;
        this.data = data;
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
        ImageView ivImage;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        CustomHolder viewCache;
        FileList actualItem = data.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.item_file_list_view, null);

            viewCache = new CustomHolder();

            viewCache.tvTitle = (TextView) convertView
                    .findViewById(R.id.item_file_list_view_title);
            viewCache.ivImage = (ImageView) convertView
                    .findViewById(R.id.item_file_list_view_imageview);

            convertView.setTag(viewCache);
        } else {
            viewCache = (CustomHolder) convertView.getTag();
        }

        viewCache.tvTitle.setText(actualItem.getTitle());
        if(actualItem.isDirectory()){
            viewCache.ivImage.setImageResource(R.drawable.ic_folder_black_24dp);
        }else{
            viewCache.ivImage.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
        }

        return convertView;
    }

}
