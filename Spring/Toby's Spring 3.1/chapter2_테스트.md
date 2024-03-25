# 2장 테스트

## 2.1 UserDaoTest 다시 보기
### 2.1.1 테스트의 유용성



### 2.1.2 UserDaoTest의 특징
```
public class UserDaoTest {
    public static void main(String[] args) throws SQLException {
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        UserDao dao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("user");
        user.setName("강태찬");
        user.setPassword("unmarried");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공");    
    }
}
```

- 위 테스트 코드는 자바에서 쉽게 실행 가능한 `main() 메소드`를 이용한다.
- 테스트할 대상인 UserDao의 오브젝트를 가져와 메소드를 호출한다.
- 테스트에 사용할 입력 값(User 오브젝트)을 `직접 코드에서 만들어` 넣어준다.
- 테스트의 결과를 `콘솔`에 출력해준다.
- 각 단계의 작업이 에러 없이 끝나면 콘솔에 성공 메시지로 출력해준다.
<br/>

- 위 테스트 방법에서 가장 돋보이는 것은 main() 메소드를 이용해 쉽게 테스트 수행을 가능하게 했다는 점과 테스트 대상인 UserDao를 직접 호출해서 사용한다는 점이다.
<br/>



#### 웹을 통한 DAO 테스트 방법의 문제점
- 보통 웹 프로그램에서 DAO를 테스트하는 방법은 서비스 계층, MVC 프레젠테이션 계층까지 포함한 모든 입출력 기능을 만들고 테스트용 웹 애플리케이션을 서버에 배치한 뒤, 웹 화면을 띄워보는 것이다.
- 하지만 DAO만을 테스트하기 위해서 이렇게 테스트 환경을 구성하는 것은 너무나도 공수가 많이든다.
- DAO뿐만 아니라 서비스 클래스, 컨트롤러, JSP 뷰 등 모든 레이어의 기능을 다 만들고 나서야 테스트가 가능하다는 점이 큰 문제다.
<br/>

- 테스트가 실패했다면, 어디에서 문제가 발생했는지를 찾아내야 하는 수고도 필요하다.
- 폼을 띄우고 값을 입력하고 저장 버튼을 눌렀는데 에러가 발생했다고 가정해보자. 에러 메시지와 호출 스택만 보고 간단하게 원인을 찾아낼 수 있는가?
- DB 연결이 원인일 수도 있고, DAO 코드가 잘못되었을 수도 있고, JDBC API를 잘못 호출해서일 수도 있다.
- `정작 테스트할 DAO의 문제가 아니라 서버환경에서 웹 화면을 통해 DAO를 테스트하려고 만든 다른 코드 때문에 에러`가 났을 수 있다.
- 사실 테스트하고 싶었던 것은 UserDao였는데 다른 계층의 코드와 컴포넌트, 심지어 서버의 설정 상태까지 모두 테스트에 영향을 줄 수 있기 때문에 이런 방식으로 테스트하는 것은 번거롭다.
<br/>



#### 작은 단위의 테스트
- 테스트하고자 하는 대상이 명확하다면 그 대상에만 집중해서 테스트하는 것이 바람직하다.
- 너무 많은 것을 한꺼번에 몰아서 테스트하면 테스트 수행 과정도 복잡해지고, 오류가 발생했을 때 정확한 원인을 찾기가 힘들어진다.
- 따라서 `테스트는 가능하면 작은 단위로 쪼개서 집중`할 수 있어야 한다.
<br/>

***테스트의 관심이 다르다면 테스트할 대상을 분리하고 집중해서 접근해야 한다.***
<br/><br/>

- UserDaoTest는 한 가지 관심에 집중할 수 있게 작은 단위로 만들어진 테스트다.
- 테스트를 수행하기 위해 웹 인터페이스나, MVC 클래스, 서비스 오브젝트 등이 필요 없고, 서버에 배포할 필요도 없다.
- 이렇게 작은 단위의 코드에 대해 테스트를 수행한 것을 `단위 테스트(Unit test)`라고 한다.
- **충분히 하나의 관심에 집중**해서 **효율적으로 테스트할 만한 범위**의 단위라고 보면 된다.
<br/>

