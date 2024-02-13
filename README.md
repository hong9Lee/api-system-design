# 서비스 구성도 
_AS-IS_  
<img width="1561" alt="web_0203" src="https://github.com/hong9Lee/web_security/assets/94272140/20b9b125-00f5-4b78-9871-c0f63d992e44">

**Issue**
- Ngrinder 테스트 시, 메모리 부족 및 요청 과부하로 인해 디폴트 테스트 구성도를 수정.  
- api instance 2대 생성, Eureka Service Discovery와 로드밸런싱을 통해 요청 수행.

## 

_TO-BE_  
<img width="1488" alt="server" src="https://github.com/hong9Lee/GoF-design-patterns/assets/94272140/75359848-1c3d-4e44-b38c-ebb6da475141">


## 1. 쿠폰 발급  
목적: 쿠폰 발급 로직을 통해 성능 비교 및 개선   
설계:  
`redis set 자료구조를 이용한 user 중복 체크`  
`redis increment를 이용한 쿠폰 총 발급횟수 제한`  
`kafka를 통한 메시지 produce`

  
#### redis + kafka를 이용한 테스트  
`ngrinder를 통한 성능 테스트 - 100, 1000, 2000명의 유저가 5분동안 트래픽 발생시키는 환경`  

[api instance 2대의 환경]  

|vuser|TPS|최고 TPS|응답 시간|총 실행 테스트|
|------|---|---|---|--|  
|100|2674.1|4,025|37.03ms|791,905|  
|1000|2751.7|4,632|362.30ms|811,837|  
|2000|2171.5|3,881|913.53ms|639,509|  

```  
TPS는 vuser 100에서 1,000으로 증가할 때 약간 증가했다.   
시스템이 더 높은 부하를 처리할 수 있는 능력이 있는 것 같다.   
그러나 vuser 1,995에서 TPS는 감소했는데 시스템이 포화 상태에 가까워진 것 같다.  
시스템 성능 저하 지점은 vuser 1000 ~ 2000 사이인 것 같다.  
응답 시간은 부하가 증가할 수록 큰 폭으로 증가하며 최적의 구간은 vuser 100 ~ 1000 구간인 것 같다.  
```

## 성능 개선  
Q1. Gateway <-> Eureka 통신을 캐싱하면 어떨까?    


Q2. 쿠폰 발급 로직을 비동기로 실행 어떨까?  
