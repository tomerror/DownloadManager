/**
 * The smallest data structure.
 */
class Chunk {
    static final int SIZE = 40960;
    static int count = 0;
    static int done = 0;

    private int idx;
    Range _range;
    byte[] _data;
    boolean _saved;
    int _bulkId;

    Chunk(int bulkId, Range range){
        idx = count++;
        _bulkId = bulkId;
        _range = range;
        _data = new byte[_range.getDistance()];
        _saved = false;
    }

    void Downloaded(){
        _saved = true;
        done += 1;
    }
}
