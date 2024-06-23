OAuth2 프로토콜 기반의 인증 방식을 적용합니다.
JWT를 활용한 인증코드를 이용 합니다.

본 프로젝트는 4가지 인증 방식을 구성하고 상황에 따라 적용하기 위함 입니다.

1. Authorization Code Grant │권한 부여 승인 코드 방식
2. Implicit Grant │ 암묵적 승인 방식
3. Resource Owner Password Credentials Grant │ 자원 소유자 자격증명 승인 방식
4. Client Credentials Grant │ 클라이언트 자격증명 승인 방식

참고 : [OAuth2.0 Document](https://oauth.net/2/)

본 인증 방식을 위해 OAuth2 구성 요소항목을 모듈로 대체하여 구성 합니다.

## Client

- 인증코드 발급을 받기 위한 사용자 입니다.

## Resource-Owner

- 자격부여 주체이며 클라이언트와 통신에 직접적인 영향을 받습니다.
- Resource-Server와 Client간 원할한 인증이 가능 하도록 Proxy를 수행합니다.

## Resource-Server(Authorization-Server)

- 클라이언트에게 인증코드를 발급하고 관리합니다.
- 발급된 인증코드의 유효성, 권한 등 자격을 검증하는 인증 모듈 입니다.

## This Project

- 본 프로젝트는 3개의 서비스를 제공 합니다.
- 인증키 평문은 임의로 생성한 UUID를 활용합니다.

1. AccessToken 발급/인증
2. RefreshToken 발급/인증
3. 인증키생성/대칭키암호화/암호문압축
