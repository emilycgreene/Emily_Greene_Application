# Filename: Makefile
# Description: Makefile to build library

CC=gcc
CFLAGS= -Wall -pedantic -std=c11
SOURCES= ./file.h ./web.h ./list.h ./hashtable.h ./queue.h ./common.h ./utils.h
CFILES= ./file.c ./web.c ./list.c ./hashtable.c ./queue.c

UTILFLAG= -ltseutil
UTILLIB=$(UTILDIR)libtseutil.a

OBJS= file.o web.o list.o hashtable.o queue.o

$(OBJS): $(CFILES) $(SOURCES)
	$(CC) $(CFLAGS) -c *.c
	make $(UTILLIB)

$(UTILLIB):
	ar -cvq $(UTILLIB) *.o

clean:
	rm -f *.o
	rm -f libtseutil.a

