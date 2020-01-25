public class Task implements Runnable {
    private Bulk mBulk;
    private String mUrl;

    public Task(Bulk bulk, String url) {
        mBulk = bulk;
        mUrl = url;
    }

    @Override
    public void run() {
        DownloadUtils.DownloadChunks(mBulk, mUrl);
    }
}
