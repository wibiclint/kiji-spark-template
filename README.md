Kiji/Spark integration example
==============================

Setting up Spark and a Bento Box
================================

Download and build Spark
------------------------

Download the latest version of Spark from github and build with sbt:

    git clone git@github.com:apache/incubator-spark.git spark
    cd spark
    export SPARK_HADOOP_VERSION=2.0.0-mr1-cdh4.3.0                                     
    sbt/sbt assembly

We want to be able to build our Spark app with this locally-built version of Spark as a dependency.
Install this new Spark JAR in your local Maven repository:

    mvn install:install-file \
      -Dfile=assembly/target/scala-2.10/spark-assembly-0.9.0-incubating-SNAPSHOT-hadoop2.0.0-mr1-cdh4.3.0.jar \
      -DgroupId=org.apache.spark -DartifactId=spark-all -Dversion=local -Dpackaging=jar

The POM file for the project references this dependency.


Building
--------

Build with Maven:

    mvn clean package


Classpath stuff
---------------

Get the classpath that Maven uses to build and add that to your classpath when running the JAR.

    mvn dependency:build-classpath

This will print out a bunch of stuff.  Set that huge bunch of stuff to an environment variable:

    export SPARKCP=/Users/clint/.m2/repository/org/scala-lang/...

Spark itself uses Avro 1.7.4, but we need 1.7.5 for Kiji, so I had to add my Avro 1.7.5 JAR to the
beginning of my classpath as well.  Finally, we need to put `$HADOOP_CONF_DIR` on the classpath so that we can grab all of the Bento Box
configuration settings.

    export SPARKCP=$HADOOP_CONF_DIR:/Users/clint/.m2/repository/org/apache/avro/avro/1.7.5/avro-1.7.5.jar:$SPARKCP


Now you can run the main class using the JAR file in `target`:

    java -cp target/KijiSpark-0.1-SNAPSHOT.jar:$SPARKCP org.kiji.spark.App --host local

Woo hoo!
