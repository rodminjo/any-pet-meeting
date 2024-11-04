# TDD 기반 Hexagonal Architecture Demo 회원관리 시스템

## 목차

---

- [📖 학습 동기](#-학습-동기)
- [📋 목표](#-목표)
    - [TDD(테스트 주도 개발)](#tdd테스트-주도-개발)
    - [Hexagonal Architecture](#hexagonal-architecture)
    - [Spring Security6](#spring-security6)
    - [Spring Cloud Config](#spring-cloud-config)
    - [SpringRestDoc + Swagger (문서 자동화)](#springrestdoc--swagger-문서-자동화)
- [💻 구현 기능](#-구현-기능)
    - [✅ 회원 관리](#-회원-관리)
    - [✅ 문서 자동화](#-문서-자동화)
    - [✅ 설정 서버화](#-설정-서버화)
- [📚 기술 스택](#-기술-스택)
- [⚙️ 시스템 구성](#️-시스템-구성)


## 📖 동기

---

- 프로젝트를 수행할 때 자주 사용되는 공통적인 코드와 기능들이 있다는 점을 지속적으로 체감
- 이를 반복해서 작성하기보다 공통모듈로 만들어 재활용하면 효율성을 높일 수 있을 것이라는 판단
- 특히 자주 사용되는 회원관리 모듈화를 목표로 수립

## 📋 목표

---

### TDD(테스트 주도 개발)

1. 테스트 주도 개발로 핵심비즈니스 로직에 대한 테스트코드 작성, 안정성과 신뢰성 있는 시스템 구축
2. 개발 초기부터 오류를 발견하여 코드 품질 향상
3. 코드수정시 사이드 이펙트를 확인 가능하여 유지보수성 향상

### Hexagonal Architecture

1. 의존성을 분리해 비즈니스 로직이 외부 요소에 의존하지 않도록 시스템 구현
2. 웹 계층, 영속 계층을 손쉽게 교체 가능
3. 도메인 중심 설계로 회원 관리 기능의 확장성 보장

### Spring Security6

1. 인증과 권한 관리를 통한 보안 강화
2. 사용자 데이터 보호 및 안전한 액세스 관리
3. SpringBoot 3.x 에 적용된 6 버전에 맞춘 기능 구현

### Spring Cloud Config

1. 중앙화된 설정 관리로 일관된 환경 설정 지원
2. 여러 환경에서 유지보수와 설정 변경 용이

### SpringRestDoc + Swagger (문서 자동화)

1. 자동화된 API 문서 생성을 통해 이해도와 접근성 향상
2. 문서 유지보수 용이성 강화
3. SpringRestDoc을 이용한 테스트코드 강제화
4. SpringRestDoc + Swagger를 활용한 문서 UI 개선

### MSA 에 적용가능하도록 구조 변경 
1. 진행중..

## 💻 구현 기능

---

### ✅ **회원 관리**

- 기본, SNS(구글, 네이버, 카카오) 회원가입
- 가입 회원 인증 이메일 발송 및 검증
- Spring Security Custom Filter 를 이용한 JWT 인증 방식의 로그인
- Spring Security Custom Filter 를 이용한 Refresh Token 재발급
- 계정별 권한 설정
- 자원별 권한 설정
- 권한에 따른 커스텀 자원 접근 관리 시스템 
- 비동기 작업시 SecurityContext에 존재하는 계정 정보 조회 가능

### ✅ 문서 자동화

- 테스트 코드 작성시 yaml 형식의 OAS 문서 생성
- 생성된 OAS문서 Swagger UI로 조회

### ✅ 설정 서버화

- Spring Cloud Config를 이용한 Security 등 주요 설정 서버화
- 설정 변경시 즉시 반영되도록 수정

## 📚 기술 스택

---

[![Java](https://skillicons.dev/icons?i=java)](https://skillicons.dev)
[![Spring Boot](https://skillicons.dev/icons?i=spring)](https://skillicons.dev)
[![Redis](https://skillicons.dev/icons?i=redis)](https://skillicons.dev)
[![MySQL](https://skillicons.dev/icons?i=mysql)](https://skillicons.dev)

- Java 17
- SpringBoot 3.2.1
- Spring Security 6.2.1
- Spring Data JPA 3.2.1
- Spring Cloud Config 4.1.2
- Redis 7.2.5
- MySQL 8.0



