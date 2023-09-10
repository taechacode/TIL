# String, StringBuffer, StringBuilder

- String 클래스는 인스턴스를 생성할 때 지정된 문자열을 변경할 수 없다.
- 반면, StringBuffer 클래스는 인스턴스를 생성할 때 지정된 문자열을 변경할 수 있다. 내부적으로 문자열 편집을 위한 버퍼(buffer)를 가지고 있으며, StringBuffer 인스턴스를 생성할 때 그 크기를 지정할 수 있다.
- StringBuffer 인스턴스를 생성할 때, 편집할 문자열의 길이를 고려하여 버퍼의 길이를 충분히 잡아주는 것이 좋다. 편집 중인 문자열이 버퍼의 길이를 넘어서게 되면 버퍼의 길이를 늘려주는 작업이 추가로 수행되어야하기 때문에 작업효율이 떨어진다.
- StringBuffer 클래스는 String 클래스와 같이 문자열을 저장하기 위한 char형 배열의 참조변수를 인스턴스 변수로 선언해 놓고 있다. StringBuffer 인스턴스가 생성될 때, char형 배열이 생성되며 이 때 생성된 char형 배열을 인스턴스 변수 value가 참조하게 된다.

```
public final class StringBuffer implements java.io.Serializable {
  private char[] value;
  ...
}
```

- String 클래스의 객체는 한 번 값이 할당되면 그 공간은 변하지 않는다.
- 그러나 StringBuffer와 StringBuilder는 한 번 값이 할당되더라도 다른 값이 할당되면 그 공간이 변하는 특성을 가지고 있다.
- 할당된 공간이 변하지 않는 특성을 <strong>불변성(Immutable)</strong>이라고 하고, 할당된 공간이 변하는 특성을 가변성(Mutable)이라고 한다.

```
		String str = "StringA";
		StringBuffer sbf = new StringBuffer();
		StringBuilder sbd = new StringBuilder();
		
		sbf.append("StringBufferA");
		sbd.append("StringBuilderA");
		
		System.out.println("String 객체 주소 (변경 전) : " + str.hashCode());
		System.out.println("StringBuffer 객체 주소 (변경 전) : " + sbf.hashCode());
		System.out.println("StringBuilder 객체 주소 (변경 전) : " + sbd.hashCode());
		
		str += "StringB";
		sbf.append("StringBufferB");
		sbd.append("StringBuilderB");
		
		System.out.println("String 객체 주소 (변경 후) : " + str.hashCode());
		System.out.println("StringBuffer 객체 주소 (변경 후) : " + sbf.hashCode());
		System.out.println("StringBuilder 객체 주소 (변경 후) : " + sbd.hashCode());
```

