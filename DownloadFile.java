import java.util.List;

/**
 * Data structure.
 * Responsible to separate the file to equal parts and create bulks.
 */
class DownloadFile {
    Bulk[] state;

    DownloadFile(int fileLength, int numberOfThread){
        List<Range> ranges = Range.getRanges(fileLength, numberOfThread);
        state = new Bulk[ranges.size()];
        for(int i=0 ; i < ranges.size() ; i++){
            state[i] = new Bulk(ranges.get(i));
        }
    }
}
