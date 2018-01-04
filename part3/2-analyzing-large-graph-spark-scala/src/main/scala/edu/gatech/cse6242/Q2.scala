package edu.gatech.cse6242

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._

object Q2 {

    case class OutEdgeVal(node:String, outWt:Int)
    case class InEdgeVal(node:String, inWt:Int)
    def main(args: Array[String]) {
        val sc = new SparkContext(new SparkConf().setAppName("Q2"))
        val sqlContext = new SQLContext(sc)
        import sqlContext.implicits._

        // read the file
        val file = sc.textFile("hdfs://localhost:8020" + args(0))
        /* TODO: Needs to be implemented */
        val outEdge = file.map(_.split("\t")).map(e => OutEdgeVal(e(0), e(2).trim.toInt)).toDF("node","outWt")
        val inEdge = file.map(_.split("\t")).map(e => InEdgeVal(e(1), e(2).trim.toInt)).toDF("node","inWt")

        val outEdgeDF = outEdge.filter("outWt >= 5").groupBy("node").agg(sum("outWt").alias("outCount"))
        val inEdgeDF = inEdge.filter("inWt >= 5").groupBy("node").agg(sum("inWt").alias("inCount"))

        val edgeJoin = inEdgeDF.join(outEdgeDF, Seq("node"), "outer")
        val edgeJoinInCount = edgeJoin.na.fill(0, Seq("inCount"))
        val edgeJoinOutCount = edgeJoinInCount.na.fill(0, Seq("outCount"))
        val diff = edgeJoinOutCount.select($"node", $"inCount" - $"outCount")


        // store output on given HDFS path.
        diff.map(x => x.mkString("\t")).saveAsTextFile("hdfs://localhost:8020" + args(1))

    }
}
