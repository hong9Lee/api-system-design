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

|vuser|TPS|최고 TPS|응답 시간|총 실행 테스트|error|
|------|---|---|---|---|--|  
|100|2674.1|4,025|37.03ms|791,905|0|  
|1000|2751.7|4,632|362.30ms|811,837|0|  
|2000|2171.5|3,881|913.53ms|639,509|226|  

```  
TPS는 vuser 100에서 1,000으로 증가할 때 약간 증가했다.   
시스템이 더 높은 부하를 처리할 수 있는 능력이 있는 것 같다.   
그러나 vuser 1,995에서 TPS는 감소했는데 시스템이 포화 상태에 가까워진 것 같다.
또한, 에러도 226건이나 발생했고 네트워크 대역폭이 포화 상태가 된 것 같다.
시스템 성능 저하 지점은 vuser 1000 ~ 2000 사이인 것 같다.  
응답 시간은 부하가 증가할 수록 큰 폭으로 증가하며 최적의 구간은 vuser 100 ~ 1000 구간인 것 같다.  
```

## 성능 개선  
Q1. Gateway <-> Eureka 통신을 캐싱하면 어떨까?    

|vuser|TPS|최고 TPS|응답 시간|총 실행 테스트|error|
|------|---|---|---|---|--|  
|1000|2462.0|3,541|405.20ms|727,448|0|  

```
오히려 지표가 떨어졌다.
클라이언트는 주기적으로(기본적으로 30초마다) Eureka 서버로부터 최신 정보를 가져와 로컬 캐시를 갱신하기 때문에, 
Gateway에 캐싱을 하는것은 오히려 불필요한 리소스를 사용한다. 
```


      
Q2. api instance가 3대로 늘어나면 지표가 눈에 띄게 개선될까?  

|vuser|TPS|최고 TPS|응답 시간|총 실행 테스트|error|
|------|---|---|---|---|--|  
|100|5166.6|7,026|18.99ms|1,536,606|0|  
|1000|3216.1|4,715|311.53ms|944,413|0|  
|2000|2800.2|5,066|708.59ms|823,625|43|

```
instance를 늘리니 대체적으로 성능이 향상되었다.
특히, 트래픽이 적은 vuser 100 테스트에서 성능이 크게 좋아졌고 응답 시간의 경우에는 2배 이상 시스템 처리 능력이 개선되었다.

vuser 1000일때로 비교해보면,
TPS가 2대일 때의 2,751.7에 비해 3,216.1로 비약적으로 상승했으며, 시스템의 최대 처리 능력이 개선되었다.
최고 TPS가 2대일 때의 4,632에 비해 4,715로 시스템 피크 성능이 개선되었다.
응답 시간이 2대일 때의 362.30ms 보다 311.53ms로 감소했다.
총 실행 테스트 수 또한 증가했다.

vuser 2000일때도 시스템 성능 지표가 향상되었으며 에러율이 줄어듬으로써 시스템 안정도가 향상되었다.
이를 통해 인프라 확장이 시스템 성능에 미치는 긍정적인 영향을 알 수 있었다.
```



      
Q3. 현재 시스템에서 instance 수를 크게 늘리게 되면 오히려 시스템 처리 능력이 떨어지게 되는 구간이 발생할까?  
여건만 된다면 인프라 확장이 무조건 좋은 것일까?
```
스케일 아웃의 한계.



```


      
Q4. 쿠폰 발급 로직을 비동기로 실행해보면 어떨까?  


      
Q5. Gateway에서 큐를 생성하여 마치 대기열 처럼 요청을 순차적으로 처리하면 성능이 떨어지게 될까?  
