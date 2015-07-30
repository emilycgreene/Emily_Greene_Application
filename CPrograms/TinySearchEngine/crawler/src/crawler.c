/* ========================================================================== */
/* File: crawler.c - Tiny Search Engine web crawler
 *
 * Author:
 * Date:
 *
 * Input:
 *
 * Command line options:
 *
 * Output:
 *
 * Error Conditions:
 *
 * Special Considerations:
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

// ---------------- Constant definitions
int pos = 0; // beginning position in the html file
int docNum = 2; // first number of the html doc names (after "1": seedPage)

// ---------------- Macro definitions

// ---------------- Structures/Types
struct stat statbuffer;

// ---------------- Private variables
int maxDepth; // maxDepth to crawl to
unsigned long hash; // where the URL hashes into in the hashtable
int tries; // number of tries made to get the webpage

// ---------------- Private prototypes
int is_numeric(const char* str);

/* ========================================================================== */

// function to check if a given argument is a number (used to assess maxDepth)
int is_numeric(const char* str) {
	char *endptr;
	strtod(str, &endptr);
	return *endptr == '\0'; 
}

// main crawler function
int main(int argc, char* argv[]) {
	
    // local variables
	FILE *fp; // file pointer for html files
	char *nextURL; // pointer to the next URL found on the seed page
	char *newURL; // pointer to the next URL in the while loop

    // check command line arguments
	if (argc != 4) {
		printf("Incorrect number of arguments provided.");
		exit(1);
	}
    // check that the second argument is a directory
    	stat(argv[2],&statbuffer);
	if S_ISDIR(statbuffer.st_mode) { }
	else {
		printf("Error, you did not supply a valid directory");
		exit(1);
	}

    // get arguments
    	char *seedURL = argv[1];
	int filename_len = strlen(argv[2])+21;

    // get the directory
	char*filename = calloc(filename_len,sizeof(char));    
    
    // check the maxDepth
	int value = is_numeric(argv[3]);
	if (value != 0) {
		sscanf(argv[3],"%i",&maxDepth);
		}
	else {
		printf("Error! maxDepth must be a number");
		exit(1);
	}
    
    // init curl
    	curl_global_init(CURL_GLOBAL_ALL);

    // initialize data structures/variables
	
	// initialize hashtable
	HashTable *table = malloc(sizeof(HashTable));
	memset(table,0,MAX_HASH_SLOT); 
	
	// initialize linked list
	List *WebPageList;
	WebPageList = createList();

    // setup seed page
    
	// get seed webpage
	// if it fails, report and exit
	if (NormalizeURL(seedURL) == 0) {
		printf("Error, bad URL");
		exit(1);
	}
    // write seed file
	
	// create WebPage object by allocating memory
	WebPage *seedPage = malloc(sizeof(WebPage));
	
	// assign values to each part of the struct
	seedPage->url = seedURL;
	seedPage->html = NULL;
	seedPage->html_len = 0;
	seedPage->depth = 0;
	
	// try to get the webpage up to MAX_TRY times
	if (!GetWebPage(seedPage)) {
		for (tries = 0; tries < MAX_TRY; tries++) {
			if (GetWebPage(seedPage)) { break; }
		}}
	
	// write html contents to a file "1" in the given directory 
	sprintf(filename,"%s/%d",argv[2],1);
	fp = fopen(filename,"w");
	fputs(seedURL,fp);
	fputs("\n",fp);
	fprintf(fp,"%d\n",seedPage->depth);
	fputs(seedPage->html,fp);
	
	// close the file and wipe the filename
	fclose(fp);
	memset(filename,'\0',filename_len);

	// add seed page to hashtable
	add(table,seedURL);

    // extract urls from seed page

	// while there are still URLs in the seed page's html
	while ((pos = GetNextURL(seedPage->html,pos,seedPage->url,&nextURL)) > 0) {
      
	// only visiting them if it wouldn't exceed maxDepth
	if ((seedPage->depth+1) > maxDepth) {
		free(seedPage);
		exit(1);
	}

	// ensure it's a valid url
	if (NormalizeURL(nextURL) != 0) {
 
	// also check if its in the right domain
		if (strncmp(URL_PREFIX,nextURL,strlen(URL_PREFIX)) == 0) {
		
	// if it is added to the hashtable it is a unique URL that 
	// hasn't been visited before, add it to the linked list 
	// of URLs to visit
		if (add(table,nextURL)) {
			// create a new webpage object
			WebPage *pages = malloc(sizeof(WebPage));
			pages->url = nextURL;
			pages->html = NULL;
			pages->html_len = 0;
			pages->depth = 1;
			
			// try to get the webpage up until the MAX_TRY
			tries = 0;
			if (!GetWebPage(pages)) {
			for (tries = 0; tries < MAX_TRY; tries++) {
				if (GetWebPage(pages)) { break; }
			}}

			// add it to the linked list
			addToEnd(WebPageList,pages);
		}}					
	}}
	
    // while there are urls to crawl
	while (WebPageList->head != NULL) {
	 // get next url from list
		WebPage *nextPage = malloc(sizeof(WebPage));
		nextPage = removeFromFront(WebPageList);
	
	// try to get the webpage up until the MAX_TRY
		tries = 0;
		if (!GetWebPage(nextPage)) {
		for (tries = 0; tries < MAX_TRY; tries++) {
			if (GetWebPage(nextPage)) { break; }
		}}
		
	// write page file
		sprintf(filename,"%s/%d",argv[2],docNum);
		fp = fopen(filename,"w");
		fputs(nextPage->url,fp);
		fputs("\n",fp);
		fprintf(fp,"%d\n",nextPage->depth);
		fputs(nextPage->html,fp);
        	
		// close the file and wipe the filename (to be used next time)
		fclose(fp);
		memset(filename,'\0',filename_len);
		
		// increment the doc num
		docNum++;
	
		// check if visiting the URLs on this page will exceed maxDepth
		if ((nextPage->depth+1) > maxDepth) {
			free(nextPage);
			continue;
		}
	pos = 0;
	// iterate through all the URLs on the page
	while ((pos = GetNextURL(nextPage->html,pos,nextPage->url,&newURL))>0) {
		// check to ensure that the URLs are the proper format
		if (NormalizeURL(newURL) != 0 ) {
			// check to ensure that they are in the right domain
			if (strncmp(URL_PREFIX,newURL,strlen(URL_PREFIX)) == 0) {
			// making sure to only add new ones to the list
			if (add(table,newURL) != 0) {
				// create a new WebPage object
				WebPage *page = malloc(sizeof(WebPage));
				page->url = newURL;
				page->html = NULL;
				page->html_len = 0;
				page->depth = nextPage->depth + 1;
				GetWebPage(page);
			
			// try to get the webpage up until the MAX_TRY
				tries = 0;
				if (!GetWebPage(page)) {
				for (tries = 0; tries < MAX_TRY; tries++) {
					if (GetWebPage(page)) { break; }
				}}
				
			// add the page to the linked list
				addToEnd(WebPageList,page);
				}
			}}
		}					
		// Sleep for a bit to avoid annoying the target
		sleep(INTERVAL_PER_FETCH);
		
		// Free resources
		free(nextPage);
	
	}
	
    // cleanup curl
    	curl_global_cleanup();

    // free resources
    	// free hashtable
	hash = JenkinsHash(seedURL,MAX_HASH_SLOT);
   	HashTableNode *freer = table->table[hash];
	HashTableNode *tempHash = NULL;
	while (freer != NULL) {
		tempHash = freer;
		freer = freer->next; 
		free(tempHash);
	}
	free(table);

	// free linked list
	free(WebPageList);
	
	// free WebPage and filename pointer
	free(seedPage);
	free(filename);
    	return 0;
}
