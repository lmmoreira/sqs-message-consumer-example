package com.deliverypf.fleet.telemetry.order.repository.s3;

import com.lorem.file.FileRepositoryService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("orderS3Repository")
public class OrderS3RepositoryImpl extends FleetTelemetryS3Repository {

    public OrderS3RepositoryImpl(final FileRepositoryService s3,
            @Value("${fleet.telemetry.order.s3.bucket-name}") final String bucketName) {
        super(s3, bucketName);
    }

    protected String getFilename(final TrackKey trackKey) {
        return trackKey.getTenant() + "/orders/" + trackKey.getKeyName() + ".json";
    }
}
