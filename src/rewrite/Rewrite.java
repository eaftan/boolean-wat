package rewrite;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/** 
 * Rewrites all constant boolean fields in a class file to a specified value. 
 */
public class Rewrite {
  
  private static class RewriteClassVisitor extends ClassVisitor {
    private final int newValue;
    
    private RewriteClassVisitor(ClassVisitor cv, int newValue) {
      super(Opcodes.ASM5, cv);
      this.newValue = newValue;
    }
    
    @Override
    public FieldVisitor visitField(int access,
        String name,
        String desc,
        String signature,
        Object value) {

      // Only inspect public static final fields of boolean type
      int publicStaticFinal = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
      if ((access & publicStaticFinal) == publicStaticFinal
          && Type.BOOLEAN_TYPE.getDescriptor().equals(desc)
          && value != null) {
        System.out.println(
            String.format("Rewriting field %s with value %s to %d", name, value, newValue));
        return super.visitField(access, name, desc, signature, newValue);
      }
      
      return super.visitField(access, name, desc, signature, value);
    }
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err.println("Usage: java Rewrite <classfile> <new_value>");
      System.exit(1);
    }

    String classFile = args[0];
    int newValue = Integer.parseInt(args[1]);
    
    // Rewrite
    Path classFilePath = Paths.get(classFile);
    byte[] bytes = Files.readAllBytes(classFilePath);
    ClassWriter cw = new ClassWriter(0);
    new ClassReader(bytes).accept(new RewriteClassVisitor(cw, newValue), ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
    Files.write(classFilePath, cw.toByteArray());
  }

}
