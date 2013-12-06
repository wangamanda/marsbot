#!/bin/bash

# update time and date
ntpdate ntp1.tu-clausthal.de

# source server header
source server-header.sh

# Additional settings
configs=$( ls ${conf}${year}-${day}*${hostname}.xml )
resultPage=true;
testServerMode=false;

runServer
