package ldc.accenture.superkanar.controllers;

import ldc.accenture.superkanar.managers.RequestManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Slf4j
public class HomeController {

    private RequestManager requestManager = RequestManager.getInstance();

    @RequestMapping(value="/" , method = RequestMethod.GET)
    public String welcome(){
        try {
            requestManager.initializeHandshake();
        } catch (Exception e) {
            log.error("Not able to initialize handshake with Salesforce");
            e.printStackTrace();
        }
        return "welcome";
    }
}
