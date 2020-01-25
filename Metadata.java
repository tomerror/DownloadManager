import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Metadata implements java.io.Serializable {
    static final String METADATA_FILE_TYPE = ".metadata";
    static final String TEMP_FILE_TYPE = ".tmp";
    private List<Range> _data;
    private int _downloadPercent;

    int GetDownloadPercent(){
        return _downloadPercent;
    }
    List<Range> GetData(){
        return _data;
    }
    public Metadata(){
        _data = new ArrayList<>();
        _downloadPercent = 0;
    }

    void AddMetadata(Range range){
        _data.add(range);
        _downloadPercent = Math.floorDiv(Chunk.done*100, Chunk.count);
        Serialization();
        MergeTmpToMetadataFile();
    }

    private void MergeTmpToMetadataFile() {
        if(FileUtils.FileExists(DownloadManager._fileName + TEMP_FILE_TYPE)){
            FileUtils.Rename(DownloadManager._fileName + TEMP_FILE_TYPE, DownloadManager._fileName + Metadata.METADATA_FILE_TYPE);
        }
    }

    private void Serialization() {
        try {
            FileOutputStream file = new FileOutputStream(DownloadManager._fileName + TEMP_FILE_TYPE);
            ObjectOutputStream out = new ObjectOutputStream(file);
            Metadata metadata = new Metadata();
            metadata._data = _data;
            metadata._downloadPercent = _downloadPercent;
            out.writeObject(metadata);
            out.close();
            file.close();

        } catch (Exception e){
            Printer.PrintError(e.getMessage());
        }
    }
    void Deserialization(String fileName){
        try {
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);
            Metadata metadata = (Metadata) in.readObject();
            _data = metadata._data;
            _downloadPercent = metadata._downloadPercent;

            in.close();
            file.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
