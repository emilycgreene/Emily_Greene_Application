#indexer.makefile
CC = gcc
CFLAGS = -Wall -pedantic -std=c11 -ggdb
UTILFLAG=-ltseutil
UTILC=$(UTILDIR)mycurl.c
UTILH=$(UTILC:.c=.h) $(UTILDIR)utils.h $(UTILDIR)common.h
SRC_DIR=src

all: indexer

indexer: $(SRC_DIR)/indexer.o $(SRC_DIR)/web.o $(SRC_DIR)/list.o $(SRC_DIR)/hashtable.o $(SRC_DIR)/file.o
	$(CC) $(CFLAGS) -o indexer $(SRC_DIR)/indexer.o $(SRC_DIR)/web.o $(SRC_DIR)/hashtable.o $(SRC_DIR)/list.o $(SRC_DIR)/file.o -lcurl

indexer.o: $(SRC_DIR)/indexer.c $(SRC_DIR)/indexer.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/indexer.c

web.o: $(SRC_DIR)/web.c $(SRC_DIR)/web.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/web.c

list.o: $(SRC_DIR)/list.c $(SRC_DIR)/list.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/list.c

hashtable.o: $(SRC_DIR)/hashtable.c $(SRC_DIR)/hashtable.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/hashtable.c

file.o: $(SRC_DIR)/file.c $(SRC_DIR)/file.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/file.c

$(UTILLIB): $(UTILC) $(UTILH)
	cd $(UTILDIR); make

clean: 
	rm -f $(SRC_DIR)/*~
	rm -f $(SRC_DIR)/*#
	rm -f ./indexer
	rm -f $(SRC_DIR)/*.o
