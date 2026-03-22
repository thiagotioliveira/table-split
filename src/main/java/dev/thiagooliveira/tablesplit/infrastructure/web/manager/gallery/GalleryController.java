package dev.thiagooliveira.tablesplit.infrastructure.web.manager.gallery;

import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gallery")
@ManagerModule(Module.GALLERY)
public class GalleryController {

  @GetMapping
  public String index(Model model) {
    return "gallery";
  }
}
