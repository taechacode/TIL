package section_01_02.message_sender_with_interface;

public class FakeMessageSender implements MessageSender{
    // 테스트용 메시지 발송
    @Override
    public void send() {
        System.out.println("FakeMessageSender, 실제로 메시지 발송되지 않음.");
    }
}
