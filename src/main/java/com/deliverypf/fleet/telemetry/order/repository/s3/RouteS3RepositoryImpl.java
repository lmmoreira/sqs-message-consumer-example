package com.deliverypf.fleet.telemetry.order.repository.s3;

import com.lorem.file.FileRepositoryService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("routeS3Repository")
public class RouteS3RepositoryImpl extends FleetTelemetryS3Repository {

    public RouteS3RepositoryImpl(final FileRepositoryService s3,
            @Value("${fleet.telemetry.order.s3.bucket-name}") final String bucketName) {
        super(s3, bucketName);
    }

    protected String getFilename(final TrackKey trackKey) {
        return trackKey.getTenant() + "/routes/" + trackKey.getKeyName() + ".json";
    }
}
