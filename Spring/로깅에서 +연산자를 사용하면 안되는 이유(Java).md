# 로깅에서 +연산자를 사용하면 안되는 이유 (Java)

## if(logger.isErrorEnabled())와 같은 logging guard 필요성 이슈
- slf4j는 parameterized logging이라고 불리는 advanced feature를 제공한다. 그리고 parameterized logging은 로깅 성능을 크게 향상시킨다.

<br/>

```
logger.debug("Entry number : " + i + " is " + String.valueOf(entry[i]));
```

<br/>

- 위와 같은 로그가 있을 때, 메시지 파라미터를 생성하는 비용(`i`와 `entry[i]`를 String으로 변환하고 다른 String들과 연결)이 발생한다. 이 작업은 메시지가 로깅되냐 안되냐에 관계없이 항상 발생한다.
- 파라미터 생성 비용을 피하는 한 가지 방법은 아래와 같이 logging buard로 둘러치는 것이다.

<br/>

```
if(logger.isDebugEnabled()) {
  logger.debug("Entry number : " + i + " is " + String.valueOf(entry[i]));
}
```

<br/>

- 이렇게 하면 로거에 대해 디버깅이 비활성화 된 경우, 파라미터 생성 비용이 들지 않는다.
- 하지만 로그 레벨을 DEBUG로 하게 되면, 로거 사용 여부를 평가하는 데 드는 비용이 발생한다.
- 한 번은 debugEnabled에, 다른 한 번은 디버그에 사용된다.

<br/>

- 이것은 로거를 평가하는 데 실제로 문장을 기록하는 데 걸리는 시간의 1% 미만이기 때문에 경미한 오버헤드이다.

<br/>

- 메시지 포맷에 따라 매우 편리한 대안이 있다. entry가 객체라고 가정하면 다음과 같이 작성할 수 있다.

<br/>

```
Object entry = new SomeObject();
logger.debug("The entry is {}.", entry);
```

- 로그 여부를 평가한 후, 그리고 결정이 긍정적일 경우에만 로거 구현에서 메시지를 포맷하고 '{}'쌍을 입력할 문자열 값으로 바꾼다.
- 다시 말해 이 포맷은 log문이 disabled인 경우 파라미터 생성 비용을 발생시키지 않는다.

<br/>

## 예외와 함께 동반되는 메시지 없이 예외를 기록할 수 있을까?

- 답은 **No**이다.
- e가 예외인 경우 ERROR 레벨에서 예외를 기록하려면 예외와 함께 동반되는 메시지를 추가해야한다.

<br/>

```
logger.error("some accompanying message", e);
```

<br/>

## exception/throwable이 있는 경우 로깅문을 파라미터화 할 수 있을까?

- 답은 **Yes**이다.
- slf4j 1.6.0 버전부터 되고, 그 이전 버전은 되지 않는다.

<br/>

```
String s = "Hello World";
try {
  Integer i = Integer.valueOf(s);
} catch (NumberFormatException e) {
  logger.error("Failed to format {}", s, e);
}
```

<br/>

- 위 예시문에서는 NumberFormatException stack trace 출력이 예상된다. (1.6.0 버전 이하에서는 무시됨)

<br/>

***출처*** <br/>
***https://www.slf4j.org/faq.html#logging_performance*** <br/>
***https://yangbongsoo.gitbook.io/study/undefined/log*** <br/>
