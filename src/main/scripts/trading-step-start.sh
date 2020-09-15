#!/bin/bash
. /etc/profile
. ~/.bash_profile
java -server -Xms1024m -Xmx1024m -jar trading-step-application-1.0.0.jar >/dev/null 2>&1 &
