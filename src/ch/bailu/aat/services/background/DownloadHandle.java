package ch.bailu.aat.services.background;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import ch.bailu.aat.helpers.AppBroadcaster;
import ch.bailu.aat.helpers.AppTheme;

public class DownloadHandle extends ProcessHandle {


    private final static String USER_AGENT_KEY = "User-Agent";
    private final static String USER_AGENT_VALUE = 
            AppTheme.getAppShortName() + "/" + AppTheme.getAppFullName() + " (aat@bailu.ch)";

    private final static int IO_BUFFER_SIZE=8*1024;

    private final String url;
    private final File file;

    private boolean downloadLock=false;


    public DownloadHandle(String source, File target) {
        file = target;
        url = source;
    }


    @Override
    public long bgOnProcess() {
        try {
            final long r = download(new URL(url), file);
            return r;
            
        } catch (IOException e) {
            return 0;
        }
    }


    
    @Override
    public String toString() {
        return url;
    }
    

    @Override
    public boolean isLocked() {
        return downloadLock;
    }



    private long download(URL url, File file) throws IOException {
        int count=0;
        long total=0;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        byte[] buffer = new byte[IO_BUFFER_SIZE];


        try {
            connection = openConnection(url);
            input = openInput(connection);

            downloadLock=true;
            output = openOutput(file);

            while (( count = input.read(buffer)) != -1) {
                total+=count;
                output.write(buffer, 0, count);
            }


        } finally {    
            close(output);
            downloadLock=false;

            close(input);
            if (connection!=null) connection.disconnect();
        }

        return total;
    }



    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty(USER_AGENT_KEY, USER_AGENT_VALUE);


        //AppLog.d(connection, connection.getRequestProperties().toString());
        /*
            Map<String, List<String>> map = connection.getRequestProperties();


            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                AppLog.d(entry, entry.getKey());
                for (int i=0; i< entry.getValue().size(); i++) {
                    AppLog.d(entry, entry.getValue().get(i));
                }
            }*/



        return (HttpURLConnection) url.openConnection();

    }

    private static InputStream openInput(HttpURLConnection connection) throws IOException {
        return connection.getInputStream();

    }

    private static OutputStream openOutput(File file) throws IOException {
        new File(file.getParent()).mkdirs();
        return new FileOutputStream(file);
    }


    public File getFile() {
        return file;
    }


    @Override
    public void broadcast(Context c) {
        AppBroadcaster.broadcast(c, 
                AppBroadcaster.FILE_CHANGED_ONDISK, file.toString(),url);
    }

};
