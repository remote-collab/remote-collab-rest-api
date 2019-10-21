#!/bin/bash

SERVICE_NAME=viper-service-admin
INGRESS_HOST=admin.service.viper.bmw.cloud
NAMESPACE=rc-service-admin
SECRET_NAME=aws-ecr-secret
EMAIL=app-factory.support@list.bmw.com

REPOSITORY_URI=$(aws ecr describe-repositories --query "repositories[?contains(repositoryName, '$SERVICE_NAME')].repositoryUri" --output text | awk -F / '{print $1 "/" $2}')
AWS_ACCESS_KEY=$(echo -n $1 | base64)
AWS_SECRET_KEY=$(echo -n $2 | base64)
AWS_PROFILE=$3
AWS_REGION=eu-west-1

OUT=$(helm ls $SERVICE_NAME)

if [ -z "$OUT" ] ; then
  echo "Project is not yet deployed, will continue with setup..."
else
  echo "will delete old project files before redeployment"
  helm delete --purge $SERVICE_NAME
fi

AWS_ACCOUNT=$(aws ecr get-authorization-token --output text --query "authorizationData[].proxyEndpoint")

mvn clean package -DAWS_ACCESS_KEY=$1 -DAWS_SECRET_KEY=$2

eval $(aws ecr --profile $AWS_PROFILE get-login --no-include-email --region $AWS_REGION)

docker build -t viper/$SERVICE_NAME .

docker tag viper/$SERVICE_NAME:latest $REPOSITORY_URI/$SERVICE_NAME:latest

docker push $REPOSITORY_URI/$SERVICE_NAME:latest


TOKEN=$(aws ecr --region=$AWS_REGION get-authorization-token --output text --query "authorizationData[].authorizationToken" | base64 --decode | cut -d: -f2)

kubectl delete namespace $NAMESPACE --ignore-not-found

kubectl create namespace $NAMESPACE

kubectl delete secret --namespace $NAMESPACE --ignore-not-found $SECRET_NAME

kubectl create secret --namespace $NAMESPACE docker-registry $SECRET_NAME \
  --docker-server=$AWS_ACCOUNT \
  --docker-username=AWS \
  --docker-password="${TOKEN}" \
  --docker-email="${EMAIL}"


echo "Deploying service to AWS ECR repository $REPOSITORY_URI"

helm install --wait viper-charts/viper-aws-helm-service-chart \
    --set=namespace.name=$NAMESPACE \
    --set=image.repository=$REPOSITORY_URI \
    --set=project.includeAwsCredentials=true \
    --set=secret.aws_accesskey=$AWS_ACCESS_KEY,secret.aws_secretkey=$AWS_SECRET_KEY \
    --set=ingress.host=$INGRESS_HOST \
    --name=$SERVICE_NAME