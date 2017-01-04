package voicebr.wiacekp.pl.voicebookreader.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import voicebr.wiacekp.pl.voicebookreader.lists.FileList;

/**
 * Created by Piotrek on 02.05.2016.
 */
public class ExtSdHelper
{
    private Context ctx;
    private File lastPatch;
    private ArrayList<FileList> filesList= new ArrayList<>( );
    private SharedPreferences sharedPreferences;

    public File getLastPath(){
        return lastPatch;
    }

    public void setLastPath(File lastPatch){
        this.lastPatch = lastPatch;
    }

    public ExtSdHelper(Context ctx){
        this.ctx=ctx;
        lastPatch= Environment.getExternalStorageDirectory();
        createDirs(-1);
        sharedPreferences = ctx.getSharedPreferences("extSD", Context.MODE_PRIVATE);
    }

    public String getFileExtension(String name){
        String out= name.split("\\.")[name.split("\\.").length-1];
        return out;
    }

    public ArrayList<FileList> getFilesList(){
        return filesList;
    }

    public ArrayList<FileList> searchAllBooks(String absolutePath){
        ArrayList<FileList> temp= new ArrayList<>( );
        ArrayList<FileList> out= new ArrayList<>( );
        lastPatch=new File(absolutePath);
        updateFiles();
        for(int j=0; j<filesList.size(); j++){
            temp.add(filesList.get(j));
        }
        for(int i=0; i<temp.size(); i++){
            if(temp.get(i).isDirectory()){
                ArrayList<FileList> temp2=searchAllBooks(temp.get(i).getFile().getAbsolutePath());
                for(int j=0; j<temp2.size(); j++){
                    out.add(temp2.get(j));
                }
            }else {
                if(getFileExtension(temp.get(i).getFile().getName()).equals("epub")){
                    out.add(temp.get(i));
                }
            }
        }
        return out;
    }

    public void updateFiles(){
        genFilesLits();
    }

    public ArrayList<FileList> createDirs(int id){
        if(id==-2){
            lastPatch=lastPatch.getParentFile();
        }else if(id==-1){
            lastPatch=Environment.getExternalStorageDirectory();
        }else{
            if(id>=filesList.size()||filesList.get(id).isDirectory()==false){
                return filesList;
            }
            lastPatch=filesList.get(id).getFile();
        }
        genFilesLits();
        return filesList;
    }

    private void genFilesLits(){
        filesList.clear();
        FileFilter folderF =new FileFilter(){
            @Override
            public boolean accept(File p1)
            {
                return p1.isDirectory();
            }
        };

        File[] folders = lastPatch.listFiles(folderF);

        for(int i=0; i<folders.length; i++){
            filesList.add(new FileList( i, folders[i].getName(), true, folders[i].getAbsolutePath(), folders[i]));
        }
        int filesStart=folders.length;

        FileFilter fileF = new FileFilter(){
            @Override
            public boolean accept(File p1)
            {
                return p1.isFile();
            }
        };

        File[] files = lastPatch.listFiles(fileF);

        for(int i=0, j=filesStart; i<files.length; i++, j++){
            filesList.add(new FileList( j,files[i].getName(), false, files[i].getAbsolutePath(), files[i]));
        }
    }
}
