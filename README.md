# A boolean that is neither true nor false

Consider this class:

```java
public class B {
  public static void main(String[] args) {
    if (A.A != true && A.A != false) {
      System.out.println("Wat");
    }
  }
}
```

Is it possible for this program to print "Wat"?  If I'm asking this, then you
already know the answer.

## Instructions

Clone this repository and run `make` (you must have a JVM installed). Then run
the following command:

```shell
java -cp classes B
```

## Why does it work?

In the JVM spec for class files, boolean constants are represented as integers
[spec]. Javac always uses 1 for true and 0 for false, but it is legal to have
a class file that contains a `public static final boolean` with a value other 
than 0 or 1.  

If a Java compiler encounters such a constant in a class file, the behavior
is undefined.  

This trick works because the demo uses ASM to edit the value of the constant 
field `A.A` to 2, then compiles B against the edited version.  The value of
`A.A` is neither `true` (1) or `false` (0), so the print statement is executed.

Note: This no longer works with Java 9. We reported it upstream, and it was
fixed.

[spec]: https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.16.1
