#!/bin/bash

cd /home/pi/marvin
java  -Djava.library.path=/usr/lib/jni -jar marvin-main-1.1.0-SNAPSHOT.jar &> out.txt
