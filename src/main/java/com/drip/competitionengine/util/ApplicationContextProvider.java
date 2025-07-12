// src/main/java/com/drip/competitionengine/util/ApplicationContextProvider.java
package com.drip.competitionengine.util;

import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        ctx = ac;                    // сохранится после старта Spring
    }

    public static <T> T getBean(Class<T> type) {
        return ctx.getBean(type);
    }
}
