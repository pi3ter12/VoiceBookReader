package voicebr.wiacekp.pl.voicebookreader.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import voicebr.wiacekp.pl.voicebookreader.lists.BooksList;
import voicebr.wiacekp.pl.voicebookreader.lists.FileList;

/**
 * Created by Piotrek on 02.05.2016.
 */
public class Helper {
    private Context ctx;
    private Activity act;

    public Helper(Context ctx, Activity act) {
        this.ctx = ctx;
        this.act = act;
    }

    public Bitmap getImage(int index){
        File file = new File(ctx.getFilesDir().getAbsolutePath()+"/cover"+index+".png");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;
    }

    public void saveImage(Bitmap bmp, String title){
        File file = new File(ctx.getFilesDir().getAbsolutePath()+"/"+title+".png");

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveNewValues(BooksList booksList, int progress) throws FileNotFoundException, JSONException {
        int procents = (progress*100)/booksList.getSize();

        JSONArray jsonArray = getJsonInfo();
        for(int i=0; i<jsonArray.length(); i++){
            if(jsonArray.getJSONObject(i).getInt("id")==booksList.getId()){
                JSONObject booksListSingle = jsonArray.getJSONObject(i);
                booksListSingle.put("procents", procents);
                booksListSingle.put("progress", progress);
                jsonArray.remove(i);
                jsonArray.put(i, booksListSingle);
                break;
            }
        }
        saveJsonInfo(jsonArray);
    }

    public void saveBooksLoaction(String path) throws FileNotFoundException {
        Log.d("Helper", ctx.getFilesDir().getAbsolutePath());
        File file = new File(ctx.getFilesDir().getAbsolutePath()+"/myPath.txt");
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.println(path);
        printWriter.close();
    }

    public String getBooksLoaction() throws FileNotFoundException {
        Log.d("Helper", ctx.getFilesDir().getAbsolutePath());
        File file = new File(ctx.getFilesDir().getAbsolutePath()+"/myPath.txt");
        Scanner in = new Scanner(file);
        String line="";
        do{
            line=in.nextLine();
        }while (in.hasNextLine());
        in.close();
        return line;
    }

    public void saveJsonInfo(JSONArray json) throws FileNotFoundException {
        Log.d("Helper", ctx.getFilesDir().getAbsolutePath());
        File file = new File(ctx.getFilesDir().getAbsolutePath()+"/info.json");
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.println(json.toString());
        printWriter.close();
    }

    public JSONArray getJsonInfo() throws FileNotFoundException, JSONException {
        Log.d("Helper", ctx.getFilesDir().getAbsolutePath());
        File file = new File(ctx.getFilesDir().getAbsolutePath()+"/info.json");
        Scanner in = new Scanner(file);
        String line="";
        do{
            line+=in.nextLine();
        }while (in.hasNextLine());
        in.close();
        return new JSONArray(line);
    }

    public ArrayList<BooksList> getbooksListFromJson(JSONArray jsonArray){
        ArrayList<BooksList> out = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            try {
                out.add(new BooksList(
                        jsonArray.getJSONObject(i).getInt("id"),
                        jsonArray.getJSONObject(i).getString("titile"),
                        jsonArray.getJSONObject(i).getString("author"),
                        new File(jsonArray.getJSONObject(i).getString("books")),
                        jsonArray.getJSONObject(i).getInt("chapters"),
                        jsonArray.getJSONObject(i).getInt("size"),
                        jsonArray.getJSONObject(i).getInt("progress"),
                        jsonArray.getJSONObject(i).getInt("procents")
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    public void saveBooksList(ArrayList<FileList> books) throws FileNotFoundException {
        Log.d("Helper", ctx.getFilesDir().getAbsolutePath());
        File file = new File(ctx.getFilesDir().getAbsolutePath()+"/books.txt");
        PrintWriter printWriter = new PrintWriter(file);
        for(int i=0; i<books.size(); i++) {
            printWriter.println(books.get(i).getFile().getAbsolutePath());
        }
        printWriter.close();
    }

    public ArrayList<String> getBooksList() throws FileNotFoundException {
        Log.d("Helper", ctx.getFilesDir().getAbsolutePath());
        File file = new File(ctx.getFilesDir().getAbsolutePath()+"/books.txt");
        Scanner in = new Scanner(file);
        ArrayList<String> out = new ArrayList<>();
        do{
            out.add(in.nextLine());
        }while (in.hasNextLine());
        in.close();
        return out;
    }

    public Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) { //todo: set cover as background

        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}
