package section_01_04.interface_example;

public class ImplementsClass implements SomeInterface, AnotherInterface{

    @Override
    public void someMethod() {
        System.out.println("ImplementsClass someMethod");
    }

    @Override
    public void anotherMethod() {
        System.out.println("ImplementsClass anotherMethod");
    }
}
