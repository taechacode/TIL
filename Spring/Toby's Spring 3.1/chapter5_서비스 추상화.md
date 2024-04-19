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
- UserDao 인터페이스에 update() 메소드가 없다는 컴파일 에러가 나므로 추가
- UserDaoJdbc의 update() 메소드는 add()와 비슷한 방식으로 추가
<br/><br/>

#### 수정 테스트 보완
- UPDATE에 WHERE절이 없을 경우 어떻게 동작을 검증할 수 있을까?
- 첫 번째 방법은 JdbcTemplate의 update()가 돌려주는 return 값을 확인한다.
	- return 해주는 row의 개수가 1 이상이라면 update() 메소드의 SQL에 문제가 있다.
- 두 번째 방법은 테스트를 보강해서 원하는 사용자 외의 정보는 변경되지 않았음을 직접 확인한다.
```
@Test
public void update() {
	dao.deleteAll();
	
	dao.add(user1); // 수정할 사용자
	dao.add(user2); // 수정하지 않을 사용자
	
	user1.setName("오민규");
	user1.setPassword("springno6");
	user1.setLevel(Level.GOLD);
	user1.setLogin(1000);
	user1.setRecommend(999);
	
	dao.update(user1);
	
	User user1update = dao.get(user1.getId());
	checkSameUser(user1, user1update);
	User user2same = dao.get(user2.getId());
	checkSameUser(user2, user2same); // WHERE절을 빼먹으면 테스트 실패
}
```
**리스트 5-13 보완된 update() 테스트**
<br/><br/>

### 5.1.3 UserService.upgradeLevels()
- 사용자 관리 로직은 어디다 두는 것이 좋을까?
- UserDaoJdbc는 적합하지 않다. DAO는 데이터를 어떻게 가져오고 조작할지를 다루는 곳이지 비즈니스 로직을 두는 곳이 아니다.
- 사용자 관리 비즈니스 로직을 담을 클래스를 하나 추가하자. 비즈니스 로직 서비스를 제공한다는 의미에서 클래스 이름은 `UserService`로 한다.
- UserService는 UserDao의 구현 클래스가 바뀌어도 영향받지 않도록 해야 한다. 따라서 DAO의 인터페이스를 사용하고 DI를 적용해야 한다.
<br/><br/>

