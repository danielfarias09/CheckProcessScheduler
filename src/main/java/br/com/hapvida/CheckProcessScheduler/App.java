package br.com.hapvida.CheckProcessScheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Hello world!
 *
 */
public class App implements Job {
	
	public static List<String> listRunningProcesses() {
	    List<String> processes = new ArrayList<String>();
	    try {
	      String line;
	      String processName;
	      Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
	      BufferedReader input = new BufferedReader
	          (new InputStreamReader(p.getInputStream()));
	      while ((line = input.readLine()) != null) {
	          if (!line.trim().equals("")) {
	              processName = line.split(",")[0];
	              processName = processName.substring(1, processName.length() -1);
	              processes.add(processName);
	          }

	      }
	      input.close();
	    }
	    catch (Exception err) {
	      err.printStackTrace();
	    }
	    return processes;
	  }
	
    public static void main( String[] args ){
		try {
			//Job que executa a cada 10 segundos
			SchedulerFactory schedFact = new StdSchedulerFactory();
			Scheduler sched = schedFact.getScheduler();
			sched.start();
			
			JobDetail job = JobBuilder.newJob(App.class)
	                .withIdentity("myJob", "group1")
	                .build();
			
			Trigger trigger = TriggerBuilder
	                .newTrigger()
	                .withIdentity("myTrigger", "group1")
	                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")) //define a peridiocidade
	                .build();
			sched.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
    }

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		List<String> processes = listRunningProcesses();    
        boolean processoEncontrado = processes.stream().anyMatch(p -> "chrome.exe".contains(p));
        System.out.println(processoEncontrado ? "Chrome sendo Executado" : "Chrome não está sendo Executado" );
		
	}
}
