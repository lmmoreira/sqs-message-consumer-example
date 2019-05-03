package com.deliverypf.fleet.telemetry.order.scheduler;

import com.deliverypf.fleet.telemetry.order.services.ConsolidationService;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;


@Component
public class ExceptionalConsolidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionalConsolidation.class);

    private static final String TWENTY_TWO_HOUR = "PT22H";
    private static final String TWENTY_THREE_HOUR = "PT23H";
    private static final String EVERY_FIVE_AM = "0 0 5 * * ?";

    private final ConsolidationService defaultConsolidationService;
    private final boolean toggle;

    @Autowired
    public ExceptionalConsolidation(final ConsolidationService defaultConsolidationService,
                                    @Value("${fleet.telemetry.order.consolidation.active.toggle:false}") final boolean toggle) {
        this.defaultConsolidationService = defaultConsolidationService;
        this.toggle = toggle;
    }

    @Scheduled(cron = EVERY_FIVE_AM)
    @SchedulerLock(name = "ExceptionConsolidation_consolidationTask",
                   lockAtLeastForString = TWENTY_TWO_HOUR,
                   lockAtMostForString = TWENTY_THREE_HOUR)
    public void consolidationTask() {

        if (!toggle) {
            return;
        }

        LOGGER.debug("Start Exception consolidation");

        final ZonedDateTime startDate = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).minusYears(100);
        final ZonedDateTime finalDate = ZonedDateTime.now().withHour(5).withMinute(0).withSecond(0).minusDays(2);

        defaultConsolidationService.trackConsolidationDateTime(ConsolidationService.Type.ORDER, startDate, finalDate);
        defaultConsolidationService.trackConsolidationDateTime(ConsolidationService.Type.ROUTE, startDate, finalDate);

        LOGGER.debug("End Exception consolidation");

    }

}
