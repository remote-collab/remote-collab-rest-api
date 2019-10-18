AWS_ACCOUNT=$1
AWS_REGION=eu-west-1

SECRET_NAME=aws-ecr-secret
NAMESPACE=$2
EMAIL=app-factory.support@list.bmw.com

TOKEN=$(aws ecr --region=$AWS_REGION get-authorization-token --output text --query "authorizationData[].authorizationToken" | base64 --decode | cut -d: -f2)

kubectl delete secret --namespace $NAMESPACE --ignore-not-found $SECRET_NAME

kubectl create secret --namespace $NAMESPACE docker-registry $SECRET_NAME \
  --docker-server=$AWS_ACCOUNT.dkr.ecr.$AWS_REGION.amazonaws.com \
  --docker-username=AWS \
  --docker-password="${TOKEN}" \
  --docker-email="${EMAIL}"