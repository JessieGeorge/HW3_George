Project 3
Jessie George

CS4551_George.java:
Updated path to work for Windows.

I've included extra menu options for half-pel so that
the regular output and extra credit output can be viewed/graded separately.

getN and getP methods to get input from user.

conductFastMotionCompensation method also prints number of matches performed.

------------------------
MotionCompensation.java:
Moved searcher instantiation so I could pass relevant params.

searchCompensate method calls a type of search method based on user's choice of type.
Fast Search / Full Search, Half-pel.

image2Frame method uses grayscale equation from Canvas.
frame2Image self-explanatory.

normalizeResidual method uses scaledError equation from Canvas.

getBlock, setBlock self-explanatory.

saveMotion method: the order of motion vector is x,y as per
sample output.

-----------------------
BlockMotionSearch.java:
Renamed searchLimV to searchLimW to represent search limit width.
Updated constructor to use relevant params.

fullSearch method considers (2*p+1)^2 candidates for one target block.
It calls helper method to check validity of block. 
If yes, it calls helper method to get that block.
Calculates MSD.
Sets best position based on minimum MSD.

fastSearch method considers 8 neighbors at a distance, 
the distance is halved each time and stops distance when dist = 0. 
It considers the center only the first time, for efficiency.
The start position is set to the position of the best matching frame from the previous round.

halfSearch method refines the output. It uses the best match from Full-pel.
It converts coordinates as needed. It checks 8 immediate neighbors.

isValidBlockPos checks if the coordinates of a block are valid within the frame dimensions. 

getRefBlock gets a block from the reference frame.

getSSD method gets the sum of square difference as an integer.
This is used to calculate MSD.