![객체주소](https://github.com/taechacode/TIL/assets/63395751/8388ee9a-449b-4d89-bf14-5ed7aaf41b6f)

- String, StringBuffer, StringBuilder의 변수를 선언해서 문자열을 변경하기 전에 객체의 주소를 해싱하여 반환해주는 hashcode()의 값을 출력해보았다.
- 기존 문자열을 변경시킨 후에 다시 hashcode()의 반환 값을 출력해보았다.
- String 클래스 객체의 주소만 바뀐 것을 확인할 수 있다.

<br/>

## String 변수에 값을 할당하는 방법

- String 변수에 값을 할당하는 방법은 2가지가 있다.
- 하나는 리터럴 변수를 대입하는 방법, 다른 하나는 new 키워드를 사용하는 방법이다.

```
		String strA = "String";
		String strB = new String("String");
		String strC = "String";
		String strD = new String("String");
		
		System.out.println(strA == strB);
		System.out.println(strA == strC);
		System.out.println(strB == strD);
```

![객체비교](https://github.com/taechacode/TIL/assets/63395751/40e1c338-6775-4daa-8757-2ae36342c95c)

- 4개의 변수 모두 "String"이라는 동일한 문자열을 갖지만 주소비교(==)의 값이 다른 것을 확인할 수 있다.
- 왜냐하면 String 타입 값 할당 방식에 따른 저장 방식이 다르기 때문이다.

### 리터럴 값으로 값을 할당하는 경우

- String을 리터럴 값으로 할당하는 경우에는 Heap 메모리 영역 안의 특별한 메모리 공간인 String constant pool에 저장된다.
- 만약 String constant pool에 존재하는 리터럴 값을 사용하게 된다면 새롭게 리터럴 값을 만들어 저장하는 것이 아니라, 현재 존재하는 값을 사용하게 된다.

![heoseungyeon_velog참조_01](https://github.com/taechacode/TIL/assets/63395751/168ed2fc-1d70-4970-8054-0d0cbf71410f)

- 위 이미지와 같은 결과로 `System.out.println(strA == strC);`의 결과가 true로 나온다.

### new 키워드로 값을 할당하는 경우

- new 키워드를 통해 String 변수에 값을 할당하게 되면 일반적인 객체와 동이랗게 Heap 영역에 동적으로 메모리 공간이 할당된다.
- 마찬가지로 같은 문자열이라도 new 키워드를 한 번 더 사용하게 되면 같은 값이지만 다른 메모리 공간(Heap 영역 안)을 참조하게 된다.

![heoseungyeon_velog참조_02](https://github.com/taechacode/TIL/assets/63395751/2e10e3a0-4886-4aa3-8556-dbca36711c5b)

- 위 이미지와 같은 결과로 `System.out.println(strA == strB);`의 결과가 `false`로 나온다.
- strA -> Heap -> **String constant pool**
- strB -> **Heap**

<br/>

## StringBuffer VS StringBuilder

![StringBuffer](https://github.com/taechacode/TIL/assets/63395751/3ce2dffa-0bee-403f-99de-8a84da46b8d7)

![StringBuilder](https://github.com/taechacode/TIL/assets/63395751/03ce1486-fa42-456e-ab82-aa85b511671a)

- StringBuffer 클래스와 StringBuilder 클래스 모두 AbstractStringBuilder라는 추상 클래스를 상속받아 구현되어 있다.
- AbstractStringBuilder 추상 클래스의 멤버 변수에는 다음 2가지 변수가 존재한다.
  + value : 문자열의 값을 저장하는 byte형 배열
  + count : 현재 문자열 크기의 값을 가지는 int형 변수
- StringBuffer와 StringBuilder 클래스의 문자열을 수정할 때는 `append()` 메서드를 사용하게 된다.

```
    public AbstractStringBuilder append(String str) {
        if (str == null) {
            return appendNull();
        }
        int len = str.length();
        ensureCapacityInternal(count + len);
        putStringAt(count, str);
        count += len;
        return this;
    }
```

- StringBuffer, StringBuilder 클래스에 문자열을 추가하게 되면 추가할 문자열의 크기(길이)만큼 현재의 문자열을 저장하는 배열의 공간을 늘려주고, 늘려준 공간에 추가할 문자열을 넣어주는 방식으로 되어있다.

```
    private void ensureCapacityInternal(int minimumCapacity) {
        // overflow-conscious code
        int oldCapacity = value.length >> coder;
        if (minimumCapacity - oldCapacity > 0) {
            value = Arrays.copyOf(value,
                    newCapacity(minimumCapacity) << coder);
        }
    }
```

- 정확히는 클래스 변수 value를 크기를 늘려준 배열을 참조하게 함으로써 내부적으로는 참조하는 배열이 바뀌지만, 겉으로 StringBuffer와 StringBuilder 클래스의 인스턴스 주소는 바뀌지 않는다.
- 위에서 살펴본 내부동작을 통해 값이 변경되더라도 **같은 주소공간을 참조**하게 되는 것이며, **값이 변경되는 가변성**을 띄게 되는 것이다.

<br/>

- 하지만 두 클래스의 기능은 동일하지만 한 가지 차이점이 존재한다. 바로 `동기화(Synchronization)`에서의 차이점이다.
- StringBuffer는 동기화를 지원하여 멀티 스레드 환경에서도 안전하게 동작할 수 있지만, StringBuilder는 동기화를 지원하지 않는다.
- 그 이유는 StringBuffer는 메서드에서 `synchronized`키워드를 사용하기 때문이다.

<br/>

- java에서 `synchronized`키워드는 여러 개의 스레드가 한 개의 자원에 접근하려고 할 때, 현재 데이터를 사용하고 있는 스레드를 제외하고 **나머지 스레드들이 데이터 접근할 수 없도록 막는 역할을 수행**한다.
- 예를 들어, 멀티 스레드 환경에서 A 스레드와 B 스레드 모두 같은 StringBuffer 클래스 객체 sb의 append() 메서드를 사용하려고 하면, 다음과 같은 절차를 수행하게 된다.

<br/>

1. A 스레드 : sb의 append() 동기화 블록에 접근 및 실행
2. B 스레드 : A 스레드 sb의 append() 동기화 블록에 들어가지 못하고 block 상태가 됨
3. A 스레드 : sb의 append() 동기화 블록에서 탈출
4. B 스레드 : block에서 running 상태가 되며 sb의 append() 동기화 블록에 접근 및 실행

<br/>

![StringBuffer추천](https://github.com/taechacode/TIL/assets/63395751/876a1617-86d2-4b4c-82f9-8a4d48831784)

- StringBuilder 클래스 주속에서 동기화가 필요할 경우 StringBuffer을 추천한다는 문구를 확인할 수 있다.
