# 6장 AOP

## 6.1 트랜잭션 코드의 분리
### 6.1.1 메소드 분리
![비즈니스 로직과 트랜잭션 경계설정의 분리](https://github.com/taechacode/TIL/assets/63395751/c38c31ed-d2ea-4315-80bf-44b181f207c5)
**리스트 6-2 비즈니스 로직과 트랜잭션 경계설정의 분리**
- 트랜잭션 경계설정에 둘러싸여져 있는 비즈니스 로직을 외부로 빼내는 작업을 보여주고 있다.
- 기존 비즈니스 로직을 `upgradeLevelsInternal()`이라는 메소드로 만들고, 메소드에서 로직을 구현하고 있다.
<br/><br/>

### 6.1.2 DI를 이용한 클래스 분리
![UserService 인터페이스 도입을 통해 약한 결합을 갖는 유연한 구조](https://github.com/taechacode/TIL/assets/63395751/d5586217-43ae-4b04-93b1-3966256d61bb)
**그림 6-2 UserService 인터페이스 도입을 통해 약한 결합을 갖는 유연한 구조**
- 기존에는 UserService 클래스와 클라이언트의 직접 연결을 통한 강한 결합
- 인터페이스를 이용해 클라이언트와 UserService 구현 클래스의 직접 결합을 막아주고, 유연한 확장이 가능하게 만들었다.
<br/><br/>

![트랜잭션 경계설정을 위한 UserServiceTx의 도입](https://github.com/taechacode/TIL/assets/63395751/9f746db3-fa6c-4dbe-9612-f4b73284304c)
**그림 6-3 트랜잭션 경계설정을 위한 UserServiceTx의 도입**
- 한 번에 두 개의 UserService 인터페이스 구현 클래스를 동시에 이용한다면 어떨까? 클라이언트가 UserService의 기능을 제대로 이용하려면 트랜잭션이 적용돼야 한다.
- 위 그림과 같은 구조에서는 UserServiceTx가 사용자 관리 로직을 담고 있는 구현 클래스인 UserServiceImpl을 대신하기 위해 만든 게 아니다.
- UserServiceTx에서는 UserService를 구현한 다른 오브젝트를 DI 받고, 비즈니스 로직에 대해서는 아무런 관여도 하지 않는다.
- UserServiceTx에 트랜잭션의 경계설정이라는 부가적인 작업을 부여한다. 이 작업은 UserService에 트랜잭션 경계설정 API를 도입한 것과 동일하다.
<br/><br/>

![트랜잭션 기능의 오브젝트가 적용된 의존관계](https://github.com/taechacode/TIL/assets/63395751/e88fa643-278c-4a20-86ac-a90becbdc3f2)
**그림 6-4 트랜잭션 기능의 오브젝트가 적용된 의존관계**
<br/><br/>

#### 트랜잭션 경계설정 코드 분리의 장점
- 첫째, 비즈니스 로직을 담당하고 있는 UserServiceImpl의 코드를 작성할 때는 트랜잭션과 같은 기술적인 내용에는 전혀 신경쓰지 않아도 된다.
- 둘째, 비즈니스 로직에 대한 테스트를 손쉽게 만들어낼 수 있다.
<br/><br/>

## 6.2 고립된 단위 테스트

## 6.3 다이내믹 프록시와 팩토리 빈
