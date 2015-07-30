# Filename: Makefile
# Description: Makefile to build up the entire TSE

CC=gcc
CFLAGS= -Wall -pedantic -std=c11
SUB_DIR=crawler indexer query util

all: subdirs

subdirs:
	for dir in $(SUB_DIR); do $(MAKE) -C $$dir; done
clean:
	for dir in $(SUB_DIR); do $(MAKE) clean -C $$dir; done
