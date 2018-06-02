#!/bin/bash
mvn -o clean package && java -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=y -jar target/integrations-1.0-SNAPSHOT.jar
