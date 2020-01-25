import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Contain collection of function the responsible on work with files.
 */
class FileUtils {

    /**
     * Write the downloaded data to the main file
     * And update the metadata on new range that downloaded
     * @param chunk
     * @param bulkPosition
     */
    static void InsertFile(Chunk chunk, int bulkPosition) {
        try{
            RandomAccessFile writeToMainFile = new RandomAccessFile(DownloadManager._fileName, "rw");
            writeToMainFile.seek(chunk._range.start + bulkPosition);
            writeToMainFile.write(chunk._data);
            writeToMainFile.close();
            chunk.Downloaded();
            DownloadManager._metadata.AddMetadata(new Range(chunk._range.start + bulkPosition, chunk._range.end  + bulkPosition));
        } catch (IOException e){
            Printer.PrintError("Error while trying write to file");
        }
    }

    static String ExtractFileName(String path){
        int section = path.split("/").length;

        return path.split("/")[section-1];
    }

    static void DeleteFile(String path) throws IOException {
        File file = new File(path);
        if(file.exists()){
            if(!file.delete()){
                throw new IOException(String.format("File %s not deleted", path));
            };
        }
    }

    static boolean IsFile(String source) {
        return !source.contains("http");
    }

    static List<String> ExtractUrls(String FileName){
        List<String> lines = new ArrayList<String>();
        try{
            File file = new File(FileName);
            Scanner reader = new Scanner(file);
            while(reader.hasNextLine()){
                lines.add(reader.nextLine());
            }
            reader.close();
        } catch (Exception e){
            Printer.PrintError("Some problem occurred while try to read the file");
        }
        return lines;
    }

    static boolean FileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    static void Rename(String source, String dest) {
        File Source = new File(source);
        File Dest = new File(dest);
        try{
            Files.copy(Source.toPath(), Dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Source.delete();
        } catch (Exception e){

        }
    }
}
