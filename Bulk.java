import java.util.ArrayList;
import java.util.List;

/**
 * Data structure.
 * Responsible to create chunks in his responsibility range.
 */
class Bulk {
    private static int count = 0;
    List<Chunk> chunks = new ArrayList<Chunk>();
    int idx;
    Range _range;

    Bulk(Range range){
        idx = count++;
        _range = range;

        int chunkAmount = Math.round((_range.getDistance()) / Chunk.SIZE) + 1;
        for(int i=0 ; i < chunkAmount ; i++){
            int start = i*Chunk.SIZE;
            int end = Math.min((start + Chunk.SIZE - 1), (_range.getDistance() - 1));
            chunks.add(new Chunk(idx, new Range(start, end)));
        }
    }

    int NumberOfBitsAlreadyDownloaded()
    {
        int count = 0;
        for (Chunk chunk : chunks){
            if(!chunk._saved)
            {
                count += chunk._range.getDistance();
            }
        }
        return _range.getDistance() - count;
    }
}
