/* ========================================================================== */
/* File: hashtable.c
 *
 * Project name: CS50 Tiny Search Engine
 * Component name: Crawler
 *
 * Author:
 * Date:
 *
 * You should include in this file your functionality for the hashtable as
 * described in the assignment and lecture.
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

int add(HashTable *table, char *URL ) {
	unsigned long bucket = JenkinsHash(URL,MAX_HASH_SLOT);
	HashTableNode *new_node;
        HashTableNode *node_ptr;

	if(!table->table[bucket]) {
		new_node = calloc(1, sizeof(HashTableNode));
		if (new_node != NULL) {
		new_node->url = URL;

		table->table[bucket] = new_node;

		return 1;  // SUCCESS
		}
	}

	for(node_ptr = table->table[bucket]; node_ptr != NULL; node_ptr = node_ptr->next) {
		if(strcmp(URL, node_ptr->url) == 0) {
			return 0; // FAILURE
		}
	}	

	new_node = calloc(1, sizeof(HashTableNode));
	if (new_node != NULL) {
	new_node->url = URL;

	for(node_ptr = table->table[bucket]; node_ptr->next != NULL; node_ptr = node_ptr->next) {
	// do nothing
	}	

	node_ptr->next = new_node;
	}

	return 1;

}