- 단위를 넘어서는 다른 코드들은 신경 쓰지 않고, 참여하지도 않고 테스트가 동작할 수 있으면 좋다.
- UserDao는 서비스, MVC 계층이 참여하고 웹 화면과 서버까지 동원하지 않고도 테스트가 가능했다.
- DAO라는 기능과 DB까지로 단위를 잡고 집중해서 테스트할 수 있었다. -> 그래서 **UserDaoTest를 단위 테스트라고 부를 수 있다.**
<br/>

- 하지만 어떤 개발자는 테스트 중에 DB가 사용되면 단위 테스트가 아니라고 한다.
- 지금까지 UserDaoTest를 수행할 때 매번 USER 테이블의 내용을 비우고 테스트를 진행했다.
- 이렇게 사용할 DB의 상태를 테스트가 관장하고 있다면 이는 단위 테스트라고 해도 된다.
- 다만, DB의 상태가 매번 달라지고, 테스트를 위해 DB를 특정 상태로 만들어줄 수 없다면 그때는 UserDaoTest가 단위 테스트로서 가치가 없어진다.
- 그런 차원에서 `통제할 수 없는 외부의 리소스에 의존하는 테스트는 단위 테스트가 아니라고 보기도 한다.`
<br/>

- 각 단위 기능은 잘 동작하는데 기능들을 묶어놓으면 안 되는 경우가 종종 발생한다.
- 이를 위해 길고 많은 단위가 참여하는 테스트가 언젠가는 필요하다.
- 단위 테스트를 아예 건너뛰고 이런 긴 테스트를 하는 경우도 있다. 수많은 에러가 발생하거나 에러는 안 나지만 기능이 제대로 동작하지 않는 경험을 하게 된다.
- 이때는 문제의 원인을 찾기가 매우 힘들다. 예외가 발생해도 그 이유를 찾는 데 많은 시간이 걸릴 수 있다.
- 예외가 발생하지 않고 정상적으로 동작했는데 막상 결과가 원하는 대로 나오지 않을 수도 있다.
<br/>

***각 단위별로 테스트를 먼저 진행하고 나서 이런 긴 테스트를 하면 어떨까?***
<br/><br/>

- 각 단위별로 테스트를 선행해도 예외가 발생하거나 실패할 수는 있지만 하지 않는 경우보다는 덜할 것이다.
- 단위 테스트를 하는 이유는 `개발자가 설계하고 만든 코드가 원래 의도한 대로 동작하는지를 개발자 스스로 빨리 확인받기 위해서`다.
- 이때 `확인의 대상과 조건이 간단하고 명확할수록 좋다.`
- 그래서 작은 단위로 제한해서 테스트하는 것이 좋다.
<br/>



#### 자동수행 테스트 코드
- UserDaoTest의 한 가지 특징은 `테스트할 데이터가 코드를 통해 제공`되고, `테스트 작업 역시 코드를 통해 자동으로 실행`된다는 점이다.
- 웹 화면에 폼을 띄우고 매번 User의 등록 값을 개발자 스스로 입력하고 버튼을 누르고, 또 조회를 위한 ID 값을 넣고 버튼을 누르는 등의 작업을 반복한다면...
- 간혹 테스트 값 입력에 실수가 있어서 오류가 나면 다시 테스트를 반복해야 하는 번거로움도 있다.
<br/>

- 하지만 UserDaoTest는 자바 클래스의 main() 메소드를 실행하는 가장 간단한 방법만으로 테스트의 전 과정이 자동으로 진행된다.
    - User 오브젝트를 만들어 적절한 값을 넣고, 이미 DB 연결 준비까지 다 되어 있는 UserDao 오브젝트를 스프링 컨테이너에서 가져와서 add() 메소드를 호출하고, 그 키 값으로 get()을 호출하는 것까지 자동으로 진행된다.
- 번거롭게 매번 입력할 필요도 없고, 테스트를 시작하기 위해 서버를 띄우고, 브라우저를 열어야 하는 불편함도 없다.
- 이렇게 하면 테스트를 자주 수행해도 부담이 없다.
<br/>

***테스트는 자동으로 수행되도록 코드로 만들어지는 것이 중요하다.***
<br/><br/>

