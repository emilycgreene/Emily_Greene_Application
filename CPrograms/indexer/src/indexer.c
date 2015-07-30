
/* ========================================================================== */
/* File: indexer.c - Tiny Search Engine web crawler
 *
 * Author: Emily Greene
 * Date: 5/7/14
 *
 * Input: 
 * Regular mode: Directory to build the index from; document to print the index to
 * Testing mode: 2 additional arguments with the file to read the index in from and the
 * file to reprint the index to
 *
 * Command line options: regular mode or testing mode
 *
 * Output: a file with the word, how many files it was found in, and each file id and 
 * the frequency of the word in each of those files
 *
 * Error Conditions: improper arguments
 *
 * Special Considerations: the directory must contain documents with numerical ids
 *
 */
/* ========================================================================== */
// ---------------- Open Issues

// ---------------- System includes e.g., <stdio.h>
#include <stdio.h>                           // printf
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <curl/curl.h>                       // curl functionality
#include <string.h>
// ---------------- Local includes  e.g., "file.h"
#include "common.h"                          // common functionality
#include "web.h"                             // curl and html functionality
#include "list.h"                            // webpage list functionality
#include "hashtable.h"                       // hashtable functionality
#include "utils.h"                           // utility stuffs
#include "file.h"

// ---------------- Constant definitions
int testing = 0; // boolean value to check whether you are in testing mode
int pos = 0;
// ---------------- Macro definitions

// ---------------- Structures/Types

// ---------------- Private variables

// ---------------- Private prototypes

/* ========================================================================== */

/**
 * Function to build a hashtable with the words and the documents they appear in 
 */
HashTable * buildIndexFromDirectory(char *dirPath) {
	// initialize local variables
	FILE *fp;
	char **filenames = NULL;
 	int num_files = 0;

	// initially allocate the hashtable to 0
	HashTable *Index = malloc(sizeof(HashTable));
	memset(Index,0,MAX_HASH_SLOT);

	// find the filenames in the directory
 	num_files = GetFilenamesInDir(dirPath, &filenames);

	// check to ensure you have files to use
 	if(num_files < 0) {
 	 // failed to get any filenames (various reasons)
		printf("failed to get the files\n");
		exit(1); 
	} 
	else {
		int i;
 		// iterate through the files
		for(i = 0; i < num_files; i++) {
			// start at the beginning of the document
 			char *word;
		
			// allocate memory for a string containing the path to the documents (based on the directory passed to indexer
			char *docPath = calloc(strlen(dirPath) + strlen(filenames[i]) + 1, sizeof(char));
			strcat(docPath,dirPath);
			strcat(docPath,filenames[i]);

			// try to open the file
			if ((fp = fopen(docPath, "r")) == NULL) {
				printf("Could not open file\n");
				exit(1);
			}

			// find the length of the file
			fseek(fp,0,SEEK_END);
			int fileLen = ftell(fp);
			rewind(fp);
			
			// allocate a string to the length of the file
			char *doc = calloc(fileLen+1,sizeof(char));

			// read text from the file into the string
			if (fread(doc,sizeof(char),fileLen,fp) == 0) {
				printf("Invalid index file\n");
				exit(1);
			}

	// make sure that you start at the beginning of the html, don't include first two lines		
			int c;
			int line = 0;
			for (c = 0; c < fileLen; c++) {
				if (line == 2) {
					break;
				}
				else {
					if (doc[c] == '\n') {
						line++;
					}
				}
			}
			
			pos = c;
			// iterate through the document, getting each word and adding to the hashtable
			while((pos = GetNextWord(doc, pos, &word)) > 0) {
				char * copyWord = calloc(strlen(word) +1, sizeof(char));
				MALLOC_CHECK(stdout,copyWord);
				memcpy(copyWord,word,strlen(word));
				free(word);

				if (strlen(copyWord) > 3 ) {
					add(Index,copyWord,i+1);
 				}
			}
			// close the file and free resources
			fclose(fp);
			free(docPath);
		}
	}
	return Index;
}

