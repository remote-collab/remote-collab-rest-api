[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# General

Spring Boot backend API enabling remote collaboration functionality like i.e. creating and joining new sessions.

An overview of the general architecture including a sample application using this service can be found under
[Remote Collab Sample UI](https://github.com/remote-collab/remote-collab-sample-ui/blob/master/README.md "README UI Component")

## Initial Setup

The following sections explain how to build, run and test this service on a local computer.

### Preconditions

- [Java 8](https://www.java.com/en/download/)

- [mvn](https://maven.apache.org/download.cgi) needs to be installed on the local machine. 

- This project uses lombok.
  See [Lombok](https://www.baeldung.com/lombok-ide) installation instructions for your IDE.
  
- Look at [Docker](https://www.docker.com/) for instructions on how to install `docker` and `docker-compose`

### Build and Run locally

`cd` into the project's root folder and execute the following commands in a shell:

1. `mvn clean package`

2. `docker-compose build && docker-compose up`

### Testing the service

The Service's REST resources can now be tested with the integrated [swagger-ui](http://localhost:8080/swagger-ui.html)

## Run with integrated sample UI  

There's a sample project demonstrating how this service can be integrated
within a web application.

For instructions on how to build and run the integrated demo see
[Remote Collab Sample UI](https://github.com/remote-collab/remote-collab-sample-ui/blob/master/README.md "README UI Component") 
