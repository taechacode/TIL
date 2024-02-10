package section_01_04.abstract_class_example;

/*
추상 클래스는 인스턴스를 생성할 수 없음.
일반적으로 하나 이상의 추상 메소드를 포함.
 */

public abstract class AbstractClass {

    public void implementedMethod() {
        System.out.println("AbstractClass implementedMethod");
        this.abstractMethod();
    }

    public abstract void abstractMethod();

}
