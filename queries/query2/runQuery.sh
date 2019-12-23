#!/bin/bash

curl -X POST \
     --data-urlencode "query=`cat query.sparql`" \
     $1
