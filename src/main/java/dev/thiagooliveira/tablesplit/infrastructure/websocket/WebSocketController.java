package dev.thiagooliveira.tablesplit.infrastructure.websocket;

import dev.thiagooliveira.tablesplit.infrastructure.websocket.model.Greeting;
import dev.thiagooliveira.tablesplit.infrastructure.websocket.model.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.name()) + "!");
    }

    @GetMapping("/websocket-test")
    public String websocketTest() {
        return "websocket-test";
    }
}
