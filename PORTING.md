## 포팅 매뉴얼 (GoatGam)

### 목적

- 타 환경(동료 PC, 새 서버, CI/CD 러너, 컨테이너)으로의 이식(Porting)을 빠르고 일관되게 수행하기 위한 절차와 체크리스트를 제공합니다.

## 1. 시스템 요구사항

- **Java 17** (JDK 17)
- **Gradle Wrapper** 동봉 사용 권장 (`goatgam/gradlew`)
- **PostgreSQL 14+** (로컬 또는 원격 RDS)
- **Docker 20+**와 Docker Engine (선택: 컨테이너 실행 시)
- OS: macOS/Linux 권장 (Windows는 WSL2 또는 Git Bash 권장)

## 2. 저장소 구조 요약

- **루트**: 프로젝트 개요 문서(`README.md`), 포팅 매뉴얼(`PORTING.md`)
- **`goatgam/`**: Spring Boot 애플리케이션 모듈
    - `build.gradle`, `settings.gradle`, `gradlew`, `gradle/`
    - `Dockerfile`
    - `src/main/java/com/sparta/goatgam/GoatgamApplication.java` (메인 진입점)
    - `src/main/resources/application.properties` (환경 변수 바인딩)

## 3. 환경 변수 정의 (.env 권장)

Spring Boot에서 `spring-dotenv`를 사용합니다. `goatgam/resources/.env` 파일을 생성하면 `application.properties`의 플레이스홀더에 주입됩니다.

```env
# Database (PostgreSQL)
DB_URL=jdbc:postgresql://<HOST>:5432/<DB_NAME>
DB_USERNAME=<DB_USER>
DB_PASSWORD=<DB_PASSWORD>

# JWT
JWT_SECRET_KEY=<A_STRONG_SECRET>

# Google Gemini
GEMINI_API_KEY=<YOUR_GEMINI_API_KEY>
```

- **필수 키**: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET_KEY`
- **선택 키**: `GEMINI_API_KEY` (AI 기능 사용 시)
- DB URL 예시: `jdbc:postgresql://localhost:5432/goatgam`

`application.properties` 주요 항목

- `spring.datasource.url=${DB_URL}`
- `spring.datasource.username=${DB_USERNAME}`
- `spring.datasource.password=${DB_PASSWORD}`
- `jwt.secret.key=${JWT_SECRET_KEY}`
- `google.gemini.api-key=${GEMINI_API_KEY}`
- JPA: `spring.jpa.hibernate.ddl-auto=update`

## 4. 데이터베이스 준비

- 로컬 Postgres 또는 원격 RDS 중 하나를 사용합니다.
- 최소 권장 권한: 대상 스키마에 대한 CREATE/ALTER/INSERT/UPDATE/DELETE 권한
- 신규 DB/유저 생성 예시

```sql
-- DB/유저 생성 예시 (운영상황에 맞게 조정)
CREATE DATABASE goatgam;
CREATE USER goatgam_user WITH ENCRYPTED PASSWORD 'strong_password';
GRANT ALL PRIVILEGES ON DATABASE goatgam TO goatgam_user;
```

## 5. 로컬 실행 (비도커)

1. 환경 파일 준비

```bash
cd goat-gam/goatgam
cp ../PORTING.md . # (참고용, 필수 아님)
# .env 파일 생성 (위 템플릿 참고)
```

2. 빌드 및 실행

```bash
# macOS/Linux
./gradlew clean build
./gradlew bootRun
```

3. 접속 확인

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## 6. JAR로 실행

```bash
cd goat-gam/goatgam
./gradlew clean bootJar
java -jar build/libs/*.jar
```

- `.env`를 같은 폴더(`goatgam/`)에 두면 자동 로드됩니다.
- 또는 환경 변수를 직접 export 후 실행해도 됩니다.

## 7. Docker로 실행

1. 사전 빌드(JAR)

```bash
cd goat-gam/goatgam
./gradlew clean bootJar
```

2. 이미지 빌드

```bash
docker build -t goatgam:local .
```

3. 컨테이너 실행

```bash
# .env 파일을 컨테이너에 주입하여 실행
docker run --rm \
  --env-file .env \
  -p 8080:8080 \
  --name goatgam \
  goatgam:local
```

4. 동작 확인

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## 8. 테스트

```bash
cd goat-gam/goatgam
./gradlew test
```

- 단위 테스트가 포함되어 있습니다. (JUnit5/Mockito)
- 통합 테스트가 별도 소스셋으로 구성되어 있는 경우, 추가 태스크 설정이 필요할 수 있습니다.

## 9. 포트/방화벽

- 기본 포트: `8080`
- 서버/CI 환경에서는 인바운드 정책에 8080 허용 필요 (또는 프록시/로드밸런서 뒤 배치)

## 10. 운영 권장 설정

- `spring.jpa.hibernate.ddl-auto`
    - 개발: `update`
    - 운영: `validate` 또는 마이그레이션 도구(Flyway/Liquibase) 권장
- `JWT_SECRET_KEY`: 충분히 길고 예측 불가한 값 사용
- 데이터베이스 커넥션 풀, 로깅(로그 로테이션) 환경 준비

## 11. 문제 해결 가이드(FAQ)

- **DB 연결 실패**: `DB_URL/USER/PASSWORD` 확인, 보안그룹/방화벽, DB 포트(5432) 확인
- **포트 충돌(8080)**: 다른 프로세스 사용 여부 확인 후 포트 변경(`server.port`)
- **도커에서 env 미반영**: `--env-file` 경로, 파일 인코딩/개행 확인
- **Swagger 접근 불가**: 애플리케이션 기동 로그에서 8080 리스닝 여부 확인

## 12. 참고 링크

- 로컬 Swagger: [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)
- README 기능/아키텍처 개요: [`README.md`](https://github.com/1-6P/goat-gam/blob/develop/README.md)
