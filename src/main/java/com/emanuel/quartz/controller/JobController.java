package com.emanuel.quartz.controller;

import com.emanuel.quartz.service.JobService;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/run-once")
    public ResponseEntity<String> runJobOnce(@RequestParam String name,
                                             @RequestParam(defaultValue = "default") String group) {
        try {
            jobService.scheduleOneTimeJob(name, group);
            return ResponseEntity.ok("✅ Job agendado para execução imediata.");
        } catch (SchedulerException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/run-in-time")
    public ResponseEntity<String> runJobOnce(@RequestParam String name,
                                             @RequestParam(defaultValue = "default") String group,
                                             @RequestParam(defaultValue = "1") int delayMinutes) {
        try {
            jobService.scheduleOneTimeJobs(name, group, delayMinutes);
            return ResponseEntity.ok("✅ Job agendado para daqui " + delayMinutes + " minuto(s).");
        } catch (SchedulerException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }


    @GetMapping()
    public ResponseEntity<List<String>> listJobs() {
        try {
            List<String> jobs = jobService.listJobs()
                    .stream()
                    .map(JobKey::toString)
                    .toList();
            return ResponseEntity.ok(jobs);
        } catch (SchedulerException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
