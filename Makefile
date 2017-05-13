all:
	mkdir -p classes
	javac -d classes -cp asm/asm-all-5.2.jar src/rewrite/Rewrite.java
	javac -d classes src/A.java
	java -cp classes:asm/asm-all-5.2.jar rewrite.Rewrite classes/A.class 2
	javac -d classes -cp classes src/B.java

clean:
	rm -rf classes



