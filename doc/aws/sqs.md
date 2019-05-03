
##### Creating queues

```
aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name <QUEUE_NAME>
```
Example:
```
aws sqs create-queue --endpoint-url=http://localhost:4576  --queue-name DEV_FLEET_TELEMETRY_ORDER_CONSOLIDATE --region sa-east-1

aws sqs create-queue --endpoint-url=http://localhost:4576  --queue-name DEV_FLEET_TELEMETRY_ROUTE_CONSOLIDATE --region sa-east-1

aws sqs create-queue --endpoint-url=http://localhost:4576 --queue-name DEV_FLEET_ORDER_TRACKING_QUEUE_DYNAMODB --region sa-east-1

```


##### Send message to SQS

```
aws sqs send-message \
--endpoint-url http://localhost:4576 \
--queue-url http://localhost:4576/queue/DEV_FLEET_TELEMETRY_ORDER_CONSOLIDATE  \
--message-body '{"eventType": "ORDER_STATE_CHANGE","parameters":{"ORDER_ID":"1234","CURRENT_ORDER_STATE": "CANCELLED"}}' \
--message-attributes '{"tenant-identifier":{"DataType": "String","StringValue": "br"}}'
```

```
aws sqs send-message \
--endpoint-url http://localhost:4576 \
--queue-url http://localhost:4576/queue/DEV_FLEET_TELEMETRY_ROUTE_CONSOLIDATE  \
--message-body '{"eventType": "ROUTE_COMPLETED", "parameters":{ "ROUTE_ID":"1234" }}' \
--message-attributes '{"tenant-identifier":{"DataType": "String","StringValue": "br"}}'
```


##### Receive message from SQS

```
aws sqs receive-message --endpoint-url http://localhost:4576 --queue-url http://localhost:4576/queue/DEV_FLEET_TELEMETRY_ORDER_CONSOLIDATE 
```


```
aws sqs receive-message --endpoint-url http://localhost:4576 --queue-url http://localhost:4576/queue/DEV_FLEET_TELEMETRY_ROUTE_CONSOLIDATE 
```