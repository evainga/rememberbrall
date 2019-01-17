package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CustomWebFilterTest {

    private CustomWebFilter customWebFilter = new CustomWebFilter();

    @Test
    public void filterForRootUrl() {
        //Given
        WebFilterChain webFilterChain = mock(WebFilterChain.class);
        MockServerHttpRequest mockServerHttpRequest = MockServerHttpRequest.get("/").build();
        MockServerWebExchange mockServerWebExchange = MockServerWebExchange.from(mockServerHttpRequest);

        //When
        customWebFilter.filter(mockServerWebExchange, webFilterChain);

        //Then
        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(webFilterChain).filter(captor.capture());
        ServerWebExchange webExchange = captor.getValue();
        assertThat(webExchange.getRequest().getPath().pathWithinApplication().value()).isEqualTo("/docs/index.html");
    }

    @Test
    public void filterForAllOtherUrls() {
        //Given
        WebFilterChain webFilterChain = mock(WebFilterChain.class);
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
