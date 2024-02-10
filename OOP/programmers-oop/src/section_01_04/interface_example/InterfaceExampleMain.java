package section_01_04.interface_example;

public class InterfaceExampleMain {

    public static void main(String[] args) {

        ImplementsClass implementsClass = new ImplementsClass();
        SomeInterface someInterface = new ImplementsClass();
        AnotherInterface anotherInterface = new ImplementsClass();

        implementsClass.someMethod();
        implementsClass.anotherMethod();
        implementsClass.defaultMethod(); // SomeInterface에 default 메소드만 있어도 default 메소드 호출 가능.

        someInterface.someMethod();
        someInterface.defaultMethod();

        anotherInterface.anotherMethod();

        // 아래는 실행불가
        // someInterface.anotherMethod();
        // anotherInterface.someMethod();
        // anotherInterface.defaultMethod();
    }

}
