#!/bin/bash
#Insert header here

DATE=`date +"%a_%b_%d_%T_%Y"`
echo "Running CrawlerTest.  View log for results."
echo "CrawlerTest.sh Results" >> CrawlerTestLog.$DATE

make all
mkdir test1
mkdir test2
mkdir test3

# check for incorrect number of arguments
echo "Output with the following incorrect arguments:" >> CrawlerTestLog.$DATE
printf "\n" >> CrawlerTestLog.$DATE

# run with no arguments provided
echo "Output with no arguments:" >> CrawlerTestLog.$DATE
crawler >> CrawlerTestLog.$DATE
printf "\n\n" >> CrawlerTestLog.$DATE

# run with 1 arguments provided
echo "Output with one argument:" >> CrawlerTestLog.$DATE
crawler http://old-www.cs.dartmouth.edu/~cs50/tse/ >> CrawlerTestLog.$DATE
printf "\n\n" >> CrawlerTestLog.$DATE

# run with 2 arguments provided
echo "Output with two arguments:" >> CrawlerTestLog.$DATE
crawler http://old-www.cs.dartmouth.edu/~cs50/tse/ test1 >> CrawlerTestLog.$DATE
printf "\n\n" >> CrawlerTestLog.$DATE

# run with invalid directory provided
echo "Output with invalid directory:" >> CrawlerTestLog.$DATE
crawler http://old-www.cs.dartmouth.edu/~cs50/tse/ fakedir 2 >> CrawlerTestLog.$DATE
printf "\n\n" >> CrawlerTestLog.$DATE

# run with invalid url provided
echo "Output with invalid url:" >> CrawlerTestLog.$DATE
crawler invalidurl test 2 >> CrawlerTestLog.$DATE
printf "\n\n" >> CrawlerTestLog.$DATE

# run with invalid maxDepth provided
echo "Output with invalid maxDepth:" >> CrawlerTestLog.$DATE
crawler http://old-www.cs.dartmouth.edu/~cs50/tse/ test1 number >> CrawlerTestLog.$DATE
printf "\n\n" >> CrawlerTestLog.$DATE

# check that it runs properly at different depths
echo "Return status with the following correct arguments:" >> CrawlerTestLog.$DATE
printf "\n" >> CrawlerTestLog.$DATE

# run with depth of 1
echo "Return status at depth of 1:" >> CrawlerTestLog.$DATE
crawler http://old-www.cs.dartmouth.edu/~cs50/tse/ test1 1
echo "$?" >> CrawlerTestLog.$DATE
echo "Finished crawling at depth of 1. Results are in the test1 directory"
printf "\n\n" >> CrawlerTestLog.$DATE


# run with depth of 2
echo "Return status at depth of 2:" >> CrawlerTestLog.$DATE
crawler http://old-www.cs.dartmouth.edu/~cs50/tse/ test2 2
echo "$?" >> CrawlerTestLog.$DATE
echo "Finished crawling at depth of 2. Results are in the test2 directory"
printf "\n\n" >> CrawlerTestLog.$DATE

# run with depth of 3
echo "Return status at depth of 3:" >> CrawlerTestLog.$DATE
crawler http://old-www.cs.dartmouth.edu/~cs50/tse/ test3 3
echo "$?" >> CrawlerTestLog.$DATE
echo "Finished crawling at depth of 3. Results are in the test3 directory"

rm -r test1
rm -r test2
rm -r test3
make clean
