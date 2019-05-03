package com.deliverypf.fleet.telemetry.order.services;

import com.deliverypf.fleet.telemetry.order.repository.dynamodb.OrderTrackDynamoDBRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.RouteTrackDynamoDBRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.OrderTrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.RouteTrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.TrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.s3.OrderS3RepositoryImpl;
import com.deliverypf.fleet.telemetry.order.repository.s3.RouteS3RepositoryImpl;
import com.deliverypf.fleet.telemetry.order.repository.s3.TrackKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultConsolidationServiceTest {

    private DefaultConsolidationService defaultConsolidationService;

    private OrderS3RepositoryImpl orderS3RepositoryImpl;
    private RouteS3RepositoryImpl routeS3RepositoryImpl;
    private OrderTrackDynamoDBRepository orderTrackRepository;
    private RouteTrackDynamoDBRepository routeTrackRepository;
    private ObjectMapper jacksonObjectMapper;

    @Before
    public void before() {
        orderS3RepositoryImpl = mock(OrderS3RepositoryImpl.class);
        routeS3RepositoryImpl = mock(RouteS3RepositoryImpl.class);
        orderTrackRepository = mock(OrderTrackDynamoDBRepository.class);
        routeTrackRepository = mock(RouteTrackDynamoDBRepository.class);
        jacksonObjectMapper = new ObjectMapper();

        final List<OrderTrackEntity> orderTrackEntityList = new ArrayList<>();
        orderTrackEntityList.add(new OrderTrackEntity("11-br", "2019-02-28T13:59:14.001Z", 10.0, 11.0));
        orderTrackEntityList.add(new OrderTrackEntity("12-br", "2019-02-28T13:59:14.001Z", 10.0, 11.0));


        final List<RouteTrackEntity> routeTrackEntityList = new ArrayList<>();
        routeTrackEntityList.add(new RouteTrackEntity("11-br", "2019-02-28T13:59:14.001Z", 10.0, 11.0));

        when(orderTrackRepository.findByTwoTrackDate(any(), any())).thenReturn(orderTrackEntityList);
        when(routeTrackRepository.findByTwoTrackDate(any(), any())).thenReturn(routeTrackEntityList);

        when(routeTrackRepository.findByIdTenant(any())).thenReturn(routeTrackEntityList);


        defaultConsolidationService = new DefaultConsolidationService(orderS3RepositoryImpl, routeS3RepositoryImpl,
            orderTrackRepository, routeTrackRepository, jacksonObjectMapper);
    }


    @Test
    public void saveOrderItemOnS3() {
        final List<TrackEntity> orderList = new ArrayList<>();
        orderList.add(new OrderTrackEntity());

        final TrackKey key = new TrackKey("1234-mx");

        defaultConsolidationService.consolidate(ConsolidationService.Type.ORDER, key, orderList);
        verify(orderS3RepositoryImpl, times(1)).save(any(), any());
    }

    @Test
    public void saveOrderItemOnS3WhenThereIsAFile() {
        when(orderS3RepositoryImpl.findByKey(any())).thenReturn(
            "[{\"trackDate\":\"2019-03-19T17:19:59.703474Z\",\"latitude\":51.4826,\"longitude\":20.0077}," +
                "{\"trackDate\":\"2019-03-18T17:19:59.703474Z\",\"latitude\":51.4826,\"longitude\":20.0077}," +
                "{\"trackDate\":\"2019-03-19T17:19:59.703474Z\",\"latitude\":51.4826,\"longitude\":20.0077}," +
                "{\"trackDate\":\"2019-03-15T17:19:59.703474Z\",\"latitude\":51.4826,\"longitude\":20.0077}," +
                "{\"trackDate\":\"2019-03-16T17:19:59.703474Z\",\"latitude\":51.4826,\"longitude\":20.0077}," +
                "{\"trackDate\":\"2019-03-17T17:19:59.703474Z\",\"latitude\":51.4826,\"longitude\":20.0077}," +
                "{\"trackDate\":\"2019-03-21T17:19:59.703474Z\",\"latitude\":51.4826,\"longitude\":20.0077}]");

        final List<TrackEntity> orderList = new ArrayList<>();
        orderList.add(new OrderTrackEntity());

        final TrackKey key = new TrackKey("1234-mx");

        defaultConsolidationService.consolidate(ConsolidationService.Type.ORDER, key, orderList);
        verify(orderS3RepositoryImpl, times(1)).save(any(), any());
    }

    @Test
    public void notSaveRouteItemOnS3() {
        final List<TrackEntity> orderList = new ArrayList<>();
        orderList.add(new OrderTrackEntity());

        final TrackKey key = new TrackKey("1234-mx");

        defaultConsolidationService.consolidate(ConsolidationService.Type.ORDER, key, orderList);

        verify(routeS3RepositoryImpl, times(0)).save(any(), any());
    }

    @Test
    public void saveRouteItemOnS3() {
        final List<TrackEntity> orderList = new ArrayList<>();
        orderList.add(new RouteTrackEntity());

        final TrackKey key = new TrackKey("1234-mx");

        defaultConsolidationService.consolidate(ConsolidationService.Type.ROUTE, key, orderList);
        verify(routeS3RepositoryImpl, times(1)).save(any(), any());
    }

    @Test
    public void deleteRouteItemOnDynamoDB() {
        final List<TrackEntity> orderList = new ArrayList<>();
        orderList.add(new RouteTrackEntity());

        final TrackKey key = new TrackKey("1234-mx");

        defaultConsolidationService.consolidate(ConsolidationService.Type.ROUTE, key, orderList);
        verify(routeTrackRepository, times(1)).delete(any());
    }

    @Test
    public void consolidateOrderBetwennDates() {
        defaultConsolidationService.trackConsolidationDateTime(ConsolidationService.Type.ORDER, any(), any());
        verify(orderS3RepositoryImpl, times(2)).save(any(), any());
    }

    @Test
    public void consolidateRouteByIdTenant() {
        defaultConsolidationService.trackConsolidationIdTenant(ConsolidationService.Type.ROUTE, "11-br");
        verify(routeS3RepositoryImpl, times(1)).save(any(), any());
    }

    @Test
    public void getAsMapTest() {
        final List<TrackEntity> orderTrackEntityList = new ArrayList<>();
        orderTrackEntityList.add(new OrderTrackEntity("11-br", "2019-02-28T13:59:14.001Z", 10.0, 11.0));
        orderTrackEntityList.add(new OrderTrackEntity("11-br", "2019-02-28T14:59:14.001Z", 10.0, 11.0));
        orderTrackEntityList.add(new OrderTrackEntity("11-br", "2019-02-28T15:59:14.001Z", 10.0, 11.0));
        orderTrackEntityList.add(new OrderTrackEntity("11-br", "2019-02-28T16:59:14.001Z", 10.0, 11.0));
        orderTrackEntityList.add(new OrderTrackEntity("12-br", "2019-02-28T16:59:14.001Z", 10.0, 11.0));
        orderTrackEntityList.add(new OrderTrackEntity("12-br", "2019-02-28T17:59:14.001Z", 10.0, 11.0));
        orderTrackEntityList.add(new OrderTrackEntity("12-br", "2019-02-28T18:59:14.001Z", 10.0, 11.0));
        orderTrackEntityList.add(new OrderTrackEntity("13-br", "2019-02-28T16:59:14.001Z", 10.0, 11.0));
        orderTrackEntityList.add(new OrderTrackEntity("14-br", "2019-02-28T17:59:14.001Z", 10.0, 11.0));

        final List<OrderTrackEntity> item1List = new ArrayList<>();
        item1List.add(new OrderTrackEntity("11-br", "2019-02-28T13:59:14.001Z", 10.0, 11.0));
        item1List.add(new OrderTrackEntity("11-br", "2019-02-28T14:59:14.001Z", 10.0, 11.0));
        item1List.add(new OrderTrackEntity("11-br", "2019-02-28T15:59:14.001Z", 10.0, 11.0));
        item1List.add(new OrderTrackEntity("11-br", "2019-02-28T16:59:14.001Z", 10.0, 11.0));
        final List<OrderTrackEntity> item2List = new ArrayList<>();
        item2List.add(new OrderTrackEntity("12-br", "2019-02-28T16:59:14.001Z", 10.0, 11.0));
        item2List.add(new OrderTrackEntity("12-br", "2019-02-28T16:59:14.001Z", 10.0, 11.0));
        item2List.add(new OrderTrackEntity("12-br", "2019-02-28T16:59:14.001Z", 10.0, 11.0));
        final List<OrderTrackEntity> item3List = new ArrayList<>();
        item3List.add(new OrderTrackEntity("13-br", "2019-02-28T16:59:14.001Z", 10.0, 11.0));
        final List<OrderTrackEntity> item4List = new ArrayList<>();
        item4List.add(new OrderTrackEntity("14-br", "2019-02-28T17:59:14.001Z", 10.0, 11.0));

        final Map<String, List<OrderTrackEntity>> assertionMap = new HashMap<>();
        assertionMap.put("11-br", item1List);
        assertionMap.put("12-br", item2List);
        assertionMap.put("13-br", item3List);
        assertionMap.put("14-br", item4List);

        final Map<String, List<TrackEntity>> map = defaultConsolidationService.getAsMap(orderTrackEntityList);

        Assert.assertEquals(assertionMap.get("11-br").size(), map.get("11-br").size());
        Assert.assertEquals(assertionMap.get("12-br").size(), map.get("12-br").size());
        Assert.assertEquals(assertionMap.get("13-br").size(), map.get("13-br").size());
        Assert.assertEquals(assertionMap.get("14-br").size(), map.get("14-br").size());
    }

}
