# Data Format Description Language ([DFDL](https://en.wikipedia.org/wiki/Data_Format_Description_Language)) Processor Example
This module is a example how to process a binary using a DFDL definition.
The DFDL definitions are stored in a Bigtable database.

The application send a request with the binary to process to a pubsub topic.

The processor service subscribes to the topic, processes every message,
applies the definition and publishes the json result to a topic in pubsub.

## Before you start
[Google’s open-source solution to DFDL Processing](https://cloud.google.com/blog/products/application-modernization/dfdl-processing-with-google-cloud)

## Project Structure

```
.
└── dfdl_example
 ├── examples # Contain a binary and dfdl definition to be used to run this example
 └── src
       └── main
           └── java
               └── com.example.dfdl
                   ├── BigtableServer # Configures bigtable database
                   ├── BigtableService # Reads dfdl definitons from a bigtable database
                   ├── DfdlDef # Embedded entities
                   ├── DfdlService # Processes the binary using a dfdl definition and output a json
                   ├── MessageController # Publishes message to a topic with a binary to be processed.
                   ├── ProcessorService # Initializes components, configurations and services.
                   ├── PubSubServer # Publishes and subscribes to topics using channels adapters.
 └── resources
      └── application.properties
 └── pom.xml
 └── README.md
```

### Tools

Before you start is recommended that you install the following tools:

1. [Google Cloud SDK](https://cloud.google.com/sdk/docs/install)
2. [Cloud Bigtable Tool](https://cloud.google.com/bigtable/docs/cbt-overview)

## Technology Stack
1. Cloud Bigtable
2. Cloud Pubsub

## Frameworks
1. Spring Boot

## Libraries
1. [Apache Daffodil](https://daffodil.apache.org/)

## Setup Instructions
### Project Setup
#### Creating a Project in the Google Cloud Platform Console

If you haven't already created a project, create one now. Projects enable you to
manage all Google Cloud Platform resources for your app, including deployment,
access control, billing, and services.

1. Open the [Cloud Platform Console][cloud-console].
1. In the drop-down menu at the top, select **Create a project**.
1. Give your project a name = my-dfdl-project
1. Make a note of the project ID, which might be different from the project
   name. The project ID is used in commands and in configurations.

[cloud-console]: https://console.cloud.google.com/

#### Enabling billing for your project.

If you haven't already enabled billing for your project, [enable
billing][enable-billing] now.  Enabling billing allows is required to use Cloud Bigtable
and to create VM instances.

[enable-billing]: https://console.cloud.google.com/project/_/settings

#### Install the Google Cloud SDK.

If you haven't already installed the Google Cloud SDK, [install the Google
Cloud SDK][cloud-sdk] now. The SDK contains tools and libraries that enable you
to create and manage resources on Google Cloud Platform.

[cloud-sdk]: https://cloud.google.com/sdk/

#### Setting Google Application Default Credentials

Set your [Google Application Default
Credentials][application-default-credentials] by [initializing the Google Cloud
SDK][cloud-sdk-init] with the command:

```
gcloud init
```
Generate a credentials file by running the
[application-default login](https://cloud.google.com/sdk/gcloud/reference/auth/application-default/login) command:

```
    gcloud auth application-default login
```

[cloud-sdk-init]: https://cloud.google.com/sdk/docs/initializing
[application-default-credentials]: https://developers.google.com/identity/protocols/application-default-credentials

### Bigtable Setup
How to create a Bigtable database instance can be found [here](https://cloud.google.com/bigtable/docs/creating-instance)

#### How to add data to bigtable
The following doc, [Writing to Bigtable](https://cloud.google.com/bigtable/docs/writing-data),
can be used to add data to bigtable to run the example.

This example connects to a Cloud Bigtable with a collection with the
following specification.
The configuration can be changed by changing the application.properties file.
```
    Table
     dfdl-schemas =>
         Column Family
            dfdl => 
               Column Family Qualifier => 
                   binary_example => {
                       'name': "binary_example"
                       'definiton':
                        "<?xml version="1.0" encoding="UTF-8"?>
                        
                        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
                            targetNamespace="http://example.com/dfdl/helloworld/"
                            xmlns:dfdl="http://www.ogf.org/dfdl/dfdl-1.0/">
                        
                          <xs:include
                              schemaLocation="org/apache/daffodil/xsd/DFDLGeneralFormat.dfdl.xsd"/>
                          <xs:annotation>
                            <xs:appinfo source="http://www.ogf.org/dfdl/">
                              <dfdl:format ref="GeneralFormat"
                                  representation="binary"/>
                            </xs:appinfo>
                          </xs:annotation>
                        
                          <xs:element name="binary_example">
                            <xs:complexType>
                              <xs:sequence>
                                <xs:element name="w" type="xs:int"
                                    dfdl:binaryNumberRep="binary"
                                    dfdl:byteOrder="bigEndian"
                                    dfdl:lengthKind="implicit"/>
                        
                                <xs:element name="x" type="xs:int"
                                    dfdl:binaryNumberRep="binary"
                                    dfdl:byteOrder="bigEndian"
                                    dfdl:lengthKind="implicit"/>
                        
                                <xs:element name="y" type="xs:double"
                                    dfdl:binaryFloatRep="ieee"
                                    dfdl:byteOrder="bigEndian"
                                    dfdl:lengthKind="implicit"/>
                        
                                <xs:element name="z" type="xs:float"
                                    dfdl:binaryFloatRep="ieee"
                                    dfdl:byteOrder="bigEndian"
                                    dfdl:lengthKind="implicit"/>
                              </xs:sequence>
                            </xs:complexType>
                          </xs:element>
                        </xs:schema>";
                        }
```
This dfdl definition example can be found in the binary_example.dfdl.xsd file.

### Pubsub Setup
The following [doc](https://cloud.google.com/pubsub/docs/quickstart-console)
can be used to set up the topics and subscriptions needed to run this example.

#### Topics
To run this example two topics need to be created:
1. A topic to publish the final json output: "data-output-json-topic"
2. A topic to publish the binary to be processed: "data-input-binary-topic"

#### Subscription
The following subscriptions need to be created:
1. A subscription to pull the binary data: data-input-binary-sub

### Redis Setup

#### Installation of Redis Emulator

Please refer to this [doc](https://redis.io/docs/getting-started/) to install a
redis emulator in your localhost

#### Initialized the server

```
   $ redis-server
```

### Basic commands to access the data

List all the keys

```
  $ redis-cli
   127.0.0.1:6379> KEYS *
   (empty array)
```

After running the example at least two times redis will show the processor in
redis

```  
   127.0.0.1:6379> KEYS *
   1) "processors::binary_example"

```

Delete a key

```
   127.0.0.1:6379> DEL "processors::binary_example"
```

Get a key

```
  127.0.0.1:6379> GET "processors::binary_example"
```

## Usage
### Initialize the application
Reference: [Building an Application with Spring Boot](https://spring.io/guides/gs/spring-boot/)
```
      ./mvnw spring-boot:run
```
### Send a request
```
    curl --data "message=0000000500779e8c169a54dd0a1b4a3fce2946f6" localhost:8081/publish
```