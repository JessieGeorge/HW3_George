
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
import java.util.Arrays; // REMOVETHIS?

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
        
        // REMOVETHIS
        System.out.println("width = " + width);
        System.out.println("frameWidth = " + frameWidth);
        System.out.println("height = " + height);
        System.out.println("frameHeight = " + frameHeight);
        System.out.println("blockWidth = " + blockWidth);
        System.out.println("blockHeight = " + blockHeight);
        System.out.println("numBlockInX = " + numBlockInX);
        System.out.println("numBlockInY = " + numBlockInY);
        System.out.println("searchLimit = " + searchLimit);
        System.out.println("searchFast = " + searchFast);
        System.out.println("searchSubPel = " + searchSubPel);
        return 0;
    }

    // TOFIX - add code to search and compensate one frame
    protected void searchCompensate(final int referenceFrame[][], final int targetFrame[][], int motionVectors[][][],
            int residualFrame[][]) throws IOException {
    	
    	int[][] refBlock = new int[blockHeight][blockWidth];
        int[][] tarBlock = new int[blockHeight][blockWidth];
        int[][] resBlock = new int[blockHeight][blockWidth];
        int[] currPos = new int[2];
        int[] bestPos = new int[2];
        int minError = Integer.MAX_VALUE;
        int maxError = 0;
        
        boolean havePrevBest = false; // Fast Search: have the best position of the previous round of neighbor comparisons
        
        double fullPelMinMSD = Integer.MAX_VALUE; // full-pel result helpful for half-pel 
        
        for (int y = 0, numBlockY = 0; y < frameHeight && numBlockY < numBlockInY; y += blockHeight, numBlockY++) {
        	for (int x = 0, numBlockX = 0; x < frameWidth && numBlockX < numBlockInX; x += blockWidth, numBlockX++) {
        		
        		getBlock(targetFrame, tarBlock, x, y);
        		currPos[0] = y;
        		currPos[1] = x;
        		
		    	if (searchFast == 1) {
		    		boolean useCenter = true; // only the first time. It's reinitialized inside the while loop.
	        		int dist = searchLimit * 2; // because we're dividing after this
	        		int v, u;
	        		while(true) {
	        			
	        			dist = dist / 2;
	            		if (dist == 0) {
	            			break;
	            		}
	            		
	        			if (havePrevBest) {
	        				v = bestPos[0];
	        				u = bestPos[1];
	        				
	        				getBlock(targetFrame, tarBlock, u, v);
		            		currPos[0] = v;
		            		currPos[1] = u;
	        			} // else you already set it above with x and y
	        			
	            		fullPelMinMSD = searcher.fastSearch(referenceFrame, tarBlock, currPos, bestPos, dist, useCenter);
	            		useCenter = false;
	            		havePrevBest = true;
	            		
	            		/*
	            		// REMOVETHIS
	            		mvBWriter.write("BLOCK #" + countBlocks + "\n");
	            		for (int j = 0; j < blockHeight; j++) {
	            			for (int i = 0; i < blockWidth; i++) {
	            				String padded = String.format("%03d", tarBlock[j][i]);
	            				mvBWriter.write(padded + " ");
	            			}
	            			mvBWriter.write("\n");
	            		}
	            		mvBWriter.write("\n");
	            		*/
	            		
	                }
		    	} else {
		    		fullPelMinMSD = searcher.fullSearch(referenceFrame, tarBlock, currPos, bestPos);
		    	}
		    	
		    	if (searchSubPel == 1) {
		    		currPos[0] = bestPos[0];
	        		currPos[1] = bestPos[1];
	        		getBlock(referenceFrame, refBlock, currPos[1], currPos[0]);
	        		double halfPelMinMSD = searcher.halfSearch(referenceFrame, refBlock, currPos, bestPos);
	        		
	        		if (fullPelMinMSD < halfPelMinMSD) {
	        			// stick with the full-pel match
	        			bestPos[0] = currPos[0];
	        			bestPos[1] = currPos[1];
	        		} // else go ahead with the half-pel match i.e bestPos was updated in halfSearch function
		    	}
		    	
		    	// motion vector
        		int dy = y - bestPos[0];
        		int dx = x - bestPos[1];
        		// keeping order of Professor's sample output
        		motionVectors[numBlockY][numBlockX][0] = dx;
        		motionVectors[numBlockY][numBlockX][1] = dy;
        		
        		// residual
        		getBlock(referenceFrame, refBlock, bestPos[1], bestPos[0]);
        		for (int j = 0; j < blockHeight; j++) {
        			for (int i = 0; i < blockWidth; i++) {
        				int err = Math.abs(tarBlock[j][i] - refBlock[j][i]);
        				resBlock[j][i] = err;
        				if (err < minError) {
        					minError = err;
        				} else if (err > maxError) {
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
    	
    	// REMOVETHIS
    	System.out.println("maxError = " + maxError);
    	System.out.println("minError = " + minError);
    	
    	int scaledError = 0;
    	double range = maxError - minError; // double type for division
    	for (int y = 0; y < frameHeight; y++) {
    		for (int x = 0; x < frameWidth; x++) {
    			scaledError = (int)Math.round((resFrame[y][x] - minError) / range * 255);
    			resFrame[y][x] = scaledError; 
    		}
    	}
    	
    	// REMOVETHIS
    	FileWriter myResWriter = new FileWriter("Test-scaled-residual-frame.txt"); 
		for (int y = 0; y < frameHeight; y++) {
			for (int x = 0; x < frameWidth; x++) {
				String padded = String.format("%03d", resFrame[y][x]);
				 myResWriter.write(padded + " ");
			}
			myResWriter.write("\n");
		}
		myResWriter.write("\n");
		myResWriter.close();
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
