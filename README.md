# circles-garden-land-merge

This utility helps in merging the user data of circles.garden und circles.land and exports a neo4j importable CSV file.

## Prerequisites

* Java 17

## Build

* `mvnw clean install`

## Run

* Set ENVS in `run-merge.sh`
* `./run-merge.sh`

## Import Neo4j Data

* Copy generated `merge-export.csv` into `import` folder of neo4j and follow instructions here:  https://github.com/ice09/circles-trust-graph-data-science
 
