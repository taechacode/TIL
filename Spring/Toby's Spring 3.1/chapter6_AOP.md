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
- 가장 편하고 좋은 테스트 방법은 가능한 한 작은 단위로 쪼개서 테스트하는 것이다.
- 작은 단위의 테스트가 좋은 이유는 테스트가 실패했을 때 그 원인을 찾기 쉽기 때문이다. 반대로 테스트에서 오류가 발견됐을 때 그 테스트가 진행되는 동안 실행된 코드의 양이 많다면 그 원인을 찾기가 매우 힘들어질 수 있다. 또한 테스트 단위가 작아야 테스트의 의도나 내용이 분명해지고, 만들기도 쉬워진다.
<br/><br/>

### 6.2.1 복잡한 의존관계 속의 테스트
- 책 그림 6-5를 보면 UserService를 분리하기 전의 테스트가 동작하는 모습을 보여주고 있다.
- UserService는 사용자 정보를 관리하는 비즈니스 로직의 구현 코드다. UserService의 코드가 바르게 작성되어 있으면 성공하고, 아니라면 실패하면 된다. 따라서 테스트의 단위는 UserService 클래스여야 한다.
- 하지만 UserService는 UserDao, TransactionManager, MailSender라는 3가지 의존관계를 갖고 있다. 따라서 그 3가지 `의존관계를 갖는 오브젝트들이 테스트가 진행되는 동안에 같이 실행된다`.
- 이런 경우의 테스트는 준비하기 힘들고, 환경이 조금이라도 달라지면 동일한 테스트 결과를 내지 못할 수도 있으며, 수행 속도는 느리고 그에 따라 테스트를 작성하고 실행하는 빈도가 점차로 떨어질 것이 분명하다. 그 오류 때문에 UserService의 테스트가 실패한다면 그 원인을 찾느라고 불필요한 시간을 낭비해야 할 수도 있다.
<br/><br/>

