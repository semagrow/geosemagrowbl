#!/bin/bash

until nc -z localhost 8080
do
  echo 'waiting for strabon to start'
  sleep 2
done

until [ -f "/kobe/dataset/gsgbl/ptcl-query" ]
do
  sleep 2
done

until [ -f "/kobe/dataset/gsgbl/sqcl-query" ]
do
  sleep 2
done

curl -X POST -H "Accept: application/json" \
     --data-urlencode "query=`cat /kobe/dataset/gsgbl/ptcl-query`" \
     http://kobeexp-ptcl.default.svc.cluster.local:8080/SemaGrow/sparql > /tmp/dump1.json

curl -X POST -H "Accept: application/json" \
     --data-urlencode "query=`cat /kobe/dataset/gsgbl/sqcl-query`" \
     http://kobeexp-sqcl.default.svc.cluster.local:8080/SemaGrow/sparql > /tmp/dump2.json

cd /geosemagrowbl/scripts

./convertResults.sh -input /tmp/dump1.json -query /kobe/dataset/gsgbl/ptcl-query -output /tmp/dump1.nt
./convertResults.sh -input /tmp/dump2.json -query /kobe/dataset/gsgbl/sqcl-query -output /tmp/dump2.nt

sort /tmp/dump1.nt | uniq > /tmp/dump1-sorted.nt
sort /tmp/dump2.nt | uniq > /tmp/dump2-sorted.nt

cat /tmp/dump1-sorted.nt /tmp/dump2-sorted.nt > /kobe/dataset/gsgbl/dump.nt

curl 'http://localhost:8080/strabon/Store' \
     -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' \
     -H 'Accept-Language: en-US,en;q=0.5' --compressed \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Origin: http://localhost:8080' \
     -H 'Connection: keep-alive' \
     -H 'Referer: http://localhost:8080/strabon/Store' \
     -H 'Upgrade-Insecure-Requests: 1' \
     --data 'view=HTML&graph=&format=N-Triples&data=&url=file%3A%2F%2F%2Fkobe%2Fdataset%2Fgsgbl%2Fdump.nt&fromurl=Store+from+URI'

sleep 2

touch /kobe/dataset/gsgbl/.dataLoaded


