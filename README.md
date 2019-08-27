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
[UI Repository ](https://github.com/visual-perceptibility/viper-ui-remote-collab-admin/blob/master/README.md "README UI Component") 

## AWS Deployment

Deployment on AWS works with a pre-defined [Helm chart](https://github.com/visual-perceptibility/viper-aws-helm-service-chart).
Before using the chart the application needs to be built with docker and pushed to AWS ECR.

### Set AWS_PROFILE

Execute `export AWS_PROFILE=<PROFILE_NAME>` on the shell. Replace `PROFILE_NAME` with a valid aws profile definition
from `~/.aws/config`

### Create TLS Cert and Key

1. CD into `src/main/resources`

2. Create certificate and key with OpenSSL
    ```
    openssl req -x509 -newkey rsa:4096 -sha256 -nodes -keyout viper-service-remote-collab-admin.key -out viper-service-remote-collab-admin.crt -subj "/CN=localhost" -days 365
    ```
    Note that you will be asked for a keystore password. Remember this password for later. It will be needed for the helm deployment.

3. Create keystore file from cert and keyfile generated in step 1
    ```
    openssl pkcs12 -export -in viper-service-remote-collab-admin.crt -inkey viper-service-remote-collab-admin.key -out keystore.p12 -name viper-service-remote-collab-admin
    ```

### Create ECR Repository in AWS

If not already done login to your AWS account and create a new `ECR Repository` named `viper/viper-service-remote-collab-admin`

### Build the service and push it to AWS ECR

1. Build the service by running `mvn clean package`

2. Run the following command to login to the ECR registry 
   ```bash
   eval $(aws ecr --profile <AWS_PROFILE_NAME> get-login --no-include-email --region <AWS_REGION>")
   ```
   where
   
   - `AWS_PROFILE_NAME` = the name of a valid AWS profile as stated in `~/.aws/config`
   - `AWS_REGION` = the region in which the ECR repo resides
   
   Example call:
   
   ```bash
    eval $(aws ecr --profile my-aws-profile get-login --no-include-email --region eu-west-1")
   ```
   
3. Create a new docker image on your local machine

   ```bash
   docker build -t viper/viper-service-remote-collab-admin .   
   ```
   
4. Tag the newly created docker image in order to push it to the registry

   ```bash
   docker tag viper/viper-service-remote-collab-admin:latest <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/viper/viper-service-remote-collab-admin:latest
   ```
   
   where
   - `AWS_ACCOUNT_ID` = the ID of the AWS account where the ECR repository resides
   - `AWS_REGION` =  the region in which the ECR repo resides
   
   Example call:
   
   ```bash
   docker tag viper/viper-service-remote-collab-admin:latest 111122223333.dkr.ecr.eu-west-1.amazonaws.com/viper/viper-service-remote-collab-admin:latest
   ```
   
5. Push the image to ECR

    ```bash
    docker push <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/viper/viper-service-remote-collab-admin:latest
    ```
    
    Example call:
    
    ```bash
    docker push 111122223333.dkr.ecr.eu-west-1.amazonaws.com/viper/viper-service-remote-collab-admin:latest
    ```

### Deploy the service with pre-built helm chart

1. CD into the project's root folder

2. Execute the `deploy-service.sh` shell script by typing in
 
   `deploy-service.sh <KEYSTORE_PASSWORD> <AWS_ACCESS_KEY> <AWS_SECRET_KEY>`
   
   and replace `KEYSTORE_PASSWORD` with the password you provided in the `Create TLS Cert and Key` section above.
   Since this service requires access to AWS DynamoDB we additionally need to pass a valid AWS Access Key and Secret Key.
   

## How does it work?

`deploy-service.sh` runs a Helm chart which is a pre-defined deployment chart for standard Spring Boot Microservice and VueJS
projects. The shell script and the chart need to be configured with parameters characterizing the application deploymnent.

### Adjust `deploy-service.sh`

In `deploy-service.sh` change the following environment variables according to your specific requirements for service and host name:

- `SERVICE_NAME` = the name of the service how it should end up in K8S
- `INGRESS_HOST` = the DNS name of the endpoint this service should be deployed to. You can choose any DNS compliant
                   name which will result in <INGRESS_HOST>.service.viper.bmw.cloud