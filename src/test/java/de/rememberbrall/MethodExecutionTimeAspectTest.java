package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.rememberbrall.logging.MemoryAppender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MethodExecutionTimeAspectTest extends MockitoTest {


    private static final String JOIN_POINT = "joinPoint";
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private Signature signature;
    @Mock
    private Object object;
    @Mock
    private Logger logger;

    @Test
    public void verifyProceedHasBeenCalledAndObjectReturned() throws Throwable {

        //given
        MethodExecutionTimeAspect aspect = new MethodExecutionTimeAspect();
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn(JOIN_POINT);
        when(joinPoint.proceed()).thenReturn(object);

        String expectedLogMessage = "Time taken by method execution of joinPoint";

        //When
        Object returnObject = aspect.measureMethodExecutionTimes(joinPoint);

        //Then
        verify(joinPoint).proceed();
//        verify(logger).info("Time taken by method execution of {} is {} milliseconds", JOIN_POINT, 2);
        assertThat(returnObject.toString()).isEqualTo("object");
    }

    @Test
    public void testLog() throws Throwable {
        //given
        MethodExecutionTimeAspect aspect = new MethodExecutionTimeAspect();
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn(JOIN_POINT);
        when(joinPoint.proceed()).thenReturn(object);

        String expectedLogMessage = "Time taken by method execution of joinPoint";

        //When
        aspect.measureMethodExecutionTimes(joinPoint);

        //Then
        List<ILoggingEvent> loggingEvents = MemoryAppender.LOG_MESSAGES.stream()
                .filter(loggingEvent -> loggingEvent.getLevel() == Level.INFO)
                .filter(loggingEvent -> loggingEvent.getFormattedMessage().contains(expectedLogMessage))
                .collect(Collectors.toList());

        assertThat(loggingEvents).hasSize(1);
    }
}