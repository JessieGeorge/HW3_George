
/*******************************************************
 * CS4551 Multimedia Software Systems
 * 
 * Summer 2021 Homework #3 Framework
 * 
 * MotionCompensation.java
 * 
 * By Yi Zhao 07/19/2021
 *******************************************************/

import java.io.*;

public class MotionCompensation {
    private BlockMotionSearch searcher;
    private int width, height;
    private int frameWidth, frameHeight; // frame resolution
    private int blockWidth, blockHeight; // block resolution
    private int numBlockInX, numBlockInY; // number of blocks in X/Y direction
    private int searchLimit; // search limit
    private int searchFast; // binary boolean
    private int searchSubPel; // binary boolean

    // main interface function
    public int process(final String refName, final String tarName, final String mvName, final String resName, int n,
            int p, int optFast, int optSub) throws IOException {
    	System.out.println();
        // read reference & target images from PPM files
        MImage refImage = new MImage(refName);
        MImage tarImage = new MImage(tarName);
        // initialize
        width = refImage.getW();
        height = refImage.getH();
        init(width, height, n, p, optFast, optSub);
        searcher = new BlockMotionSearch(frameWidth, frameHeight, n, p);
        
        // allocate work space
        int[][] refFrame = new int[frameHeight][frameWidth];
        int[][] tarFrame = new int[frameHeight][frameWidth];
        int[][] resFrame = new int[frameHeight][frameWidth];
        int[][][] motionVectors = new int[numBlockInY][numBlockInX][2];
        // convert images to gray-scale frames
        image2Frame(refImage, refFrame);
        image2Frame(tarImage, tarFrame);
        // conduct motion search and compensation
        searchCompensate(refFrame, tarFrame, motionVectors, resFrame);
        // save motion vectors
        saveMotion(mvName, motionVectors);
        // save residual frame
        MImage resImage = new MImage(width, height);
        frame2Image(resFrame, resImage);
        resImage.write2PPM(resName);
        return 0;
    }

    // initialize
    public int init(int width, int height, int n, int p, int optFast, int optSub) {
    	// padded to be divisible by n
		if (width % n != 0) {
			frameWidth = width + (n - (width % n));
		} else {
			frameWidth = width;
		}
		
		if (height % n != 0) {
			frameHeight = height + (n - (height % n));
		} else {
			frameHeight = height;
		}
    	
        blockWidth = n; 
        blockHeight = n;
        
        numBlockInX = width / n;
        numBlockInY = height / n;
        
        searchLimit = p;
        
        searchFast = optFast;
        searchSubPel = optSub;
        
        return 0;
    }

    // Search and compensate one frame
    protected void searchCompensate(final int referenceFrame[][], final int targetFrame[][], int motionVectors[][][],
            int residualFrame[][]) throws IOException {
    	
    	int[][] refBlock = new int[blockHeight][blockWidth];
        int[][] tarBlock = new int[blockHeight][blockWidth];
        int[][] resBlock = new int[blockHeight][blockWidth];
        int[] currPos = new int[2];
        int[] bestPos = new int[2];
        int minError = Integer.MAX_VALUE;
        int maxError = 0;
        
        // full-pel result helpful for half-pel
        double fullPelMinMSD = Integer.MAX_VALUE;  
        int[] fullPelBestPos = new int[2];
        
        int subLevel = 0;
        
        for (int y = 0, numBlockY = 0; y < frameHeight && numBlockY < numBlockInY; y += blockHeight, numBlockY++) {
        	for (int x = 0, numBlockX = 0; x < frameWidth && numBlockX < numBlockInX; x += blockWidth, numBlockX++) {
        		
        		getBlock(targetFrame, tarBlock, x, y);
        		currPos[0] = y;
        		currPos[1] = x;
        		
		    	if (searchFast == 1) {
		    		fullPelMinMSD = searcher.fastSearch(referenceFrame, tarBlock, currPos, bestPos);
		    	} else {
		    		fullPelMinMSD = searcher.fullSearch(referenceFrame, tarBlock, currPos, bestPos);
		    	}
		    	
		    	if (searchSubPel == 1) {
		    		subLevel = 1;
		    		
		    		fullPelBestPos[0] = bestPos[0];
		    		fullPelBestPos[1] = bestPos[1];
		    		// save a copy that doesn't get modified by halfSearch function
		    		int originalFullPelBestY = bestPos[0];
		    		int originalFullPelBestX = bestPos[1];
	        		double halfPelMinMSD = searcher.halfSearch(referenceFrame, tarBlock, fullPelBestPos, bestPos);
	        		
	        		if (fullPelMinMSD < halfPelMinMSD) {
	        			// stick with the full-pel match
	        			bestPos[0] = originalFullPelBestY;
	        			bestPos[1] = originalFullPelBestX;
	        			subLevel = 0;
	        		} // else go ahead with the half-pel match i.e bestPos was updated in halfSearch function
	        		
		    	}
		    	
		    	// motion vector
		    	/* If it's half-pel, subLevel=1,
		    	 * and left shift by 1 basically multiplies target coords by 2
		    	 * to match bestPos because we doubled coords in the halfSearch function.
		    	 * Coding this as per Professor's instructions.
		    	 */
        		int dy = (y << subLevel) - bestPos[0];
        		int dx = (x << subLevel) - bestPos[1];
        		// keeping order of Professor's sample output
        		motionVectors[numBlockY][numBlockX][0] = dx;
        		motionVectors[numBlockY][numBlockX][1] = dy;
        		
        		// residual
        		if (subLevel == 1) {
        			// gotta convert bestPos so that getBlock doesn't go out of bounds for half-pel
        			bestPos[1] = (int)Math.round(bestPos[1]/2.0);
        			bestPos[0] = (int)Math.round(bestPos[0]/2.0);
        		}
        		getBlock(referenceFrame, refBlock, bestPos[1], bestPos[0]);
        		for (int j = 0; j < blockHeight; j++) {
        			for (int i = 0; i < blockWidth; i++) {
        				int err = Math.abs(tarBlock[j][i] - refBlock[j][i]);
        				resBlock[j][i] = err;
        				if (err < minError) {
        					minError = err;
        				} 
        				
        				if (err > maxError) {
        					maxError = err;
        				}
        			}
        		}
        		
        		setBlock(residualFrame, resBlock, x, y);
        	}
        }
        
        normalizeResidual(residualFrame, minError, maxError);
    }
    
