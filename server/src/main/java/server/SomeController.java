package server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class SomeController {

    @GetMapping("/")
    @ResponseBody
    public String get() {
        return "Received get request!";
    }

    @PostMapping("/")
    @ResponseBody
    public String post() {
        return "Received post request!";
    }

    @PutMapping("/")
    @ResponseBody
    public String put() {
        return "Received put request!";
    }

    @DeleteMapping("/")
    @ResponseBody
    public String delete() {
        return "Received delete request!";
    }
}