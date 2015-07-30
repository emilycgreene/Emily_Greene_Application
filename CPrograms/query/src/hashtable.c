/* ========================================================================== */
/* File: hashtable.c
 *
 * Project name: CS50 Tiny Search Engine
 * Component name: Crawler
 *
 * Author: Emily Greene
 * Date: 5/7/14
 *
 * Purpose of hashtable: to organize WordNodes and their corresponding
 * DocumentNodes in order to store and access them as needed
 */
/* ========================================================================== */

// ---------------- Open Issues

// ---------------- System includes e.g., <stdio.h>
#include <string.h>                          // strlen
#include <stdio.h>
// ---------------- Local includes  e.g., "file.h"
#include "common.h"
#include "utils.h"
#include "hashtable.h"
// common functionality

// ---------------- Constant definitions

// ---------------- Macro definitions

// ---------------- Structures/Types

// ---------------- Private variables

// ---------------- Private prototypes

/**
 * Jenkin's function to find which slot to hash the word into
 * (trying to reduce as many collisions as possible
 */
unsigned long JenkinsHash(const char *str, unsigned long mod)
{
    size_t len = strlen(str);
    unsigned long hash, i;

    for(hash = i = 0; i < len; ++i)
    {
        hash += str[i];
        hash += (hash << 10);
        hash ^= (hash >> 6);
    }

    hash += (hash << 3);
    hash ^= (hash >> 11);
    hash += (hash << 15);

    return hash % mod;
}

/** 
 * Function to add a word in a given document to the hashtable
 */
int add(HashTable *table, char *data, int docNum) {
	// find the slot to hash the word into
	unsigned long bucket = JenkinsHash(data,MAX_HASH_SLOT);
	WordNode *new_node;
        WordNode *node_ptr;
	DocumentNode *doc_node;
	DocumentNode *doc_ptr;
	DocumentNode *tail;


	// if the hash slot is empty, create a new WordNode and DocumentNode
	if(!table->table[bucket]) {
		new_node = calloc(1,sizeof(WordNode));
		doc_node = calloc(1,sizeof(DocumentNode));

		if (new_node != NULL) {

			// set up all the pointers
			doc_node->next = NULL;
			doc_node->doc_id = docNum;
			doc_node->freq = 1;

			new_node->next = NULL;
			new_node->word = data;
			new_node->numDocs = 1;
			new_node->page = doc_node;
			table->table[bucket] = new_node;
		return 1;  // SUCCESS
		}
	}
	// if the hash slot has a word in it, iterate through the linked list looking for the word you're trying to add
	for(node_ptr = table->table[bucket]; node_ptr != NULL; node_ptr = node_ptr->next) {
		// if the word is already there, check to see if the doc is there
		if(strcmp(data, node_ptr->word) == 0) {
			doc_ptr = node_ptr->page;
			while (doc_ptr != NULL) {
				
				// if the doc is already there, just increment freq
				if (docNum == doc_ptr->doc_id){
					doc_ptr->freq++;
				return 1; // SUCCESS
				}

			// increment pointer
			tail = doc_ptr;
			doc_ptr = doc_ptr->next;
			}

			// if the doc is not already there, add a new DocumentNode		
			doc_node = calloc(1,sizeof(DocumentNode));

			// set up all the pointers
			doc_node->next = NULL;
			doc_node->doc_id = docNum;
			doc_node->freq = 1;
			node_ptr->numDocs++;
			tail->next = doc_node;

		return 1; // SUCCESS
		}
	}	

	// if the word and the doc aren't there, create a new WordNode and DocumentNode
	new_node = calloc(1, sizeof(WordNode));
	doc_node = calloc(1,sizeof(DocumentNode));

	if (new_node != NULL) {

		// set up all the pointers
		doc_node->next = NULL;
		doc_node->doc_id = docNum;
		doc_node->freq = 1;

		new_node->next = NULL;
		new_node->word = data;
		new_node->numDocs = 1;
		new_node->page = doc_node;
		
		// iterate through to find the last item in the list
		for(node_ptr = table->table[bucket]; node_ptr->next != NULL; node_ptr = node_ptr->next) {
	// do nothing
		}	

		// set the old last item's next pointer to the new word
		node_ptr->next = new_node;
	}

	return 1; // SUCCESS

}

void freer(HashTable *table) {
	WordNode *pointer;
	WordNode *temp;
	DocumentNode *docptr;
	DocumentNode *doctemp;
	int i;
	for (i=0; i<MAX_HASH_SLOT; i++) {
		if (table->table[i] != NULL) {
			pointer = table->table[i]; 
			while (pointer != NULL) {
				if (pointer->page != NULL) {
					docptr = pointer->page;
					while (docptr != NULL) {
						doctemp = docptr->next;
						free(docptr);
						docptr = doctemp;
					}
				}
				temp = pointer->next;
				free(pointer->word);
				free(pointer);
				pointer = temp;
			}
		}
	}
	free(table);
}
					
			
