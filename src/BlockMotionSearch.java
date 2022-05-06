import java.io.FileWriter;
import java.io.IOException;

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
    private int searchLimW, searchLimH; // search limit

    public BlockMotionSearch(int frameWidth, int frameHeight, int n, int p) {
        setFrameSize(frameWidth, frameHeight);
        setBlockSize(n, n);
        setSearchLimit(p, p);
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
    public void setSearchLimit(int limW, int limH) {
    	searchLimW = limW;
        searchLimH = limH;
    }

    // TOFIX - add code to conduct full motion search for one target block
    public int fullSearch(final int refFrame[][], final int tarBlock[][], final int startPos[], int bestPos[]) throws IOException {
    	int[][] refBlock = new int[blockHeight][blockWidth];
    	
    	FileWriter myFWriter = new FileWriter("Test-full-search-ref-blocks.txt"); 
    	int countSearchBlock = 0;
    	
    	// Using formula from lec12 pdf, page 11, first slide.
    	for (int y = 0; y < (2 * searchLimH + 1); y++) {
    		for (int x = 0; x < (2 * searchLimW + 1); x++) {
    			int refPosY = startPos[0] - (searchLimH - y);
    			int refPosX = startPos[1] - (searchLimW - x);
    			
    			// TODO: what is sublevel for full search?
    			int subLevel = 0;
    			
    			boolean topLeft = isValidBlockPos(refPosX, refPosY, subLevel);
    			boolean topRight = isValidBlockPos(refPosX + blockWidth - 1, refPosY, subLevel);
    			boolean bottomLeft = isValidBlockPos(refPosX, refPosY + blockHeight - 1, subLevel);
    			boolean bottomRight = isValidBlockPos(refPosX + blockWidth - 1, refPosY + blockHeight - 1, subLevel);
    					
    			boolean isValidBlockBoundary = topLeft && topRight 
    											&& bottomLeft && bottomRight;
    			
    			countSearchBlock++;
    			myFWriter.write("SEARCH BLOCK #" + countSearchBlock + "\n");
    			if(isValidBlockBoundary) {
    				myFWriter.write("valid\n");
    				getRefBlock(refFrame, refBlock, refPosX, refPosY, subLevel); 
    				
    				// REMOVETHIS
            		for (int j = 0; j < blockHeight; j++) {
            			for (int i = 0; i < blockWidth; i++) {
            				String padded = String.format("%03d", refBlock[j][i]);
            				myFWriter.write(padded + " ");
            			}
            			myFWriter.write("\n");
            		}
            		myFWriter.write("\n");
            		
    			} else {
    				// REMOVETHIS
    				myFWriter.write("INvalid\n");
    			}
    			
    		}
    	}
    	
    	myFWriter.close();
        System.exit(1); // REMOVETHIS
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

    // check validity of block position
    public boolean isValidBlockPos(final int blkPosX, final int blkPosY, final int subLevel) {
    	// fits within the dimension of the reference frame
        return (blkPosX >= 0 && blkPosX < frameWidth) 
        		&& (blkPosY >= 0 && blkPosY < frameHeight);
    }

    // TOFIX - add code to get one reference block from frame
    protected void getRefBlock(final int refFrame[][], int refBlock[][], int refPosX, int refPosY, int subLevel) {
    	for (int j = refPosY; j < refPosY + blockHeight; j++) {
    		for (int i = refPosX; i < refPosX + blockWidth; i++) {
    			refBlock[j - refPosY][i - refPosX] = refFrame[j][i];
    		}
    	}
    }

    // TOFIX - add code to compute MSD between two blocks
    public int getMSD(final int tarBlock[][], final int refBlock[][]) {
        return 0;
    }
}
