package com.deliverypf.fleet.telemetry.order.repository.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import com.lorem.file.FileRepositoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public abstract class FleetTelemetryS3Repository implements S3Repository<TrackKey, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FleetTelemetryS3Repository.class);

    private final FileRepositoryService s3;
    private final String bucketName;

    public FleetTelemetryS3Repository(final FileRepositoryService s3, final String bucketName) {
        this.s3 = s3;
        this.bucketName = bucketName;
    }

    @Override
    public final void save(final TrackKey trackKey, final String tracks) {
        Assert.notNull(trackKey, "TrackKey should not be null");
        Assert.hasText(tracks, "tracks should not be null or empty");
        LOGGER.debug("Saving tracks with key name: {}", trackKey.getKeyName());

        try {
            s3.put(bucketName, getFilename(trackKey), tracks);
        } catch (final AmazonServiceException e) {
            LOGGER.error("Cannot save the tracks on S3, key: {}", trackKey.getKeyName());
            throw new FleetTelemetryException(e);
        }
    }

    protected abstract String getFilename(final TrackKey trackKey);

    @Override
    public final String findByKey(final TrackKey trackKey) {
        Assert.notNull(trackKey, "TrackKey should not be null");

        try {
            return s3.getString(bucketName, getFilename(trackKey));
        } catch (final SdkClientException e) {
            LOGGER.error("Cannot find/read a S3 file, key: {}", trackKey.getKeyName());
            throw new FleetTelemetryException(e);
        }
    }

}
