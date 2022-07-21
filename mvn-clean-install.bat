@echo off 
setlocal enabledelayedexpansion

set topic[0]=.\accounts\ 
set topic[1]=.\cards\ 
set topic[2]=.\loans\  
set topic[3]=.\eurekaserver\  
set topic[4]=.\gatewayserver\  
set topic[5]=.\configservergit\  

(for /l %%n in (0,1,5) do ( 
    CALL mvn -f !topic[%%n]! clean install
))

CALL docker build .\accounts\ -t microservice/accounts