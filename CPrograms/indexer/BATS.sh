#!/bin/bash 
make clean
make indexer

DATA_PATH=/net/class/cs50/tse/crawler/     #where the crawler data is
INDEX_FILE=index.dat

echo "Testing Indexer. This log is the output of BATS.sh" > log.txt

# date and time stamp of the beginning of the run
START=`date`
echo "Started at: $START" >> log.txt

# program hostname
echo "Program run on:" >> log.txt
uname >> log.txt
hostname >> log.txt

# test indexer with incorrect arguments
# improper number of arguments given
./indexer >> log.txt

./indexer $DATA_PATH/lvl0/ index.dat index.dat >> log.txt

./indexer $DATA_PATH/lvl0/ index.dat index.dat new_index.dat new_index.dat >> log.txt

# invalid directory
./indexer invalid_dir index.dat >> log.txt

# not including the last "/"
./indexer $DATA_PATH/lvl0 index.dat >> log.txt



# check to see if the testing function of indexer works.
compare_index() {
# you can use sort to order the index files to look at them else they remain unordered
echo ”Indexes have been built, read and rewritten correctly!” >> log.txt
sort -u index.dat >> /dev/null
sort -u new_index.dat >> /dev/null
DIFF=`diff index.dat new_index.dat` # check return value of diff
if [[ $DIFF -eq 0 ]];then
	echo ”Index storage passed test!” >> log.txt
else 
	echo ”Index storage didn’t pass test!” >> log.txt
fi
}

# We index a directory, recorded it into a file (index.dat) and sort.
# Then we read index.dat to memory and write it back to see
# whether program can read in and write out index storage file correctly.

# Test at level 0
echo "Test at level 0" >> log.txt
./indexer $DATA_PATH/lvl0/ $INDEX_FILE $INDEX_FILE new_index.dat >> log.txt
compare_index

# Test at level 1
echo "Test at level 1" >> log.txt
./indexer $DATA_PATH/lvl1/ $INDEX_FILE $INDEX_FILE new_index.dat >> log.txt
compare_index

# Test at level 2
echo "Test at level 2" >> log.txt
./indexer $DATA_PATH/lvl2/ $INDEX_FILE $INDEX_FILE new_index.dat >> log.txt
compare_index

# Test at level 3
echo "Test at level 3" >> log.txt
./indexer $DATA_PATH/lvl3/ $INDEX_FILE $INDEX_FILE new_index.dat >> log.txt
compare_index

END=`date`
echo "Test ended on: $END" >> log.txt
echo "BATS Tests completed. See log.txt for more information."
