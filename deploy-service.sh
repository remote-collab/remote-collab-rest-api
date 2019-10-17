#!/bin/bash

SERVICE_NAME=viper-service-admin
INGRESS_HOST=admin.service.viper.bmw.cloud

REPOSITORY_URI=$(aws ecr describe-repositories --query "repositories[?contains(repositoryName, '$SERVICE_NAME')].repositoryUri" --output text | awk -F / '{print $1 "/" $2}')
AWS_ACCESS_KEY=$(echo -n $1 |base64)
AWS_SECRET_KEY=$(echo -n $2 |base64)
AWS_NAMESPACE=default

echo "Deploying service to AWS ECR repository $REPOSITORY_URI"

helm install --wait viper-charts/viper-aws-helm-service-chart \
    --set=project.type=backend \
    --set=namespace.name=$AWS_NAMESPACE \
    --set=image.repository=$REPOSITORY_URI \
    --set=project.includeAwsCredentials=true \
    --set=secret.aws_accesskey=$AWS_ACCESS_KEY,secret.aws_secretkey=$AWS_SECRET_KEY \
    --set=ingress.host=$INGRESS_HOST \
    --name=$SERVICE_NAME
