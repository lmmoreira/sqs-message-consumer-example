package com.deliverypf.fleet.telemetry.order.exceptions;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class WorkerException extends RuntimeException {

    private static final Logger logger = getLogger(WorkerException.class);

    public WorkerException(final Throwable cause) {
        super(cause);
        logger.error("WorkerException cause {} ", cause);
    }

}
