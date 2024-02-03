# 서비스 구성도 
<img width="1561" alt="web_0203" src="https://github.com/hong9Lee/web_security/assets/94272140/20b9b125-00f5-4b78-9871-c0f63d992e44">


## 1. 쿠폰 발급
목적: 쿠폰 발급 로직을 통해 동시성 처리와 성능 비교  
설계:  
`redis set 자료구조를 이용한 user 중복 체크`  
`redis increment를 이용한 쿠폰 총 발급횟수 제한`  
`ngrinder를 통한 성능 테스트 (process: 10 thread: 100) - 1000명의 유저가 1분동안 트래픽 발생시키는 환경`  
  
#### 1. redis + kafka를 이용한 테스트

#### 2. redis + rabbitMQ를 이용한 테스트

#### 3. 발급 로직의 비동기 테스트
