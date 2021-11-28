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

# 4강
ThreadPoolTaskExecutor는 런타임중에 코어pool사이즈를 JMX를 통해 변경 가능
## Servlet 3.0
### 쓰레드가 블록킹되면 생기는 일
쓰레드가 컨텍스트스위칭 되는 것은 CPU자원을 많이 잡아먹음.

서블릿은 블록킹방식인 InputStream 베이스의 HttpServletRequest/HttpServletResponse를 사용하므로 서블릿은 블록킹IO방식이다.

블록킹IO를 많이 사용하면 CPU자원이 많이 소모됨.

블록킹 되는 순간에 쓰레드가 waiting상태로 빠지고, 다시 정상적인 running 쓰레드가 될때 컨텍스트 스위칭 -> 블록킹 한번에 컨텍스트스위칭 2번 발생
그래서 블록킹IO를 많이 사용하면 CPU자원이 불필요하게 많이 소모됨.

### 쓰레드를 많이 만들면?
쓰레드 하나하나가 stackTrace와 자기 데이터를 갖고있기 때문에 무한정 만들다보면 OOM 에러 발생

쓰레드를 많이 만들면 context switching이 엄청 많이 발생하면서 cpu에 부하가 점점 커짐 -> 레이턴시 안좋아지고 처리율 떨어짐

### http요청을 처리하는 서블릿 스레드 동작방식
```
1           ST1 - req -> WorkThread -> res(html)
2           ST2 - req -> blocking IO(DB, API) -> res(html)
3   NIO     ST3
4           ST4
5           ST5
```


### 비동기 서블릿 - 비동기 작업 수행
서블릿쓰레드는 하나로 사용/반납을 통해서 돌려쓰지만(서블릿 쓰레드 풀보다 더 많은 요청이 들어와도 처리할 수 있도록, 요청이 서블릿스레드에 머무는 기간을 짧게 함),

그 뒤에 작업쓰레드를 더 만들어서 작업쓰레드가 실제 작업 수행함.

