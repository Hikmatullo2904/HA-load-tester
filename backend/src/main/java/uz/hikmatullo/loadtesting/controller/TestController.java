package uz.hikmatullo.loadtesting.controller;

import org.springframework.web.bind.annotation.*;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;

@RestController
@RequestMapping("/test")
public class TestController {

    public static int counter;

    @GetMapping
    public String test(@RequestHeader String data) {
        counter++;
        return "test";
    }

    @GetMapping("/counter")
    public int counter() {
        int c = counter;
        counter = 0;
        return c;
    }

    @PostMapping
    public String post(@RequestBody NodeConnectRequest request) {
        counter++;
        return "test";
    }

}
