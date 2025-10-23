package com.rainydaysengine.rainydays.application.service.ipratelimit;

import com.rainydaysengine.rainydays.errors.ApplicationError;
import com.rainydaysengine.rainydays.utils.ToJson;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RequestFilter extends OncePerRequestFilter {

    private final IpRateLimiter ipRateLimiter;

    public RequestFilter(IpRateLimiter ipRateLimiter) {
        this.ipRateLimiter = ipRateLimiter;
    }

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String ip = extractClientIp(request);
            // Think of this way:
            // Can I take 1 token from this bucket? If yes, how many tokens are left?
            ConsumptionProbe probe = ipRateLimiter.tryConsumeAndGetProbe(ip);

            // isConsumed() -> true if request is allowed (token successfully taken)
            // false if rate limit is exceeded (no tokens left)
            // System.out.println("Remaining token: " + probe.getRemainingTokens());
            if (!probe.isConsumed()) {
                // Too many request
                long waitForNanoSeconds = probe.getNanosToWaitForRefill();
                // +1 is a safety buffer.
                // Sample: waitForNanoSeconds = 0.3s then convert to seconds = 0. So without
                // +1, it would retry immediately and still blocked.
                long waitSeconds = TimeUnit.NANOSECONDS.toSeconds(waitForNanoSeconds) + 1;

                ApplicationError error = ApplicationError.tooManyRequest("Too many request", waitSeconds);

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write(ToJson.toJson(error.getErrorDetails()));

                return;
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Internal server error\"}");

        }
    }

    private String extractClientIp(HttpServletRequest req) {
        String xfwd = req.getHeader("X-Forwarded-For");
        if (xfwd != null && !xfwd.isEmpty()) {
            // might be comma-separated list
            return xfwd.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
