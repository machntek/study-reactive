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

## CompletableFuture 예외 처리
예외 처리 방법 2가지 있다.

1. 예외가 일어나는걸 끝까지 계속 넘기는 방식
2. 예외가 일어나면 적당한 형태의 의미있는 다른값으로 변환해서 다음단계로 넘기는 방식

## CompletableFuture async 함수
예시) thenApplyAsync

현재 쓰레드를 관리하는 쓰레드풀의 정책에 따라 새로운 쓰레드를 할당하거나, 기존에 사용하다가 대기하고있는 캐시된 쓰레드를 다시 쓰거나, 또다른 쓰레드를 쓰거나 등 쓰레드풀 전략에 따라 달라짐.

### 쓰레드풀
Executors.newFixedThreadPool()의 특징 : 새로운 비동기작업을 요청할때마다 계속 다음쓰레드 할당함(이전 쓰레드가 종료 됐다고 하더라도). 다 쓴다음에 다시 캐시로 돌아온얘들을 재사용.

쓰레드풀을 직접 구성할때는 하드웨어 특성이나 코어 갯수, 전체적으로 자원을 어떻게 분배하고 할당할 것이냐, 어느정도 자원을 늘이고 줄임에 따라 레이턴시나 throughput이 달라지는것 등을 실험적으로 체크해가며 전략을 만들어야함.

# 8강
스프링부트2.0부터는 webFlux인 경우 기본으로 netty가 동작함.
undertow와 netty는 http서버 네트워크 라이브러리임(서블릿컨테이너는 아님).

네트워크 라이브러리 빈 변경 방법(아래)
```java
        @Bean
        NettyReactiveWebServerFactory netty4ClientHttpRequestFactory() {
            return new NettyReactiveWebServerFactory();
        }
```

## Mono
```java
        WebClient client = WebClient.create();
        @GetMapping("/rest")
        public Mono<String> rest(int idx) {
            Mono<ClientResponse> res = client.get().uri(URL1, idx).exchange();
            return Mono.just("Hello");
        }
```
- Mono는 Publisher 인터페이스를 구현함.
- Publisher는 만들어 놓는다고 해서 자기가 알아서 publishing을 하는게 아님. 
- 비동기 작업을 수행해서 그 결과를 퍼블리싱하는 코드를 정의한 것.
- Subscriber가 subscribe하지 않으면 데이터를 쏘지 않음
- 따라서 위처럼만 작성을 하면 client 의 http 요청 처리는 실행되지 않음

```java
res.subscribe();
```

위 처럼 subscribe() 를 해줘야 publisher 코드가 실행 됨

그럼, 스프링 코드에서 언제 subscribe 하는가?

Mono 타입을 리턴하면 스프링 webflux 프레임워크가 리턴타입을 보고 Mono 타입인 경우 정보를 꺼내기 위해 subscribe() 호출함 .


- 리액티브란것은 이벤트 드리븐 스타일의 프로그래밍을 위해 시작됨(ex. 앱 터치, 스크롤, 네트워크 결과 오는 등 UI가 있는 서비스에서 주로.)
- 혹은 여러대의 파트너 서버에서 이벤트(메시지)가 푸시로 날라오고 이를 처리하는 경우
- 리액티브는 위와 같은 방식에 적합
- 이런 경우, 데이터가 하나가 아니라 계속 흘러오는 방식으로 데이터가 온다고 보기 때문에, 스트림 스타일로 만들어서 Flux라는 이름으로 사용함
- 그거 외에 우리가 전통적으로 작업하던 방식(ex. DB조회, API 호출로 결과 받아옴 등)에서는 굳이 스트림으로 받지 않아도 됨
- 이런 경우(결과가 하나만 들어있거나 비어있을 수 있는 경우)의 리액티브 스타일의 데이터를 다루는것에 적합하도록 만든것이 Mono임.
- RxJava 1버전대에서는 Single이라는 이름도 있음.

### 리액티브스타일 장점
- 리액티브스타일 프로그래밍의 장점중 하나는 코드에 로우레벨의 기술적인 코드가 등장하지 않는다는 것이다(감춰져있다).
   - (ex. ExecutorService처럼 쓰레드를 직접 제어하는 작업이 나오지 않고, API 호출이 훨씬 추상화 돼있고 등)

- 그럼에도 불구하고 처음 리액티브 스타일에 익숙해지기 전까지는
  '이 코드가 어느 쓰레드에서 동작을 할 것인가', '언제 실행이 될 것인가', '언제 subscribe/publish 가 일어날 것인가' 
  등을 생각해봐야함
   - 확인하기 위해 doOnNext 로 로그를 남길 수 있음 
   
