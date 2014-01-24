# Random notes while looking through code

What happens when we start a Kiji MR job?

Look at `KijiProduce` in KijiMR.  This is the tool that we use to run a command-line Kiji producer
job.  Before running the Hadoop job, the command-line tool sets up a bunch of configuration
parameters in an instance of `KijiProduceJobBuilder`.  The `KijiProduce` instance calls
its `configure(jobBuilder)` method, which also calls superclass methods of that same name.  These
methods seem to set up the `KijiProduceJobBuilder` with whatever the default Hadoop `Configuration`
settings are, and then populate some other, Producer-specific stuff.  See, for example,
`JobTool.java`, line 101:

    jobBuilder.withConf(getConf());

(`JobTool` extends `BaseTool`, which extends `Configured`.)

`KijiProduceJobBuilder` extends `KijiTableInputJobBuilder`, which extends `MapReduceJobBuilder`.
All three of these classes together contain a lot of useful information that we clearly need to run
Kiji-flavored Hadoop jobs:

- JAR directories
- A base Hadoop `Configuration` object
- A `MapReduceJobOutput`
- A map from keys to `KeyValueStore` instances
- `KijiURI`
- Starting and ending `EntityId`s
- The `KijiProducer` class
- `KijiProducer`, `KijiMapper`, and `KijiReducer` instances
- A `KijiDataRequest`

Eventually from all of these builders, we get a configured map-reduce job, ready to run (see line
116 of `MapReduceJobBuilder`, which contains the `build()` method).  We should be able to call the
`getConfiguration` method on the `Job` we get from the `MapReduceJobBuilder` in our Spark code when
we call `newAPIHadoopRDD`.
