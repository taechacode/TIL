package section_01_02.message_sender_with_interface;

public class WithInterfaceExampleMain {
    public static void main(String[] args) {
        MessageSender messageSender = new FakeMessageSender();
        //MessageSender messageSender = new RealMessageSender();
        Client client = new Client(messageSender);
        client.someMethod();
    }
}