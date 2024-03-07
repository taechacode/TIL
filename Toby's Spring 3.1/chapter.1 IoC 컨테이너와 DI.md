챕터1에서는 스프링이 어떤 것이고, 무엇을 제공하는지보다는 스프링이 관심을 갖는 오브젝트의 설계와 구현, 동작원리에 집중.

1.1 초난감 DAO

1.1.1 User
@Getter
@Setter
public class User {
String id;
String name;
String password;

~

USERS TABLE
Id VARCHAR(10) PK
Name VARCHAR(20) Not Null
Password VARCHAR(20) Not Null

1.1.2 UserDao
UserDAO
1. DB연결을 위한 Connection 획득
2. SQL을 담은 Statement(or PreparedStatement) 만든다.
3. 만들어진 Statement를 실행.
4. add 메소드 -> INSERT, get 메소드 -> SELECT
5. get 메소드 실행 결과는 ResultSet으로 받아서 User 오브젝트에 옮기기
6. 작업 중에 생성된 Connection, Statement, ResultSet 같은 리소스는 작업을 마친 후 닫아주기
7. JDBC API가 만들어내는 예외(Exception)을 잡아서 직접 처리하거나, 메소드에 throws를 선언

1.1.3 main()을 이용한 DAO 테스트 코드
id : whiteship
name : 백기선
password : married
위와 같은 User를 만들어서 getter를 통해 Id를 기준으로 User를 조회해서 새로운 인스턴스에 저장.
기존에 만든 인스턴스(user)와 조회를 통해 만든 인스턴스(user2)가 동일한 프로퍼티 값을 가지고 있는지 확인.

이렇게 만들었다간... 짤린다!
어떤 점이 문제일까 궁금하다. 은행을 다니면서 보았던 코드들은 사실 여기서 일일히 DB Connection 정보를 하드코딩하고 close를 코드의 말미에 적는다는 점을 빼고는 사실상 거의 똑같았다.

1.2 DAO의 분리
1.2.1 관심사의 분리
변화는 대체로 집중된 한 가지 관심에 대해 일어나지만 그에 따른 작업은 한 곳에 집중되지 않는 경우가 많다.
-> DB 접속용 암호를 변경하려고 Dao 클래스 수백 개를 모두 수정해야한다면? 위의 예시 코드 같은 경우 그런 일이 발생할 수 있다.

변화가 한 번에 한 가지 관심에 집중돼서 일어난다면, 우리가 준비해야 할 일은 한 가지 관심이 한 군데에 집중되게 하는 것이다.
관심이 같은 것끼리는 모으고, 관심이 다른 것은 떨어져 있게 해야 한다.

1.2.2 커넥션 만들기의 추출
UserDao의 관심사항
1. DB 연결 커넥션
2. DB SQL Statement
3. 사용 리소스 닫아주기
-> 앞서 생각했던 비효율적인 부분들과 동일하다. 회사에서 사용하는 현행 시스템에서는 해결된 문제.

중복 코드의 메소드 추출
중복된 DB 연결 코드를 getConnection()이라는 이름의 독립적인 메소드로 제작.
 
변경사항에 대한 검증 : 리팩토링과 테스트
예제 main() 테스트의 문제점은 다시 구동하였을 경우 DB USERS 테이블에 이전에 테스트했던 내용이 찌꺼기처럼 들어있어 예외가 발생한다. id가 PK이기 때문에 whiteship id를 가진 user가 2개 이상 들어갈 수 없다.
DB Connection, Close와 관련된 동작을 분리해서 메소드로 만든 것. (메소드 추출 기법)
이렇게 미래의 변화에 좀 더 손쉽게 대응할 수 있는 코드로 개선하는 것을 리팩토링이라고 한다.

1.2.3 DB 커넥션 만들기의 독립
UserDao가 발전을 거듭해서 세계적으로 유명한 사용자 관리 DAO가 되었다.
그런데 이걸 팔려고 보니 다른 회사들이 기존 UserDao와 다른 종류의 DB를 사용 중이다.
이걸 커스텀하게끔 하면 내부 코드를 다른 회사들이 Copy 할 수 있기 때문에 위험하다.

상속을 통한 확장
UserDao에서 getConnection() 메소드를 추상 메소드로 만든다. 안의 내용은 각자 구현하도록.
이렇게 슈퍼 클래스의 기본적인 로직의 흐름을 만들고, 기능의 일부를 필요에 맞게 구현해서 사용하게 만드는 패턴 : 템플릿 메소드 패턴(template method pattern)

"UserDao에 팩토리 메소드 패턴을 적용해서 getConnection()을 분리합시다"
-> 이번에 읽으면서 감명깊었던 경제적이고 개발자적인 말.. 위의 내용들이 이 한 문장에 담겨 있다.

그런데 여기에도 문제점이 있다. -> 상속
UserDao가 다른 목적으로 상속을 사용한다면?
슈퍼 클래스 내부의 변경이 생긴다면?
상속을 통한 상하위 클래스 관계는 밀접하다. 따라서 슈퍼 클래스의 변경은 모든 서브클래스의 수정을 야기할 수 있다.