- 애플리케이션을 구성하는 클래스 안에 테스트 코드를 포함시키는 것보다는 별도로 테스트용 클래스를 만들어서 테스트 코드를 넣는 편이 낫다.
- 처음에는 UserDao 클래스 하나만 존재하여, 그 안에 main() 메소드를 만들어 사용했지만, `클래스를 분리하고 유연한 설계구조로 발전`시키면서 `테스트 코드를 넣을 위치를 결정하기가 애매`하기 때문에 `UserDao라는 별개의 클래스를 만들고 그 안에 테스트 코드`를 넣도록 했다.
<br/>

- 자동으로 수행되는 테스트의 장점은 자주 반복할 수 있다는 것이다.
- 번거로운 작업이 없고 테스트를 빠르게 실행할 수 있기 때문에 언제든 코드를 수정하고 나서 테스트를 해볼 수 있다.
<br/>

- 때로는 단 한 줄의 코드를 건드렸는데 전체 기능에 영향을 주기도 한다.
- 또는 개발을 일단 완료하고 실전에서 운용 중인 상황에서 코드를 수정하려고 한다면 아무리 간단한 수정이라고 하더라도 전체 애플리케이션에 심각한 문제를 일으키지는 않을까 하는 두려움이 앞선다.
- 그럴 때 만들어둔 기능에 대한 테스트가 있다면 수정 후 빠르게 전체 테스트를 수행해서 수정 때문에 다른 기능에 문제가 발생하지는 않는지 재빨리 확인할 수 있다.
<br/>



#### 지속적인 개선과 점진적인 개발을 위한 테스트
- 테스트가 없었다면, 다양한 방법을 동원해서 코드를 수정하고 설계를 개선해나가는 과정이 그다지 미덥지 않았을 수 있다.
- 테스트 환경을 구성하다가 지쳐서 포기했을 수도 있고, DAO가 아닌 다른 환경에서의 문제 때문에 에러를 해결하다가 시간을 더 소요했을 수도 있다.
- 하지만 DAO 코드를 만들자마자 바로 DAO로서의 기능에 문제가 없는지 검증해주는 테스트 코드를 만들어뒀기 때문에, 그때부터는 조금씩 코드를 개선해나가는 작업을 진행할 수 있었다.
<br/>

- UserDao의 기능을 추가하려고 할 때도 미리 만들어둔 테스트 코드는 유용하게 쓰일 수 있다.
- 조금씩 기능을 더 추가해가면서 그에 대한 테스트도 함께 추가하는 식으로 점진적인 개발이 가능해진다.
- 테스트를 이용하면서 새로운 기능에 대한 동작 확인뿐만 아니라, 기존에 만들어뒀던 기능들이 새로운 기능을 추가하느라 수정한 코드에 영향을 받지 않고 여전히 잘 동작하는지도 확인할 수 있다.
<br/>


### 2.1.3 UserDaoTest의 문제점
#### 수동 확인 작업의 번거로움
- 테스트를 수행하는 과정과 입력 데이터의 준비를 모두 자동으로 진행하도록 만들어졌지만, 여전히 사람의 눈으로 확인하는 과정이 중요하다.
- add()에서 User 정보를 DB에 INSERT하고, 이를 다시 get()을 이용해 SELECT 했을 때 입력한 값과 가져온 값이 일치하는지를 테스트 코드를 확인해주지 않는다.
- 콘솔에 출력된 값을 보고 등록과 조회가 성공적으로 되고 있는지를 확인하는 건 사람의 책임이다.
- 몇 가지 필드의 값에 대해 등록한 것과 조회한 것이 일치하는지를 확인하는 일은 사람이 해도 수고가 적지만, 검증해야 하는 양이 많고 복잡해지면 부담감이 클 것이다.
- 또한 작은 차이는 발견하지 못하고 넘어가는 실수를 범할 가능성도 있다.
<br/>

#### 실행 작업의 번거로움
- 간단히 실행이 가능한 main() 메소드라고 하더라도 매번 그것을 실행하는 것은 번거롭다.
- 만약 DAO가 수백 개가 되고 그에 대한 main() 메소드도 그만큼 만들어진다면...?
- 전체 기능을 테스트해보기 위해 main() 메소드를 수백 번 실행해야 하는 수고가 필요하다.
- 결과를 눈으로 확인해서 기록하고, 이를 종합해서 테스트 결과를 정리하려면 상당히 큰 작업이 될 것이다.
- 따라서 좀 더 편리하고 체계적으로 테스트를 실행하고 그 결과를 확인하는 방법이 필요하다.
<br/>



