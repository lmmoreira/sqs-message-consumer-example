# README #

Fleet Telemetry Order project is responsible for handle fleet telemetry order information.

## Technologies and Dependencies ##

* Storage
    * AWS S3
	* AWS DynamoDB
* Async Communication
    * AWS SQS
* Java 11
* Java Frameworks
    * Spring Boot
    * Spring Webflux
    * Spring Quartz

## Operation ##

There are three message consumers in this service to handle the following SQS queues:

- `<ENV>_FLEET_ORDER_TRACKING_QUEUE_DYNAMODB`: The consumer extracts route and/or order telemetry information from messages
(track date, latitude and longitude) and saves it on `fleet_route_tracking` and/or `fleet_order_tracking`, respectively.
- `<ENV>_DEV_FLEET_TELEMETRY_ROUTE_CONSOLIDATE`: This queue consumer fetches route telemetry data from `fleet_route_tracking` when a message arrives (driver's route is finished)
and consolidates this information on `<env>-lorem-fleet-worker-telemetry` S3 bucket.
- `<ENV>_DEV_FLEET_TELEMETRY_ORDER_CONSOLIDATE`: Similarly, this queue consumer fetches order telemetry data from `fleet_order_tracking` when a message arrives
(order is finished or cancelled) and consolidates this information on `<env>-lorem-fleet-worker-telemetry` S3 bucket.

Also, there's a scheduled Spring Quartz job configured on `ExceptionalConsolidation` class that handles consolidation exceptions, that is,
when a message does not arrive on `<ENV>_DEV_FLEET_TELEMETRY_ROUTE_CONSOLIDATE` and `<ENV>_DEV_FLEET_TELEMETRY_ORDER_CONSOLIDATE` queues or any other error occur.
The DynamoDB table `fleet_schedule_lock` is used for this job's lock.

It also exposes reactive controllers to read this stored tracking data of order and route.

## Build and deploy ##

Build and deploy are made via [Jenkins job](http://dev-jenkins1.dc.lorem.com.br:8080/view/Logistics/job/telemetry/job/fleet-telemetry-order-pipeline/).


## Developing locally using `localstack` ##

Locally we use `localstack` to simulate AWS services. Before start, make sure you have
[lorem's local environment project](https://bitbucket.org/lorem/lorem-delivery-platform-local-environment/src/master/) up and running on your development machine
following its instructions.

All the SQS queues, DynamoDB tables and S3 bucket required for running this application are configured on project's above.

#### Working with AWS services

[SQS](doc/aws/sqs.md)

[DynamoDB](doc/aws/dynamodb.md)

[S3](doc/aws/s3.md)

##### Running this application on profile localstack

Finally, execute the project with maven
```
mvn spring-boot:run -Dspring.profiles.active=localstack   
```# sqs-message-consumer-example
=======
# sqs-message-consumer-example
