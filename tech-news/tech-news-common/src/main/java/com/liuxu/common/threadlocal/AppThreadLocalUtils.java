package com.liuxu.common.threadlocal;

public class AppThreadLocalUtils {
    private final static ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程中的用户Id
     * 
     * @param userId
     */
    public static void setUserId(Long userId) {
        userThreadLocal.set(userId);
    }

    /**
     * 获取线程中的用户Id
     * 
     * @return
     */
    public static Long getUserId() {
        return userThreadLocal.get();
    }

    /**
     * 清空线程中的用户信息
     */
    public static void clear() {
        userThreadLocal.remove();
    }
}
