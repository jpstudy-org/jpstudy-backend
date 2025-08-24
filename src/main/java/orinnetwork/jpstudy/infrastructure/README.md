# 인프라스트럭처 계층
- 어떻게 구현할 것인가?

## 주요 역할
- 도메인 계층의 Repository 인터페이스 구현
- 데이터베이스 접근 및 영속성 관리
- 외부 API 클라이언트
- 메시지 큐 연동
- 프레임워크 관련 설정 및 구현

## 주요 구성 요소
- JPA/NMyBatis 구현체
- RestTemplate, WebClient 등 외부 통신 모듈
- S3UJploader 등 외부 서비스 연동 클래스
- Spring Security, Caching 등 프레임워크 설정