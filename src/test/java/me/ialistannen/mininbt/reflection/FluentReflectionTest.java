package me.ialistannen.mininbt.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentField;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;
import org.junit.jupiter.api.Test;

class FluentReflectionTest {

  @Test
  public void getPrivateField() {
    TestClass testClass = new TestClass();
    testClass.hey = 200;

    int read = (int) FluentType.ofUnknown(TestClass.class).findField()
        .withName("hey")
        .findSingle().getOrThrow()
        .getValue(testClass).getOrThrow();

    assertEquals(
        200,
        read
    );
  }

  @Test
  public void setPrivateField() {
    TestClass testClass = new TestClass();
    testClass.hey = 200;

    FluentType.ofUnknown(TestClass.class).findField()
        .withName("hey")
        .findSingle().getOrThrow()
        .setValue(testClass, -20).ensureSuccessful();

    assertEquals(
        -20,
        testClass.hey
    );
  }

  @Test
  public void findDeclaredFieldFromSuperclass() {
    FluentType.ofUnknown(Sub.class).findField()
        .withName("hey")
        .findSingle().ensureSuccessful();
  }

  @Test
  public void dontWalkFieldHierarchy() {
    ReflectiveResult<FluentField> value = FluentType.ofUnknown(Sub.class).findField()
        .withName("hey")
        .dontWalkHierarchy()
        .findSingle();

    assertFalse(
        value.isPresent(),
        "Found the parent field"
    );
  }

  @Test
  public void findDeclaredMethod() throws NoSuchMethodException {
    FluentMethod method = FluentType.ofUnknown(TestClass.class)
        .findMethod()
        .withName("hey")
        .findSingle()
        .getOrThrow();

    assertEquals(
        TestClass.class.getDeclaredMethod("hey"),
        method.getUnderlying()
    );
  }

  @Test
  public void findSuperclassPublicMethod() {
    List<FluentMethod> methods = new FluentType<>(Sub.class)
        .findMethod()
        .withName("hey")
        .withParameters()
        .withReturnType(String.class)
        .findAll().getOrThrow();

    assertEquals(
        1,
        methods.size()
    );
  }

  @Test
  public void invokeDeclaredMethod() {
    String argument = "Hello world";
    String result = (String) FluentType.ofUnknown(TestClass.class)
        .findMethod()
        .withName("echo")
        .withParameters(String.class)
        .findSingle()
        .getOrThrow()
        .invokeStatic(argument)
        .getOrThrow();

    assertEquals(
        argument,
        result
    );
  }

  @Test
  public void invokeDeclaredConstructor() {
    TestClass testClass = new FluentType<>(TestClass.class)
        .findConstructor()
        .withParameters()
        .findSingle().getOrThrow()
        .createInstance()
        .getOrThrow();

    assertEquals(
        -10,
        testClass.hey
    );
  }

  private static class TestClass {

    private int hey;

    public TestClass() {
      hey = -10;
    }

    private void hey() {
    }

    private static String echo(String input) {
      return input;
    }
  }

  private static class Super {

    private int hey;

    public String hey() {
      return "";
    }
  }

  private static class Sub extends Super {

  }
}