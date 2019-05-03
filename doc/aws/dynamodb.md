
##### Create table on DynamoDB/localstack
```
aws dynamodb create-table  --endpoint-url=http://localhost:4569 \
    --table-name fleet_order_tracking \
    --attribute-definitions \
        AttributeName=id_tenant,AttributeType=S \
        AttributeName=track_date,AttributeType=S \
    --key-schema AttributeName=id_tenant,KeyType=HASH AttributeName=track_date,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```

```
aws dynamodb create-table  --endpoint-url=http://localhost:4569 \
    --table-name fleet_route_tracking \
    --attribute-definitions \
        AttributeName=id_tenant,AttributeType=S \
        AttributeName=track_date,AttributeType=S \
    --key-schema AttributeName=id_tenant,KeyType=HASH AttributeName=track_date,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1

```

```
aws dynamodb create-table --endpoint-url=http://localhost:4569 --table-name fleet_schedule_lock --attribute-definitions AttributeName=_id,AttributeType=S --key-schema AttributeName=_id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```

##### List tables
```
aws dynamodb list-tables --endpoint-url=http://localhost:4569 
```

##### Put item on fleet_order_tracking

```
aws dynamodb put-item --endpoint-url=http://localhost:4569  \
--table-name fleet_order_tracking  \
--item '{"id_tenant":{"S": "1234-br"}, "latitude":{"N":"51.4826"}, "longitude":{"N":"20.0077"}, "track_date":{"S":"2019-02-20T17:18:59.703474Z"}}'
```

##### Put item on fleet_order_tracking

```
aws dynamodb put-item --endpoint-url=http://localhost:4569  \
--table-name fleet_route_tracking  \
--item '{"id_tenant":{"S": "1234-br"}, "latitude":{"N":"51.4826"}, "longitude":{"N":"20.0077"}, "track_date":{"S":"2019-02-20T17:18:59.703474Z"}}'
```

##### Scan table
```
aws dynamodb scan --endpoint-url=http://localhost:4569  --table-name fleet_order_tracking
```

##### Get Iten
```
aws dynamodb get-item --endpoint-url=http://localhost:4569 \
--table-name fleet_order_tracking  \
--key '{"id_tenant":{"S":"1234-br"}, "track_date": {"S": "2019-02-20T17:20:59.703474Z" }}'
```

##### Query
```
aws dynamodb query --endpoint-url=http://localhost:4569  \
--table-name fleet_order_tracking \
--projection-expression "id_tenant, latitude, longitude, track_date" \
--key-condition-expression "id_tenant = :value" \
--expression-attribute-values '{":value" : {"S":"1234-br"}}'

```

```
aws dynamodb query --endpoint-url=http://localhost:4569  \
--table-name fleet_route_tracking \
--projection-expression "id_tenant, latitude, longitude, track_date" \
--key-condition-expression "id_tenant = :value" \
--expression-attribute-values '{":value" : {"S":"1234-br"}}'

```

##### Delete(drop) table
```
aws dynamodb delete-table  --endpoint-url http://localhost:4569 --table-name fleet_order_tracking
```
