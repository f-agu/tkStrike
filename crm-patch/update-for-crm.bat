@echo off

java -cp app\lib\h2-1.4.199.jar org.h2.tools.RunScript -url "jdbc:h2:./app/db/tkStrike30" -user SA -script sql\set-node-ids.sql -showResults

pause