출처: 토비의 봄TV 8회(https://www.youtube.com/watch?v=aSTuQiPB4Ns&list=PLv-xDnFD-nnmof-yoZQN8Fs2kVljIuFyC&index=7)
![비동기서블릿](https://user-images.githubusercontent.com/19985682/143540827-0e1bdd7a-f0a2-4642-972c-42d43cf195c6.jpeg)

### 비동기 서블릿 - DeferredResult 큐 사용
대기상태에 있다가 외부에서 다른종류의 이벤트가 발생(메세지를 타고 오거나, 이메일이 오거나, 연계된 서버에서 API호출이 오거나 등)할때 해당 메세지를 받아야하는 대상들에게 결과를 써주는 상황에 유용

워커쓰레드가 따로 만들어지지 않는다. DeferredResult 오브젝트만 메모리에 유지가 되면 언제든지 오브젝트 불러와서 거기에 결과를 넣어주면 됨.(mvc컨트롤러에서 리턴되는것처럼 리턴이 됨)

출처: 토비의 봄TV 8회(https://www.youtube.com/watch?v=aSTuQiPB4Ns&list=PLv-xDnFD-nnmof-yoZQN8Fs2kVljIuFyC&index=7)
![비동기서블릿 - DeferredResult](https://user-images.githubusercontent.com/19985682/143540852-ba19f61b-7108-4877-ae31-4126e2f5684e.jpeg)

### 예시
```
INFO 3851 --- [nio-8080-exec-1] c.m.r.live4.MachntekAsyncApplication     : callable  // 서블릿스레드는 비동기작업을 리턴하고 바로 반납

INFO 3851 --- [         task-1] c.m.r.live4.MachntekAsyncApplication     : async // 비동기작업을 수행하는 워커스레드. 작업이 끝나면 서블릿 스레드를 할당받아서 빠르게 응답하고 다시 반납.
```
-> 서블릿 스레드의 가용성이 높아짐(thorughput 향상)

### NIO의 커넥션
커넥션을 맺는거는 OS레벨에서 일종의 FILE을 오픈하는것. 커널에서 제한이 걸려있으면 그 이상 못열음
NIO와 같이 비동기인 경우에는 커넥션은 동시에 계속 물고 있을 수 있음. OS가 허용해주는 만큼 가능.
OS만 잘 튜닝하면 JAVA로 100만커넥션도 가능.

그걸로 데이터를 쓰고 복잡한 CPU작업을 하는건 다른얘기지만, 커넥션의 경우 몇만 커넥션을 물고있는거는 간단.

## 스프링MVC 비동기 수행방식
1. Callable
2. DeferredResult
3. ResponseBodyEmitter
    - http 안에 데이터를 여러번에 나눠서 보낼수 있는 기술

# 5강
백단에 별개의 서비스를 호출하는게 많이 있는 경우, 단순히 비동기 서블릿을 사용하는것만으로 문제를 해결하기 어려운경우가 많이 있음.

쓰레드 생성하면 CPU와 메모리 모두 씀
쓰레드개수가 코어개수를 넘어서면 Context-switching 많이 발생.(CPU 자원 많이 먹음)

외부API 호출은 쓰레드가 블록킹돼고, Context-switching 두번 발생.

### Callable과 Runnable 차이
Callable은 리턴값이 있고 Runnable은 없다
Callable은 Exception 던지도록 선언돼있고 Runnable은 없다.

### 이슈(해결)
~~AsyncRestTemplate이 NIO로 동작 안하는듯하다.~~
RemoteService의 서블릿쓰레드가 하나만 만들어져있던걸, 100개로 늘림

### AsyncRestTemplate
AsyncRestTemplate은 기본적으로 자바의 기본API 와 추가 쓰레드를 만드는것을 이용함

튜닝을 하면 Nonblocking IO를 이용해서 외부API를 호출하는 라이브러리를 쓸 수 있다.(ex. apache의 asyncHttpClient, netty 등)

Netty의 기본 쓰레드 갯수: CPU의 코어 갯수 * 2 (자바에서 코어갯수 가져올 수 있음. 이를 이용해 확인)
2배로 하는 이유 : 코어당 하나만 쓰는건 비효율적. 적정한 작업들이 실시간으로 병렬적으로 진행되도록 함.(by 토비)

### Non-Blocking IO는 메모리나 자원을 많이쓰는가?
메모리는 오브젝트를 만들때 사용됨.
가벼운 오브젝트가 많이 만들어지는건 서버에 전혀 부담이 아님.
Non-Blokcing IO를 쓴다는건 외부 리소스를 대기하고 있는거지, 리소스를 많이 잡고있는것이 아님.

콜백방식에서는 exception을 던지면, 비동기 작업이라서 어느 stackTrace를 타고 실행되고있는지 모름. 그래서 예외를 전파해봤자 정확히 스프링MVC가 처리하는데가 받으리란 보장이 없음. -> DeferredResult 활용

### DB에서의 NIO
DB(JDBC)는 블록킹이 걸린다고 생각을 하고, 쓰레드를 그만큼 할당하거나 자원(CPU 등 리소스)를 그만큼 손해볼거라고 가정하고 소스 짜야함(2017년 기준)
오라클에서 비동기JDBC 스펙을 만들기 시작(2017)

# 6강
DeferredResult.setErrorResult()에 Exception을 던지면 client한테도 실패처리가 됨(500에러가 던져짐).

DeferredResult.setErrorResult()에 메세지(ex. e.toString())를 넘기면, api호출에 대한 정상적인 결과로 client에게 String이 넘어감(에러긴 하지만 에러메세지가 응답으로 나감)

# 7강
CompletableFuture.runAsync() 는 Runnable 인터페이스를 파라미터로 받으므로, 비동기 작업에 의해 만들어진 결과값을 다음 체인에서 사용할 수 없음.

CompletableFuture.supplyAsync() 를 써서 결과값을 다음 체인에서 사용 가능
