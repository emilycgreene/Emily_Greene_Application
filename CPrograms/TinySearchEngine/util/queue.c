#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "queue.h"
#include "common.h"
#include "utils.h"

// create a new list
Queue * createQueue() {
	
	Queue *queue=malloc(sizeof(Queue));
	queue->head = queue->tail = NULL;
	return queue;	
}

// add a node to the end of the list
void PriorityAdd(Queue *queue, int docid, int freq) {
	// if there's nothing in the list, create the first node
	if (NULL == queue->tail) {
		QueueNode *firstNode = malloc(sizeof(QueueNode));
		firstNode->docid=docid;
		firstNode->freq=freq;
		firstNode->next=NULL;
		firstNode->prev=firstNode;
		queue->head = firstNode;
		queue->tail = firstNode;
		
	}
	else {
		// create a new node
		// allocate memory
		QueueNode *new = malloc(sizeof(QueueNode));
		QueueNode *pointer = queue->head;

		new->docid=docid;
		new->freq=freq;	

		// iterate through the queue looking to see where the new node should go
		// queue is in order from greatest to least freq of word
		while (new->freq < pointer->freq) {
			if (pointer->next != NULL) {
				pointer=pointer->next;
			}
			else {
				break;
			}
		}		
		// fix up links
		if (pointer->prev == pointer) {
			queue->head = new;
			new->prev = new;
			new->next = pointer;
			pointer->prev = new;	
		}
		else if (pointer != NULL) {
			new->prev = pointer->prev;
			new->next = pointer;
			pointer->prev->next = new;
			pointer->prev = new;
		}
		else {
			new->prev = pointer;
			new->next = NULL;
			pointer->next = new;
		}
	}
}

void removeNode(QueueNode *node) {
	if (node != NULL) {
		node->prev->next = node->next;
		node->next->prev = node->prev;
	}
}

// remove item from the front of the queue
QueueNode * pop(Queue *queue) {
	
	// check to see if the queue is empty
	if (NULL == queue->head) {
		return NULL;
	}
	else {
		// get the data from the first item in the queue
		QueueNode *first = queue->head;	
		// fix up links
		queue->head=queue->head->next;
		return first;

	}
}	

