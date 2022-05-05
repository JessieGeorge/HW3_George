
/*******************************************************
 * CS4551 Multimedia Software Systems @ Author: Elaine Kang
 * 
 * Spring 2020 Homework #3 Framework by Yi Zhao
 *******************************************************/

import java.util.Scanner;

public class CS4551_George {
    public static final String videoFrameName = "../VideoFrames/Walk_%03d.ppm";
    
    public static Scanner in = new Scanner(System.in);
    
    public static void main(String[] args) {

        // the program does not expect command line argument
        // if there is any command line argument, exit the program
        if (args.length != 0) {
            usage();
            System.exit(1);
        }

        // define main variables
        boolean done = false;
        
        // main menu
        while (!done) {
            int choice = menu();
            switch (choice) {
            case 1:
                conductBlockMotionCompensation(videoFrameName, in);
                break;
            case 2:
                conductFastMotionCompensation(videoFrameName, in);
                break;
            case 3:
                done = true;
                break;
            default:
                System.out.println("Invalid command!");

            }
        }
    }

    public static void usage() {
        System.out.println("\nUsage: java CS4551_George\n");
    }

    public static int menu() {
        int choice = 0;
        
        // main menu display
        String message = "\nMain Menu-----------------------------------\n"
        		+ "1. Block-based Motion Compensation\n"
        		+ "2. Fast Motion Compensation\n"
        		+ "3. Quit\n"
        		+ "Please enter the task number [1-3]:";
        
        System.out.println(message);
        
        try {
            choice = in.nextInt();
            // myInput.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return choice;
    }
    
    public static int getN() {
        int n = 8; // default
        String message = "\nEnter a value for n, the macro block size "
        		+ "[must be 8, 16, or 24]:";
        System.out.println(message);
        
        try {
            n = in.nextInt(); // Assuming valid input
            // myInput.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return n;
    }
    
    public static int getP() {
        int p = 8; // default
        String message = "\nEnter a value for p, the search window "
        		+ "[must be 2, 4, 8, 12, or 16]:";
        System.out.println(message);
        
        try {
            p = in.nextInt(); // Assuming valid input
            // myInput.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return p;
    }

    public static int conductBlockMotionCompensation(String vidName, Scanner in) {
        // get parameter input from user
        int n = getN();
        int p = getP();
        int fast = 0;
        int sub = 0;
        int start = 50;
        int count = 10;
        MotionCompensation coder = new MotionCompensation();
        for (int curNo = start; curNo < start + count; curNo++) {
            String refName = String.format(vidName, curNo - 1);
            String tarName = String.format(vidName, curNo);
            String mvName = String.format("mv_%03d.txt", curNo);
            String resName = String.format("res_%03d.ppm", curNo);
            coder.process(refName, tarName, mvName, resName, n, p, fast, sub);
        }
        return 0;
    }

    public static int conductFastMotionCompensation(String vidName, Scanner in) {
    	// get parameter input from user
        int n = getN();
        int p = getP();
        int fast = 1;
        int sub = 0;
        int start = 50;
        int count = 10;
        MotionCompensation coder = new MotionCompensation();
        for (int curNo = start; curNo < start + count; curNo++) {
            String refName = String.format(vidName, curNo - 1);
            String tarName = String.format(vidName, curNo);
            String mvName = String.format("mv_%03d.txt", curNo);
            String resName = String.format("res_%03d.ppm", curNo);
            coder.process(refName, tarName, mvName, resName, n, p, fast, sub);
        }
        return 0;
    }
}
