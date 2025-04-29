package com.NikolaySHA.ExclusiveService.util;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap();
    private final Map<String, Instant> blockedUsers = new ConcurrentHashMap();
    
    public LoginAttemptService() {
    }
    
    public void loginFailed(String email) {
        if (!this.isBlocked(email)) {
            this.attemptsCache.put(email, this.attemptsCache.getOrDefault(email, 1) + 1);
            if (this.attemptsCache.get(email) >= 3) {
                this.blockedUsers.put(email, Instant.now().plusMillis(120000L));
            }
            
        }
    }
    public boolean isBlocked(String email) {
        if (!this.blockedUsers.containsKey(email)) {
            return false;
        } else if (Instant.now().isAfter(this.blockedUsers.get(email))) {
            this.blockedUsers.remove(email);
            this.attemptsCache.remove(email);
            return false;
        } else {
            return true;
        }
    }
    public void loginSucceeded(String email) {
        this.attemptsCache.remove(email);
        this.blockedUsers.remove(email);
    }
}
