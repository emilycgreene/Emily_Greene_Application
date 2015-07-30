#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "list.h"
#include "common.h"
#include "utils.h"

// create a new list
List * createList() {
	
	List *list=malloc(sizeof(List));
	list->head = list->tail = NULL;
	return list;	
}

// add a node to the end of the list
void addToEnd(List *list, void *item) {
	// if there's nothing in the list, create the first node
	if (NULL == list->tail) {
		ListNode *firstNode = malloc(sizeof(ListNode));
		firstNode->item =item;
		firstNode->next=NULL;
		firstNode->prev=NULL;
		list->head = firstNode;
		list->tail = firstNode;
		
	}
	else {
		// create a new node
		// allocate memory
		ListNode *new = malloc(sizeof(ListNode));
		// fix up links
		new->item = item;
		new->prev = list->tail;
		new->next = NULL;
		list->tail->next = new;
		list->tail = new;
	}
}

// remove item from the front of the list
void* removeFromFront(List *list) {
	void *data = malloc(sizeof(WebPage));
	
	// check to see if the list is empty
	if (NULL == list->head) {
		printf("List is empty");
		exit(1);
	}
	else {
		// get the data from the first item in the list
		data = list->head->item;
		
		// fix up links
		if (list->head->next != NULL) {
			list->head=list->head->next;
			list->head->prev = NULL;
		}
		else {
			list->head=NULL;
			list->tail=NULL;
		}

	// return a pointer to the data
	return data;
	}
}	

