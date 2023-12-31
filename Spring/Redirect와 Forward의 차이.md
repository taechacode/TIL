# Redirect와 Forward의 차이

- JSP 환경에서 현재 작업 중인 페이지에서 다른 페이지로 이동하는 2가지 방식의 페이지 전환 기능
- 다음 사례에서 고객은 클라이언트이고, 123번과 124번은 URL이며, 상담원은 서버가 된다.

### 첫번째 사례 : Redriect
1) 고객이 고객센터로 상담원에게 123번으로 전화를 건다.
2) 상담원은 고객에게 다음과 같이 이야기한다. "고객님, 해당 문의사항은 124번으로 문의 부탁드립니다."
3) 고객은 다시 124번으로 문의해서 일을 처리한다.

### 두번째 사례 : Forward
1) 고객이 고객센터로 상담원에게 123번으로 전화를 건다.
2) 상담원은 해당 문의사항에 대해 잘 알지 못해서 옆의 다른 상담원에게 해당 문의사항에 대한 답을 얻는다.
3) 상담원은 고객에게 문의사항을 처리해준다.

<br/>

## Redirect

![리다이렉트](https://github.com/taechacode/TIL/assets/63395751/6b341c68-1740-4caf-af65-89bce51306ff)

- 웹 컨테이너(Web Container)은 `리다이렉트(Redirect)` 명령이 들어오면 웹 브라우저에게 다른 페이지로 이동하라는 명령을 내린다.
- 위의 첫번째 사례에서 고객은 전화를 끊고 124번으로 다시 전화를 거는 것과 동일하다.

<br/>

- 웹 브라우저는 URL을 지시된 주소로 바꾸고 그 주소로 이동한다.
- **다른 웹 컨테이너에 있는 주소로 이동이 가능**하다. (예 : 123 -> 124)

<br/>

- **새로운 페이지에서는 Request, Response 객체가 새롭게 생성**된다.
- 위의 첫번째 사례에서 123번에서 고객이 요청했던 문의사항은 사라지고 124번으로 다시 걸어서 123번에서 요청했었던 문의사항을 다시 말해야한다.
- 리다이렉트의 경우 최초 요청을 받은 URL1에서 클라이언트에 리다이렉트할 URL2를 반환(Return)하고, 클라이언트는 이전과 다른 **새로운 요청을 생성하여 URL2에 다시 요청**을 보낸다. 따라서 처음 보냈던 최초 요청정보는 더이상 유효하지 않게 된다.

<br/>

## Forward

![포워드](https://github.com/taechacode/TIL/assets/63395751/9c2ffbfc-8a18-4bf1-b639-7307a2ab451e)

- 웹 컨테이너(Web Container) 차원에서의 페이지 이동으로, 실제로 웹 브라우저는 다른 페이지로 이동했는지 알 수 없다.
- 위 두번째 사례에서 고객은 상담원이 누구에게 물어봤는지 알 수 없다.

<br/>

- 웹 브라우저에는 최초 호출한 URL만 표시되고, 이동한 페이지의 URL 정보를 볼 수 없다.
- 위 두번째 사례에서 고객은 123번으로만 전화했기 때문에 알 수 없다.

<br/>

- **동일한 웹 컨테이너에 있는 페이지로만 이동이 가능**하다.
- **현재 실행중인 페이지와 포워드(Forward)에 의해 호출될 페이지는 Request, Response 객체를 공유**한다.
- 위 두번째 사례에서 고객이 요청한 문의사항은 고객이 전화를 끊을 때까지 유효한 것과 동일하다.

<br/>

- 포워드 방식은 다음 이동한 URL로 요청정보를 그대로 전달한다. 그렇기 때문에 사용자가 최초로 요청한 요청정보는 다음 URL에서도 유효하다.

<br/>

## Redirect와 Forward의 차이점 정리

- 다시 정리해보자면 리다이렉트와 포워드의 차이점은 크게 2가지로 나눌 수 있다.
  - 첫번째, URL의 변화여부 (리다이렉트는 변하지만, 포워드는 변하지 않는다.)
  - 두번째 객체의 재사용여부 (리다이렉트는 재사용하지 않지만, 포워드는 재사용한다.)

<br/>

- 위와 같은 차이점 때문에 웹 애플리케이션을 작성할 때 리다이렉트와 포워드 2가지 방식 중 하나를 적절히 선택하여 사용해야 한다.
- 예를 들어 게시판 애플리케이션을 작성한다고 하자. 사용자가 보낸 요청정보를 이용하여 글쓰기 기능을 수행하는 CGI(Common Gateway Interface)가 있다면, 이 CGI의 응답 페이지는 리다이렉트가 옳다.
- 사용자가 실수 혹은 고의로 글쓰기 CGI 응답 페이지에서 새로고침을 누르면 어떻게 될까?
  - 포워드의 경우 요청정보가 그대로 살아있기 때문에 똑같은 글이 여러번 등록될 수 있다.
  - 리다이렉트의 경우 처음 글을 작성할 때 보냈던 요청정보는 존재하지 않는다. 또한 글쓰기 기능을 하는 URL1이 아닌 URL2로 요청을 보내기 때문에 글쓰기가 여러번 수행되지 않는다.

<br/>

- **시스템(Seesion, DB)에 변화가 생기는 요청(로그인, 회원가입, 글쓰기)** 의 경우 `리다이렉트(Redirect)` 방식으로 응답하는 것이 바람직하다.
- **시스템에 변화가 생기지 않는 단순조회(리스트보기, 검색)** 의 경우 `포워드(Forward)` 방식으로 응답하는 것이 바람직하다.

<br/>

***출처*** <br/>
***https://doublesprogramming.tistory.com/63*** <br/>
