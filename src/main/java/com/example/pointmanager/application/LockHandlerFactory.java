package com.example.pointmanager.application;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class LockHandlerFactory {
    private PessimisticLockHandler pessimisticLockHandler;
    private OptimisticLockHandler optimisticLockHandler;

    public LockStrategyHandler getLockHandler(boolean usePessimisticLock) {
        if (usePessimisticLock) {
            return pessimisticLockHandler;
        } else {
            return optimisticLockHandler;
        }

    }
}
