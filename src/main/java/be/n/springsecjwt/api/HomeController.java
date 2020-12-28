package be.n.springsecjwt.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping("/welcome")
    public String home(){
        return "<h1> Welcome to the home page </1>";
    }
}
