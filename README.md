Kiji/Spark integration example
------------------------------

To run:

    mvn clean package

Get the classpath that Maven uses to build and add that to your classpath when running the JAR.

    mvn dependency:build-classpath

This will print out a bunch of stuff.  Set that huge bunch of stuff to an environment variable:

    export SPARKCP=/Users/clint/.m2/repository/org/scala-lang/...

Now you can run the main class using the JAR file in `target`:

    java -cp target/KijiSpark-0.1-SNAPSHOT.jar:$SPARKCP org.kiji.spark.App --host local

Woo hoo!
