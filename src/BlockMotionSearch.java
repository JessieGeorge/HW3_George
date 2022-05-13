import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

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

    // full motion search for one target block
    public double fullSearch(final int refFrame[][], final int tarBlock[][], final int startPos[], int bestPos[]) throws IOException {
    	int[][] refBlock = new int[blockHeight][blockWidth];
    	
    	// REMOVETHIS
    	FileWriter myFWriter = new FileWriter("Test-full-search-ref-blocks.txt"); 
    	int countSearchBlock = 0;
    	
    	double minMSD = Double.MAX_VALUE;
    	
    	// Using formula from lec12 pdf, page 11, first slide.
    	for (int y = 0; y < (2 * searchLimH + 1); y++) {
    		for (int x = 0; x < (2 * searchLimW + 1); x++) {
    			int refPosY = startPos[0] - (searchLimH - y);
    			int refPosX = startPos[1] - (searchLimW - x);
    			
    			int subLevel = 0;
    			
    			// REMOVETHIS
    			countSearchBlock++;
    			myFWriter.write("SEARCH BLOCK #" + countSearchBlock + "\n");
    			
    			if(isValidBlockPos(refPosX, refPosY, subLevel)) {
    				myFWriter.write("valid\n"); // REMOVETHIS
    				
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
            		
            		int SSD = getSSD(tarBlock, refBlock);
            		double MSD = SSD / (blockHeight * blockWidth); // mean square difference
            		
            		// best match
            		if (MSD < minMSD) {
            			minMSD = MSD;
            			/* the position of the top left pixel of 
            			 * the best matching block in the reference image
            			 */
            			bestPos[0] = refPosY;
                		bestPos[1] = refPosX;
            		}
    			} else {
    				// REMOVETHIS
    				myFWriter.write("INvalid\n");
    			}
    			
    		}
    	}
    	
    	myFWriter.close(); // REMOVETHIS
        return minMSD;
    }

    // logarithmic motion search for one target block
    public double fastSearch(final int refFrame[][], final int tarBlock[][], final int startPos[], int bestPos[]) throws IOException {

    	boolean havePrevBest = false; // have the best position of the previous round of neighbor comparisons
		boolean useCenter = true; // only the first time. It's reinitialized inside the while loop.
		int dist = searchLimH; // because only need to use one since the limits are equal
		int[][] refBlock = new int[blockHeight][blockWidth];
		double minMSD = Double.MAX_VALUE;
		
		// REMOVETHIS
    	FileWriter myFWriter = new FileWriter("Test-fast-search-ref-blocks.txt");
    	
		while(dist > 0) {
			int countSearchBlock = 0; // REMOVETHIS
			
			if (havePrevBest) {
				// start from the previous best
				startPos[0] = bestPos[0];
				startPos[1] = bestPos[1];
			} 
			
			for (int y = 0; y < 3; y++) {
	    		for (int x = 0; x < 3; x++) {
	    			
	    			if (y == 1 && x == 1 && !useCenter) {
	    				/*
	    				 *  don't consider the center, 
	    				 *  because we already considered it when we
	    				 *  got the best match in the previous round
	    				 */
	    				continue;
	    			}
	    			
	    			int refPosY = startPos[0] - (dist - (y * dist));
	    			int refPosX = startPos[1] - (dist - (x * dist));
	    			
	    			int subLevel = 0;
	    			
	    			// REMOVETHIS
	    			countSearchBlock++;
	    			myFWriter.write("SEARCH BLOCK #" + countSearchBlock + "\n");
	    			
	    			if(isValidBlockPos(refPosX, refPosY, subLevel)) {
	    				myFWriter.write("valid\n"); // REMOVETHIS
	    				
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
	            		
	            		int SSD = getSSD(tarBlock, refBlock);
	            		double MSD = SSD / (blockHeight * blockWidth); // mean square difference
	            		
	            		// best match
	            		if (MSD < minMSD) {
	            			minMSD = MSD;
	            			/* the position of the top left pixel of 
	            			 * the best matching block in the reference image
	            			 */
	            			bestPos[0] = refPosY;
	                		bestPos[1] = refPosX;
	            		}
	    			} else {
	    				// REMOVETHIS
	    				myFWriter.write("INvalid\n");
	    			}
	    			
	    		}
	    	}
			
			useCenter = false;
    		havePrevBest = true;
    		dist = dist / 2;
        }
    	
    	myFWriter.close(); // REMOVETHIS
    	return minMSD;
    }

    // half-pixel motion search for one target block
    public double halfSearch(final int refFrame[][], final int tarBlock[][], final int startPos[], int bestPos[]) throws IOException {
int[][] refBlock = new int[blockHeight][blockWidth];
    	
    	// REMOVETHIS
    	FileWriter myHWriter = new FileWriter("Test-half-search-ref-blocks.txt");
    	myHWriter.write("startPos = " + Arrays.toString(startPos) + "\n");
    	int countSearchBlock = 0;
    	
    	double minMSD = Double.MAX_VALUE;
    	
    	startPos[0] *= 2;
    	startPos[1] *= 2;
    	
    	int dist = 1; // immediate neighbors
    	
    	int subLevel = 1;
    	
    	for (int y = 0; y < 3; y++) {
    		for (int x = 0; x < 3; x++) {
    			
    			if (y == 1 && x == 1) {
    				/*
    				 *  don't consider the center, 
    				 *  because we already considered it when we
    				 *  got the best match in the previous round
    				 */
    				continue;
    			}
    			
    			int refPosY = startPos[0] - (dist - (y * dist));
    			int refPosX = startPos[1] - (dist - (x * dist));
    			
    			// REMOVETHIS
    			countSearchBlock++;
    			myHWriter.write("SEARCH BLOCK #" + countSearchBlock + "\n");
    			myHWriter.write("refPosY = " + refPosY + " and refPosX = " + refPosX + "\n");
    			
    			System.out.println("refPosY = " + refPosY + " and refPosX = " + refPosX + "\n"); // REMOVETHIS
    			
    			if(isValidBlockPos(refPosX, refPosY, subLevel)) {
    				myHWriter.write("valid\n"); // REMOVETHIS
    				
    				getRefBlock(refFrame, refBlock, refPosX, refPosY, subLevel); 
    				
    				// REMOVETHIS
            		for (int j = 0; j < blockHeight; j++) {
            			for (int i = 0; i < blockWidth; i++) {
            				String padded = String.format("%03d", refBlock[j][i]);
            				myHWriter.write(padded + " ");
            			}
            			myHWriter.write("\n");
            		}
            		myHWriter.write("\n");
            		
            		int SSD = getSSD(tarBlock, refBlock);
            		double MSD = SSD / (blockHeight * blockWidth); // mean square difference
            		
            		// best match
            		if (MSD < minMSD) {
            			minMSD = MSD;
            			/* the position of the top left pixel of 
            			 * the best matching block in the reference image
            			 */
            			System.out.println("Inside minMSD conditional, refPosY = " + refPosY + " and refPosX = " + refPosX + "\n"); // REMOVETHIS
            			
            			bestPos[0] = refPosY;
                		bestPos[1] = refPosX;
            		}
    			} else {
    				// REMOVETHIS
    				myHWriter.write("INvalid\n");
    			}
    			
    		}
    	}
    	
    	myHWriter.close(); // REMOVETHIS
    	return minMSD;
    }

    // check validity of block position
    public boolean isValidBlockPos(final int blkPosX, final int blkPosY, final int subLevel) {
    	// fits within the dimension of the reference frame
    	
    	if (subLevel == 1) {
    		// half-pel
            return (blkPosX/2.0 >= 0 && blkPosX/2.0 <= (frameWidth - blockWidth)) 
            		&& (blkPosY/2.0 >= 0 && blkPosY/2.0 <= (frameHeight - blockHeight));
    	} else {
    		// full-pel
    		return (blkPosX >= 0 && blkPosX <= (frameWidth - blockWidth)) 
            		&& (blkPosY >= 0 && blkPosY <= (frameHeight - blockHeight));
    	}
    	
    }

    // get one reference block from frame
    protected void getRefBlock(final int refFrame[][], int refBlock[][], int refPosX, int refPosY, int subLevel) {
    	
    	if (subLevel == 1) {
    		// half-pel
    		
    		// q stands for quotient, m stands for mod
    		int Xq = refPosX / 2;
    		int Xm = refPosX % 2;
    		int Yq = refPosY / 2;
    		int Ym = refPosY % 2;
    		
    		for (int row = 0; row < blockHeight; row++) {
    			for (int col = 0; col < blockWidth; col++) {
    				
    				boolean interpolated = false;
    				
    				if (Xm == 1 && Ym == 0) {
    					if (isValidBlockPos(Xq + col + 1, Yq + row, 0)) {
    						// sending subLevel 0 to isValidBlockPos because we already did conversion for Xq, Yq at their initialization.
    						refBlock[row][col] = (refFrame[Yq + row][Xq + col] + refFrame[Yq + row][Xq + col + 1]) / 2;
    						interpolated = true;
    					}
    				} else if (Xm == 0 && Ym == 1) {
    					if (isValidBlockPos(Xq + col, Yq + row + 1, 0)) {
    						// sending subLevel 0 to isValidBlockPos because we already did conversion for Xq, Yq at their initialization.
    						refBlock[row][col] = (refFrame[Yq + row][Xq + col] + refFrame[Yq + row + 1][Xq + col]) / 2;
    						interpolated = true;
    					}
    				} else if (Xm == 1 && Ym == 1) {
    					if (isValidBlockPos(Xq + col + 1, Yq + row + 1, 0)) {
    						// sending subLevel 0 to isValidBlockPos because we already did conversion for Xq, Yq at their initialization.
    						refBlock[row][col] = (refFrame[Yq + row][Xq + col] 
												+ refFrame[Yq + row][Xq + col + 1]
												+ refFrame[Yq + row + 1][Xq + col] 
												+ refFrame[Yq + row + 1][Xq + col + 1]) / 4;
    						interpolated = true;
    					}
    				} 
    				
    				if (!interpolated || (Xm==0 && Ym==0)){
    					refBlock[row][col] = refFrame[Yq + row][Xq + col];
    				}
    			}
    		}
    		
    	} else {
    		// full-pel
    		
    		for (int row = 0; row < blockHeight; row++) {
    			for (int col = 0; col < blockWidth; col++) {
    				refBlock[row][col] = refFrame[refPosY + row][refPosX + col];
    			}
    		}
    		
    		/* REMOVETHIS?
    		for (int j = refPosY; j < refPosY + blockHeight; j++) {
        		for (int i = refPosX; i < refPosX + blockWidth; i++) {
        			refBlock[j - refPosY][i - refPosX] = refFrame[j][i];
        		}
        	}
        	*/
    	}
    }

    // compute SSD between two blocks
    public int getSSD(final int tarBlock[][], final int refBlock[][]) {
    	int SSD = 0; // sum of the square difference
    	for (int j = 0; j < blockHeight; j++) {
    		for (int i = 0; i < blockWidth; i++) {
    			int diff = tarBlock[j][i] - refBlock[j][i];
    			SSD += (int)Math.pow(diff, 2);
    		}
    	}
    	
        return SSD;
    }
}
