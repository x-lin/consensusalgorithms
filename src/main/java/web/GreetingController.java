package web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author LinX
 */
@RestController
public class GreetingController {
    private static final String template = "Long live Solomon, %s!";

    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting( @RequestParam(value = "name", defaultValue = "World") final String name ) {
        return new Greeting( this.counter.incrementAndGet(),
                String.format( template, name ) );
    }
}
