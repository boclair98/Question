# Question

> 직무와 면접 유형을 선택하면 맞춤형 면접 질문을 제공하는 Spring Boot 기반 면접 시뮬레이션 서비스

## 프로젝트 소개

`Question`은 면접을 준비하는 사용자가 직무와 면접 유형을 선택하고, 실제 면접처럼 제한 시간 안에 답변을 연습할 수 있도록 만든 웹 서비스입니다.

회원가입과 로그인 이후 직무를 선택하면 인성 면접 또는 직무 면접 질문을 랜덤으로 제공하며, 질문 화면에서는 40초 타이머와 질문 기록을 함께 보여줍니다.

## 주요 기능

| 기능 | 설명 |
| --- | --- |
| 회원가입 | 이름, 이메일, 비밀번호, 성별, 직무, 경력, 전화번호, 나이를 입력받고 Bean Validation으로 기본 검증을 수행합니다. |
| 로그인 | 이메일과 비밀번호를 기반으로 사용자를 조회하고, 로그인 정보를 Session에 저장합니다. |
| 직무 선택 | 개발자, 디자이너, 마케팅, 영업, 반도체 엔지니어 중 면접 직무를 선택할 수 있습니다. |
| 면접 유형 선택 | 인성 면접과 직무 면접 중 원하는 유형을 선택합니다. |
| 랜덤 질문 제공 | 선택한 직무와 면접 유형에 맞는 질문 목록을 섞어 랜덤 질문을 제공합니다. |
| 면접 타이머 | 질문 확인 시간 10초와 답변 시간 30초를 합쳐 총 40초 타이머를 제공합니다. |
| 질문 기록 | 브라우저 LocalStorage를 활용해 현재 세션에서 나온 질문 기록을 사이드바에 표시합니다. |

## 기술 스택

### Backend

- Java 17
- Spring Boot 3.5.4
- Spring MVC
- Spring Data JPA
- Bean Validation
- Lombok

### Frontend

- Thymeleaf
- HTML/CSS
- Tailwind CSS
- JavaScript

### Database

- MySQL

### Build

- Gradle

## 프로젝트 구조

```text
src
└── main
    ├── java/com/example/interview
    │   ├── Controller
    │   │   └── MainController.java
    │   ├── Entity
    │   │   ├── User.java
    │   │   └── Question.java
    │   ├── Enum
    │   │   ├── Career.java
    │   │   ├── Gender.java
    │   │   └── Job.java
    │   ├── Repository
    │   │   └── UserRepository.java
    │   └── Service
    │       ├── QuestionService.java
    │       └── UserService.java
    └── resources
        ├── application.properties
        └── templates
            ├── index.html
            ├── interview
            │   ├── choice.html
            │   ├── question.html
            │   └── questionadd.html
            └── user
                ├── login.html
                └── signup.html
```

## 핵심 구현 내용

### 1. Session 기반 로그인 상태 관리

로그인 성공 시 `loginUser` 객체를 Session에 저장하고, 메인 화면과 면접 시작 요청에서 로그인 여부를 확인합니다.

```java
session.setAttribute("loginUser", user);
```

이를 통해 로그인하지 않은 사용자가 면접을 시작하거나 질문 추가 페이지에 접근하려는 경우 로그인 화면으로 이동하도록 처리했습니다.

### 2. 직무/유형 기반 질문 분리

`QuestionService`에서 질문을 `personalityQuestions`, `technicalQuestions`로 분리하고, 직무 값에 따라 질문 목록을 선택합니다.

```java
if ("personality".equals(type)) {
    pool = personalityQuestions.getOrDefault(job, List.of());
} else if ("technical".equals(type)) {
    pool = technicalQuestions.getOrDefault(job, List.of());
}
```

질문 목록은 `Collections.shuffle()`로 섞은 뒤 지정한 개수만 반환하여 사용자가 매번 다른 질문을 받을 수 있도록 구성했습니다.

### 3. 회원가입 입력 검증

`User` 엔티티에 `@NotBlank`, `@Email`, `@Size`, `@NotNull`, `@Min` 등의 검증 애노테이션을 적용했습니다.

이를 통해 회원가입 단계에서 필수 입력값 누락, 이메일 형식 오류, 비밀번호 길이 부족 등을 서버 측에서 검증합니다.

### 4. 40초 면접 타이머

질문 화면에서 JavaScript를 활용해 총 40초 타이머를 제공합니다.

- 질문 확인 시간: 10초
- 답변 시간: 30초
- 남은 시간이 10초 이하일 때 경고 스타일 적용
- 시간이 종료되면 다음 질문 요청

## 실행 방법

### 1. 저장소 클론

```bash
git clone https://github.com/boclair98/Question.git
cd Question
```

### 2. MySQL 데이터베이스 생성

```sql
CREATE DATABASE interview;
```

### 3. DB 설정

`src/main/resources/application.properties`에서 로컬 MySQL 환경에 맞게 DB 접속 정보를 설정합니다.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/interview?useSSL=false&useUnicode=true&serverTimezone=Asia/Seoul
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 4. 애플리케이션 실행

```bash
./gradlew bootRun
```

Windows 환경에서는 아래 명령어를 사용할 수 있습니다.

```bash
gradlew.bat bootRun
```

### 5. 접속

```text
http://localhost:8080
```

## 화면 흐름

```text
메인 화면
  → 로그인 / 회원가입
  → 직무 선택
  → 면접 유형 선택
  → 랜덤 질문 제공
  → 40초 타이머 기반 답변 연습
  → 다음 질문 반복
```

## 배운 점

- Spring MVC 구조에서 Controller, Service, Repository의 역할을 분리하는 흐름을 경험했습니다.
- Session을 활용해 로그인 상태를 유지하고, 사용자 상태에 따라 페이지 접근을 제어했습니다.
- Bean Validation을 사용해 폼 입력값을 서버 측에서 검증하는 방법을 익혔습니다.
- Thymeleaf와 Spring MVC를 연결해 서버 렌더링 기반 웹 화면을 구성했습니다.
- 질문 데이터를 직무와 면접 유형 기준으로 분리하면서 서비스 로직을 구조화하는 방법을 학습했습니다.

## 개선 예정

- 질문 데이터를 코드 내부 `Map`이 아닌 DB 테이블로 관리
- 질문 추가 기능과 관리자 검수 플로우 구현
- 비밀번호 암호화 적용
- 로그아웃 기능 구현
- 직무/면접 유형별 질문 중복 방지 고도화
- Spring Security 기반 인증/인가 구조로 개선
- 테스트 코드 보강

## 프로젝트 의의

이 프로젝트는 단순히 질문을 출력하는 기능을 넘어서, 사용자의 선택값에 따라 면접 흐름을 다르게 구성하고 제한 시간 안에 답변을 연습할 수 있도록 만든 서비스입니다.

Spring Boot, Thymeleaf, JPA, MySQL을 활용해 웹 서비스의 기본 구조를 직접 구현하며 백엔드 개발의 핵심 흐름인 요청 처리, 서비스 로직 분리, 데이터 저장, 입력 검증, 화면 연동을 경험했습니다.
