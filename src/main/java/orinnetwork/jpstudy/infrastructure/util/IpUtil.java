package orinnetwork.jpstudy.infrastructure.util;

import jakarta.servlet.http.HttpServletRequest;


public class IpUtil {

    /**
     * Cloudflare 및 프록시 환경을 고려하여 실제 클라이언트 IP를 반환합니다.
     *
     * @param request HttpServletRequest
     * @return 클라이언트 IP 주소
     */
    public static String getClientIp(HttpServletRequest request) {
        // 1. Cloudflare 헤더 (최우선)
        String ip = request.getHeader("CF-Connecting-IP");

        // 2. 일반적인 프록시 헤더 (X-Forwarded-For)
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }

        // 3. 기타 웹서버 프록시 헤더들
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        // 4. 최후의 수단 (프록시가 없을 경우)
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 5. X-Forwarded-For에 여러 IP가 있는 경우, 첫 번째 IP 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }

        return ip;
    }
}
