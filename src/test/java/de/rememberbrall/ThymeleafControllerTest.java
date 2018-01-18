package de.rememberbrall;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Flux;

public class ThymeleafControllerTest {

    @InjectMocks
    private ThymeleafController thymeleafController;
    @Mock
    private RememberbrallService rememberbrallService;
    @Mock
    private Entry entry;
    @Mock
    private Model model;

    @BeforeTest
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    //    @Test
    //    public void forwardToRestDocumentation() {
    //        ModelAndView modelAndView = rememberbrallController.forwardToRestDocumentation();
    //        assertThat(modelAndView.getViewName()).isEqualTo("forward:/docs/index.html");
    //    }

    @Test
    public void showAllEntries() {
        //given
        when(rememberbrallService.getAllEntries()).thenReturn(Flux.just(entry, entry, entry));
        //when
        String showAllEntries = thymeleafController.showAllEntries(model);
        //then
        assertThat(showAllEntries).isEqualTo("thymeleaf-entries");
    }
}
