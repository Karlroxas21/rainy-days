package com.rainydaysengine.rainydays.application.service.ipratelimit;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;

@Service
public class IpRateLimiter {

    private final ProxyManager<String> proxyManager;
    private final BucketConfiguration configuration;

    @Value("${ratelimit.max}")
    private int MAX_REQUEST_PER_MINUTE;


    public IpRateLimiter(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
        this.configuration = BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(MAX_REQUEST_PER_MINUTE, Refill.intervally(1, Duration.ofMinutes(1))))
                .build();
    }

    public ConsumptionProbe tryConsumeAndGetProbe(String ip) {
        Bucket bucket = proxyManager.getProxy("ip-rl:" + ip, () -> configuration);
        return bucket.tryConsumeAndReturnRemaining(1);
    }
}