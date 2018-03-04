package de.rememberbrall.logging;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class MemoryAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    public static final List<ILoggingEvent> LOG_MESSAGES = new ArrayList<>();

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }

        event.prepareForDeferredProcessing();
        LOG_MESSAGES.add(event);
    }
}
