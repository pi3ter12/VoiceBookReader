package voicebr.wiacekp.pl.voicebookreader.lists;

import java.io.File;

/**
 * Created by Piotrek on 02.05.2016.
 */
public class FileList {
    private int id;
    private String title;
    private boolean isDirectory;
    private String path;
    private File file;

    public FileList(int id, String title, boolean isDirectory, String path, File file) {
        this.id = id;
        this.title = title;
        this.isDirectory = isDirectory;
        this.path = path;
        this.file = file;
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

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
