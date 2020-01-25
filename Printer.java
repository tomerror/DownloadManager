class Printer {
    private static int lastLoadingBar = -1;
    static void StartDownloading()
    {
        System.out.println(String.format("Downloading %s...",
                DownloadManager._numberOfThreads == 1 ? "" : String.format("using %d connections", DownloadManager._numberOfThreads)));
    }

    static void ThreadStartDownloading(Bulk bulk, String url)
    {
        int downloadedByte = bulk.NumberOfBitsAlreadyDownloaded();
        System.out.println(String.format("[%d] %s downloading range (%d - %d%s) from:\n%s",
                Thread.currentThread().getId(),
                downloadedByte == 0 ? "Start" : "Continue",
                bulk._range.start,
                bulk._range.end,
                downloadedByte != 0 ? String.format(" | left %d bytes", bulk._range.getDistance() - downloadedByte) : "",
                url));
    }

    private static void ThreadEndDownloading()
    {
        System.out.println(String.format("[%d] Finished downloading",Thread.currentThread().getId()));
    }

    static synchronized void LoadingBar(int i, int j)
    {
        double x = i*100/j;
        if(lastLoadingBar < x){
            if(x == 100){
                ThreadEndDownloading();
            }else{
                System.out.println(String.format("Downloaded %d",(int)x)+"%");
                lastLoadingBar = (int)x;
            }
        }
    }

    static synchronized void InitLoadingBar()
    {
        if(lastLoadingBar == 0){
            lastLoadingBar = DownloadManager._metadata.GetDownloadPercent();
            System.out.println(String.format("Downloaded %d",(int)lastLoadingBar)+"%");
        }
    }

    static void ThreadComplete()
    {
        System.out.println(String.format("[%d] Finished downloading", Thread.currentThread().getId()));
    }

    static void DownloadComplete()
    {
        System.out.println("Download succeeded");
    }

    static void FileExists()
    {
        System.err.println("File already exists.");
        System.out.println("Would you like to overwrite the file? (y/n)");
    }

    static void YesNoAnswer()
    {
        PrintError("Invalid input.");
        System.out.println("Type y - yes or n - no");
    }

    static void PrintError(String message)
    {
        System.err.println(message);
    }

    static void DownloadFailed() {
        PrintError("Download failed");
    }

}
