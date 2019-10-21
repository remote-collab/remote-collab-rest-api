[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# General

Admin backend to create new rooms. Containing security logic.

# Initial Setup

## Preconditions

- [mvn](https://maven.apache.org/download.cgi) needs to be installed on the local machine. 

- This project uses lombok.
  [Lombok](https://www.baeldung.com/lombok-ide) installation instructions for your IDE.
  
- [Helm](https://helm.sh/docs/using_helm/#install-helm) needs to be installed and properly configured for your AWS account

- You need to know how to execute Helm charts manually or from an existing repository. If you do not have a custom Helm chart repository you can copy
  the whole content from [viper-aws-helm-service-chart](https://github.com/visual-perceptibility/viper-aws-helm-service-chart) into a `helm` subfolder of
  your current project root folder. You then have to adjust the `helm install` command in the `deploy-service.sh` file and provide the dedicated path to your
  local helm chart.

## Start Application 
Follow instructions in the 
[UI Repository ](https://github.com/visual-perceptibility/viper-ui-standalone/blob/master/README.md "README UI Component") 

## AWS Deployment

Deployment on AWS works with a pre-defined [Helm chart](https://github.com/visual-perceptibility/viper-aws-helm-service-chart).
Before using the chart the application needs to be built with docker and pushed to AWS ECR.

### Set AWS_PROFILE

Execute `export AWS_PROFILE=<PROFILE_NAME>` on the shell. Replace `PROFILE_NAME` with a valid aws profile definition
from `~/.aws/config`

### Create ECR Repository in AWS

If not already done login to your AWS account and create a new `ECR Repository` named `viper/viper-service-admin`

### Build the service and push it to AWS ECR

1. Build the service by running `mvn clean package`

2. Run the following command to login to the ECR registry 
   ```bash
   eval $(aws ecr --profile <AWS_PROFILE_NAME> get-login --no-include-email --region <AWS_REGION>)
   ```
   where
   
   - `AWS_PROFILE_NAME` = the name of a valid AWS profile as stated in `~/.aws/config`
   - `AWS_REGION` = the region in which the ECR repo resides
   
   Example call:
   
   ```bash
    eval $(aws ecr --profile my-aws-profile get-login --no-include-email --region eu-west-1)
   ```
   
3. Create a new docker image on your local machine

   ```bash
   docker build -t viper/viper-service-admin .   
   ```
   
4. Tag the newly created docker image in order to push it to the registry

   ```bash
   docker tag viper/viper-service-admin:latest <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/viper/viper-service-admin:latest
   ```
   
   where
   - `AWS_ACCOUNT_ID` = the ID of the AWS account where the ECR repository resides
   - `AWS_REGION` =  the region in which the ECR repo resides
   
   Example call:
   
   ```bash
   docker tag viper/viper-service-admin:latest 111122223333.dkr.ecr.eu-west-1.amazonaws.com/viper/viper-service-admin:latest
   ```
   
5. Push the image to ECR

    ```bash
    docker push <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/viper/viper-service-admin:latest
    ```
    
    Example call:
    
    ```bash
    docker push 111122223333.dkr.ecr.eu-west-1.amazonaws.com/viper/viper-service-admin:latest
    ```

### Create kubernetes secret for AWS ECR access

In order to access the previously generated ECR repository during deployment time a secret containing the
docker registry's login token needs to be created.

1. Retrieve the docker login token through the AWS command line.
   ```
   aws ecr --region=<AWS_REGION> get-authorization-token --output text --query "authorizationData[].authorizationToken" | base64 --decode | cut -d: -f2
   ```
   Replace `AWS_REGION` with the region your repository is located at, e.g. `eu-west-1`
   
2. Create a kubernetes secret with name `aws-ecr-secret` and put in the login token from step 1.
   ```
   kubectl create secret docker-registry aws-ecr-secret \
   --docker-server=111122223333.dkr.ecr.eu-west-1.amazonaws.com \
   --docker-username=AWS \
   --docker-password=<PASSWORD_FROM_STEP_1> \
   --docker-email=<ARBITRARY_EMAIL_ADDRESS>
   ```
### Deploy the service with pre-built helm chart

1. CD into the project's root folder

2. Execute the `deploy-service.sh` shell script by typing in
 
   `deploy-service.sh <AWS_ACCESS_KEY> <AWS_SECRET_KEY> <AWS_PROFILE>`
   
   Since this service requires access to AWS DynamoDB we additionally need to pass a valid AWS Access Key and Secret Key.
   Also, we need to pass in a valid AWS Profile.

## How does it work?

`deploy-service.sh` runs a Helm chart which is a pre-defined deployment chart for standard Spring Boot Microservice and VueJS
projects. The shell script and the chart need to be configured with parameters characterizing the application deploymnent.

### Adjust `deploy-service.sh`

In `deploy-service.sh` change the following environment variables according to your specific requirements for service and host name:

- `SERVICE_NAME` = the name of the service how it should end up in K8S
- `INGRESS_HOST` = the DNS name of the endpoint this service should be deployed to. You can choose any DNS compliant
                   name which will result in <INGRESS_HOST>.service.viper.bmw.cloud
                   
                   
## Troubleshooting

When your service won't be deployed due to missing AWS ECR credentials you need to refresh the `aws-ecr-secret`.
Just call the `refresh_secret.sh` script to automatically get the secret deleted and deployed with a fresh token:

```
./refresh_secret.sh <ACCOUNT_ID> <K8S_NAMESPACE>
```

where

- `ACCOUNT_ID` = the ID of your aws account, e.g 123456789123
- `K8S_NAMESPACE` = the namespace the secret needs to be deployed to. The namespace must be the same as the one your services are deployed to

Example Call:

```
./refresh_secret.sh 123456789123 remote-collab
```