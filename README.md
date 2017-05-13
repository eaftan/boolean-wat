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

In the [JVM spec for class files][spec], boolean constants are represented as integers.
Javac always uses 1 for true and 0 for false, but it is legal to have
a class file that contains a `public static final boolean` with a value other 
than 0 or 1.  

If a Java compiler encounters such a constant in a class file, the behavior
is undefined.  

This trick works because the demo uses ASM to edit the value of the constant 
field `A.A` to 2, then compiles B against the edited version.  The value of
`A.A` is neither `true` (1) or `false` (0), so the print statement is executed.

## What does it mean?

So, is this just a bug in javac, or a real problem with the Java or JVM spec?
It's unclear.  We asked upstream for clarification, and they gave us [this cryptic 
response][compiler-dev]:

```
In Lib.class, the field B contains a ConstantValue attribute that is not 
ill-formed, since the attribute points to a CONSTANT_Integer c.p. entry 
that is appropriate for a Z-typed field. A class file reader such as 
javac must not reject Lib.class on this basis. However, javac should 
handle an out-of-band value in the CONSTANT_Integer c.p. entry as a 
quality-of-implementation detail.
```

We ended up submitting a javac patch for this that issues an error on class
files that contain such an out-of-range constant value, which was accepted.
Thus, this demo does not work with Java 9.

My take is that this *is* a problem as it is unspecified what an implementation
should do in this case.  The spec either should state that such class files are
invalid, or it should specify how to interpret such out-of-range constant values.

[spec]: https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.16.1
[compiler-dev]: http://mail.openjdk.java.net/pipermail/compiler-dev/2016-November/010500.html