## 2.2 UserDaoTest 개선
### 2.2.1 테스트 검증의 자동화
- UserDaoTest의 첫번째 문제점인 테스트 결과의 검증 부분을 코드로 만들어보자.
- 이 테스트를 통해 확인하고 싶은 것은 add() 메소드를 통해 전달한 User 오브젝트에 담긴 사용자 정보와 get() 메소드를 통해 다시 DB에서 가져온 User 오브젝트의 정보가 일치하는가에 대한 것이다.
- 모든 테스트는 성공과 실패의 두 가지 결과를 알 수 있다.
    - 테스트 실패는 테스트가 진행되는 동안에 에러가 발생해서 실패하는 경우(`테스트 에러`)와, 테스트 작업 중에 에러가 발생하진 않았지만 그 결과가 기대한 것과 다르게 나오는 경우(`테스트 실패`)로 구분해볼 수 있다.
<br/>

```
System.out.println(user2.getName());
System.out.println(user2.getPassword());
System.out.println(user2.getId() + " 조회 성공");
```
**수정 전 테스트 코드**

```
if(!user.getName().equals(user2.getName())) {
    System.out.println("테스트 실패 (name)");
} else if(!user.getPassword().equals(user2.getPassword())) {
    System.out.println("테스트 실패 (password)");
} else {
    System.out.println("조회 테스트 성공");
}
```
**수정 후 테스트 코드**
<br/><br/>

- 위의 수정 후 테스크 코드는 처음 add()에 전달한 User 오브젝트와 get()을 통해 가져오는 User 오브젝트의 값을 비교해서 일치하는지 확인하는 것이다.
- 만약 다른 값이 있다면 그때는 테스트가 실패했다고 출력하고 테스트를 종료한다.
- name과 password 둘 중 어떤 값 때문에 실패했는지 알 수 있도록 출력 메시지에 필드 이름을 추가해서 표시해주었다.
- 모든 비교가 성공적으로 끝난다면 그때는 테스트가 성공했다고 메시지가 출력될 것이다.
<br/>

```
user123 등록 성공
user123 조회 성공
```
**add() 메소드 성공 여부 메시지**
<br/><br/>

- add() 메소드를 통한 등록 행위 자체는 별다르게 검증할 것이 없다.
- add()를 호출하고 나서 에러가 발생하지 않으면 일단 성공으로 간주한다.
- 그리고 get()을 통한 검증 과정에서 사실 add()의 작업에 함께 확인하는 것이다.
- 만약 add() 메소드의 잘못으로 등록 행위가 제대로 이루어지지 않은 것이 있다면 검증 코드를 통과하지 못할 것이다.
<br/>

- 이 테스트는 UserDao의 두 가지 기능이 정상적으로 동작하는지를 언제든지 손쉽게 확인할 수 있게 해준다.
- 스프링 프레임워크 대신 다른 프레임워크나 기술로 전환하는 큰 변화가 일어나더라도 UserDao가 전과 같이 정상적으로 동작하는지 확인하는 것은 이 테스트 한 번이면 충분하다.
- 자동화된 테스트를 위한 xUnit 프레임워크를 만든 켄트 벡은 `"테스트란 개발자가 마음 편하게 잠자리에 들 수 있게 해주는 것"`이라고 했다.
- 만들어진 코드의 기능을 모두 점검할 수 있는 포괄적인 테스트(comprehensive test)를 만들면서부터는, 개발한 애플리케이션은 이후에 어떤 과감한 수정을 하고 나서도 테스트를 모두 돌려보고 나면 안심이 된다.
<br/>



### 2.2.2 테스트의 효율적인 수행과 결과 관리
- main() 메소드로 만든 테스트는 테스트로서 필요한 기능은 모두 갖춘 셈이다.
- 하지만 좀 더 편리하게 테스를 수행하고 편리하게 결과를 확인하려면 main() 메소드로는 한계가 있다.
- 일정한 패턴을 가진 테스트를 만들 수 있고, 많은 테스트를 간단히 실행시킬 수 있으며, 테스트 결과를 종합해서 볼 수 있고, 테스트가 실패한 곳은 빠르게 찾을 수 있는 기능을 갖춘 테스트 지원 도구와 그에 맞는 테스트 작성 방법이 필요하다.
- 자바는 이를 위한 테스트 도구가 여러 가지 존재하는데, 이 책에서는 `자바 테스팅 프레임워크`라고 불리는 `JUnit`을 소개하고 있다.

