package com.deliverypf.fleet.telemetry.order.repository.s3;

import com.lorem.file.FileRepositoryService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RouteS3RepositoryImplTest {

    private final String bucketName = "bucket-test";
    private final FileRepositoryService s3 = mock(FileRepositoryService.class);
    private final String tracks = "[{\"trackDate\":\"2019-01-01\",\"latitude\":10.0,\"longitude\":20.0}]";
    private final RouteS3RepositoryImpl repository = new RouteS3RepositoryImpl(s3, bucketName);

    @Test
    public void shouldSaveWithCorrectFileName() {
        repository.save(new TrackKey(1, "br"), tracks);
        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(s3).put(anyString(), argument.capture(), anyString());
        assertEquals("br/routes/1-br.json", argument.getValue());
    }

    @Test
    public void shouldFindWithCorrectFilename() {
        when(s3.getString(anyString(), anyString())).thenReturn(tracks);

        repository.findByKey(new TrackKey(1, "br"));
        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(s3).getString(anyString(), argument.capture());
        assertEquals("br/routes/1-br.json", argument.getValue());
    }

}
