package com.aslmk.cloudfilestorage;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

import java.util.concurrent.ConcurrentHashMap;

@TestConfiguration(proxyBeanMethods = false)
@Profile("test")
public class NoRedisSessionTestConfig {

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    @Primary
    public SessionRepository<? extends Session> sessionRepository() {
        return new MapSessionRepository(new ConcurrentHashMap<>());
    }
}
