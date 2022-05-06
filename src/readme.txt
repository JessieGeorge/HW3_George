Project 3
Jessie George

CS4551_George.java:
Updated path to work for Windows.
getN and getP methods to get input from user.
conductFastMotionCompensation method also prints number of matches performed.

MotionCompensation.java:
Moved searcher instantiation so I could pass relevant params.
searchCompensate method calls a type of search method based on user's choice of type.

fullSearchCompensate considers immediate neighbors.

fastSearchCompensate considers neighbors at a distance, 
the distance is halved each time and stops after processing dist = 1. 
The target frame is set to the position of the best matching frame from the previous round.

image2Frame method uses grayscale equation from Canvas.
frame2Image self-explanatory.

normalizeResidual method uses scaledError equation from Canvas.

getBlock, setBlock self-explanatory.

saveMotion method: the order of motion vector is y,x.

BlockMotionSearch.java:
Renamed searchLimV to searchLimW to represent search limit width.
Updated constructor to use relevant params.

fullSearch method considers (2*p+1)^2 candidates for one target block.
It checks if the boundary positions of a block are valid. If yes, it gets that
block from the reference frame.
Calculates MSD.
Sets best position based on minimum MSD.

fastSearch method considers 9 (or 8) candidates for one target block.
The rest is similar to fullSearch.

halfSearch method TODO

isValidBlockPos, getRefBlock self-explanatory.

getSSD method gets the sum of square difference as an integer.
This is used to calculate MSD.



