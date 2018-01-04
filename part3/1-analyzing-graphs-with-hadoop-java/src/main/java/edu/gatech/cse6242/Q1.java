package edu.gatech.cse6242;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Q1 {
 
  public static class TargetMapper extends Mapper<Object, Text, Text, IntWritable> {
       
      private Text target = new Text();
      public void map(Object key, Text value, Context context
                ) throws IOException, InterruptedException {
            StringTokenizer st = new StringTokenizer(value.toString(), "\r");
            while (st.hasMoreTokens()) {
                String[] edge = st.nextToken().split("\t");
                target.set(edge[1]);
                context.write(target, new IntWritable(Integer.parseInt(edge[2])));
            }
        }
       
    }
 
  public static class EmailsReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
       
	  private IntWritable totalCount = new IntWritable();  
	  public void reduce(Text key, Iterable<IntWritable> targets, Context context) throws IOException, InterruptedException{
           
            int min = 0;
           
            for (IntWritable target : targets)  {
                if(target.get() < min || min ==0) {
                	min = target.get();
                }
            }
           
            totalCount.set(min);
           
            context.write(key, totalCount);
           
           
        }
    }
  
  public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "Q1");

	    job.setJarByClass(Q1.class);
	    job.setMapperClass(TargetMapper.class);
	    job.setCombinerClass(EmailsReducer.class);
	    job.setReducerClass(EmailsReducer.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);

	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
