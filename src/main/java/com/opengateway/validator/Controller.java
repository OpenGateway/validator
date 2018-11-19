package com.opengateway.validator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
class Controller {

    @GetMapping("/stub")
    Mono<String> stub() {
        return Mono.just("stub\n");
    }
}
