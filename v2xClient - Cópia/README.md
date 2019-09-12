# v2xclient

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>com.multicert.project.v2x</groupId>
    <artifactId>v2xclient</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "com.multicert.project.v2x:v2xclient:1.0.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/v2xclient-1.0.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import com.multicert.project.v2x.client.invoker.*;
import com.multicert.project.v2x.client.invoker.auth.*;
import com.multicert.project.v2x.client.model.*;
import com.multicert.project.v2x.client.api.RaControllerApi;

import java.io.File;
import java.util.*;

public class RaControllerApiExample {

    public static void main(String[] args) {
        
        RaControllerApi apiInstance = new RaControllerApi();
        VehiclePojo vehicle = new VehiclePojo(); // VehiclePojo | vehicle
        try {
            ConfigResponse result = apiInstance.configureVehicleUsingPOST(vehicle);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RaControllerApi#configureVehicleUsingPOST");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://localhost:8080*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*RaControllerApi* | [**configureVehicleUsingPOST**](docs/RaControllerApi.md#configureVehicleUsingPOST) | **POST** /api/conf | Configure a vehicle within the RASerice.
*RaControllerApi* | [**requestAuthorizationTicketUsingPOST**](docs/RaControllerApi.md#requestAuthorizationTicketUsingPOST) | **POST** /api/authorization | Request an authorization ticket.
*RaControllerApi* | [**requestEnrollmentCertUsingPOST**](docs/RaControllerApi.md#requestEnrollmentCertUsingPOST) | **POST** /api/enrollment | Request an enrollment credential.


## Documentation for Models

 - [ConfigResponse](docs/ConfigResponse.md)
 - [Request](docs/Request.md)
 - [Response](docs/Response.md)
 - [VehiclePojo](docs/VehiclePojo.md)


## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author