#### JUnit 테스트로 전환
#### 테스트 메소드 전환
#### 검증 코드 전환
#### Junit 테스트 실행
<br/>



## 2.3 개발자를 위한 테스팅 프레임워크 JUnit
### 2.3.1 Junit 테스트 실행 방법
### 2.3.2 테스트 결과의 일관성
- 지금까지 테스트를 실행하면서 가장 불편했던 점은, 매번 UserDaoTest 테스트를 실행하기 전에 DB의 USER 테이블 데이터를 모두 삭제해줘야 할 때였다.
- 만약 삭제 행위를 하지 않고 테스트를 다시 수행한다면 기존에 들어있던 데이터와 새로 넣으려는 데이터의 PK가 중복되어 에러가 발생한다.
- 여기서 생각해볼 문제는 테스트가 외부 상태에 따라 성공하기도 하고 실패하기도 한다는 점이다.
- 반복적으로 테스트를 수행했을 때 `코드에 변경사항이 없다면 테스트는 항상 동일한 결과`를 내야한다.
- 가장 좋은 해결책은 addAndGet() 테스트를 마치고 나면 테스트가 등록한 사용자 정보를 삭제해서, 테스트를 수행하기 이전 상태로 만들어주는 것이다.
<br/>

### deleteAll()의 getCount() 추가
#### deleteAll
- 먼저 추가할 것은 deleteAll() 메소드로, USER 테이블의 모든 레코드를 삭제해주는 간단한 기능을 가지고 있다.

```
public void deleteAll() throws SQLException {
    Connection c = dataSource.getConnection();
    PreparedStatement ps = c.prepareStatement("DELETE FROM USERS");
    ps.executeUpdate();

    ps.close();
    c.close();
}
```
<br/>

#### getCount()
- 두번째 추가할 것은 getCount() 메소드로, USER 테이블의 레코드 개수를 돌려준다.

```
public int getCount() throws SQLException {
    Connection c = dataSource.getConnection();
    PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM USERS");
    ResultSet rs = ps.executeQuery();
    rs.next();
    int count = rs.getInt(1);

    rs.close();
    ps.close();
    c.close();

    return count;
}
```
<br/>

### deleteAll()과 getCount()의 테스트
- deleteAll()과 getCount() 메소드의 기능은 add()와 get()처럼 독립적으로 자동 실행되는 테스트를 만들기가 애매하다.
- 굳이 테스트를 하자면 가능은 하다. USER 테이블에 수동으로 데이터를 넣고 deleteAll()을 실행한 뒤에 테이블에 남은 게 있는지 확인해야 한다.
- 기존 addAndGet() 테스트에서 deleteAll()을 주요 테스트 기능이 실행된 후에 수행되게끔 하고, 정상적으로 수행되었다면 getCount()로 DB에서 가져온 레코드의 개수가 0이어야 한다.
- deleteAll() 수행 후 getCount() 값 확인을 통해 deleteAll()을 테스트할 수 있지만 getCount()는 어떻게 수행할까? -> `add() 수행 직후 getCount()를 수행하여 deleteAll() 수행 직후 getCount() 값과 비교`한다.

```
@Test
public void addAndGet() throws SQLException {
    ...
    dao.deleteAll();
    assertThat(dao.getCount(), is(0));

    User user = new User();
    user.setId("taechacode");
    user.setName("강태찬");
    user.setPassword("springno1");

    dao.add(user);
    asssertThat(dao.getCount(), is(1));

    User user2 = dao.get(user.getId());

    assertThat(user2.getName(), is(user.getName()));
    assertThat(user2.getPassword(), is(user.getPassword()));
}
```
**deleteAll()과 getCount()가 추가된 addAndGet() 테스트**
<br/><br/>

