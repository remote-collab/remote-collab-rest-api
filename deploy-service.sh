#!/bin/bash

SERVICE_NAME=viper-service-admin
INGRESS_HOST=viper-admin.service.viper.bmw.cloud

REPOSITORY_URI=$(aws ecr describe-repositories --query "repositories[?contains(repositoryName, '$SERVICE_NAME')].repositoryUri" --output text)
KEYSTORE=$(cat src/main/resources/keystore.p12 |base64)
KEYSTORE_PASS=$(echo -n $1 |base64)
AWS_ACCESS_KEY=$(echo -n $2 |base64)
AWS_SECRET_KEY=$(echo -n $3 |base64)

echo "Deploying service to AWS ECR repository $REPOSITORY_URI"

#helm install --wait viper-charts/viper-aws-helm-service-chart \
#    --set=project.type=backend \
#    --set=image.repository=$REPOSITORY_URI,image.name=$SERVICE_NAME \
#    --set=secret.keystore=$KEYSTORE,secret.password=$KEYSTORE_PASS \
#    --set=project.includeAwsCredentials=true \
#    --set=secret.aws_accesskey=$AWS_ACCESS_KEY,secret.aws_secretkey=$AWS_SECRET_KEY \
#    --set=ingress.hosts={$INGRESS_HOST} \
#    --name=$SERVICE_NAME
