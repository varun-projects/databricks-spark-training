{"version":"NotebookV1","origId":503877321547914,"name":"Lab","language":"scala","commands":[{"version":"CommandV1","origId":503877321547916,"guid":"fc51189f-0c40-4e4f-bd54-1b598b265a5b","subtype":"command","commandType":"auto","position":1.0,"command":"%md\n# Log File Streaming Lab\n\nIn this lab, we'll be reading (fake) log data from a stream.","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"e6838bdd-1fda-45b2-8dd7-636c06774cf2"},{"version":"CommandV1","origId":503877321547917,"guid":"87f640b8-4048-4e51-9ef2-30b0ea8bc5fb","subtype":"command","commandType":"auto","position":2.0,"command":"import org.apache.spark._\nimport org.apache.spark.streaming._\nimport org.apache.spark.sql._\nimport scala.util.Random\nimport java.sql.Timestamp\nimport com.databricks.training.helpers.uniqueIDForUser\n\nrequire(sc.version.replace(\".\", \"\").toInt >= 140, \"Spark 1.4.0 or greater is required.\")","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"26a21d85-26ca-42e1-b26d-30b6b70ae864"},{"version":"CommandV1","origId":503877321547918,"guid":"111827ee-da32-4d59-bd60-d4432ece0668","subtype":"command","commandType":"auto","position":3.0,"command":"val BatchIntervalSeconds= 1\nval UniqueID = uniqueIDForUser()\nval OutputDir = s\"dbfs:/tmp/streaming/logs/$UniqueID\"\nval OutputFile = s\"$OutputDir/${(new java.util.Date).getTime}.parquet\"\nval CheckpointDir = s\"$OutputDir/checkpoint\"","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"300ce5bd-7470-40e8-a4ee-580c5658f751"},{"version":"CommandV1","origId":503877321547919,"guid":"b6b35181-cdd5-4479-99e5-102823569441","subtype":"command","commandType":"auto","position":4.0,"command":"%md\nWe need to pull in some configuration data.\n*In the next cell, EITHER change the path so it is an absolute path (starting with / up to the correct notebook)\nOR just copy and paste the configuration cell*","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"97ec425a-0491-4ad5-afd7-46881989edfa"},{"version":"CommandV1","origId":503877321547920,"guid":"0907d9f6-755d-4273-8d61-a80cf11aba7f","subtype":"command","commandType":"auto","position":5.0,"command":"val LogServerHost = \"52.26.184.74\"\nval LogServerPort = 9001","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"3c733d97-054e-417d-8569-8e9cd31cac84"},{"version":"CommandV1","origId":503877321547921,"guid":"60952a9c-085c-4042-a5f0-0cea1ff4032c","subtype":"command","commandType":"auto","position":6.0,"command":"%md \n## Create our Streaming Process\n\nWe're going to connect to a TCP server that serves log messages, one per line. We'll read the messages and write them to a Parquet file.\n\nWe're going to be using a complicated-looking regular expression to take each log message apart:\n\n```\n^\\[(.*)\\]\\s+\\(([^)]+)\\)\\s+(.*)$\n```","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"64d2f048-18ec-4df3-a7dd-1e103b9f6c1b"},{"version":"CommandV1","origId":503877321547922,"guid":"ba511618-2f0b-45fb-9d8d-13650c8c5b39","subtype":"command","commandType":"auto","position":7.0,"command":"%md\nThe following object contains the code for our stream reader.\n\n**NOTE**: In Databricks (and in a Scala REPL), it helps to wrap your streaming solution in an object, to prevent scope confusion when Spark attempts to gather up and serialize the variables and functions for distribution across the cluster.","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"33c18d5d-5f00-41be-a638-d7bbd4ddfea5"},{"version":"CommandV1","origId":503877321547923,"guid":"64de748d-730c-4319-a52b-a320fabca7b6","subtype":"command","commandType":"auto","position":8.0,"command":"object Runner extends Serializable {\n  import java.text.SimpleDateFormat\n  import java.util.Date\n  import java.sql.Timestamp\n  \n  case class LogMessage(messageType: String, timestamp: Timestamp, message: String)\n\n  // Log messages look like this: [2015/09/10 21:15:46.680] (ERROR) Server log backup FAILED. See backup logs.\n  private val LogLinePattern = \"\"\"^\\[(.*)\\]\\s+\\(([^)]+)\\)\\s+(.*)$\"\"\".r\n  \n  /** Stop the streaming context.\n    */\n  def stop() {\n    StreamingContext.getActive.map { ssc =>\n      ssc.stop(stopSparkContext=false, stopGracefully=false)\n      println(\"Stopped active streaming context.\")\n    }\n  }\n  \n  /** Start the streaming context.\n    */\n  def start() {\n    val ssc = StreamingContext.getActiveOrCreate(CheckpointDir, createStreamingContext)\n    ssc.start()\n    println(\"Started streaming context.\")\n  }\n  \n  /** Restart the streaming context (convenience method).\n    */\n  def restart() {\n    stop()\n    start()\n  }\n  \n  // This method creates and initializes our stream.\n  private def createStreamingContext(): StreamingContext = {\n    val ssc = new StreamingContext(sc, Seconds(BatchIntervalSeconds))\n    ssc.checkpoint(CheckpointDir)\n    val DateFmt = new SimpleDateFormat(\"yyyy/MM/dd HH:mm:ss.S\")\n\n    val ds = ssc.socketTextStream(LogServerHost, LogServerPort)\n\n    // What we're getting is a stream of log lines. As each RDD is created by\n    // Spark Streaming, have the stream create a new RDD that extracts just\n    // the error messages, dropping the rest.\n    val keepLogsStream = ds.flatMap { line =>\n      line match {\n        case LogLinePattern(timeString, messageType, message) => {\n          // Valid log line. Parse the date. NOTE: If this were a real\n          // application, we would want to catch errors here.\n          val timestamp = new Timestamp(DateFmt.parse(timeString).getTime)\n          val logMessage = LogMessage(messageType, timestamp, message)\n          Some(logMessage)\n        }\n        \n        case _ => None // malformed line\n      }\n    }\n\n    // Now, each RDD created by the stream will be converted to something\n    // we can save to a persistent store that can easily be read, later,\n    // into a DataFrame.\n    keepLogsStream.foreachRDD { rdd =>\n      if (! rdd.isEmpty) {\n        // Using SQLContext.createDataFrame() is preferred here, since toDF()\n        // can pick up (via implicits) objects that aren't serializable.\n        val sqlContext = SQLContext.getOrCreate(SparkContext.getOrCreate())\n        val df = sqlContext.createDataFrame(rdd)\n        df.write.mode(SaveMode.Append).parquet(OutputFile)\n      }\n    }\n        \n    // Start our streaming context.\n    ssc.start()\n\n    ssc\n  }\n}","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"5dc61f03-af34-47f3-a3e7-8564665dff97"},{"version":"CommandV1","origId":503877321547924,"guid":"b2407f01-7450-40ca-872a-ca77083b6cbe","subtype":"command","commandType":"auto","position":9.0,"command":"%md For good measure, let's make sure our `Runner` class is serializable. If it isn't, we'll get an error when we try to start it.","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"9b47b725-3d5b-4826-9c89-66bfaeca8dd0"},{"version":"CommandV1","origId":503877321547925,"guid":"f3113844-d724-453e-935b-c19a2119ab82","subtype":"command","commandType":"auto","position":10.0,"command":"import java.io.{ByteArrayOutputStream, ObjectOutputStream}\nval os = new ObjectOutputStream(new ByteArrayOutputStream)\nos.writeObject(Runner)","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"3997a3e9-7907-4b8b-9a21-038898eebd2c"},{"version":"CommandV1","origId":503877321547926,"guid":"fdf7c586-20e4-4e88-a208-fd88e7c34c5c","subtype":"command","commandType":"auto","position":11.0,"command":"dbutils.fs.rm(OutputDir, recurse=true)\ndbutils.fs.mkdirs(OutputDir)\nRunner.restart()","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"e5f9f8aa-cced-4f25-95f4-cfa5ebe94284"},{"version":"CommandV1","origId":503877321547927,"guid":"a21879a3-607f-466f-9ce9-74691c3d2316","subtype":"command","commandType":"auto","position":12.0,"command":"display( dbutils.fs.ls(OutputDir) )","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"f13ac327-6e55-4d73-a6e3-ab2220dac3b4"},{"version":"CommandV1","origId":503877321547928,"guid":"7a8652e5-8fb7-447e-a484-a95c83bd09fd","subtype":"command","commandType":"auto","position":13.0,"command":"// Is there anything in the output directory?\ndisplay( dbutils.fs.ls(OutputFile) )","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"33b3fcd8-f5cf-4696-888d-b739b8d383de"},{"version":"CommandV1","origId":503877321547929,"guid":"25a8208b-0051-4891-9f2b-d9b6b500b046","subtype":"command","commandType":"auto","position":14.0,"command":"%md\nLet the stream run for awhile. Then, execute the following cell to stop the stream.","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"b0841e7f-2d7b-4e3e-bd56-630ad56b4056"},{"version":"CommandV1","origId":503877321547930,"guid":"943c36a1-d654-43cd-ba97-a361f58be3b3","subtype":"command","commandType":"auto","position":15.0,"command":"Runner.stop()","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"bb0bfa9b-58a8-4922-8187-57eb761b6f5b"},{"version":"CommandV1","origId":503877321547931,"guid":"c20e00e8-8049-46c6-8039-241824a138eb","subtype":"command","commandType":"auto","position":16.0,"command":"%md Okay, now let's take a look at what we have in the Parquet file. **NOTE**: This next command may fail periodically if the stream is still running.","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"4c2f35d1-c13e-4701-bc7c-9681184117e7"},{"version":"CommandV1","origId":503877321547932,"guid":"9aabe744-02ee-4888-8e43-b39995b1e787","subtype":"command","commandType":"auto","position":17.0,"command":"val df = sqlContext.read.parquet(OutputFile)","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"44cc0366-48b0-4b75-b920-2adc5cb82cc2"},{"version":"CommandV1","origId":503877321547933,"guid":"89852a21-bef4-421e-98c0-5a883875c296","subtype":"command","commandType":"auto","position":18.0,"command":"%md How many messages did we pull down?","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"08f26e52-431c-48c9-bae9-b56e793689a4"},{"version":"CommandV1","origId":503877321547934,"guid":"a76ecb20-a485-4a74-99c6-bf624d331e35","subtype":"command","commandType":"auto","position":19.0,"command":"df.count","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"2e8f7c4e-e3b3-4ce9-8269-0dc54b6dbd8a"},{"version":"CommandV1","origId":503877321547935,"guid":"5ca2bcd8-9a61-4092-b41b-fe0188d05069","subtype":"command","commandType":"auto","position":20.0,"command":"display(df)","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"2b481cd0-9b61-4a78-a622-1b82e776fb3c"},{"version":"CommandV1","origId":503877321547936,"guid":"7ccbbf4d-56c5-41b8-8145-44384f14a9f5","subtype":"command","commandType":"auto","position":21.0,"command":"df.registerTempTable(\"messages\")","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"0de9ebdd-2ba3-495d-913c-b398190c1bd6"},{"version":"CommandV1","origId":503877321547937,"guid":"fe196443-4b4a-413d-b681-e424d6e6471f","subtype":"command","commandType":"auto","position":22.0,"command":"%md\n## Exercise 1\n\nHow many messages of each type (ERROR, INFO, WARN) are there?\n\n**HINT**: Use your DataFrame knowledge.","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"c7510c2c-dc84-4eae-b51f-9729dbb03a01"},{"version":"CommandV1","origId":503877321547938,"guid":"b22f665a-b8b7-4fe2-9632-33d023f6ded3","subtype":"command","commandType":"auto","position":23.0,"command":"%sql select messageType, count(messageType) from messages group by messageType","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"fa1d42f6-cb2e-497e-995c-452e6d52fbec"},{"version":"CommandV1","origId":503877321547939,"guid":"7ffa1642-0347-4796-a8cc-c79f90114863","subtype":"command","commandType":"auto","position":24.0,"command":"%md \n## Exercise 2\n\nModify the `Runner`, above, to keep only the ERROR messages.","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"416082b2-0d15-4c53-b9fe-60bd94174904"},{"version":"CommandV1","origId":503877321547940,"guid":"b6b65fd7-bc49-4273-a1eb-0c2951efd040","subtype":"command","commandType":"auto","position":25.0,"command":"display(dbutils.fs.ls(\"dbfs:/tmp/streaming\"))","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"77e27cc3-4bf5-4388-bc06-0b1ff2b929ad"},{"version":"CommandV1","origId":503877321547941,"guid":"722a947a-2bcd-4a0b-88d3-06cce1afc696","subtype":"command","commandType":"auto","position":26.0,"command":"","commandVersion":0,"state":"finished","results":null,"errorSummary":null,"error":null,"workflows":[],"startTime":0.0,"submitTime":0.0,"finishTime":0.0,"collapsed":false,"bindings":{},"inputWidgets":{},"displayType":"table","width":"auto","height":"auto","xColumns":null,"yColumns":null,"pivotColumns":null,"pivotAggregation":null,"customPlotOptions":{},"commentThread":[],"commentsVisible":false,"parentHierarchy":[],"diffInserts":[],"diffDeletes":[],"globalVars":{},"latestUser":"","commandTitle":"","showCommandTitle":false,"hideCommandCode":false,"hideCommandResult":false,"iPythonMetadata":null,"streamStates":{},"nuid":"eef238cc-d804-4d56-83c9-8ec428bc7c20"}],"dashboards":[],"guid":"ef228ea2-b09e-4090-86bc-17009e5f3f0a","globalVars":{},"iPythonMetadata":null,"inputWidgets":{}}