#!/bin/bash
ps -ef | grep trading-step | grep -v grep | awk '{print $2}' | xargs kill -s 9