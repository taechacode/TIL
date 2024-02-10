package section_01_02.message_sender_with_interface;

public class RealMessageSender implements MessageSender{
    // 실제로 메시지를 발송
    @Override
    public void send() {
        System.out.println("RealMessageSender, 실제로 메시지 발송");
    }
}
