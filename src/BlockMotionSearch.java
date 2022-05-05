
/*******************************************************
 * CS4551 Multimedia Software Systems
 * 
 * Summer 2021 Homework #3 Framework
 * 
 * BlockMotionSearch.java
 * 
 * By Yi Zhao 07/19/2020
 *******************************************************/

public class BlockMotionSearch {
    private int frameWidth, frameHeight; // frame resolution
    private int blockWidth, blockHeight; // block resolution
    private int searchLimH, searchLimV; // search limit

    public BlockMotionSearch() {
        setFrameSize(640, 480);
        setBlockSize(8, 8);
        setSearchLimit(8, 8);
    }

    // set frame size (w/h)
    public void setFrameSize(int W, int H) {
        frameWidth = W;
        frameHeight = H;
    }

    // set block size (w/h)
    public void setBlockSize(int BW, int BH) {
        blockWidth = BW;
        blockHeight = BH;
    }

    // set search limit
    public void setSearchLimit(int limH, int limV) {
        searchLimH = limH;
        searchLimV = limV;
    }

    // TOFIX - add code to conduct full motion search for one target block
    public int fullSearch(final int refFrame[][], final int tarBlock[][], final int startPos[], int bestPos[]) {
        return 0;
    }

    // TOFIX - add code to conduct logarithmic motion search for one target block
    public int fastSearch(final int refFrame[][], final int tarBlock[][], final int startPos[], int bestPos[]) {
        return 0;
    }

    // TOFIX - add code to conduct half-pixel motion search for one target block
    public int halfSearch(final int refFrame[][], final int tarBlock[][], final int startPos[], int bestPos[]) {
        return 0;
    }

    // TOFIX - add code to check validity of block position
    public boolean isValidBlockPos(final int blkPosX, final int blkPosY, final int subLevel) {
        return false;
    }

    // TOFIX - add code to get one reference block from frame
    protected void getRefBlock(final int refFrame[][], int refBlock[][], int refPosX, int refPosY, int subLevel) {
    }

    // TOFIX - add code to compute SSD between two blocks
    public int getSSD(final int tarBlock[][], final int refBlock[][]) {
        return 0;
    }
}
