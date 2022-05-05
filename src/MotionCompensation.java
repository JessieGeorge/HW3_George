
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
    private int frameWidth, frameHeight; // frame resolution
    private int blockWidth, blockHeight; // block resolution
    private int numBlockInX, numBlockInY; // number of blocks in X/Y direction
    private int searchLimit; // search limit
    private int searchFast;
    private int searchSubPel;

    public MotionCompensation() {
        searcher = new BlockMotionSearch();
    }

    // main interface function
    public int process(final String refName, final String tarName, final String mvName, final String resName, int n,
            int p, int optFast, int optSub) {
    	System.out.println();
        // read reference & target images from PPM files
        MImage refImage = new MImage(refName);
        MImage tarImage = new MImage(tarName);
        // initialize
        final int width = refImage.getW();
        final int height = refImage.getH();
        init(width, height, n, p, optFast, optSub);
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

    // TOFIX - add code to initialize
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
            int residualFrame[][]) {
        int[][] refBlock;
        int[][] tarBlock;
        int[][] resBlock;
        int[] currPos = new int[2];
        int[] bestPos = new int[2];
    }

    // TOFIX - add code to convert image to gray-scale frame
    protected void image2Frame(final MImage image, int frame[][]) {
    }

    // TOFIX - add code to convert gray-scale frame to image
    protected void frame2Image(final int frame[][], MImage image) {
    }

    // TOFIX - add code to normalize residual frame
    protected void normalizeResidual(int resFrame[][]) {
    }

    // TOFIX - add code to get one block from frame
    protected void getBlock(final int frame[][], int block[][], int x, int y) {
    }

    // TOFIX - add code to set one block in frame
    protected void setBlock(int frame[][], final int block[][], int x, int y) {
    }

    // TOFIX - add code to save motion vectors
    protected int saveMotion(final String mvName, final int motion[][][]) {
        return 0;
    }
}