HashTable * readFile(char *filename) {
	FILE *filep;

	// initialize hashtable
	HashTable *newIndex = malloc(sizeof(HashTable));
	memset(newIndex,0,MAX_HASH_SLOT);
	if ((filep = fopen(filename, "r")) == NULL) {
		printf("Could not open file\n");
		exit(1);
	}
	fseek(filep,0,SEEK_END);
	int fileLength = ftell(filep);
	rewind(filep);
	char *endptr = calloc(fileLength+1,sizeof(char));
	if (fread(endptr,sizeof(char),fileLength,filep) == 0) {
		printf("Invalid index file\n");
		exit(1);
	}
	char* frontptr = endptr;
	int findex = 0;
	int j;
	// iterate through all of the characters in the file
	for (j = 0; j < fileLength; j++) {
		// if you hit a space, you finished the first word
		if (endptr[j] ==  ' ') {
			// find the length of the word, calloc a string and store the word
			int length = j - (findex);
			char *word = calloc(length + 1, sizeof(char));
			strncpy(word,frontptr,length);
		
			// update the pointers
			j++;
			findex = j;
			frontptr = (endptr +findex);
		
			// iterate looking for the first number (number of docs)
			while (endptr[j] != ' ') {
				j++;
			}
		
			// find the length of the number and calloc a string to store it in
			int numlen = j - findex;
			char *num = calloc(numlen + 1, sizeof(char));
			strncpy(num,frontptr,numlen);
			
			// convert the string into a number
			int numberDocs = atoi(num);
			
			// update the pointers
			if (endptr[j] != '\n') {
				j++;
			}
			findex = j;
			frontptr = (endptr +findex);
			
			// create an array to store the doc ids and their corresponding frequencies
			int k;
			int intArray[2*numberDocs];

			// iterate through the doc ids and frequencies
			for (k = 0; k < 2*numberDocs; k++) {

				// look for the next number
				while (endptr[j] !=  ' ') {
					j++;
				}

				// find the length of the number and calloc a string
				int nameLen = j - findex;
				char *nameDoc = calloc(nameLen + 1, sizeof(char));
				strncpy(nameDoc,frontptr,nameLen);

				// convert the string into a number and store it in the array
				int intDoc = atoi(nameDoc);
				intArray[k] = intDoc;
			
				// update the pointers
				j++;
				findex = j;
				frontptr = (endptr +findex);
			}
			findex++;
			frontptr = (endptr +findex);
			// iterate through the array
			int l;
			for (l = 0; l < 2*numberDocs; l++) {
				int m;
				// treat the array as pairs of numbers
				// take the second number in the pair (freq) and add to the hashtable that number of times
				for (m = 0; m < intArray[l+1]; m++) {
					add(newIndex,word,intArray[l]);
				}
				// increment counter through the array to treat numbers as pairs
				l++;
			} 
		} 
	}
	// return the hashtable created from the file
	return newIndex;				
}

int saveFile(char *filename, HashTable *invIndex) {
	// initialize temporary  pointers
	FILE *file;
	WordNode *node_ptr;
	DocumentNode *docTemp;

	// try to open file
	if ((file = fopen(filename,"w")) == NULL) {
		printf("Unable to open file\n");
		exit(1);
	}
	
	// iterate through the hashtable
	int ind;
	for (ind = 0; ind < MAX_HASH_SLOT; ind++) {

		// do nothing if the hash slot is empty
		if (invIndex->table[ind] == NULL) {}
		else {
			// set the node pointer to the first WordNode in the hash slot
			node_ptr = invIndex->table[ind];
			do {
				// add the word and doc freq to the file
				fprintf(file,"%s ",node_ptr->word);
				fprintf(file,"%d ",node_ptr->numDocs);
				fflush(file);

				// iterate through the docs associated with the word
				docTemp = node_ptr->page;
				int index;
				for (index = 0; index < node_ptr->numDocs; index++) {
					
					// add the doc id and the word freq to the file
					fprintf(file,"%d ",docTemp->doc_id);
					fprintf(file,"%d ",docTemp->freq);
					fflush(file);
					
					docTemp = docTemp->next;
					} 
						
				fprintf(file,"\n");

				node_ptr = node_ptr->next;
			} while (node_ptr != NULL);
		}
	}
	fclose(file);
	return 1;
}



int main(int argc, char* argv[]) {
//Program parameter processing
// check arguments
	if (argc != 3) {

		// check to see if it's in testing mode
		if (argc == 5) {
			testing = 1;
		}
		else {
			printf("Error, incorrect number of arguments\n");
			exit(1);
		}
	}

	// check validity of the directory
	if (IsDir(argv[1]) == 0) {
		printf("Error, invalid directory supplied.\n");
		exit(1);
	}

// Initialize data structures
    // allocate Inverted_index, zero it, and set links to NULL.
	HashTable *Inverted_index = malloc(sizeof(HashTable));
	memset(Inverted_index,0,MAX_HASH_SLOT);

	printf("Building the index\n");

	// build the hashtable according to the given directory
	Inverted_index = buildIndexFromDirectory(argv[1]);
	printf("Saving file\n");
	saveFile(argv[2], Inverted_index);
  	printf( "done!\n");
   

// if testing then proceed

	if (testing == 1) {

		printf("Testing index\n");
	
		// Reload the index from the file and rewrite it to a new file
		HashTable *wordindex = malloc(sizeof(HashTable));
		memset(wordindex,0,MAX_HASH_SLOT);

		wordindex = readFile(argv[3]);
		
		saveFile(argv[4], wordindex);
		printf("test complete\n");


		free(wordindex);
	}
// Done
	free(Inverted_index);
	return 0;
}
