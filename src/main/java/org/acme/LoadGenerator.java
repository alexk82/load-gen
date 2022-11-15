package org.acme;

import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

@ApplicationScoped
public class LoadGenerator {

    @ConfigProperty(name = "stress_time_ms", defaultValue = "100")
    Integer stressTimeMs;

    @Scheduled(cron = "0/1 * * * * ?")
    void doSomething(ScheduledExecution execution) throws NoSuchAlgorithmException {
        Log.info(String.format("running something %s", execution.getFireTime()));

        Random r = new Random();

        long startTime = System.nanoTime();
        long endtime = startTime + (stressTimeMs * 1000 * 1000); // 100ms

        long count = 0;
        while (System.nanoTime() < endtime) {
            byte[] b = new byte[20];
            r.nextBytes(b);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hash = Base64.getEncoder().encodeToString(digest.digest(b));
            Log.trace(String.format("calculated hash %s", hash));
            count++;
        }

        Log.info(String.format("calculated %d hashes in %d ms", count, (System.nanoTime() - startTime) / 1000 / 1000));
    }
}
