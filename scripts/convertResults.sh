#!/bin/bash

scriptdir="$(dirname "$0")"
cd "$scriptdir"

java -cp ../utils/target/geosemagrow-bl-1.0-SNAPSHOT-jar-with-dependencies.jar JsonQueryResultConverter $*