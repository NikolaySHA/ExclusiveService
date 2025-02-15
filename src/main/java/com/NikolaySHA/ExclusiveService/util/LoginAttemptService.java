package com.NikolaySHA.ExclusiveService.util;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MS = 2 * 60 * 1000; // 2 минути
    
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Instant> blockedUsers = new ConcurrentHashMap<>();
    
    public void loginFailed(String email) {
        if (isBlocked(email)) {
            return;
        }
        
        attemptsCache.put(email, attemptsCache.getOrDefault(email, 1) + 1);
        
        if (attemptsCache.get(email) >= MAX_ATTEMPTS) {
            blockedUsers.put(email, Instant.now().plusMillis(BLOCK_DURATION_MS));
        }
    }
    
    public boolean isBlocked(String email) {
        if (!blockedUsers.containsKey(email)) {
            return false;
        }
        
        if (Instant.now().isAfter(blockedUsers.get(email))) {
            blockedUsers.remove(email);
            attemptsCache.remove(email);
            return false;
        }
        
        return true;
    }
    
    public void loginSucceeded(String email) {
        attemptsCache.remove(email);
        blockedUsers.remove(email);
    }
}
