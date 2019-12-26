#!/bin/bash
pid=`ps aux | grep marvin-main | awk '{print $2}'`
kill -9 $pid
