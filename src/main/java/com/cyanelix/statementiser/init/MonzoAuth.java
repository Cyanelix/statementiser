package com.cyanelix.statementiser.init;

import com.cyanelix.statementiser.client.MonzoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class MonzoAuth {
    private final MonzoClient monzoClient;

    @Autowired
    public MonzoAuth(MonzoClient monzoClient) {
        this.monzoClient = monzoClient;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            monzoClient.requestAuthorisationCode();
        } catch (HttpStatusCodeException ex) {
            System.err.println(ex.getResponseBodyAsString());
            System.exit(1);
        }
    }
}
