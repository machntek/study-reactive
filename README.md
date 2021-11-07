// Duality
// Observer Pattern
// Reactive Streams - 표준 - Java9 API


### Iterator
Java의 forEach는 Iterable을 구현한 것을 넣는다. 
Iterable은 Iterator를 리턴하는 함수로 구현한다.

### Observable
Observable  // Source가 -> (Event/Data)가 발생하면 -> Observer에게 알림

옵저버패턴의 장점 : Observable이 뭔가(데이터/이벤트)를 만들어 내면, 이에 관심있어 하는 옵저버에게 브로드캐스트/멀티캐스트 가능
여러개의 옵저버가 동시에 한꺼번에 이벤트를 받을 수 있다.

옵저버블을 별도의 쓰레드에서 비동기적으로 동작하도록 시킴. 그러면 메인쓰레드 혹은 다른 쓰레드에서 동작하는 옵저버들이 결과를 한꺼번에 받아옴
(push방식으로 옵저버 패턴을 사용하면 별개의 쓰레드에서 동작하는 코드를 손쉽게 만들 수 있다.)

## Servlet 3.0
### 쓰레드가 블록킹되면 생기는 일
쓰레드가 컨텍스트스위칭 되는 것은 CPU자원을 많이 잡아먹음.

블록킹 되는 순간에 쓰레드가 waiting상태로 빠지고, 다시 정상적인 running 쓰레드가 될때 컨텍스트 스위칭 -> 블록킹 한번에 컨텍스트스위칭 2번 발생
그래서 블록킹IO를 많이 사용하면 CPU자원이 불필요하게 많이 소모됨.

### 쓰레드를 많이 만들면?
쓰레드 하나하나가 stackTrace와 자기 데이터를 갖고있기 때문에 무한정 만들다보면 OOM 에러 발생

쓰레드를 많이 만들면 context switching이 엄청 많이 발생하면서 cpu에 부하가 점점 커짐 -> 레이턴시 안좋아지고 처리율 떨어짐

### http요청을 처리하는 서블릿 스레드 동작방식
1           ST1 - req -> WorkThread -> res(html)
2           ST2 - req -> blocking IO(DB, API) -> res(html)
3   NIO     ST3
4           ST4
5           ST5

### 예시

INFO 3851 --- [nio-8080-exec-1] c.m.r.live4.MachntekAsyncApplication     : callable  // 서블릿스레드는 비동기작업을 리턴하고 바로 반납

INFO 3851 --- [         task-1] c.m.r.live4.MachntekAsyncApplication     : async // 비동기작업을 수행하는 워커스레드. 작업이 끝나면 서블릿 스레드를 할당받아서 빠르게 응답하고 다시 반납.

-> 서블릿 스레드의 가용성이 높아짐(thorughput 향상)

