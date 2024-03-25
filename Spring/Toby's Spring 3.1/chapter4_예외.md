# 4장 예외
## 4.1 사라진 SQLException
```
public void deleteAll() throws SQLException {
    this.jdbcContext.executeSql("delete from users");
}
```
**JdbcTemplate 적용 전**
<br/><br/>

```
public void deleteAll() {
    this.jdbcTemplate.update("delete from users");
}
```
**JdbcTemplate 적용 후**
<br/><br/>

- JdbcTemplate 적용 전에는 있었던 throws SQLException이 선언이 적용 후에는 사라졌다.
- SQLException은 JDBC API의 메소드들이 던지는 것이므로 당연히 있어야 한다.
- 비록 로그를 남기기 위해 catch를 했다고 해도 다시 JDBC 템플릿 메소드 밖으로 던져서 예외상황이 발생했다는 사실을 알려야 한다.
- 이 SQLException은 어디로 사라진 것일까?
<br/>

### 4.1.1 초난감 예외처리

### 예외 블랙홀
<br/>

```
try {
    ...
} catch(SQLException e) {
}
```
**초난감 예외처리 코드 1**
<br/><br/>

- JDBC API를 썼더니 IDE가 친절하게도 빨간 줄을 그어주며 '처리되지 않은 예외가 있다'라고 에러 표시를 해준다. 이를 보고 자바 언어 기초 시간에 배운 대로 try/catch 블록을 둘러싸주는 것으로 해결한다. 컴파일러 에러 메시지도 없어지고 간단한 예제에서라면 별문제 없이 잘 동작한다.
- 위의 예시 코드 또한 예외를 잡고는 아무것도 하지 않는다. 예외 발생을 무시해버리고 정상적인 상황인 것처럼 다음 라인으로 넘어가겠다는 분명한 의도가 있는 게 아니라면 `연습 중에도 절대 만들어서는 안되는 코드다.`
<br/>

- 예외가 발생하면 그것을 catch 블록을 써서 잡아내는 것까지는 좋은데 아무것도 하지 않고 별문제 없는 것처럼 넘어가 버리는 건 원치 않는 예외가 발생하는 것보다도 훨씬 더 나쁜 일이다.
- 왜냐하면 프로그램 실행 중에 `어디선가 오류가 있어서 예외가 발생했는데 그것을 무시하고 계속 진행해버리기 때문`이다.
- 결국 발생한 예외로 인해 어떤 기능이 비정상적으로 동작하거나, 메모리가 리소스가 소진되거나, 예상치 못한 다른 문제를 일으킬 것이다.
- 최종적으로 오작동을 하거나 시스템 오류가 나서 운영자가 알아차렸을 때는 이미 조치를 취하기엔 너무 늦었다.
- 더 큰 문제는 그 시스템 오류나 이상한 결과의 원인이 무엇인지 찾아내기가 매우 힘들다는 것이다.
<br/>

```
} catch(SQLException e) {
    System.out.println(e);
}
```
**초난감 예외처리 코드 2**
<br/><br/>

```
} catch(SQLException e) {
    e.printStackTrace();
}
```
**초난감 예외처리 코드 3**
