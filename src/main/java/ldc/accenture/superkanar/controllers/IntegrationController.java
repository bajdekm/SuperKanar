package ldc.accenture.superkanar.controllers;

import ldc.accenture.superkanar.managers.RequestManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/i")
public class IntegrationController {

    private RequestManager requestManager = RequestManager.getInstance();
    private String authCode;
    private String access_token;

    @RequestMapping("/handshake")
    public ModelAndView handshakeSalesforce(HttpServletRequest request){
        ModelAndView mov = new ModelAndView("welcome");
        requestManager.fillInParamMap(request);
        this.authCode = requestManager.extractCodeFromResponse();
        requestManager.sendOAuthAuthRequest(this.authCode);
        return mov;
    }
}
