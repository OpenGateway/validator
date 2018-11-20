package com.opengateway.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

@RunWith(SpringRunner.class)
public class FluxTest {

    @Test
    public void simpleFlux() {
        System.out.println("1");
        Flux<Integer> myFlux = Flux.just("csaa5", "dsds56", "dsds567", "dsds").map(String::length).log();
        System.out.println("2");

        myFlux.subscribe(new Subscriber<Integer>() {
            private Subscription s;
            int onNextAmount;

            @Override
            public void onSubscribe(Subscription s) {
                this.s = s;
                s.request(2);

            }

            @Override
            public void onNext(Integer integer) {

                System.out.println("onNext "+integer);
                onNextAmount++;
                if (onNextAmount % 2 == 0) {
                    s.request(2);
                }

            }

            @Override
            public void onError(Throwable t) {
                System.out.println("error");
            }

            @Override
            public void onComplete() {
                System.out.println("done");
            }
        });
        System.out.println("3");
    }
}
