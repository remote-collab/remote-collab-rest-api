 1. Use OpenVidu as media server 

Date: 2019-07-01

## Status

Accepted

## Context

To prevent future problems the media server must be scalable on the aws cluster. 

The kurento media server was selected as first choice.  

The kurento media server does not support a clustered environment. If you want to use more than one instance you have to 
implement the loadbalancer as part of the business logic. 

OpenVidu is the "commercial" version of the kurento media server.  

## Decision

Use OpenVidu as cloud media server

## Consequences

Data needs to be replicated across the ElasticSearch cluster. This separate cluster needs proper maintenance. 

 * No client library needed, since openvidu brings a higher level frontend library.
 * Cloud setup of media server will be provided by OpenVidu. 
 