### 동일한 결과를 보장하는 테스트
- 위 테스트는 이제 반복해서 여러 번 실행해도 계속 성공할 것이다.
- 하지만 사실 모든 상황에서 위 테스트 코드가 동일한 결과를 보장하는 테스트 코드라고 불릴 수는 없다.
- 왜냐하면 addAndGet() 테스트 실행 이전에 다른 이유로 USER 테이블에 데이터가 들어가있다면 deleteAll()이 실행되기 전에 add() 단계에서 테스트가 실패할 것이다.
- 또 addAndGet() 테스트만 DB를 사용할 것이 아니라면 이전에 어떤 작업을 하다가 DB가 어떤 상태에서 addAndGet() 테스트에 임하게 될 지 알 수 없다.
- 테스트 후에 USER 테이블을 지워주는 것도 좋지만, 그보다는 테스트하기 전에 테스트 실행에 문제가 되지 않는 상태를 만들어주는 편이 더 나을 것이다.
<br/>

### 2.3.3 포괄적인 테스트
- 앞에서 getCount() 메소드를 테스트에 적용하긴 했지만 기존의 테스트에서 확인할 수 있었던 것은 deleteAll()을 실행했을 때 테이블이 비어 있는 경우(0)와 add()를 한 번 호출한 뒤의 결과(1)뿐이다.
- 두 개 이상의 레코드를 add() 했을 때는 getCount()의 실행 결과가 어떻게 될까?
<br/>

### getCount() 테스트
- 이번에는 여러 개의 User를 등록해가면서 getCount()의 결과를 매번 확인해보겠다.
- JUnit은 하나의 클래스 안에 여러 개의 테스트 메소드가 들어가는 것을 허용한다. @Test가 붙어 있고 public 접근자가 있으며 리턴 값이 void형이고 파라미터가 없다는 조건을 지키기만 하면 된다.
- 테스트 시나리오는 이렇다.
    - 먼저 USER 테이블의 데이터를 모두 지우고 getCount()로 레코드 개수가 0임을 확인한다.
    - 그리고 3개의 사용자 정보를 하나씩 추가하면서 매번 getCount()의 결과가 하나씩 증가하는지 확인하는 것이다.
<br/>

```
public User(String id, String name, String password) {
    this.id = id;
    this.name = name;
    this.password = password;
}

public User() {
}
```
**파라미터가 있는 User 클래스 생성자**
<br/>

```
UserDao dao = context.getBean("userDao", UserDao.class);
User user = new User("taechacode", "강태찬", "springno1");
```
**간편해진 UserDaoTest의 User 인스턴스 생성**
<br/>

```
@Test
public void count() throws SQLException {
    ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

    UserDao dao = context.getBean("userDao", UserDao.class");
    User user1 = new User("taechacode", "강태찬", "springno1");
    User user2 = new User("tlswltjq", "신지섭", "springno2");
    User user3 = new User("albireo3754", "윤상진", "springno3");

    dao.deleteAll();
    assertThat(dao.getCount(), is(0));

    dao.add(user1);
    assertThat(dao.getCount(), is(1));

    dao.add(user2);
    assertThat(dao.getCount(), is(2));

    dao.add(user3);
    assertThat(dao.getCount(), is(3));
}
```
**getCount() 테스트**
<br/>

- 여기서 주의해야 할 점은 addAndGet() 테스트와 count() 테스트가 어떤 순서로 실행될지는 알 수 없다는 것이다.
- JUnit은 특정한 테스트 메소드의 실행 순서를 보장해주지 않는다.
- `테스트의 결과가 테스트 실행 순서에 영향을 받는다면 그 테스트는 잘못 만든 것이다.`
<br/>



### addAndGet() 테스트 보완
- add() 후에 레코드 개수를 확인하고, get()으로 읽어와서 값도 비교해보아서 add()의 기능은 충분히 검증하였다.
- 하지만 id를 조건으로 해서 사용자를 검색하는 기능을 가진 get()에 대한 테스트는 검증이 부족하다.
- 2개의 User를 add()하고, 각 User의 id를 파라미터로 전달해서 get() 실행한다. 이렇게 해서 가져온 User 객체와 기존에 add() 했었던 객체를 비교하면 주어진 id에 해당하는 정확한 User 정보를 가져오는지 확인할 수 있다.
<br/>

