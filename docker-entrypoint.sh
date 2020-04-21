#!/bin/sh

set -e

echo "ENABLE_DATADOG=$ENABLE_DATADOG"

if [ "_$ENABLE_DATADOG" = _true ]; then
    apk update
    apk add wget
    wget -O dd-java-agent.jar "https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.datadoghq&a=dd-java-agent&v=LATEST"

    java -javaagent:dd-java-agent.jar -jar app.jar
else
    java -jar app.jar
fi