    // Convert image to gray-scale frame
    protected void image2Frame(final MImage image, int frame[][]) {
    	
    	int rgb[] = new int[3];
    	for (int y = 0; y < height; y++) {
    		for (int x = 0; x < width; x++) {
    			image.getPixel(x, y, rgb);
    			frame[y][x] = (int)(Math.round(0.299 * rgb[0] 
    							+ 0.587 * rgb[1] 
    							+ 0.114 * rgb[2]));
    		}
    	}
    }

    // Convert gray-scale frame to image
    protected void frame2Image(final int frame[][], MImage image) {
    	
    	int rgb[] = new int[3];
    	for (int y = 0; y < frameHeight; y++) {
    		for (int x = 0; x < frameWidth; x++) {
    			rgb[0] = frame[y][x];
    			rgb[1] = frame[y][x];
    			rgb[2] = frame[y][x];
    			image.setPixel(x, y, rgb);
    		}
    	}
    }

    // Normalize residual frame
    protected void normalizeResidual(int resFrame[][], int minError, int maxError) throws IOException {
    	
    	int scaledError = 0;
    	double range = maxError - minError; // double type for division
    	for (int y = 0; y < frameHeight; y++) {
    		for (int x = 0; x < frameWidth; x++) {
    			scaledError = (int)Math.round((resFrame[y][x] - minError) / range * 255);
    			resFrame[y][x] = scaledError; 
    		}
    	}
    }

    // Get one block from frame
    protected void getBlock(final int frame[][], int block[][], int x, int y) {
    	
    	for (int j = y; j < y + blockHeight; j++) {
    		for (int i = x; i < x + blockWidth; i++) {
    			block[j - y][i - x] = frame[j][i];
    		}
    	}
    }

    // Set one block in frame
    protected void setBlock(int frame[][], final int block[][], int x, int y) {
    	
    	for (int j = y; j < y + blockHeight; j++) {
    		for (int i = x; i < x + blockWidth; i++) {
    			frame[j][i] = block[j - y][i - x];
    		}
    	}
    }

    // save motion vectors
    protected int saveMotion(final String mvName, final int motion[][][]) {
    	try {
    		File f = new File(mvName);
    		if (f.createNewFile()) {
    			System.out.println("Created file: " + mvName);
    		}
	    } catch (IOException e) {
	    	System.out.println("An error occurred.");
	    	e.printStackTrace();
	    }
    	
    	try {
    		FileWriter myWriter = new FileWriter(mvName);
    		// keeping format of Professor's sample output
    		for (int numBlockY = 0; numBlockY < numBlockInY; numBlockY++) {
    			for (int numBlockX = 0; numBlockX < numBlockInX; numBlockX++) {
    				myWriter.write("(" + motion[numBlockY][numBlockX][0] + "," + motion[numBlockY][numBlockX][1] + ") ");
    			}
    			myWriter.write("\n");
    		}
    		
    		myWriter.close();
    		System.out.println("Wrote motion vectors to " + mvName);
	    } catch (IOException e) {
	    	System.out.println("An error occurred.");
	    	e.printStackTrace();
	    }
    	
        return 0;
    }
}
