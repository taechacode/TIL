# Bean과 Component의 차이

- 스프링은 개발의 제어권이 `스프링 컨테이너(IoC 컨테이너)`에 있다고 한다. 그래서 이것을 `IoC(Inversion Of Control)`, `제어의 역전`이라고 한다.

<br/>

- 스프링이 개발자 대신 객체를 제어하기 위해서는 객체들이 `빈(Bean)`으로 등록되어있어야 한다.
- 과거에는 객체를 빈으로 등록하기 위해 XML로 지정했어야 한다고 하는데, 요즘에는 애노테이션으로 간단하게 등록할 수 있다.

<br/>

## 스프링에서 빈으로 등록하는 방법

- 스프링 MVC에서는 `@Controller`, `@Service`, `@Repository` 등으로 빈으로 등록할 수 있으며, Configuration 관련 객체들은 `@Bean`과 `@Component`로 스프링 컨테이너에 객체를 빈으로 등록할 수 있다.
- 그럼 `@Bean`과 `@Component`의 차이는 무엇일까?
  - `@Bean`은 **메소드 레벨**에서 선언하며, 반환되는 객체(인스턴스)를 개발자가 수동으로 빈으로 등록하는 애노테이션이다.
  - 반면 `@Component`는 **클래스 레벨**에서 선언함으로써 스프링이 런타임 시에 컴포넌트 스캔을 하여 자동으로 빈을 찾고(detect) 등록하는 애노테이션이다.

### @Bean 사용 예제

```
@Configuration
public class AppConfig {
  @Bean
  public MemberService memberService() {
    return new MemberServiceImpl();
  }
}
```

### @Component 사용 예제

```
@Component
public class AppConfig {
  // ...
}
```

- 블로그 <기억보단 기록을>의 저자인 동욱님께서는 개발자가 컨트롤이 불가능한 외부 라이브러리를 빈으로 등록하고 싶을 때 `@Bean`을 사용하며, 개발자가 직접 컨트롤이 가능한 클래스의 경우 `@Component`를 사용한다고 한다.

<br/>

## 빈과 컴포넌트의 차이 정리

![빈과컴포넌트의차이](https://github.com/taechacode/TIL/assets/63395751/3cad5516-58a8-4410-8df1-e2ff146f82fa)

<br/>

***출처*** <br/>
***https://jojoldu.tistory.com/27*** <br/>
***https://www.baeldung.com/spring-component-annotation*** <br/>
***https://youngjinmo.github.io/2021/06/bean-component/*** <br/>
