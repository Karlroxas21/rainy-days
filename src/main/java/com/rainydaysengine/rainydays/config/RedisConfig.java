package com.rainydaysengine.rainydays.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;


    @Bean
    public RedisClient redisClient() {
        return RedisClient.create(RedisURI.create(this.redisHost));
    }

    @Bean
    public StatefulRedisConnection<String, byte[]> redisConnection(RedisClient redisClient) {
        return redisClient.connect(new StringByteCodec());
    }

    @Bean
    public ProxyManager<String> proxyManager(StatefulRedisConnection<String, byte[]> redisConnection) {
        return LettuceBasedProxyManager.builderFor(redisConnection)
                .build();
    }


    public static class StringByteCodec implements RedisCodec<String, byte[]> {
        @Override
        public String decodeKey(ByteBuffer bytes) {
            return StandardCharsets.UTF_8.decode(bytes).toString();
        }

        @Override
        public byte[] decodeValue(ByteBuffer bytes) {
            byte[] arr = new byte[bytes.remaining()];
            bytes.get(arr);

            return arr;
        }

        @Override
        public ByteBuffer encodeKey(String key ) {
            return StandardCharsets.UTF_8.encode(key);
        }

        @Override
        public ByteBuffer encodeValue(byte[] value) {
            return ByteBuffer.wrap(value);
        }
    }

}
