package orinnetwork.jpstudy.domain.post;

public enum PostStatus {
    ACTIVE,     // 공개 상태
    SUSPENDED,  // 중단 상태 (명확한 사유는 알 수 없지만 게시글을 중단해야할 때),
    REJECTED,   // 금지 상태
    DELETED,    // 삭제
}