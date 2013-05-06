#!/bin/sh

latex $1
bibtex $1
latex $1
latex $1
dvips -Ppdf $1
dvipdf $1