![UserService의 의존 관계](https://github.com/taechacode/TIL/assets/63395751/e301cba0-d4da-4d80-b6a5-f5dd94034ef4)
<br/>
**그림 5-1 UserService의 의존관계**
<br/><br/>

#### UserService 클래스와 빈 등록
```
package springbook.user.service;
...
public class UserService {
	UserDao userDao;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}	
}
```
**리스트 5-14 UserService 클래스**
<br/><br/>

```
<bean id="userService" class="springbook.user.service.UserService">
	<property name="userDao" ref="userDao" />
</bean>
```
**리스트 5-15 userService 빈 설정**
<br/><br/>

#### UserServiceTest 테스트 클래스
- UserServiceTest 클래스를 추가하고 테스트 대상인 UserService 빈을 제공받을 수 있도록 @Autowired가 붙은 인스턴스 변수로 선언해준다.
```
package springbook.user.service;
...
@RunWith(SpringJunit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserService {
	@Autowired
	UserService userService;
}
```
**리스트 5-16 UserServiceTest 클래스**
- 이 상태로 JUnit 테스트로 실행해보면 테스트 메소드가 하나도 없다고 에러가 날 것이다.
<br/><br/>

```
@Test
public void bean() {
	assertThat(this.userService, is(notNullValue()));
}
```
**리스트 5-17 userService 빈의 주입을 확인하는 테스트**
<br/><br/>

#### upgradeLevels() 메소드
```
public void upgradeLevels() {
	
	List<User> users = userDao.getAll();
	
	for(User user : users) {
		
		Boolean changed = null; // 레벨의 변화가 있는지를 확인하는 플래그
		
		if(user.getLevel() == Level.BASIC && user.getLogin() >= 50) { // BASIC 레벨 업그레이드 작업
			user.setLevel(Level.SILVER);
			changed = true;
		} else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30) { // SILVER 레벨 업그레이드 작업
			user.setLevel(Level.GOLD);
			changed = true;
		} else if(user.getLevel() == Level.GOLD) { // GOLD 레벨은 변경이 일어나지 않는다
			changed = false;
		} else { // 일치하는 조건이 없으면 변경 없음
			changed = false;
		}
		
		if(changed) { // 레벨의 변경이 있는 경우에만 update() 호출
			userDao.update(user);
		}
		
	}
}
```
**리스트 5-18 사용자 레벨 업그레이드 메소드**
<br/><br/>

#### upgradeLevels() 테스트
```
class UserServiceTest {
	...
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList( // 배열을 리스트로 만들어서 가변인자로 넣어주고 있다.
				new User("bumjin", "박범진", "p1", Level.BASIC, 49, 0);
				new User("joytouch", "강명성", "p2", Level.BASIC, 50, 0);
				new User("erwins", "신승한", "p3", Level.SILVER, 60, 29);
				new User("madnite1", "이상호", "p4", Level.SILVER, 60, 30);
				new User("green", "오민규", "p5", Level.GOLD, 100, 100);
		);
	}
}
```
**리스트 5-19 리스트로 만든 테스트 픽스처**
<br/><br/>

```
@Test
public void upgradeLevels() {
	
	userDao.deleteAll();
	
	for(User user : users) {
		userDao.add(user);
	}
	
	// 각 사용자별로 업그레이드 후의 예상 레벨을 검증한다.
	checkLevel(users.get(0), Level.BASIC);
	checkLevel(users.get(1), Level.SILVER);
	checkLevel(users.get(2), Level.SILVER);
	checkLevel(users.get(3), Level.GOLD);
	checkLevel(users.get(4), Level.GOLD);
	
}

private void checkLevel(User user, Level expectedLevel) {
	User userUpdate = userDao.get(user.getId());
	assertThat(userUpdate.getLevel(), is(expectedLevel));
}
```
**리스트 5-20 사용자 레벨 업그레이드 테스트**
<br/><br/>

### 5.1.4 UserService.add()
- 아직 구현되지 않은 비즈니스 로직이 하나 있다. 처음 가입하는 사용자는 기본적으로 BASIC 레벨이어야 한다는 부분이다. 이 로직은 어디에 담는 것이 좋을까?
- 일단 UserDaoJdbc의 add() 메소드는 적합하지 않아 보인다. UserDaoJdbc는 주어진 User 오브젝트를 DB에 정보를 넣고 읽는 방법에만 관심을 가져야지, 비즈니스적인 의미를 지닌 정보를 설정하는 책임을 지는 것은 바람직하지 않다.
- 그렇다면 User 클래스에서 아예 level 필드는 Level.BASIC으로 초기화하는 것은 어떨까? 하지만 처음 가입할 때를 제외하면 무의미한 정보인데 단지 이 로직을 담기 위해 클래스에서 직접 초기화하는 것은 문제가 있어 보인다.
- 그렇다면 사용자 관리에 대한 비즈니스 로직을 담고 있는 UserService에 이 로직을 넣으면 어떨까? UserDao의 add() 메소드는 사용자 정보를 담은 User 오브젝트를 받아서 DB에 넣어주는 데 충실한 역할을 한다면, UserService에도 add()를 만들어두고 사용자가 등록될 때 적용할 만한 비즈니스 로직을 담당하게 하면 될 것이다.
<br/><br/>

```
@Test
public void add() {
	
	userDao.deleteAll();
	
	User userWithLevel = users.get(4); // GOLD 레벨이 이미 지정된 User라면 레벨을 초기화하지 않아야 한다.
	
	// 레벨이 비어 있는 사용자. 로직에 따라 등록 중에 BASIC 레벨도 설정돼야 한다.
	User userWithoutLevel = users.get(0);
	userWithoutLevel.setLevel(null);

	// add() 메소드를 통해 초기화한 뒤에 DB에 저장된다.
	userService.add(userWithLevel);
	userService.add(userWithoutLevel);
	
	// 저장된 결과를 가져와서 확인한다.
	User userWithLevelRead = userDao.get(userWithLevel.getId());
	User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

	assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
	assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC)); // 디폴트인 BASIC 레벨로 설정되었는지 확인

}
```
**리스트 5-21 add() 메소드의 테스트**
<br/><br/>

```
public void add(User user) {
	if(user.getLevel() == null) {
		user.setLevel(Level.BASIC);		
	}
	userDao.add(user);
}
```
**리스트 5-22 사용자 신규 등록 로직을 담은 add() 메소드**
<br/><br/>

### 5.1.5 코드 개선
- 코드에 중복된 부분은 없는가?
- 코드가 무엇을 하는 것인지 이해하기 불편하지 않은가?
- 코드가 자신이 있어야 할 자리에 있는가?
- 앞으로 변경이 일어난다면 어떤 것이 있을 수 있고, 그 변화에 쉽게 대응할 수 있게 작성되어 있는가?
<br/><br/>

#### upgradeLevels() 메소드 코드의 문제점
```
if(user.getLevel() == Level.BASIC && user.getLogin() >= 50) { // BASIC 레벨 업그레이드 작업
	user.setLevel(Level.SILVER);
	changed = true;
}
...

if(changed) { // 레벨의 변경이 있는 경우에만 update() 호출
	userDao.update(user);
}
```
- for 루프 속에 들어있는 if/elseif/else 블록들이 읽기 불편하다. 레벨의 변화 단계와 업그레이드 조건, 조건이 충족됐을 때 해야 할 작업이 한데 섞여 있어서 로직을 이해하기가 쉽지 않다.
- 이런 if 조건 블록이 레벨 개수만큼 반복된다. 만약 새로운 레벨이 추가된다면 Level Enum도 수정해야 하고, upgradeLevels()의 레벨 업그레이드 로직을 담은 코드에 if 조건식과 블록을 추가해줘야 한다.
- 현재 레벨과 업그레이드 조건을 동시에 비교하는 부분도 문제가 될 수 있다. BASIC이면서 로그인 횟수가 50이 되지 않은 경우에는 마지막 else 블록으로 이동한다, 새로운 레벨이 추가돼도 역시 기존의 if 조건들에 맞지 않을 테니 else 블록으로 이동할 것이다. 성격이 다른 두 가지 경우가 모두 한 곳에서 처리되는 것은 뭔가 이상하다.
<br/><br/>

#### upgradeLevels() 리팩토링
```
public void upgradeLevels() {
	List<Users> users = userDao.getAll();
	for(User user : users) {
		if(canUpgradeLevel(user)) {
			upgradeLevel(user);
		}
	}
}
```
**리스트 5-23 기본 작업 흐름만 남겨둔 upgradeLevels()**
<br/><br/>

```
private boolean canUpgradeLevel(User user) {
	Level currentLevel = user.getLevel();
	
	// 레벨별로 구분해서 조건을 판단한다.
	switch(currentLevel) {
		case BASIC: return (user.getLogin() >= 50);
		case SILVER: return (user.getRecommend() >= 30);
		case GOLD: return false;
		
		// 현재 로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킨다.
		// 새로운 레벨이 추가되고 로직을 수정하지 않으면 에러가 나서 확인할 수 있다.
		default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
	}
	
}
```
**리스트 5-24 업그레이드 가능 확인 메소드**
- 상태에 따라서 업그레이드 조건만 비교하면 되므로, 역할과 책임이 명료해진다.
<br/><br/>

```
private void upgradeLevel(User user) {
	if(user.getLevel() == Level.BASIC) user.setLevel(Level.SILVER);
	else if(user.getLevel() == Level.SILVER) user.setLevel(Level.GOLD);
	userDao.update(user);
}
```
**리스트 5-25 레벨 업그레이드 작업 메소드**
- 사용자 오브젝트의 레벨정보를 다음 단계로 변경하고, 변경된 오브젝트를 DB에 업데이트하는 두 가지 작업을 수행한다.
- 하지만 이 메소드에도 문제점이 있다. 먼저, 다음 단계가 무엇인가 하는 로직과 그때 사용자 오브젝트의 level 필드를 변경해준다는 로직이 함께 있는데다, 너무 노골적으로 드러나 있다.
- 게다가 예외상황에 대한 처리가 없다. 만약 업그레이드 조건을 잘못 파악해서 더 이상 다음 단계가 없는 GOLD 레벨인 사용자를 업그레이드하려고 이 메소드를 잘못 호출한다면 아무것도 처리하지 않고 그냥 DAO의 업데이트 메소드만 실행될 것이다.
- 레벨이 늘어나면 if문이 점점 길어질 것이고, 레벨 변경 시 사용자 오브젝트에서 level 필드 외의 값도 같이 변경해야 한다면 if 조건 뒤에 붙는 내용도 점점 길어질 것이다.
<br/><br/>

```
public enum Level {
	
	// Enum 선언에 DB에 저장할 값과 함께 다음 단계의 레벨 정보도 추가한다.
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);
	
	private final int value;
	private final Level next; // 다음 단계의 레벨 정보를 스스로 갖고 있도록 Level 타입의 next 변수를 추가한다.
	
	Level(int value, Level next) {
		this.value = value;
		this.next = next;
	}
	
	public int intValue() {
		return value;
	}
	
	public Level nextLevel() {
		return this.next;
	}
	
	public static Level valueOf(int value) {
		switch(value) {
			case 1: return BASIC;
			case 2: return SILVER;
			case 3: return GOLD;
			default: throw new AssertionError("Unknown value: " + value);
		}
	}
}
```
**리스트 5-26 업그레이드 순서를 담고 있도록 수정한 Level**
- 먼저 레벨의 순서와 다음 단계 레벨이 무엇인지를 결정하는 일은 Level에게 맡긴다. 레벨의 순서를 굳이 UserService에 담아둘 이유가 없다.
<br/><br/>

```
public void upgradeLevel() {
	Level nextLevel = this.level.nextLevel();
	if(nextLevel == null) {
		throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
	} else {
		this.level = nextLevel;
	}
}
```
**리스트 5-27 User의 레벨 업그레이드 작업용 메소드**
<br/><br/>

```
private void upgradeLevel(User user) {
	user.upgradeLevel();
	userDao.update(user);
}
```
**리스트 5-28 간결해진 upgradeLevel()**
<br/><br/>

#### User 테스트
```
package springbook.user.service;
...
public class UserTest {
	User user;
	
	@Before
	public void setUp() {
		user = new User();
	}

	// Level Enum에 정의된 모든 레벨을 가져와서 User에 설정해두고 User의 upgradeLevel()을 실행해서 다음 레벨로 바뀌는지를 확인하는 테스트
	@Test()
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levles) {
			if(level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}

	// 더 이상 업그레이드할 레벨이 없는 경우에 upgradeLevel()을 호출하면 예외가 발생하는지를 확인하는 테스트
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if(level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
		}
	}
	
}
```
**리스트 5-29 User 테스트**
<br/><br/>

#### UserServiceTest 개선
```
@Test
public void upgradeLevels() {
	
	userDao.deleteAll();
	for(User user : users) userDao.add(user);
	
	userService.upgradeLevels();
	
	checkLevelUpgraded(users.get(0), false);
	checkLevelUpgraded(users.get(1), true);
	checkLevelUpgraded(users.get(2), false);
	checkLevelUpgraded(users.get(3), true);
	checkLevelUpgraded(users.get(4), false);
	
}

private void checkLevelUpgraded(User user, boolean upgraded) { // upgraded 변수는 어떤 레벨로 바뀔 것인가가 아니라, 다음 레벨로 업그레이드될 것인가를 지정한다.
	User userUpdate = userDao.get(user.getId());
	if(upgraded) {
		assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel())); // 업그레이드가 일어났는지 확인
	} else {
		assertThat(userUpdate.getLevel(), is(user.getLevel())); // 업그레이드가 일어나지 않았는지 확인
	}
}
```
**리스트 5-30 개선한 upgradeLevels() 테스트**
- 각 사용자에 대해 업그레이드를 확인하려는 것인지 아닌지가 좀 더 이해하기 쉽게 true, false로 나타나 있어서 보기 좋다.
<br/><br/>

```
case BASIC: return (user.getLogin() >= 50); // UserService
new User("joytouch", "강명성", "p2", Level.BASIC, 50, 0) // UserServiceTest
```
- 업그레이드 조건인 로그인 횟수와 추천 횟수가 애플리케이션 코드와 테스트 코드에 중복돼서 나타난다.
- 테스트와 애플리케이션 코드에 나타난 이런 숫자의 중복도 제거해줘야 한다. 한 가지 변경 이유가 발생했을 때 여러 군데를 고치게 만든다면 중복이기 때문이다.
- 기준이 되는 최소 로그인 횟수가 변경될 때도 한 번만 수정할 수 있도록 만든다.
<br/><br/>

```
public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
public static final int MIN_RECCOMEND_FOR_GOLD = 30;

private boolean canUpgradeLevel(User user) {
	
	Level currentLevel = user.getLevel();
	switch(currentLevel) {
		case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
		case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
		case GOLD: return false;
		default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
	}
	
}
```
**리스트 5-31 상수의 도입 (UserService 수정)**
<br/><br/>

```
import static springbook.user.service.Userservice.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.Userservice.MIN_RECCOMEND_FOR_GOLD;
...

@Before
public void setUp() {
	users = Arrays.asList(
			new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0);
			new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0);
			new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1);
			new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD);
			new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE);
	);
}
```
**리스트 5-32 상수를 사용하도록 만든 테스트 (UserServiceTest 수정)**
- 숫자로만 되어 있는 경우에는 비즈니스 로직을 상세히 코멘트로 달아놓거나 설계문서를 참조하기 전에는 이해하기 힘들었던 부분이 이제는 무슨 의도로 어떤 값을 넣었는지 이해하기 쉬워졌다.
<br/><br/>

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

## 부록
```
package rps.core.member;

public class Member {

    private Long id;
    private String name;
    private MemberType memberType;
    private Long burdfeePayinAmt;
    private Long limitPayinAmt;
    private Long limitTaxAmt;
    private int pensionCount;

    public Member(Long id, String name, MemberType memberType) {
        this.id = id;
        this.name = name;
        this.memberType = memberType;
        this.burdfeePayinAmt = 0L;
        this.limitPayinAmt = 18000000L;
        this.limitTaxAmt = 0L;
        if(memberType == MemberType.PER) {
            limitTaxAmt = 9000000L;
        }
        this.pensionCount = 0;
    }
```
**기존 개인 프로젝트에서 개발한 Member 클래스**
- Member 객체 생성자 단계에서 입금 한도 및 세액공제 한도를 체크하는 것이 옳은가?

<br/><br/>
### Java Enum 활용기 (우아한형제들 기술블로그)
***https://techblog.woowahan.com/2527/***
