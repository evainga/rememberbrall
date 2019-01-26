package de.rememberbrall;

import java.time.Duration;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class ThymeleafController {

    private final RememberbrallService rememberbrallService;

    //        @GetMapping(path = "/")
    //        public ModelAndView forwardToRestDocumentation() {
    //            return new ModelAndView("forward:/docs/index.html");
    //    }
    //    This is being solved by a workaround in CustomWebFilterClass, see https://github.com/spring-projects/spring-boot/issues/9785

    @GetMapping(path = "/thymeleaf-entries")
    public String showAllEntries(final Model model) {
        final Flux<Entry> entryStream = this.rememberbrallService.getAllEntries().delayElements(Duration.ofMillis(50));
        final IReactiveDataDriverContextVariable entryDriver = new ReactiveDataDriverContextVariable(entryStream, 1, 1);
        model.addAttribute("entries", entryDriver);
        return "thymeleaf-entries";
    }


}
