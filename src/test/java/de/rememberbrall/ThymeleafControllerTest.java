package de.rememberbrall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import reactor.core.publisher.Flux;

public class ThymeleafControllerTest {

    private ThymeleafController thymeleafController;

    private final RememberbrallService rememberbrallService = mock(RememberbrallService.class);
    private final Entry entry = mock(Entry.class);
    private final Model model = mock(Model.class);

    @Before
    public void initMocks() {
        thymeleafController = new ThymeleafController(rememberbrallService);

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
