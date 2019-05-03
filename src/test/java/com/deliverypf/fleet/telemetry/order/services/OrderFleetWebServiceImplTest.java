package com.deliverypf.fleet.telemetry.order.services;

import com.deliverypf.fleet.telemetry.order.dto.TrackDTO;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.OrderTrackDynamoDBRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.TrackRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.OrderTrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.s3.OrderS3RepositoryImpl;
import com.deliverypf.fleet.telemetry.order.repository.s3.S3Repository;
import com.deliverypf.fleet.telemetry.order.repository.s3.TrackKey;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderFleetWebServiceImplTest {

    @Test
    public void shouldSwitchOnlyWhenDynamoDBReturnEmpty() throws InterruptedException {
        final TrackRepository<OrderTrackEntity> ddbRepository = mock(OrderTrackDynamoDBRepository.class);
        when(ddbRepository.findByIdTenant(anyString())).thenReturn(getTracksFromDDB());
        final S3Repository<TrackKey, String> s3Repository = mock(OrderS3RepositoryImpl.class);
        when(s3Repository.findByKey(any())).thenReturn(getTracksFromS3());
        final ConsolidationService consolidationService = mock(ConsolidationService.class);
        final OrderFleetWebServiceImpl service =
            new OrderFleetWebServiceImpl(ddbRepository, s3Repository, consolidationService);

        final Flux<TrackDTO> flux = service.getTracks(1L, "BR");

        flux.subscribe(c -> {
            verify(ddbRepository, times(1)).findByIdTenant(anyString());
            verify(s3Repository, never()).findByKey(any());
        });
    }

    private String getTracksFromS3() {
        return "[{\"trackDate\":\"2019-03-22T18:20:19.410734Z\",\"latitude\":51.4826,\"longitude\":20.0077}]";
    }

    private List<OrderTrackEntity> getTracksFromDDB() {
        return Collections.singletonList(new OrderTrackEntity("1L", "2019-01-01", 1D, 2D));
    }


}
