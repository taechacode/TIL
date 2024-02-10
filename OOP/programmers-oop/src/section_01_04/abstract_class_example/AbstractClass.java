package section_01_04.abstract_class_example;

public abstract class AbstractClass {

    public void implementedMethod() {
        System.out.println("AbstractClass implementedMethod");
        this.abstractMethod();
    }

    public abstract void abstractMethod();

}
