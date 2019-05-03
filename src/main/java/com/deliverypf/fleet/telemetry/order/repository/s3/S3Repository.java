package com.deliverypf.fleet.telemetry.order.repository.s3;

public interface S3Repository<K, T> {

    void save(K k, T t);

    String findByKey(K k);

}
