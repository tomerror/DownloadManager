public class QueueListener implements Runnable {
    private DownloadFile _file;

    QueueListener(DownloadFile file){
        _file = file;
    }
    @Override
    public void run() {
        try {
            DownloadUtils.ReadFromQueue(_file);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
