
/* ========================================================================== */
/* File: query.c - Tiny Search Engine 
 *
 * Author: Emily Greene
 * Date: 5/14/14
 *
 * Input: .dat file created by indexer, directory of files created by crawler
 *
 * Command line options: when prompted with "QUERY:" users can enter any number
 *  of search terms up to 1000 total characters along with OR and AND operators
 *
 * Output: the doc ids containing the HTML and the URLs of the websites 
 * containing the search terms in order of relevance (most relevant on top)
 *
 * Error Conditions: improper arguments
 *
 * Special Considerations: the .dat file must be formatted correctly and the 
 * directory must contain files with numerical names.  The .dat file and the 
 * directory must be related from the same crawl of pages
 *
 */
/* ========================================================================== */
// ---------------- Open Issues

// ---------------- System includes e.g., <stdio.h>
#include <stdio.h>                           // printf
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
// ---------------- Local includes  e.g., "file.h"
#include "common.h"                          // common functionality
#include "queue.h"                            // priority queue functionality
#include "hashtable.h"                       // hashtable functionality
#include "utils.h"                           // utility stuffs
#include "file.h"
// ---------------- Constant/Macro definitions
int added; // boolean to test to see if the doc has been added to the priority queue
int exists; // boolean to test whether the word is in 
int matched; // boolean to test whether the doc appears in both words
FILE *fp; // open crawler file
int sizeDocList = 0; // how many docs word A is in

// pointers for user inputted string
char *strptr;
int location = 0;

// string components for strcat
char* docLen;
char* docPath; 

// pointers for iterating/adding/freeing priority queue
Queue *priority;
QueueNode *queueptr;
QueueNode *temp;
QueueNode *pointer;

// pointers to iterate through hashtable
HashTable *table;
unsigned long hash;
WordNode *wordptr;
DocumentNode *docptr;

DocumentNode **docids; // array of documents that the words are in



// ---------------- Structures/Types

// ---------------- Private variables

// ---------------- Private prototypes

HashTable * readFile(char *filename) {
	// local variables
	FILE *filep; // file to read in from
	HashTable *newIndex; // hashtable to add words to

	// pointers for the contents of the file
	char *frontptr;
	char *endptr;
	int findex = 0;
	int j;

	// the number of the document
	int numberDocs;
	
	
	// variables for iterating/for-loops
	int k;
	int l;
	int m;
	
	// initialize hashtable
	newIndex = malloc(sizeof(HashTable));
	memset(newIndex,0,MAX_HASH_SLOT);
	if ((filep = fopen(filename, "r")) == NULL) {
		printf("Could not open file\n");
		exit(1);
	}
	// find the length of the file
	fseek(filep,0,SEEK_END);
	int fileLength = ftell(filep);
	rewind(filep);
	endptr = calloc(fileLength+1,sizeof(char));

	// read in the file and return an error if invalid
	if (fread(endptr,sizeof(char),fileLength,filep) == 0) {
		printf("Invalid index file\n");
		exit(1);
	}

	frontptr = endptr;

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
			numberDocs = atoi(num);
			free(num);
		
			// create an array to store the doc ids and their corresponding frequencies
			int intArray[2*numberDocs];
		
			// update the pointers
			if (endptr[j] != '\n') {
				j++;
			}
			findex = j;
			frontptr = (endptr +findex);
			
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
				free(nameDoc);		
			
				// update the pointers
				j++;
				findex = j;
				frontptr = (endptr +findex);
			}
			findex++;
			frontptr = (endptr +findex);

			// iterate through the array
			for (l = 0; l < 2*numberDocs; l++) {

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
	// free resources
	free(endptr);
	fclose(filep);

	// return the hashtable created from the file
	return newIndex;				
}


