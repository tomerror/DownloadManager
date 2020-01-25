import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.TimeoutException;

class HTTPUtils {
    private static final int TIMEOUT = 80000;
    /**
     * Responsible to open a connection to server by range and download the data.
     * This method execute for each chunk.
     * @param source - Server's url
     * @param bulkStart - Starting point of chunk's bulk for calculate relative range
     * @param chunk - chunk for setup the range
     */
    static void DownloadRange(String source, int bulkStart, Chunk chunk)
        throws SocketTimeoutException, IOException
    {
        URL Url = new URL(source);
        HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
        String range = String.format("bytes=%d-%d", bulkStart+chunk._range.start, bulkStart+chunk._range.end);
        connection.setRequestProperty("Range", range);
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        InputStream inputStream = connection.getInputStream();
        inputStream.readNBytes(chunk._data, 0, chunk._data.length);
        connection.disconnect();
    }

    /**
     * This method get file's url and return the file's size.
     * @param url - file's url
     * @return - file's size
     */
    static Integer GetFileSize(String url) {
        int size = -1;
        try{
            URL Url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
            connection.setConnectTimeout(TIMEOUT);
            size = connection.getContentLength();
        }catch (SocketTimeoutException ex){
            System.out.println(ex.getMessage());
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return size;
    }
}
