javac *.java
PAUSE
jar cvfm AutoJudge.jar manifest.txt *.class
del *.class