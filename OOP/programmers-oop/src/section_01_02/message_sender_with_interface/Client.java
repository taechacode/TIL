package section_01_02.message_sender_with_interface;

/*
Client 클래스는 외부에서 MessageSender를 주입받기 때문에
Client 소스코드 자체는 MessageSender의 종류가 변하더라도 변경이 일어나지 않는다.
 */

public class Client {

    private MessageSender messageSender;

    Client(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void someMethod() {
        // 메시지 보내기 전 실행되는 어떤 작업
        messageSender.send();
    }

}
