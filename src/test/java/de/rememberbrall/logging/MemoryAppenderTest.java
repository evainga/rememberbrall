package de.rememberbrall.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import ch.qos.logback.classic.spi.LoggingEvent;

public class MemoryAppenderTest {
    @Test
    public void append() {
        // given
        MemoryAppender appender = new MemoryAppender();
        appender.start();
        LoggingEvent event = new LoggingEvent();

        // when
        appender.append(event);

        // then
        assertThat(MemoryAppender.LOG_MESSAGES).hasSize(34);
    }
}