```
@Test
public void addAndGet() throws SQLException {

    ....

    UserDao dao = context.getBean("userDao", UserDao.class");
    User user1 = new User("taechacode", "강태찬", "springno1");
    User user2 = new User("tlswltjq", "신지섭", "springno2");

    dao.deleteAll();
    assertThat(dao.getCount(), is(0));

    dao.add(user1);
    dao.add(user2);
    assertThat(dao.getCount(), is(2));

    User userget1 = dao.get(user1.getId());
    assertThat(userget1.getName(), is(user1.getName());
    assertThat(userget1.getPassword(), is(user1.getPassword());

    User userget2 = dao.get(user2.getId());
    assertThat(userget2.getName(), is(user2.getName());
    assertThat(userget2.getPassword(), is(user2.getPassword());
}
```
**get() 테스트 기능을 보완한 addAndGet() 테스트**
<br/><br/>


### get() 예외조건에 대한 테스트
- get() 메소드에 전달된 id 값에 해당하는 사용자 정보가 없다면 어떻게 될까?
- 보통 2가지 방법이 있다.
    - 하나는 null과 같은 특별한 값을 return하는 것이다.
    - 다른 하나는 id에 해당하는 정보를 찾을 수 없다고 예외를 던지는 것이다.
- 아래의 테스트 코드는 id에 해당하는 정보를 찾을 수 없을 때 예외가 발생하는 경우 성공하는 테스트 코드이다.
- get() 메소드에서 쿼리 결과의 첫 번째 row를 가져오게 하는 rs.next()를 실행할 때 가져올 row가 없다는 SQLException이 발생할 것이다.
<br/>

```
@Test(expected=EmptyResultDataAccessException.class)
public void getUserFailure() throws SQLException {
    Application context = new GenericXmlApplicationContext("applicationContext.xml");

    UserDao dao = context.getBean("userDao", UserDao.class);
    dao.deleteAll();
    assertThat(dao.getCount(), is(0));

    dao.get("unknown_id");
}
```
**get() 메소드의 예외상황에 대한 테스트**
<br/><br/>


### 테스트를 성공시키기 위한 코드의 수정
- 위의 예외처리 테스트뿐만 아니라 다른 테스트들도 성공하도록 get() 메소드를 수정해보자.
- 아래의 get() 메소드를 수정하다가 기존 코드를 잘못 건드렸을 경우 정상적인 조건에서 get()을 실행시켰을 때 문제가 발생할 수 있다.
- 이때는 addAndGet() 테스트가 실패할 것이므로, 이를 확인하고 다시 get() 메소드의 오류를 잡아주면 된다.
- 최종적으로 모든 테스트가 성공하면, 새로 추가한 기능도 정상적으로 동작하고 기존의 기능에도 영향을 주지 않았다면 확신을 얻을 수 있다.
<br/>

```
public void get() throws SQLException {
    ...

    ResultSet rs = ps.executeQuery();

    User user = null;
    if(rs.next()) {
        user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
    }

    rs.close();
    ps.close();
    c.close();

    if(user == null) throw new EmptyResultDataAccessException(1);

    return user;
}
```
**데이터를 찾지 못하면 예외를 발생시키도록 수정한 get() 메소드**
<br/><br/>


### 포괄적인 테스트
- 개발자가 테스트를 직접 만들 때 자주 하는 실수는 성공하는 테스트만 골라서 만드는 것이다.
- 개발자도 조금만 신경을 쓰면 자신이 만든 코드에서 발생할 수 있는 다양한 상황과 입력 값을 고려하는 포괄적인 테스트를 만들 수 있다.
- 스프링의 창시자인 로드 존슨은 `"항상 네거티브 테스트를 먼저 만들라"`는 조언을 했다.
- 개발자는 빨리 테스트를 만들어 성공하고 다음 단계로 넘어가고 싶어하기 때문에, 성공할만한 테스트를 먼저 작성하게 되기 쉽다. 그래서 `부정적인 케이스를 먼저 만드는 습관을 들이는 게 좋다.`
<br/>

