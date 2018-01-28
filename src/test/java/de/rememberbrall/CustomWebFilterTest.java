package de.rememberbrall;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.testng.annotations.Test;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CustomWebFilterTest extends MockitoTest {

    @Mock
    private WebFilterChain webFilterChain;

    @InjectMocks
    private CustomWebFilter customWebFilter;


    @Test
    public void filterForRootUrl() {
        //Given
        MockServerHttpRequest mockServerHttpRequest = MockServerHttpRequest.options("/").build();
        MockServerWebExchange mockServerWebExchange = MockServerWebExchange.from(mockServerHttpRequest);

        ServerHttpRequest mutatedHttpRequest = mockServerWebExchange.getRequest().mutate().path("/docs/index.html").build();
        ServerWebExchange mutatedServerWebExchange = mockServerWebExchange.mutate().request(mutatedHttpRequest).build();

        when(webFilterChain.filter(mutatedServerWebExchange)).thenReturn(Mono.empty());

        //When
        Mono<Void> mono = customWebFilter.filter(mockServerWebExchange, webFilterChain);

        //Then
//        StepVerifier
//                .create(mono)
//                .expectNextCount(0)
//                .verifyComplete();
        verify(webFilterChain, atLeastOnce()).filter(mutatedServerWebExchange);
    }

    @Test
    public void filterForAllOtherUrls() {
        //Given
        MockServerHttpRequest mockServerHttpRequest = MockServerHttpRequest.options("/dummyUrl").build();
        MockServerWebExchange mockServerWebExchange = MockServerWebExchange.from(mockServerHttpRequest);
        when(webFilterChain.filter(mockServerWebExchange)).thenReturn(Mono.empty());

        //When
        Mono<Void> mono = customWebFilter.filter(mockServerWebExchange, webFilterChain);

        //Then
        StepVerifier
                .create(mono)
                .expectNextCount(0)
                .verifyComplete();
    }
}
