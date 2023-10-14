# System.out.println() 대신 logger를 사용해야 하는 이유.md

![System out println() 대신 logger를 사용해야 하는 이유 1](https://github.com/taechacode/TIL/assets/63395751/a41d3d71-5417-46e2-963b-d1a51a9d79d8)

- System.out.println()은 자바 표준 입력 클래스인 System을 사용하기 때문에 클래스 경로에 추가해야 될 라이브러리나 추가 구성이 필요 없기 때문에 간편하고 쉽게 사용할 수 있다.
- 하지만 프로덕션 코드(Production Code)에서는 System.out.println() 메서드를 통한 출력이 지양되는데 그 이유는 무엇인지 알아보자.

<br/>

## 로깅(Logging)이란?

- 로깅은 프로그램 실행 동작을 일련의 기록인 로그(Log)의 생성을 통해 남겨놓는 일을 말한다.
- 로그는 재현하기 힘든 버그나 성능에 대한 통계 등, 프로그램 동작에 있어 유용한 정보를 제공하기 때문에 로깅 작업은 실제 서비스 개발에 필수적인 부분이다.

<br/>

## System.out.println() 메서드를 사용했을 때의 문제점

### 1. 성능 저하의 원인이 된다

```
/**
 * Terminates the current line by writing the line separator string.  The
 * line separator string is defined by the system property
 *<code>line.separator</code>, and is not necessarily a single newline
 * character (<code>'\n'</code>).
 */
public void println() {
    newLine();
}
```

- System.out.println()에서 println() 메서드를 보면 내부적으로 newLine() 메서드를 호출하는 것을 확인할 수 있다.

```
private void newLine() {
    try {
        synchronized (this) {
            ensureOpen();
            textOut.newLine();
						...
        }
    }
		...
}
```

- newLine() 메서드의 try 부분을 보면 `synchronized` 키워드가 사용된 것을 볼 수 있다.
- `synchronized`는 메서드나 블록 코드에 동기화 영역을 표시하는 것으로, 동기화된 블록은 한 시점에 1개의 스레드만 접근이 가능하다.
- 즉, 1개의 스레드가 접근중일 때 접근을 시도하는 다른 스레드들은 블록 안의 스레드가 실행을 마치고 블록을 벗어날 때까지 `블록(blocked) 상태(멀티 스레드의 동시 접근 방지)`가 된다.
- 다시 말해 newLine() 메서드의 synchronized 키워드로 인해서 만약 System.out.println() 메서드를 여러 스레드가 사용하게 된다면 **오버헤드(Overhead)가 발생하여 프로세스 처리가 늦어지게** 된다.

<br/>

### 2. 로그 출력 레벨을 사용할 수 없다

- 프로젝트 개발 단계에서는 디버깅을 위한 상세한 로그들을 출력하고 활용하는 경우가 많지만, 실제 프로덕션 환경에서 동작하는 코드의 경우에는 리소스의 낭비를 줄이기 위해 에러 및 장애가 발생할 때의 문제를 진단할 수 있는 로그만 남긴다.
- 만약 모든 로그가 쌓인다면, 문제 해결을 위해 필요한 로그는 찾기 힘들고 의미 없는 로그가 쌓여 서버의 용량을 차지할 수도 있다.
- 때문에 여러 로깅 프레임워크는 프로그램 동작 환경(로컬 개발환경, 개발 서버, 프로덕션 서버)에 맞는 로그가 출력될 수 있도록 `로그 출력 레벨`이라는 기능을 제공한다.

```
log.trace("Trace Log Message");
log.debug("Debug Log Message");
log.info("Info Log Message");
log.warn("Warn Log Message");
log.error("Error Log Message");
```

- 로그 레벨은 TRACE > DEBUG > INFO > WARN > ERROR > FATAL이 있다. (slf4j 같은 경우 FATAL 레벨이 없다.)

<br/>

- 하지만 System.out.println()의 경우 **System.out.println()** 을 사용한 인포메이션 로그와 **System.err.println()** 을 사용한 에러 로그 2가지로만 분류가 가능하지 때문에 로깅 프레임워크와 같은 레벨별 출력이 불가능하다.
- 때문에 프로덕션 환경이 되었을 때 불필요한 System.out.println()을 일일히 주석처리를 하거나 제거하는 작업이 필요하며, 그 과정에서 작업이 누락되는 등의 실수가 발생할 수 있다.

<br/>

### 3. 에러 발생 시 추적할 수 있는 최소한의 정보가 남지 않는다 (날짜, 시간, 문제 수준 등)

![System out println() 대신 logger를 사용해야 하는 이유 2](https://github.com/taechacode/TIL/assets/63395751/07f5d390-7c8d-479f-8362-04181d6837ce)

- 로깅 프레임워크를 사용했을 때는 기본적으로 에러 발생 날짜와 시간, 문제 수준, 발생 경로 등의 정보를 얻을 수 있지만 System.out.println() 메서드를 사용했을 때는 에러 발생 시 추적할 수 있는 최소한의 정보가 남지 않는다는 문제점이 있다.
- 남겨지는 정보 외에도 로그 메시지의 경우 개발자가 확인하기 쉽도록 공통적인 형태를 가지고 있는 것이 좋다.
- 로깅 프레임워크를 사용했을 때는 지정되는 포맷이 있기 때문에 다른 설정 과정이 필요하지 않다는 장점도 있다.

<br/>

### 4. 파일로 저장되지 않으므로 휘발성이 강하다

- System.out.println()은 기본적으로 표준 출력 용도로만 사용되므로 콘솔에만 출력되고 따로 기록되지 않는다.
- 만약 System.out 또는 System.err을 사용하여 출력을 파일로 리다이렉션 하더라도 파일 크기를 제어할 수 없으므로 해당 프로그램이 실행되는 동안 파일 크기가 제한없이 계속 증가한다.
- 파일 크기가 커지면 큰 용량의 로그 파일을 열어서 분석하기 어려울 수 있다.

<br/>

- 로깅 프레임워크(예를 들어 Log4J2)은 파일레 로그를 체계적으로 기록하고 특정 조건에 따라 파일을 `롤오버(Rollover)`하는 매커니즘을 제공한다.
- 여기서 롤오버는 로그를 남기는 대상 파일을 변경하는 것을 의미한다. 가령, log.txt을 대상으로 로그를 남기다가 특정 시간이 넘어가면 이전 파일을 저장하고 다른 파일에서 로그를 남기도록 할 수 있다.
- 로깅 프레임워크는 날짜 및 시간에 따라 롤오버할 파일을 구성하거나 파일에 로그가 쌓여 일정 크기를 넘어서면 롤링할 수도 있다.

<br/>

***출처*** <br/>
***https://hudi.blog/do-not-use-system-out-println-for-logging/*** <br/>
***https://www.baeldung.com/java-system-out-println-vs-loggers***