### 2.3.4 테스트가 이끄는 개발
- get() 메소드의 예외 테스트를 만드는 과정을 다시 돌이켜보면, 새로운 기능을 넣기 위해 UserDao 코드를 수정하고 그런 다음 수정한 코드를 검증하기 위해 테스트를 만드는 순서로 진행한 것이 아니다.
- 반대로 테스트를 먼저 만들어 실패하는 것을 보고 나서 UserDao의 코드에 손을 대기 시작했다. -> `이런 순서를 따라서 개발을 진행하는 구체적인 개발 전략(TDD)이 실제로 존재한다!`
<br/>

### 기능설계를 위한 테스트
- 먼저 존재하지 않는 id로 get() 메소드를 실행하면 특정한 예외가 던져져야 한다는 기능을 먼저 결정했지만, UserDao 코드를 바로 수정하는 것이 아니라 getUserFailure() 테스트를 먼저 만들었다.
- 그것은 만들어진 코드를 보고 어떻게 테스트할지에 대한 것을 생각한 것이 아니라, 추가하고 싶은 기능을 코드로 표현하려고 했기 때문이다.
<br/>

![getUserFailure() 테스트 코드에 나타난 기능](https://github.com/Tobystudy/toby-spring-study/assets/63395751/58a7dcec-ae72-4ef5-842e-31568ec368a1)
**getUserFailure() 테스트 코드에 나타난 기능**
<br/>

- 이 테스트 코드는 마치 잘 작성된 하나의 기능정의서처럼 보인다.
- 그래서 기능설계, 구현 테스트라는 일반적인 개발 흐름의 기능설계에 해당하는 부분을 이 테스트 코드가 일부분 담당하고 있다고 볼 수도 있다.
- 추가하고 싶은 기능을 일반 언어가 아니라 테스트 코드로 표현해서, 마치 `코드로 된 설계문서`처럼 만들어놓은 것이다.
- 그러고 나서 실제 기능을 가진 애플리케이션 코드를 만들고 나면, 바로 이 테스트를 실행해서 설계한 대로 코드가 동작하는지를 빠르게 검증할 수 있다.
- 만약 테스트가 실패하면 이때는 설계한 대로 코드가 만들어지지 않았음을 바로 알 수 있다.
- 결국 `테스트가 성공한다면, 그 순간 코드 구현과 테스트라는 두 가지 작업이 동시에 끝나는 것이다.`
<br/>

### 테스트 주도 개발
<br/>

## 2.4 스프링 테스트 적용

## 2.5 학습 테스트로 배우는 스프링

## 부록. 차세대 통합 테스트를 진행하면서
- 금융권 차세대는 단위 서버라 하더라도 수많은 거래 기능이 들어가고, 각 거래가 선후행 작업이 있거나 관계가 얽혀있는 경우가 많기 때문에 테스트 하기가 까다롭다.
- 그나마 화면 조작 테스트는 그저 CRUD를 가시성이 좀 더 좋게 표현한 CRUD에 불과하기 때문에 비 IT 담당자여도 테스트가 가능하다. (금융 창구 프로그램을 super excel 프로그램이라고 불리우는 이유..)
<br/>

- 하지만 Batch 프로그램은 작업(Job)을 수행시키는 주체도 IT 담당자이고, Batch로 인해 영향을 받은 데이터에 접근이 가능한 담당자도 주로 IT 담당자이다.
- 사용자가 직접 INSERT할 데이터를 수기로 넣고, 혹은 그저 넣어진 데이터를 SELECT하는 화면과는 달리, Batch 프로그램은 parameter의 상태가 복잡하고 경우의 수가 매우 많다.
- 또한 주로 단건 처리가 이루어지는 화면과는 달리 대량 데이터 처리가 주목적이기 때문에 별도의 스트레스 테스트가 필요하다.
    - 정석적인 방법은 아니지만, 너무 많은 데이터의 I/O가 이루어진다고 판단될 경우 화면에서는 조회범위나 INSERT 데이터량을 강제시킬 수 있다.
    - 하지만 Batch로 작업하는 것은 거래 중요도가 높고 데이터의 양과 무관하게 반드시 처리해야 할 데이터인 경우가 많기 때문에 어떻게든 소화해야만 한다.
<br/>

- [Spring Batch Test Reference](https://docs.spring.io/spring-batch/reference/#endToEndTesting)
- [Batch Test Code 작성 및 이슈 해결](https://clack2933.tistory.com/55)
- [Spring Batch 테스트하기](https://multifrontgarden.tistory.com/291)
