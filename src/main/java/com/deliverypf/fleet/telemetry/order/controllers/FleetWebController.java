package com.deliverypf.fleet.telemetry.order.controllers;

import com.deliverypf.fleet.telemetry.order.dto.TrackDTO;
import com.deliverypf.fleet.telemetry.order.services.ConsolidationService;
import com.deliverypf.fleet.telemetry.order.services.FleetWebService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/fleet/telemetry")
public class FleetWebController {

    private final FleetWebService orderFleetWebService;
    private final FleetWebService routeFleetWebService;
    private final ExecutorService executorService;

    public FleetWebController(@Qualifier("orderFleetWebService") final FleetWebService orderFleetWebService,
            @Qualifier("routeFleetWebService") final FleetWebService routeFleetWebService,
            @Value("${telemetry.amount.threads.consolidate:500}") final Integer amountThreadsConsolidate) {
        this.orderFleetWebService = orderFleetWebService;
        this.routeFleetWebService = routeFleetWebService;
        this.executorService = Executors.newFixedThreadPool(amountThreadsConsolidate);
    }

    @GetMapping("/{tenant}/route/{routeId}")
    public Flux<TrackDTO> getRouteTracks(@PathVariable("routeId") final Long routeId, @PathVariable("tenant")
    final String tenant) {
        return routeFleetWebService.getTracks(routeId, tenant);
    }

    @GetMapping("/{tenant}/order/{orderId}")
    public Flux<TrackDTO> getOrderTracks(@PathVariable("orderId") final Long orderId, @PathVariable("tenant")
    final String tenant) {
        return orderFleetWebService.getTracks(orderId, tenant);
    }

    @PostMapping("/order/consolidation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void orderconsolidation(@RequestParam("from") final String from, @RequestParam("to") final String to) {
        executorService.submit(() -> {
            final ZonedDateTime startDate = LocalDate.parse(from).atStartOfDay(ZoneId.of("Z")).withHour(5).withMinute(00);
            final ZonedDateTime finalDate = LocalDate.parse(to).atStartOfDay(ZoneId.of("Z")).withHour(5).withMinute(00);
            orderFleetWebService.trackConsolidation(ConsolidationService.Type.ORDER, startDate, finalDate);
        });
    }

    @PostMapping("/order/consolidation/{idTenant}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void orderConsolidationId(@PathVariable("idTenant") final String idTenant) {
        executorService.submit(
            () -> orderFleetWebService.trackConsolidationIdTenant(ConsolidationService.Type.ORDER, idTenant));
    }

    @PostMapping("/route/consolidation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void routeconsolidation(@RequestParam("from") final String from, @RequestParam("to") final String to) {
        executorService.submit(() -> {
            final ZonedDateTime startDate = LocalDate.parse(from).atStartOfDay(ZoneId.of("Z")).withHour(5).withMinute(00);
            final ZonedDateTime finalDate = LocalDate.parse(to).atStartOfDay(ZoneId.of("Z")).withHour(5).withMinute(00);
            routeFleetWebService.trackConsolidation(ConsolidationService.Type.ROUTE, startDate, finalDate);
        });
    }

    @PostMapping("/route/consolidation/{idTenant}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void routeConsolidationId(@PathVariable("idTenant") final String idTenant) {
        executorService.submit(
            () -> routeFleetWebService.trackConsolidationIdTenant(ConsolidationService.Type.ROUTE, idTenant));
    }

}
