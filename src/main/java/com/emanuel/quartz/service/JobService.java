package com.emanuel.quartz.service;

import com.emanuel.quartz.job.SampleJob;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JobService {

    private final Scheduler scheduler;

    public JobService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleOneTimeJob(String jobName, String group) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(SampleJob.class)
                .withIdentity(jobName, group)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName + "Trigger", group)
                .startAt(DateBuilder.futureDate(60, DateBuilder.IntervalUnit.SECOND))
                .build();


        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void scheduleOneTimeJobs(String name, String group, int delayMinutes) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(SampleJob.class)
                .withIdentity(name, group)
                .build();

        Date startTime = Date.from(Instant.now().plus(delayMinutes, ChronoUnit.MINUTES));

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(name + "Trigger", group)
                .startAt(startTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }


    public List<JobKey> listJobs() throws SchedulerException {
        List<JobKey> keys = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            keys.addAll(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)));
        }
        return keys;
    }
}
