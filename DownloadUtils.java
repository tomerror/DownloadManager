import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class DownloadUtils {
    private static final int THREAD_SLEEP_TIME = 2000;
    /**
     * The main method of this program.
     * Responsible to download a file by given DownloadFile structure.
     * @param file
     */
    static void StartDownload(DownloadFile file) {
        Printer.StartDownloading();
        Runnable r = null;

        ExecutorService pool = Executors.newFixedThreadPool(DownloadManager._numberOfThreads+1);
        for(int i = 0; i < file.state.length ; i++){
            r = new Task(file.state[i], getUrl(i));
            pool.execute(r);
        }
        r = new QueueListener(file);
        pool.execute(r);
        pool.shutdown();
        CheckIfProgramDone();

    }

    /**
     * This method is responsible to recover from failure, in the last run, on purpose to complete the download.
     * @param downloadFile - Reference to data structure
     * @param Ranges - Reference to Metadata ranges
     */
    static void ResumeDownload(DownloadFile downloadFile, List<Range> Ranges)
    {
        DownloadFile temp = RebuildDownloadFile(downloadFile, Ranges);
        DownloadUtils.StartDownload(temp);
    }

    /**
     * A recursive method that help ResumeDownload to rebuild the DownloadFile structure.
     * Extract from metadata file all the ranges that already downloaded
     * Moving each chunk in DownloadFile's structure and checking if this chunk already downloaded by his range.
     * In case of found a chunk that already downloaded, the method decide if resize chunk's range or skip on him.
     * @param downloadFile - Reference to data structure
     * @param Ranges - Reference to Metadata ranges
     * @return a Download
     */
    private static DownloadFile RebuildDownloadFile(DownloadFile downloadFile, List<Range> Ranges)
    {
        List<Chunk> newChunks = new ArrayList<Chunk>();
        List<Range> RangesThatHaveIntersection = new ArrayList<>();
        for (Range range: Ranges) {
            for (Bulk bulk : downloadFile.state) {
                for (Chunk chunk: bulk.chunks) {
                    if(!Range.equalRanges(chunk._range.add(bulk._range), range)){
                        if(Range.intersection(chunk._range.add(bulk._range), range)){
                            List<Range> newRanges = Range.getNewRange(chunk._range.add(bulk._range), range);
                            if(!newRanges.get(0).equals(new Range(-1, -1))){
                                chunk._range = newRanges.get(0);
                                chunk._range = chunk._range.subtraction(bulk._range);
                                chunk._data  = new byte[chunk._range.getDistance()];
                                if(newRanges.size() > 1){
                                    Chunk newChunk = new Chunk(bulk.idx, newRanges.get(1));
                                    newChunks.add(newChunk);
                                    RangesThatHaveIntersection.add(range);
                                }
                            }else{
                               chunk.Downloaded();
                            }
                        }
                    }
                    else{
                        chunk.Downloaded();
                    }
                }
            }
        }
        if(newChunks.size() > 0){
            for (Chunk chunk : newChunks) {
                for (Bulk bulk : downloadFile.state) {
                    if(chunk._range.subset(bulk._range)){
                        chunk._range = chunk._range.subtraction(bulk._range);
                        bulk.chunks.add(chunk);
                    }
                }
            }
            RebuildDownloadFile(downloadFile, RangesThatHaveIntersection);
        }
        return downloadFile;
    }

    /**
     * The first type of thread.
     * Create for each bulk.
     * Responsible to download the bulk's range and to save the data into the queue
     * @param bulk - Given bulk to work on
     * @param Url - Url
     */
    static void DownloadChunks(Bulk bulk, String Url)
    {
        Printer.ThreadStartDownloading(bulk, Url);
        Printer.InitLoadingBar();
        try {
            for (Chunk chunk : bulk.chunks) {
                if(!chunk._saved){
                    HTTPUtils.DownloadRange(Url, bulk._range.start, chunk);
                    DownloadManager._queue.add(chunk);
                }
            }
            Printer.ThreadComplete();
        } catch (IOException e){
            if(DownloadManager._failure == 0){
                synchronized (DownloadUtils.class){
                    if(DownloadManager._failure == 0){
                        DownloadManager._failure++;
                        Printer.PrintError("The connection was lost");
                    }
                }
            }
        }
    }

    /**
     * The second type of thread is responsible on read from queue and write to disk
     * And also to detect when the program is finish and to call CloseProgram()
     * @param file - The data structure that was created
     * @throws InterruptedException
     */
    static void ReadFromQueue(DownloadFile file) throws InterruptedException
    {
        while((Chunk.done != Chunk.count) || (DownloadManager._queue.size() != 0)){
            if(DownloadManager._queue.size() != 0){
                while(DownloadManager._queue.size() != 0){
                    Chunk chunk = DownloadManager._queue.poll();
                    FileUtils.InsertFile(chunk , file.state[chunk._bulkId]._range.start);
                    Printer.LoadingBar(Chunk.done, Chunk.count);
                }
            }
            else{
                Thread.sleep(THREAD_SLEEP_TIME);
            }
        }
    }

    /**
     * Given each thread a url by mod on the sources list
     * @param ThreadId
     * @return
     */
    private static String getUrl(int ThreadId)
    {
        String url;
        int idx = ThreadId % DownloadManager._sources.size();
        url = DownloadManager._sources.get(idx);
        return url;
    }

    /**
     * The main thread continue to run until he was detect the end of the program or failure
     */
    private static void CheckIfProgramDone()
    {
        while ((Chunk.done != Chunk.count)&&(DownloadManager._failure == 0)){
            try{
                Thread.sleep(THREAD_SLEEP_TIME);
            }catch (Exception e){
                Printer.PrintError("Something goes wrong :(");
            }
        }
        CloseProgram();
    }

    /**
     * Responsible to close the program in clear way
     * Otherwise he will declare that the download fail.
     */
    private static void CloseProgram()
    {
        if(DownloadManager._failure == 0) {
            try{
                FileUtils.DeleteFile(DownloadManager._fileName + Metadata.METADATA_FILE_TYPE);
                Printer.DownloadComplete();
            }catch (IOException ex){
                DownloadFail();
            }
        }
        else{
            DownloadFail();
        }

    }

    private static void DownloadFail(){
        Printer.DownloadFailed();
        System.exit(0);
    }
}
