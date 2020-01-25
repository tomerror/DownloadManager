import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

class DownloadManager {
    static int _failure;
    private static Integer _fileSize;
    static List<String> _sources = new ArrayList<>();
    static String _fileName;
    static Integer _numberOfThreads;
    static Metadata _metadata;
    static ArrayBlockingQueue<Chunk> _queue;


    DownloadManager(String source, int numberOfThreads)
    {
        setSources(source);
        _fileName = FileUtils.ExtractFileName(_sources.get(0));
        _fileSize = HTTPUtils.GetFileSize(_sources.get(0));
        if(_fileSize != -1){
            _numberOfThreads = _fileSize / numberOfThreads < Chunk.SIZE ? _fileSize / Chunk.SIZE : numberOfThreads;
            _metadata = new Metadata();
            _queue = new ArrayBlockingQueue<>((_fileSize*8/Chunk.SIZE)+1);
            _failure = 0;
            DownloadFile();
        } else {
            Printer.PrintError("Can't find file. please check the URL entered and your internet connection");
        }
    }

    /**
     * The method decide if to start download from scratch or continue the last download
     */
    private static void DownloadFile()
    {
        DownloadFile downloadFile = new DownloadFile(_fileSize, _numberOfThreads);
        if(!ExistsBackUp()){
            if(!FileUtils.FileExists(_fileName)){
                DownloadUtils.StartDownload(downloadFile);
            }else{
                Printer.FileExists();

                String answer = new Scanner(System.in).nextLine();
                while(!answer.matches("[yn]")){
                    Printer.YesNoAnswer();
                    answer = new Scanner(System.in).nextLine();
                }
                if(answer.matches("[y]")){
                    DownloadUtils.StartDownload(downloadFile);
                }
            }
        }
        else{
            _metadata.Deserialization(_fileName + Metadata.METADATA_FILE_TYPE);
            DownloadUtils.ResumeDownload(downloadFile, _metadata.GetData());
        }
    }

    /**
     * Check is exists a backup files.
     * First check if the metadata file is exists. if it is then the program continue the last run.
     * Second check if the temp
     * @return
     */
    private static boolean ExistsBackUp()
    {
        boolean exists = false;
        if(!FileUtils.FileExists(_fileName + Metadata.METADATA_FILE_TYPE)) {
            if(!FileUtils.FileExists(_fileName + Metadata.TEMP_FILE_TYPE)) {
                exists = false;
            } else {
                FileUtils.Rename(_fileName + Metadata.TEMP_FILE_TYPE, _fileName + Metadata.METADATA_FILE_TYPE);
                exists = true;
            }
        }else{
            exists = true;
        }
        return exists;
    }

    /**
     * Extract source paths from a file
     * @param source
     */
    private void setSources(String source)
    {
        if(FileUtils.IsFile(source)){
            _sources = FileUtils.ExtractUrls(source);
        }
        else {
            _sources.add(source);
        }
    }
}
