@echo off
call detect.cmd
java -cp .;samplePlayer.jar BattleshipMain ..\config.txt ..\loc1.txt ..\loc2.txt greedy greedy