### 기타
리액티브 스타일에서는 쓰레드라고 하지않고 스케쥴러라고 표현하기도 함.

# 9강
리액티브웹으로 어플리케이션을 띄우면 디폴트로 Netty 위에다 애플리케이션을 띄움.

기존에는 스프링MVC가 서블릿이라는 기술을 기반으로 서버가 동작하기때문에 톰캣을 기본으로 사용했음.

이제는 더이상 서블릿이라는 자바의 웹 기술을 의존하지 않도록 스프링 웹플럭스 기술이 나옴. 

HTTP를 지원하는 웹서버 혹은 네트워크 서버라면 서블릿을 직접 지원하지 않더라도 어떤종류의 라이브러리든지 쓸 수 있음(Netty 포함 4가지).

어느 웹엔진위에 올라가있는지에 따라서 작성한 애플리케이션 코드가 달라질 일은 없다(서버설정코드 정도만 변경됨).

Netty는 가벼운 서버이고, 서블릿 위에서 스프링이 동작하기 위해 하는 부가작업이 생략되므로, 스프링WebFlux 어플리케이션은 빨리 뜸

### Mono

```java
    @GetMapping("/")
    Mono<String> hello() {
        Mono m = Mono.just("Hello Webflux").log();
        return m;
    }
```
위에 대한 api호출을 하면 아래와 같이 로그가 찍힌다.

```text
2021-12-11 10:42:45.928  INFO 971 --- [ctor-http-nio-2] reactor.Mono.Just.1                      : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
2021-12-11 10:42:45.929  INFO 971 --- [ctor-http-nio-2] reactor.Mono.Just.1                      : | request(unbounded)
2021-12-11 10:42:45.929  INFO 971 --- [ctor-http-nio-2] reactor.Mono.Just.1                      : | onNext(Hello Webflux)
2021-12-11 10:42:45.932  INFO 971 --- [ctor-http-nio-2] reactor.Mono.Just.1                      : | onComplete()
```
Mono라는건 기본적으로 데이터를 하나만 갖고있기때문에 onNext한번 하고 나면 그다음에 날리는건 onComplete밖에 없다

Mono, Flux 처럼 Publisher로 정의해놓은 것들은 Subscriber가 subscribe 해야 그 로직을 탐(Publihser가 보내는 데이터 스트림을 구독하겠다고 명시적으로 선언하는 시점에 데이터가 가기 시작함)

```text
                오퍼레이터       오퍼레이터
Publisher -> (Publisher) -> (Publisher) -> Subscriber
```

### Mono.just

```java
    Mono.just(myService.findById(1));
```
Mono.just()는 동기적으로 실행이되기 때문에(just는 미리 준비된것임), 이미 다 준비된 publishing할 데이터를 먼저 가져다가 publish 할 준비를 함. 위의 예에선 myService.findById(1) 이 먼저 실행 되고 그 결과값이 just로 들어감.

### Subscribe

```java
    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
        Mono m = Mono.fromSupplier(() -> generatedHello()).doOnNext(c -> log.info(c)).log();
        m.subscribe();
        log.info("pos2");
        return m;
    }

    private String generatedHello() {
        log.info("method generateHello()");
        return "Hello Mono";
        }
```
위의 예처럼 WebFlux에 리턴하기 전에 subscribe()를 하면 2번 publishing 된다(아래 로그 참고)
```text
2021-12-12 21:34:19.009  INFO 2784 --- [ctor-http-nio-2] c.m.reactive.live9.MachntekApplication   : pos1
2021-12-12 21:34:19.017  INFO 2784 --- [ctor-http-nio-2] reactor.Mono.PeekFuseable.1              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2021-12-12 21:34:19.019  INFO 2784 --- [ctor-http-nio-2] reactor.Mono.PeekFuseable.1              : | request(unbounded)
2021-12-12 21:34:19.019  INFO 2784 --- [ctor-http-nio-2] c.m.reactive.live9.MachntekApplication   : method generateHello()
2021-12-12 21:34:19.019  INFO 2784 --- [ctor-http-nio-2] c.m.reactive.live9.MachntekApplication   : Hello Mono
2021-12-12 21:34:19.019  INFO 2784 --- [ctor-http-nio-2] reactor.Mono.PeekFuseable.1              : | onNext(Hello Mono)
2021-12-12 21:34:19.019  INFO 2784 --- [ctor-http-nio-2] reactor.Mono.PeekFuseable.1              : | onComplete()
2021-12-12 21:34:19.020  INFO 2784 --- [ctor-http-nio-2] c.m.reactive.live9.MachntekApplication   : pos2
2021-12-12 21:34:19.029  INFO 2784 --- [ctor-http-nio-2] reactor.Mono.PeekFuseable.1              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2021-12-12 21:34:19.029  INFO 2784 --- [ctor-http-nio-2] reactor.Mono.PeekFuseable.1              : | request(unbounded)
2021-12-12 21:34:19.029  INFO 2784 --- [ctor-http-nio-2] c.m.reactive.live9.MachntekApplication   : method generateHello()
2021-12-12 21:34:19.029  INFO 2784 --- [ctor-http-nio-2] c.m.reactive.live9.MachntekApplication   : Hello Mono
2021-12-12 21:34:19.029  INFO 2784 --- [ctor-http-nio-2] reactor.Mono.PeekFuseable.1              : | onNext(Hello Mono)
2021-12-12 21:34:19.031  INFO 2784 --- [ctor-http-nio-2] reactor.Mono.PeekFuseable.1              : | onComplete()
```

