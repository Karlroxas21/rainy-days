package com.rainydaysengine.rainydays.application.service.ipratelimit;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IpRateLimiter {

    private final ProxyManager<String> proxyManager;
    private final BucketConfiguration configuration;

    // Injecting MAX_REQUEST_PER_MINUTE value in constructor because
    // @Value injection happens after the contrusctor is called.
    public IpRateLimiter(ProxyManager<String> proxyManager,
                         @Value("${ratelimit.max}") int MAX_REQUEST_PER_MINUTE) {
        this.proxyManager = proxyManager;
        this.configuration = BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(MAX_REQUEST_PER_MINUTE, Refill.intervally(MAX_REQUEST_PER_MINUTE, Duration.ofMinutes(1))))
                .build();
    }

    public ConsumptionProbe tryConsumeAndGetProbe(String ip) {
        Bucket bucket = proxyManager.getProxy("ip-rl:" + ip, () -> configuration);
        return bucket.tryConsumeAndReturnRemaining(1);
    }
}