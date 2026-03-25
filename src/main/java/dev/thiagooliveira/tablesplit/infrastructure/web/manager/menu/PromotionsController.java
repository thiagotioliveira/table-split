package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu;

import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/promotions")
@ManagerModule(Module.PROMOTIONS)
public class PromotionsController {

    @GetMapping
    public String index() {
        return "promotions";
    }
}
