# 5장 서비스 추상화

## 5.1 사용자 레벨 관리 기능 추가
- UserDao는 User 오브젝트에 담겨 있는 사용자 정보는 CRUD만 하고 비즈니스 로직은 갖고 있지 않다.
- 여기에 간단한 비즈니스 로직을 추가해보자.
  - 사용자의 레벨은 BASIC, SILVER, GOLD 세 가지 중 하나다.
  - 사용자가 처음 가입하면 BASIC 레벨이 되며, 이후 활동에 따라서 한 단계씩 업그레이드될 수 있다.
  - 가입 후 50회 이상 로그인을 하면 BASIC에서 SILVER 레벨이 된다.
  - SILVER 레벨이면서 30번 이상 추천을 받으면 GOLD 레벨이 된다.
  - 사용자 레벨의 변경 작업은 일정한 주기를 가지고 일괄적으로 진행된다. 변경 작업 전에는 조건을 충족하더라도 레벨의 변경이 일어나지 않는다.

### 5.1.1 필드 추가
#### Level 이늄

```
class User {
	private static final int BASIC = 1;
	private static final int SILVER = 2;
	private static final int GOLD = 3;
	
	int level;
	
	public void setLevel(int level) {
		this.level = level;
	}	
}
```
**리스트 5-1 정수형 상수 값으로 정의한 사용자 레벨**
<br/><br/>

```
if(user1.getLevel() == User.BASIC) {
  user1.setLevel(User.SILVER);
}
```
**리스트 5-2 사용자 레벨 상수 값을 이용한 코드**
<br/><br/>

- 문제는 level 타입이 int이기 때문에 다른 종류의 정보를 넣는 실수를 해도 컴파일러가 체크해주지 못한다.
- getSum() 메소드가 1, 2, 3과 같은 값을 돌려주면 기능은 문제없이 돌아가는 것처럼 보이겠지만 사실은 레벨이 엉뚱하게 바뀌는 심각한 버그가 만들어진다.

```
user1.setLevel(other.getSum());
```

- 또, 아래와 같이 범위를 벗어나는 값을 넣을 위험도 있다.

```
user1.setLevel(1000);
```
- 숫자 타입을 직접 사용하는 것보다는 자바 5 이상에서 제공하는 `이늄enum`을 이용하는 게 안전하고 편리하다.
<br/><br/>

```
package springbook.user.domain;
...
public enum Level {
	BASIC(1), SILVER(2), GOLD(3); // 세 개의 이늄 오브젝트 정의
	
	private final int value;
	
	Level(int value) { // DB에 저장할 값을 넣어줄 생성자를 만들어둔다.
		this.value = value;
	}
	
	public int intValue() { // 값을 가져오는 메소드
		return value;
	}
	
	public static Level valueOf(int value) { // 값으로부터 Level 타입 오브젝트를 가져오도록 만든 스태틱 메소드
		switch(value) {
			case 1: return BASIC;
			case 2: return SILVER;
			case 3: return GOLD;
			default: throw new AssertionError("Unknown value: " + value);
		}
	}
}
```
**리스트 5-3 사용자 레벨용 이늄**
<br/><br/>

#### User 필드 추가
```
public class User {
	...
	Level level;
	int login;
	int recommend;
	
	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	...
	
	// login, recommend getter/setter 생략
}
```
**리스트 5-4 User에 추가된 필드**
<br/><br/>

