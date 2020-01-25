import java.util.ArrayList;
import java.util.List;

public class Range implements java.io.Serializable {
    int start;
    int end;

    public Range(int Start, int End){
        start = Start;
        end = End;
    }

    static List<Range> getRanges(int fileLength, int threads) {
        List<Range> ranges = new ArrayList<Range>();
        int bulk = Math.round(fileLength / threads)+1;
        for(int i = 0 ; i < fileLength ; i++){
            int end = i+bulk-1 < fileLength ? i+bulk-1 : fileLength - 1;
            ranges.add(new Range(i, end));
            i = i + bulk - 1;
        }
        return ranges;
    }

    boolean equals(Range range){
        return (start == range.start) && (end == range.end);
    }


    /**
     * Get a range and remove from him indexes that already downloaded.
     * @param save - Index to save
     * @param remove - Indexes to remove
     * @return
     */
    static List<Range> getNewRange(Range save, Range remove) {
        List<Range> range = new ArrayList<Range>();
        if(save.start == remove.start){
            //inside but share point
            if(save.end > remove.end){
                range.add(new Range(remove.end+1, save.end));
            }
            else{
                range.add(new Range(-1,-1));
            }
        }else{
            if(save.end == remove.end){
                //inside but share point
                if(save.start < remove.start){
                    range.add(new Range(save.start, remove.start - 1));
                }
                else{
                    range.add(new Range(-1,-1));
                }
            } else
            {
                //Remove right block
                if ((save.start < remove.start) && (save.end < remove.end)) {
                    range.add(new Range(save.start, remove.start-1));
                } else {
                    // Remove left block
                    if ((save.start > remove.start) && (save.end > remove.end)) {
                        range.add(new Range(remove.end + 1, save.end));
                    } else {
                        // Remove inside block
                        if ((save.start > remove.start) && (save.end < remove.end)) {
                            range.add(new Range(-1, -1));
                        } else {
                            //left save
                            range.add(new Range(save.start, remove.start-1));

                            //right save
                            range.add(new Range(remove.end+1, save.end));
                        }
                    }
                }
            }
        }
        return range;
    }


    int getDistance(){
        return end - start + 1;
    }

    public static int sum(List<Range> ranges){
        int sum = 0;
        for (Range range : ranges){
            sum += range.getDistance();
        }
        return sum;
    }
    Range add(Range range)
    {
        return new Range(start + range.start, end + range.start);
    }

    Range subtraction(Range range){
        return new Range(start - range.start, end - range.start);
    }

    static boolean equalRanges(Range a, Range b){
        return (a.start == b.start) && (a.end == b.end);
    }

    static boolean intersection(Range a, Range b)
    {
        return (a.end >= b.start) && (b.end >= a.start);
    }

    boolean subset(Range range) {
        return (range.start <= start) && (end <= range.end);
    }
}

