import java.io.FileNotFoundException;

public class IdcDm {
    private static String Source;
    private static int numberOfThreads = 1;

    public static void main(String[] args){
        try{
            CheckParamsValidation(args);
            if (args.length > 1) {
                CheckNumberOfThreads(args[1]);
            }
            DownloadManager downloadManager = new DownloadManager(Source, numberOfThreads);
        } catch (Exception e) {
            Printer.PrintError(e.getMessage());
        }
    }

    private static void CheckParamsValidation(String[] args)
            throws Exception
    {
        if(args.length > 1){
            Source = args[0];
        } else{
            throw new Exception("usage:\n\tjava IdcDm URL|URL-LIST-FILE [MAX-CONCURRENT-CONNECTIONS]");
        }

        if(FileUtils.IsFile(Source)){
            if(!FileUtils.FileExists(Source)){
                throw new FileNotFoundException(String.format("File '%s' not found", Source));
            }
        }
    }

    private static void CheckNumberOfThreads(String param)
            throws IllegalArgumentException
    {
        try{
            numberOfThreads = Integer.parseInt(param);
        } catch (Exception e){
            throw new IllegalArgumentException(String.format("%s is an illegal argument. Please insert a number", param));
        }
    }
}