### 6.2.2 테스트 대상 오브젝트 고립시키기
#### 테스트를 위한 UserServiceImpl 고립
![고립시킨 UserServiceImpl에 대한 테스트 구조](https://github.com/taechacode/TIL/assets/63395751/acdad14c-4712-42d4-a965-fd7aaf21ddb8)
**그림 6-6 고립시킨 UserServiceImpl에 대한 테스트 구조**
- UserServiceImpl에 대한 테스트가 진행될 때 사전에 테스트를 위해 준비된 동작만 하도록 만든 2개의 목 오브젝트에만 의존하는, 완벽하게 고립된 테스트 대상으로 만들었다.
- UserDao는 단지 테스트 대상의 코드가 정상적으로 수행되도록 도와주기만 하는 스텁(Stub)이 아니라, 부가적인 검증 기능까지 가진 목 오브젝트로 만들었다. 그 이유는 고립된 환경에서 동작하는 upgradeLevels()의 테스트 결과를 검증할 방법이 필요하기 때문이다.
<br/><br/>

#### 고립된 단위 테스트 활용
- upgradeLevels() 테스트는 다음과 같은 단계로 진행된다.

1. 테스트 실행 중에 UserDao를 통해 가져올 테스트용 정보를 DB에 넣는다. UserDao는 결국 DB를 이용해 정보를 가져오기 때문에 최후의 의존 대상인 DB에 직접 정보를 넣어줘야 한다.
2. 메일 발송 여부를 확인하기 위해 MailSender에 목 오브젝트를 DI 해준다.
3. 실제 테스트 대상인 userService의 메소드를 실행한다.
4. 결과가 DB에 반영됐는지 확인하기 위해서 UserDao를 이용해 DB에서 데이터를 가져와 결과를 확인한다.
5. 목 오브젝트를 통해 UserService에 의한 메일 발송이 있었는지를 확인하면 된다.

- 처음 2가지는 UserService의 upgradeLevels() 메소드가 실행되는 동안에 사용하는 의존 오브젝트가 테스트의 목적에 맞게 동작하도록 준비하는 과정이다.
- 첫 번째 작업은 의존관계를 따라 마지막에 등장하는 DB를 준비하는 것인 반면에, 두 번째는 테스트를 의존 오브젝트와 서버 등에서 고립시키도록 테스트만을 위한 목 오브젝트를 준비한다는 점이 다르다.
- 네 번째와 다섯 번째는 테스트 대상 코드를 실행한 후에 결과를 확인하는 작업이다.
- 네 번째는 의존관계를 따라 결국 최종 결과가 반영된 DB의 내용을 확인하는 방법인 반면, 다섯 번째는 메일 서버까지 갈 필요 없이 목 오브젝트를 통해 upgradeLevels() 메소드가 실행되는 주엥 메일 발송 요청이 나간 적이 있는지만 확인하도록 되어 있다.
<br/><br/>

#### UserDao 목 오브젝트
- UserDao와 DB까지 직접 의존하고 있는 첫 번째와 네 번째의 테스트 방식도 목 오브젝트를 만들어서 적용해보자.
```
public void upgradeLevels() {
	List<User> users = userDao.getAll(); // 업그레이드 후보 사용자 목록을 가져온다.
	for(User user : users) {
		if(canUpgradeLevel(user)) {
			upgradeLevel(user);
		}
	}
}

protected void upgradeLevel(User user) {
	user.upgradeLevel();
	userDao.update(user); // 수정된 사용자 정보를 DB에 반영한다.
	sendUpgradeEmail(user);
}
```
**리스트 6-11 사용자 레벨 업그레이드 작업 중에 UserDao를 사용하는 코드**
<br/><br/>

```
static class MockUserDao implements UserDao {
	private List<User> users; // 레벨 업그레이드 후보 User 오브젝트 목록
	private List<User> updated = new ArrayList<>(); // 업그레이드 대상 오브젝트를 저장해둘 목록
	
	private MockUserDao(List<User> users) {
		this.users = users;
	}
	
	public List<User> getUpdated() {
		return this.updated;
	}
	
	// 스텁 기능 제공
	public List<User> getAll() {
		return this.users;
	}
	
	// 목 오브젝트 기능 제공
	public void update(User user) {
		updated.add(user);
	}
	
	// 테스트에 사용되지 않는 메소드들
	public void add(User user) { throw new UnspportedOperationException(); }
	public void deleteAll() { throw new UnspportedOperationException(); }
	public void get(String id) { throw new UnspportedOperationException(); }
	public int getCount() { throw new UnspportedOperationException(); }
}
```
**리스트 6-12 UserDao 오브젝트**
- MockUserDao는 2개의 User 타입 리스트를 정의해둔다.
- 하나는 생성자를 통해 전달받은 사용자 목록을 저장해뒀다가, getAll() 메소드가 호출되면 DB에서 가져온 것처럼 돌려주는 용도다. 목 오브젝트를 사용하지 않을 때는 일일이 DB에 저장했다가 다시 가져와야 했지만, MockUserDao는 미리 준비된 테스트용 리스트를 메모리에 갖고 있다가 돌려주기만 하면 된다.
- 다른 하나는 update() 메소드를 실행하면서 넘겨준 업그레이드 대상 User 오브젝트를 저장해뒀다가 검증을 위해 돌려주기 위한 것이다. upgradeLevels() 메소드가 실행되는 동안 업그레이드 대상으로 선정된 사용자가 어떤 것인지 확인하는 데 쓰인다.
<br/><br/>

```
@Test
public void upgradeLevels() throws Exception {
	
	// 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성하면 된다.
	UserServiceImpl userServiceImpl = new UserServiceImpl();
	
	// 목 오브젝트로 만든 UserDao를 직접 DI 해준다.
	MockUserDao mockUserDao = new MockUserDao(this.users);
	userServiceImpl.setUserDao(mockUserDao);
	
	MockMailSender mockMailSender = new MockMailSender();
	userServiceImpl.setMailSender(mockMailSender);
	
	userServiceImpl.upgradeLevels();
	
	List<User> updated = mockUserDao.getUpdated(); // MockUserDao로부터 업데이트 결과를 가져온다.
	
	// 업데이트 횟수와 정보를 확인한다.
	assertThat(updated.size(), is(2));
	checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
	checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
	
	List<String> request = mockMailSender.getRequests();
	assertThat(request.size(), is(2));
	assertThat(request.get(0), is(users.get(1).getEmail()));
	assertThat(request.get(1), is(users.get(3).getEmail()));
}

private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
	assertThat(updated.getId(), is(expectedId));
	assertThat(updated.getLevel(), is(expectedLevel));
}
```
**리스트 6-13 MockUserDao를 사용해서 만든 고립된 테스트**
<br/><br/>

### 6.2.3 단위 테스트와 통합 테스트
- 단위 테스트의 단위는 정하기 나름이다. 사용자 관리 기능 전체를 하나의 단위로 볼 수도 있고 하나의 클래스나 하나의 메소드를 단위로 볼 수도 있다. 중요한 것은 하나의 단위에 초점을 맞춘 테스트라는 점이다.
- 이 책에서는 테스트 대상 클래스를 목 오브젝트 등의 테스트 대역을 이용해 의존 오브젝트나 외부의 리소스를 사용하지 않도록 고립시켜서 테스트하는 것을 `단위 테스트`로 부르고 있다.
- 반면에 2개 이상의, 성격이나 계층이 다른 오브젝트가 연동되도록 만들어 테스트하거나, 또는 외부의 DB나 파일, 서비스 등의 리소스가 참여하는 테스트는 `통합 테스트`라고 부르고 있다.
<br/>

- 테스트를 먼저 만들어두는 TDD는 코드를 만들자마자 바로 테스트가 가능하다는 장점이 있다.
- 코드를 만들고 나서 오랜 시간이 지난 뒤에 작성하는 테스트는 테스트 대상 코드에 대한 이해가 떨어지기 때문에 불완전해지기 쉽고 작성하기도 번거롭다.
- 테스트하기 편하게 만들어진 코드는 깔끔하고 좋은 코드가 될 가능성이 높다.
<br/><br/>

## 6.3 다이내믹 프록시와 팩토리 빈
### 6.3.1 프록시와 프록시 패턴, 데코레이터 패턴
![전략 패턴 적용을 통한 부가기능 구현의 분리](https://github.com/taechacode/TIL/assets/63395751/d5f60fba-6440-4d25-b867-7e8c3a6a406a)
**그림 6-7 전략 패턴 적용을 통한 부가기능 구현의 분리**
- 위 그림은 트랜잭션과 같은 부가적인 기능을 위임을 통해 외부로 분리했을 때의 결과를 보여준다. 구체적인 구현 코드는 제거햇을지라도 위임을 통해 기능을 사용하는 코드는 핵심 코드와 함께 남아 있다.
- 트랜잭션이라는 기능은 사용자 관리 비즈니스 로직과는 성격이 다르기 때문에 아예 그 적용 사실 자체를 밖으로 분리할 수 있다.
<br/><br/>

![부가기능과 핵심기능의 분리](https://github.com/taechacode/TIL/assets/63395751/cc875728-60cd-446b-a2d5-aa2ce5f679f5)
**그림 6-8 부가기능과 핵심기능의 분리**
- 위 그림과 같이 부가기능 전부를 핵심 코드가 담긴 클래스에서 독립시킬 수 있다. 이 방법을 통해 UserServiceTx를 만들었고, UserServiceImpl에는 트랜잭션 관련 코드가 하나도 남지 않게 됐다.
- 핵심기능은 부가기능을 가진 클래스의 존재 자체를 모른다. 따라서 부가기능이 핵심기능을 사용하는 구조가 되었다.
- 문제는 이렇게 구성했더라도 클라이언트가 핵심기능을 가진 클래스를 직접 사용해버리면 부가기능이 적용될 기회가 없다.
<br/><br/>

![핵심기능 인터페이스의 적용](https://github.com/taechacode/TIL/assets/63395751/04eaa6fa-9e4b-4165-bd6a-ad404f4b3376)
**그림 6-9 핵심기능 인터페이스의 적용**
- 부가기능은 마치 자신이 핵심기능을 가진 클래스인 것처럼 꾸며서, 클라이언트가 자신을 거쳐서 핵심기능을 사용하도록 만들어야 한다.
- 그러기 위해서 클라이언트는 인터페이스를 통해서만 핵심기능을 사용하게 하고, 부가기능 자신도 같은 인터페이스를 구현한 뒤에 자신이 그 사이에 끼어들어야 한다.
- 그러면 클아이언트는 인터페이슴나 보고 사용을 하기 때문에 자신은 핵심기능을 가진 클래스를 사용할 것이라고 기대하지만, 사실은 부가기능을 통해 핵심기능을 이용하게 된다.
<br/><br/>

![프록시와 타깃](https://github.com/taechacode/TIL/assets/63395751/fcd7f75c-5f06-463a-8771-a4fdd29341a8)
**그림 6-10 프록시와 타깃**
- 부가기능 코드에서는 핵심기능으로 요청을 위임해주는 과정에서 자신이 가진 부가적인 기능을 적용해줄 수 있다. 비즈니스 로직 코드에 트랜잭션 기능을 부여해주는 것이 바로 그런 대표적인 경우다.
- 이렇게 마치 자신이 클라이언트가 사용하려고 하는 실제 대상인 것처럼 위장해서 클라이언트의 요청을 받아주는 것을 대리자, 대리인과 같은 역할을 한다고 해서 `프록시 proxy`라고 부른다. 그리고 프록시를 통해 최종적으로 요청을 위임받아 처리하는 실제 오브젝트를 `타깃 target` 또는 `실체 real subject`라고 부른다.
- 위 그림에서는 클라이언트가 프록시를 통해 타깃을 사용하는 구조를 보여주고 있다.
<br/><br/>

- 프록시는 사용 목적에 따라 두 가지로 구분할 수 있다.
- 첫째는 클라이언트가 타깃에 접근하는 방법을 제어하기 위해서다.
- 둘째는 타깃에 부가적인 기능을 부여해주기 위해서다.
- 두 가지 모두 대리 오브젝트라는 개념의 프록시를 두고 사용한다는 점은 동일하지만, 목적에 따라서 디자인 패턴에서는 다른 패턴으로 구분한다.
<br/><br/>