![USER 테이블 추가 필드](https://github.com/taechacode/TIL/assets/63395751/aba6cfd7-79ea-4c05-8a2a-74fbc100ac8f)
<br/>
**표 5-1 USER 테이블 추가 필드**
<br/><br/>

#### UserDaoTest 테스트 수정
```
public class UserDaoTest {
	...
	@Before
	public void setUp() {
		this.user1 = new User("jinia91", "최원진", "springno1", Level.BASIC, 1, 0); // 추가된 필드를 위한 초기값
		this.user2 = new User("albireo3754", "윤상진", "springno2", Level.BASIC, 55, 10);
		this.user3 = new User("tlswltjq", "신지섭", "springno3", Level.BASIC, 100, 40);
	}
}
```
**리스트 5-5 수정된 테스트 픽스처**
<br/><br/>

```
class User {
	...
	public User(String id, String name, String password, Level level, int login, int recommend) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}
}
```
**리스트 5-6 추가된 필드를 파라미터로 포함하는 생성자**
<br/><br/>

```
private void checkSameUser(User user1, User user2) {
	assertThat(user1.getId(), is(User2.getId()));
	assertThat(user1.getName(), is(User2.getName()));
	assertThat(user1.getPassword(), is(User2.getPassword()));
	assertThat(user1.getLevel(), is(user2.getLevel()));
	assertThat(user1.getLogin(), is(user2.getLogin()));
	assertThat(user1.getRecommend(), is(user2.getRecommend()));
}
```
**리스트 5-7 새로운 필드를 포함하는 User 필드 값 검증 메소드**
<br/><br/>

```
@Test public void addAndGet() {
	...
	User userget1 = dao.get(user1.getId());
	checkSameUser(userget1, user1);
	
	User userget2 = dao.get(user2.getId());
	checkSameUser(userget2, user2);
}
```
**리스트 5-8 checkSameUser() 메소드를 사용하도록 만든 addAndGet() 메소드**
<br/><br/>

#### UserDaoJdbc 수정
- 등록을 위한 INSERT 문장이 들어 있는 add() 메소드의 SQL과 각종 조회 작업에 사용되는 User 오브젝트 매핑용 콜백인 userMapper에 추가된 필드를 넣는다.
```
user.setLevel(Level.valueOf(rs.getInt("level")));
user.setLogin(rs.getInt("login"));
user.setRecommend(rs.getInt("recommend"));
```
**리스트 5-9 추가된 필드를 위한 UserDaoJdbc의 수정 코드**
<br/><br/>

### 5.1.2 사용자 수정 기능 추가
- 수정할 정보가 담긴 User 오브젝트를 전달하면 id를 참고해서 사용자를 찾아 필드 정보를 UPDATE 문을 이용해 모두 변경해주는 메소드를 만들어보자.

#### 수정 기능 테스트 추가
```
@Test
public void update() {
	dao.deleteAll();
	dao.add(user1);

	// 픽스처에 들어 있는 정보를 변경해서 수정 메소드를 호출한다.
	user1.setName("오민규");
	user1.setPassword("springno6");
	user1.setLevel(Level.GOLD);
	user1.setLogin(1000);
	user1.setRecommend(999);
	dao.update(user1);
	
	User user1update = dao.get(user1.getId());
	checkSameUser(user1, user1update);
}
```
**리스트 5-10 사용자 정보 수정 메소드 테스트**
<br/><br/>

#### UserDao와 UserDaoJdbc 수정
#### 수정 테스트 보완
<br/>

### 5.1.3 UserService.upgradeLevels()
#### UserService 클래스와 빈 등록
#### UserServiceTest 테스트 클래스
#### upgradeLevels() 메소드
#### upgradeLevels() 테스트
<br/>

### 5.1.4 UserService.add()
<br/>

### 5.1.5 코드 개선
#### upgradeLevels() 메소드 코드의 문제점
#### upgradeLevels() 리팩토링
#### User 테스트
#### UserServiceTest 개선
<br/>

## 5.2 트랜잭션 서비스 추상화

### 5.2.1 모 아니면 도
#### 테스트용 UserService 대역
#### 강제 예외 발생을 통한 테스트
#### 테스트 실패의 원인
<br/>

### 5.2.2 트랜잭션 경계설정
#### JDBC 트랜잭션의 트랜잭션 경계설정
#### UserService와 UserDao의 트랜잭션 문제
#### 비즈니스 로직 내의 트랜잭션 경계설정
#### UserService 트랜잭션 경계설정의 문제점
<br/>

### 5.2.3 트랜잭션 동기화
#### Connection 파라미터 제거
#### 트랜잭션 동기화 적용
#### 트랜잭션 테스트 보완
#### JdbcTemplate과 트랜잭션 동기화
<br/>

### 5.2.4 트랜잭션 서비스 추상화
#### 기술과 환경에 종속되는 트랜잭션 경계설정 코드
#### 트랜잭션 API의 의존관계 문제와 해결책
#### 스프링의 트랜잭션 서비스 추상화
#### 트랜잭션 기술 설정의 분리
<br/>

## 5.3 서비스 추상화와 단일 책임 원칙
#### 수직, 수평 계층구조와 의존관계
#### 단일 책임 원칙
#### 단일 책임 원칙의 장점
<br/>

## 5.4 메일 서비스 추상화

### 5.4.1 JavaMail을 이용한 메일 발송 기능
#### JavaMail 메일 발송
<br/>

### 5.4.2 JavaMail이 포함된 코드의 테스트
<br/>

### 5.4.3 테스트를 위한 서비스 추상화
#### JavaMail을 이용한 테스트의 문제점
#### 메일 발송 기능 추상화
#### 테스트용 메일 발송 오브젝트
#### 테스트와 서비스 추상화
<br/>

### 5.4.4 테스트 대역
#### 의존 오브젝트의 변경을 통한 테스트 방법
#### 테스트 대역의 종류와 특징
#### 목 오브젝트를 이용한 테스트
<br/>
