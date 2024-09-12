# reactive-solar-fleet

Manage your solar space fleet with this project. It is a combination of Microservices and Reactive Programming in a multimodule Maven project with Hexagonal Arch, CQRS, Event Sourcing, and Kafka for synchronizing databases. Besides I have used Redis to save relevant information from other components.

:rotating_light::no_entry:**Under construction**
- [Kafka](https://kafka.apache.org/) [9092] + [9093] 
- [Kafka-UI](https://docs.kafka-ui.provectus.io/) [8080]
- REST API ms-position with [OpenApi Swagger](https://swagger.io/) and two separate DB for R/RW actions [8081]
- REST API ms-shipclass with [OpenApi Swagger](https://swagger.io/) and two separate DB for R/RW actions [8082]
- REST API ms-spacecraft with [OpenApi Swagger](https://swagger.io/) and two separate DB for R/RW actions [8083]
- REST API ms-crewmember with [OpenApi Swagger](https://swagger.io/) and two separate DB for R/RW actions [8084]

```mermaid
  
  
```

## Table of contents

- [Installation](#installation)
- [Usage](#usage)
- [It's not a bug, it's a feature](#features)
- [Maintainers](#maintainers)
- [License](#license)


## Installation

1. First of all clone or download the project.

1. Inside the main folder, you could find two docker-compose yaml files.

1. From there use the command line to start the project in dev or production mode

```
    **Generate .jar**
    mvn clean package
    
    **Developer mode**  
    docker-compose -f docker-compose-dev.yml up -d

    **Production mode**
    docker-compose -f docker-compose-prod.yml up -d
```
      
The dev environment is ready for using with your IDE. The microservice attempts to communicate with Kafka using the local host. In production, it uses the archive Dockerfile to build an image of the project, so you wont need the IDE.
   
4. You could stop the project and free resources with any of these orders

```
    **Developer mode**
    docker-compose -f docker-compose-dev.yml down --rmi local -v
      
    **Production mode**
    docker-compose -f docker-compose-prod.yml down --rmi local -v  
```
   
## Usage

First of all, please visit the REST API documentation. Replace ${port} for the suitable microservice port:

    http://localhost:${port}/swagger-ui.html
    
[Kafka-UI](https://docs.kafka-ui.provectus.io/) allow you to check your [Kafka](https://kafka.apache.org/) server using a practical dashboard, so visit the following url:

    http://localhost:8080

The initial data load is performed by the kafka-data-init microservice. It uses Kafka to publish events, which the other microservices then use to update their databases.    

## Features

#### :arrow_right: Unit testing for business logic classes using Test Containers for Repositories

#### :arrow_right: Include two docker-compose yaml files for easy change of environment

#### :arrow_right: Hexagonal Architecture following Clean Architecture principle

#### :arrow_right: CQRS pattern with independent databases for Read or RW actions

#### :arrow_right: DB synchronization by Publisher-Subscriber pattern

#### :arrow_right: Redis DB in every microservice work as caches to store ID references for other REST APIs

#### :arrow_right: Event Sourcing for persisting as an ordered sequence of events

#### :arrow_right: Extensive use of Reactive Programming 


## Maintainers

Just me, [Iván](https://github.com/Ivan-Montes) :sweat_smile:


## License

[GPLv3 license](https://choosealicense.com/licenses/gpl-3.0/)


---


[![Java](https://badgen.net/static/JavaSE/21/orange)](https://www.java.com/es/)
[![Maven](https://badgen.net/badge/icon/maven?icon=maven&label&color=red)](https://https://maven.apache.org/)
[![Spring](https://img.shields.io/badge/spring-blue?logo=Spring&logoColor=white)](https://spring.io)
[![GitHub](https://badgen.net/badge/icon/github?icon=github&label)](https://github.com)
[![Eclipse](https://badgen.net/badge/icon/eclipse?icon=eclipse&label)](https://https://eclipse.org/)
[![SonarQube](https://badgen.net/badge/icon/sonarqube?icon=sonarqube&label&color=purple)](https://www.sonarsource.com/products/sonarqube/downloads/)
[![Docker](https://badgen.net/badge/icon/docker?icon=docker&label)](https://www.docker.com/)
[![Kafka](https://badgen.net/static/Apache/Kafka/cyan)](https://kafka.apache.org/)
[![GPLv3 license](https://badgen.net/static/License/GPLv3/blue)](https://choosealicense.com/licenses/gpl-3.0/)