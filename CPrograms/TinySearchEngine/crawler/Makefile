# crawler.makefile
CC = gcc
CFLAGS = -Wall -pedantic -std=c11
UTILFLAG=-ltseutil
UTILC=$(UTILDIR)mycurl.c
UTILH=$(UTILC:.c=.h) $(UTILDIR)utils.h $(UTILDIR)common.h
SRC_DIR=src

all: crawler

crawler: $(SRC_DIR)/crawler.o $(SRC_DIR)/web.o $(SRC_DIR)/list.o $(SRC_DIR)/hashtable.o
	$(CC) $(CFLAGS) -o crawler $(SRC_DIR)/crawler.o $(SRC_DIR)/web.o $(SRC_DIR)/hashtable.o $(SRC_DIR)/list.o -lcurl

crawler.o: $(SRC_DIR)/crawler.c $(SRC_DIR)/crawler.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/crawler.c

web.o: $(SRC_DIR)/web.c $(SRC_DIR)/web.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/web.c

list.o: $(SRC_DIR)/list.c $(SRC_DIR)/list.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/list.c

hashtable.o: $(SRC_DIR)/hashtable.c $(SRC_DIR)/hashtable.h
	$(CC) $(CFLAGS) -c $(SRC_DIR)/hashtable.c

$(UTILLIB): $(UTILC) $(UTILH)
	cd $(UTILDIR); make

clean: 
	rm -f $(SRC_DIR)/*~
	rm -f $(SRC_DIR)/*#
	rm -f ./crawler
	rm -f $(SRC_DIR)/*.o
