package io.amtech.projectflow.test;

import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

public class TransactionalUtils {
    public TransactionalUtils() {
    }

    @Transactional
    public <R> R txRun(Supplier<R> supplier) {
        return supplier.get();
    }

    @Transactional
    public void txRun(Runnable runnable) {
        runnable.run();
    }
}
