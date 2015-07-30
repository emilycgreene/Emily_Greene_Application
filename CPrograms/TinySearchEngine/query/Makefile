# indexer.makefile
CC = gcc
CFLAGS = -Wall -pedantic -std=c11 -ggdb
UTILFLAG=-ltseutil
UTILH= $(UTILDIR)utils.h $(UTILDIR)common.h
SRC_DIR=src

all: query

query: $(SRC_DIR)/query.o $(SRC_DIR)/queue.o $(SRC_DIR)/hashtable.o $(SRC_DIR)/file.o
	$(CC) $(CFLAGS) -o query $(SRC_DIR)/query.o $(SRC_DIR)/queue.o $(SRC_DIR)/hashtable.o $(SRC_DIR)/file.o

query.o: $(SRC_DIR)/query.c $(SRC_DIR)/query.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/query.c

queue.o: $(SRC_DIR)/queue.c $(SRC_DIR)/queue.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/queue.c

hashtable.o: $(SRC_DIR)/hashtable.c $(SRC_DIR)/hashtable.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/hashtable.c

file.o: $(SRC_DIR)/file.c $(SRC_DIR)/file.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/file.c

clean: 
	rm -f $(SRC_DIR)/*~
	rm -f $(SRC_DIR)/*#
	rm -f ./query
	rm -f $(SRC_DIR)/*.o
