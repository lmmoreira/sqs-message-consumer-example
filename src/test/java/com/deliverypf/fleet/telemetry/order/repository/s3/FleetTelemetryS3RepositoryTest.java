package com.deliverypf.fleet.telemetry.order.repository.s3;

import com.lorem.file.FileRepositoryService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FleetTelemetryS3RepositoryTest {

    private final String bucketName = "bucket-test";
    private final FileRepositoryService s3 = mock(FileRepositoryService.class);
    private final String tracks = "[{\"trackDate\":\"2019-01-01\",\"latitude\":10.0,\"longitude\":20.0}]";
    private final OrderS3RepositoryImpl repository = new OrderS3RepositoryImpl(s3, bucketName);

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullTrackKeyNameOnSave() {
        repository.save(null, this.tracks);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyTracksOnSave() {
        repository.save(new TrackKey(1, "br"), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullTracksOnSave() {
        repository.save(new TrackKey(1, "br"), null);
    }

    @Test
    public void shouldCallOncePutObject() {
        repository.save(new TrackKey(1, "br"), tracks);
        verify(s3, times(1)).put(anyString(), anyString(), anyString());
    }

    @Test
    public void shouldSaveWithCorrectBucketName() {
        repository.save(new TrackKey(1, "br"), tracks);
        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(s3).put(argument.capture(), anyString(), anyString());
        assertEquals(bucketName, argument.getValue());
    }

    @Test
    public void shouldSaveWithValidContent() {
        repository.save(new TrackKey(1, "br"), tracks);
        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(s3).put(anyString(), anyString(), argument.capture());
        assertNotNull(argument.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullTrackKeyNameOnFind() {
        repository.findByKey(null);
    }

    @Test
    public void shouldFindWithCorrectBucketName() {
        when(s3.getString(anyString(), anyString())).thenReturn("Valid content");

        repository.findByKey(new TrackKey(1, "br"));
        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(s3).getString(argument.capture(), anyString());
        assertEquals(bucketName, argument.getValue());
    }

}
