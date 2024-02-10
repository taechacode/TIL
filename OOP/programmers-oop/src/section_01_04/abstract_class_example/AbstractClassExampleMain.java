package section_01_04.abstract_class_example;

public class AbstractClassExampleMain {

    public static void main(String[] args) {

        AbstractClass abstractClass = new AbstractClass() {
            @Override
            public void abstractMethod() {
                // main 메소드에서 abstract 메소드 구현 후 사용가능
                System.out.println("AbstractClass abstractMethod(Override)");
            }
        };

        abstractClass.implementedMethod();
        abstractClass.abstractMethod();

        AbstractClass extendedClass = new ExtendedClass();
        extendedClass.implementedMethod();
        extendedClass.abstractMethod();
        // 위 2개의 메소드 모두 ExtendedClass에서 구현한 abstractMethod 호출

    }

}
