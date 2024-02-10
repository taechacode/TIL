package section_01_04.interface_example;

public interface SomeInterface {

    void someMethod();

    default void defaultMethod() {
        System.out.print("defaultMethod : ");
        this.someMethod();
    }

}
