package edu.gatech.cse6242;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

public class Q4 {

	public static class NodeDegreeMapper extends Mapper<Object, Text, Text, IntWritable> {
	       
		private Text source = new Text();
		private Text target = new Text();
		private IntWritable posOne = new IntWritable(1);
		private IntWritable negOne = new IntWritable(-1);
	    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
	    	StringTokenizer st = new StringTokenizer(value.toString(), "\r");
	        while (st.hasMoreTokens()) {
	        	String[] edge = st.nextToken().split("\t");
	        	source.set(edge[0]);
	        	target.set(edge[1]);
	        	context.write(source, posOne);
	        	context.write(target, negOne);
	        }
	    }
	}
	
	  public static class NodeDegreeReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
	       
		  private IntWritable diff = new IntWritable();  
		  public void reduce(Text key, Iterable<IntWritable> degrees, Context context) throws IOException, InterruptedException{

	            int sum = 0;

	            for (IntWritable degree : degrees)  {
	            	sum += degree.get();
	            }

	            diff.set(sum);

	            context.write(key, diff);
		  }
	  }
	  
	  public static class DegreeCountMapper extends Mapper<Object, Text, Text, IntWritable> {
	       
			private Text degree = new Text();
			private IntWritable posOne = new IntWritable(1);
		    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		    	System.out.println(value.toString());
		    	StringTokenizer st = new StringTokenizer(value.toString(), "\r");
		        while (st.hasMoreTokens()) {
		        	String[] difference = st.nextToken().split("\t");
		        	degree.set(difference[1]);
		        	context.write(degree, posOne);
		        }
		    }
		}
	  
	  public static class DegreeCountReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
	       
		  private IntWritable count = new IntWritable();  
		  public void reduce(Text key, Iterable<IntWritable> degrees, Context context) throws IOException, InterruptedException{

	            int sum = 0;

	            for (IntWritable degree : degrees)  {
	            	sum += degree.get();
	            }

	            count.set(sum);

	            context.write(key, count);
		  }
	  }
	  
	  
	
	public static void main(String[] args) throws Exception {

		/*JobControl jobControl = new JobControl("jobChain"); 
	    Configuration conf1 = new Configuration();
	    
	    Job job1 = Job.getInstance(conf1, "Q4");  
	    job1.setJarByClass(Q4.class);
	    job1.setJobName("Node Degree");

	    job1.setMapperClass(NodeDegreeMapper.class);
	    job1.setCombinerClass(NodeDegreeReducer.class);
	    job1.setReducerClass(NodeDegreeReducer.class);
	    job1.setOutputKeyClass(Text.class);
	    job1.setOutputValueClass(IntWritable.class);
	    
	    FileInputFormat.addInputPath(job1, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job1, new Path("temp"));

	    ControlledJob controlledJob = new ControlledJob(conf1);
	    controlledJob.setJob(job1);

	    jobControl.addJob(controlledJob);
	    
	    Configuration conf2 = new Configuration();
	    
	    Job job2 = Job.getInstance(conf2, "Q4");  
	    job2.setJarByClass(Q4.class);
	    job2.setJobName("Degree Count");

	    job2.setMapperClass(DegreeCountMapper.class);
	    job2.setCombinerClass(DegreeCountReducer.class);
	    job2.setReducerClass(DegreeCountReducer.class);
	    job2.setOutputKeyClass(Text.class);
	    job2.setOutputValueClass(IntWritable.class);
	    
	    job2.setInputFormatClass(KeyValueTextInputFormat.class);
	    FileInputFormat.addInputPath(job2, new Path("temp"));
	    FileOutputFormat.setOutputPath(job2, new Path(args[1]));
	    
	    ControlledJob controlledJob2 = new ControlledJob(conf2);
	    controlledJob2.setJob(job2);
	    
	    controlledJob2.addDependingJob(controlledJob); 
	    // add the job to the job control
	    jobControl.addJob(controlledJob2);
	    Thread jobControlThread = new Thread(jobControl);
	    jobControlThread.start();*/
		
		Configuration confHDFS = new Configuration();
		Path tempDir = new Path("temp");
		FileSystem fs = FileSystem.get(confHDFS);
		if (fs.exists(tempDir)) {
		    fs.delete(tempDir, true);
		}
		
		JobControl jobControl = new JobControl("jobChain");
	    Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "Q4");
	
	    /* TODO: Needs to be implemented */
	    job.setJarByClass(Q4.class);
	    job.setMapperClass(NodeDegreeMapper.class);
	    job.setCombinerClass(NodeDegreeReducer.class);
	    job.setReducerClass(NodeDegreeReducer.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);    
	
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, tempDir);
	    
	    ControlledJob controlledJob = new ControlledJob(conf);
	    controlledJob.setJob(job);

	    jobControl.addJob(controlledJob);
	    
	    
	    Configuration conf2 = new Configuration();
	    Job job2 = Job.getInstance(conf2, "Q4");
	
	    /* TODO: Needs to be implemented */
	    job2.setJarByClass(Q4.class);
	    job2.setMapperClass(DegreeCountMapper.class);
	    job2.setCombinerClass(DegreeCountReducer.class);
	    job2.setReducerClass(DegreeCountReducer.class);
	    job2.setOutputKeyClass(Text.class);
	    job2.setOutputValueClass(IntWritable.class);
	
	    FileInputFormat.addInputPath(job2, tempDir);
	    FileOutputFormat.setOutputPath(job2, new Path(args[1]));
	    
	    ControlledJob controlledJob2 = new ControlledJob(conf2);
	    controlledJob2.setJob(job2);
	    
	    controlledJob2.addDependingJob(controlledJob);
	    
	    jobControl.addJob(controlledJob2);
	    Thread jobControlThread = new Thread(jobControl);
	    jobControlThread.start();
	    while (!jobControl.allFinished()) {
	    	Thread.sleep(5000);
	    }
	    System.exit(0);
  }
}