Mono나 Flux같은 Publihser 들은 하나 이상의 Subscriber를 가질 수 있다(publishing하는 소스는 하나인데, subscribe하는건 여러개가 가능).

Publihser가 데이터를 공급하는건 소스타입을 2가지로 나눈다.

1. Cold 타입 :
    - 데이터가 만들어져서 고정돼있는 경우. 즉, 어느 subscriber가 요청하던지 항상 동일한 (미리 셋팅돼있는)결과가 가는 경우
    - Subscriber가 새로운 구독을 할 때마다 Publihser가 갖고있는 데이터를 처음부터 다시 만들어서 다시 보내줌(리플레이함).
    - 여러번 수행 가능
2. Hot 타입 :
    - DB에서 데이터를 읽어오거나 미리 준비된 데이터를 가공해서 보내주는 방식이 아닌, 실시간으로 일어나는 외부의 이벤트들(유저 인터페이스에 의한 액션, 외부 시스템에서 실시간으로 날라오는 데이터 등)로 발생한 데이터.
    - Publihser가 기동을 시작해서 데이터를 100개 publishing 한 시점에서, 새로운 subscribe를 하면 101번째부터 데이터를 받기 시작함.(그 앞의 데이터를 다시 쭈욱 받아오는게 아님)
    - 즉, 구독하는 그 시점부터 실시간으로 발생하는 데이터만 가져온다.

# 10강
1. Mono<List> 와 Flux 의 차이 1
    - Mono<List> 는 컬렉션타입 자체(ex. List)를 다룸. 하나의 element만 가짐(여기서는 List)
    - Flux 는 각 Object에 대해 reactor 오퍼레이터를 걸 수 있음.
  
2. Mono<List>와 Flux 의 차이 2
   - http 스트림을 지원하려면(데이터를 청크단위로 나눠서 보내는 방법을 사용하려면) Flux 를 이용하면 편리함  
   - 굳이 스트림으로 만든다는것은 약간의 딜레이들 두고 데이터들이 올 수 있다는것(이벤트가 발생할때만 하나씩 온다거나 등의 구조). 데이터단위로 쪼개서 들어옴
   - Flux안의 요소 하나하나를 스트림 단위로 쪼개서 보낼 수 있음.(아래 예시)

```java
    @GetMapping(value="/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events() {
        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
        return Flux.fromIterable(list);
    }
```
결과(Flux를 이용해 스트림방식으로 리턴)
```
> curl localhost:8080/events
data:{"id":1,"value":"event1"}

data:{"id":2,"value":"event2"}
```
참고(Mono를 이용해 List 리턴)
```
> curl localhost:8080/event/1
[{"id":1,"value":"event1"},{"id":2,"value":"event2"}]
```

### Flux에서 delay의 쓰레드 처리
delayElements 가 background 쓰레드를 만들어서 처리함(이 쓰레드가 10초동안 물고있음). 외부IO처럼 IO를 던져놓고 빠져나갔다가 들어올수 있는게 아니라, delay같은 개념은 그 순간에 blocking이 들어감.
```java
    @GetMapping(value="/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events() {
        return Flux
                .fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
                .delayElements(Duration.ofSeconds(1))
                .take(10); // 각 element에 map 등의 reactor 라이브러리의 오퍼레이터 작업 걸 수 있다.
    }
```

스트림(event stream)이란거는 서버가 다른쪽에서 데이터를 일정한 간격 혹은 랜덤하게 받아올 때마다 클라이언트가 매번 요청을 보내는게 아니라, 클라이언트가 요청 한번만 던져놓고 나면 서버가 계속 push 개념처럼 데이터를 쏴줄때 유용하게 쓸 수 있음