int main(int argc, char* argv[]) {
	FILE *file; // file to read in from (index.dat)
	
	// buffers
	char read[200];
	char input[1000];
	char readin[200];
	char readWord[200];
	
	// check arguments
	
	// check number of arguments
	if (argc != 3) {
		printf("Incorrect number of arguments\n");
		exit(1);
	}
	
	// check directory
	if (IsDir(argv[2]) == 0) {
		printf("Error, invalid directory supplied.\n");
		exit(1);
	}

	// ensure file can be opened/read
	if ((file = fopen(argv[1],"r")) == NULL) {
		printf("Unable to open file\n");
		exit(1);
	}

	// initialize hash table
	table = readFile(argv[1]);
	docids = calloc(1000000,sizeof(DocumentNode*));
	
	// close file
	fclose(file);

	do {	
		// prompt for user input
		priority = createQueue();
		printf("QUERY:");
	
		// buffer to read in the user input
		if (fgets(input,1000,stdin) == NULL) {
			break;
		}
	
		// find the length of the input
		int inputlen = strlen(input);
		strptr = input;	

		// read in first word
		sscanf(strptr,"%s",readin);
		char *word = readin;

		// increment pointers
		strptr += strlen(word) +1;
		location += strlen(word) +1;
	
		// ensure that that word isn't null, then hash it into
		// the hashtable and find its corresponding WordNode
		if (word != NULL) {
			hash = JenkinsHash(word,MAX_HASH_SLOT); 
			wordptr = table->table[hash];
			exists = 0;
			
			// iterate through words to find given word
			while (wordptr != NULL) {
				if (strcmp(word,wordptr->word) == 0) {
					int ind = 0;
					docptr = wordptr->page;
					
					// iterate through docs corresponding
					// to the word
					while (docptr != NULL) {	
						(docids)[ind] = docptr;
						ind++;
						docptr=docptr->next;
					}
					
					// the list of docs will be the amount 
					// of docs found + 1
					sizeDocList = ind + 1;

				// break out of the loop if the word is found
				exists = 1;
				break;
				}

				wordptr=wordptr->next;
			}
		}

		// if the word doesn't exist, the array has NULL in it
		if (exists == 0) {
			(docids)[0] = NULL;
		}

		// iterate through the rest of the words
		while (location < inputlen) {

			// read in the next word
			sscanf(strptr,"%s",read);
			word = read;

			// increment pointers
			strptr += strlen(word) +1;
			location += strlen(word) + 1;
		
			// if there's an AND operator, treat it as if there was just 
			// the implicit operator
			if (strcmp(word,"AND") == 0) {
				continue;
			}

			// if there's an OR operator, add the current docs in the array
			// to the priority queue, clear the array, and start over
			else if (strcmp(word,"OR") == 0) {
				int j;
				for (j=0;j<sizeDocList;j++) {
	
					// if there's nothing in the index, continue
					if ((docids)[j] == NULL) {
						continue;
					}	
					else {
						// iterate through the priority queue to
						// see if the doc is already there
						added = 0;
						for (pointer=priority->head; pointer != NULL; pointer=pointer->next) {
					
							// if it's already there, add the frequencies together, remove the old node, and
							// add in an updated node
							if((docids)[j]->doc_id == pointer->docid) {
								int frequency = pointer->freq;
								removeNode(pointer);
								(docids)[j]->freq += frequency;
								added = 1;
								PriorityAdd(priority,(docids)[j]->doc_id,(docids)[j]->freq);
								break;
							}	
						}

						// if it's not already in the priority queue, add it
						if (added == 0) {
							PriorityAdd(priority,(docids)[j]->doc_id,(docids)[j]->freq);
						}
					}
				
					// free resources
					(docids)[j] = NULL;
					free((docids)[j]);			
				}

				// read in the next word
				sscanf(strptr,"%s",readWord);
				word = readWord;

				// increment pointers
				strptr += strlen(word) +1;
				location += strlen(word) + 1;
				
				// hash word into the table
				if (word != NULL) {
					hash = JenkinsHash(word,MAX_HASH_SLOT); 
					wordptr = table->table[hash];
					exists = 0;
			
					// iterate through the words to find the word
					while (wordptr != NULL) {
						if (strcmp(word,wordptr->word) == 0) {
							int index = 0;
							docptr = wordptr->page;
							
							// iterate through the documents associated with the word
							while (docptr != NULL) {	
								(docids)[index] = docptr;
								index++;
								docptr=docptr->next;
							}
						
							// find the size of the new doc list
							sizeDocList = index + 1;
							exists = 1;
							break;
						}
						wordptr=wordptr->next;
					}
				}
			
				// if the word doesn't exist, set the index to NULL
				if (exists == 0) {
					(docids)[0] = NULL;
				}			
			}
			else {
				// iterate through the list, looking to see if the word matches
				int k;
				for (k=0;k<sizeDocList;k++) {

					// only if the index is not NULL
					if ((docids)[k] != NULL) {
						hash = JenkinsHash(word,MAX_HASH_SLOT); 
						wordptr = table->table[hash];
						matched = 0;
						exists = 0;
						while (wordptr != NULL) {
						
							// if the word is there, interate through the documents assocaited
							// with the word
							if (strcmp(word,wordptr->word) == 0) {
								docptr = wordptr->page;
								while (docptr != NULL) {
								
									// if the doc is there, then total the frequency
									if ((docids)[k]->doc_id == docptr->doc_id) {
										(docids)[k]->freq += docptr->freq;
										matched = 1;
									}
									docptr=docptr->next;
								}
								exists = 1;
								break;
							}
							wordptr=wordptr->next;
							}
				
						// if the new word doesn't appear in the doc, erase the doc from the list
						if (matched == 0) {
							
							// free resources
							(docids)[k] = NULL;
							free((docids)[k]);
						}
					}	
				}
			}	
		}
		int l;
		// iterate through the doc list
		for (l=0;l<sizeDocList;l++) {
		
			// if the index is empty continue to iterate
			if ((docids)[l] == NULL) {
				continue;
			}	
			else {
				// if it's full, add it to the priority queue
				added = 0;
	
			// check to see if the doc is already in the queue
			for (pointer=priority->head; pointer != NULL; pointer=pointer->next) {
				if((docids)[l]->doc_id == pointer->docid) {
					
					// if it is already in the queue, remove the node and add
					// a new node with updated frequency
					int freq = pointer->freq;
					removeNode(pointer);
					free(pointer);
					(docids)[l]->freq += freq;
					added = 1;
					PriorityAdd(priority,(docids)[l]->doc_id,(docids)[l]->freq);
					break;	
						
					}
				}
				
				// if it's not already in the queue, add it to the queue
				if (added == 0) {
					PriorityAdd(priority,(docids)[l]->doc_id,(docids)[l]->freq);
				}
			}
	
			// free resources
			(docids)[l] = NULL;
			free((docids)[l]);
		}

		// if there's nothing in the queue then there are no matches
		if (priority->head == NULL) {
			printf("No results found.");
		}
		else {
			// continue to pop until there's nothing in the priority queue
			while ((queueptr = pop(priority)) != NULL) {
			
				// find the doc number of the page
				int document = queueptr->docid;	
			
				// create the doc path from the path and the doc id
				docLen = calloc(6,sizeof(char));
				sprintf(docLen,"%d", document);
				docPath = calloc(strlen(argv[2]) + strlen(docLen) + 1, sizeof(char));
				strcat(docPath,argv[2]);
				strcat(docPath,docLen);
		
				// try to open the file
	        	        if ((fp = fopen(docPath, "r")) == NULL) {
					printf("Could not open file\n");
	        	                exit(1);
	        	        }
				
				// read the url in from the file
				char *buffer = calloc(3000,sizeof(char));
				fgets(buffer,3000,fp);
				fclose(fp);

				// print to stdout
				printf("Doc Id: %d ",queueptr->docid);
				printf("URL: %s\n", buffer);
		
		
				// free resources
				temp = queueptr;
				free(docLen);
				free(docPath);
				free(buffer);
				free(temp);
			}
		}
		free(priority);
	
	} while (1);
	
	// free resources
	free(priority);
	free(docids);	
	freer(table);
	return 0;				
					
}
