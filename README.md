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

