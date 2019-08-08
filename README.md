[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# General

Admin backend to create new rooms. Containing security logic.

# Initial Setup

## Preconditions

[mvn](https://maven.apache.org/download.cgi) needs to be installed on the local machine. 

This project uses lombok.
[Lombok](https://www.baeldung.com/lombok-ide) installation instructions for your IDE. 



## Start Applikation 
Follow instructions in the 
[UI Repository ](https://github.com/visual-perceptibility/viper-ui-remote-collab-admin/blob/master/README.md "README UI Component") 




### From the documentation

IMPORTANT! /PATH/TO/VIDEO/FILES must be the same in property openvidu.recording.path=/PATH/TO/VIDEO/FILES and in both sides of flag -v /PATH/TO/VIDEO/FILES:/PATH/TO/VIDEO/FILES

Working example on windows: 
docker run -p 4443:4443 --rm -v /var/run/docker.sock:/var/run/docker.sock -v /c/temp:/c/temp -e openvidu.recording=true -e openvidu.recording.path=/c/temp openvidu/openvidu-server-kms:2.10.0


