#!/bin/bash

SERVICE_NAME=viper-service-remote-collab-admin
INGRESS_HOST=rc-backend.service.viper.bmw.cloud

REPOSITORY_URI=$(aws ecr describe-repositories --output json |jq -r .repositories[-1].repositoryUri | awk -F / '{print $1 "/" $2}')
KEYSTORE=$(cat src/main/resources/keystore.p12 |base64)
KEYSTORE_PASS=$(echo -n $1 |base64)
AWS_ACCESS_KEY=$(echo -n $2 |base64)
AWS_SECRET_KEY=$(echo -n $3 |base64)

echo "Deploying service to AWS ECR repository $REPOSITORY_URI"

#helm upgrade --install --wait --set image.repository=$REPOSITORY_URI --set=secret.keystore=$KEYSTORE,secret.password=$KEYSTORE_PASS $SERVICE_NAME helm/

echo "aws access key: $AWS_ACCESS_KEY"
echo "aws secret key: $AWS_SECRET_KEY"

helm install --wait viper-charts/viper-aws-helm-chart-service \
    --set=image.repository=$REPOSITORY_URI,image.name=$SERVICE_NAME \
    --set=secret.keystore=$KEYSTORE,secret.password=$KEYSTORE_PASS \
    --set=secret.aws_accesskey=$AWS_ACCESS_KEY,secret.aws_secretkey=$AWS_SECRET_KEY \
    --set=ingress.hosts={$INGRESS_HOST} \
    --name=$SERVICE_NAME
