#!/bin/bash

curl -X POST \
     --data-urlencode "ptcl-query=`cat ptcl-query.sparql`" \
     --data-urlencode "sqcl-query=`cat sqcl-query.sparql`" \
     --data-urlencode "query=`cat query.sparql`" \
     $1
