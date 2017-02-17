package net.eekie.metrics;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
    properties = {"scheduledtasks.addremoveanimals.fixedRate:100", "logging.level.net.eekie.metrics: DEBUG"})
public class MetricIntegrationIT {

    @Rule
    public OutputCapture capture = new OutputCapture();

    @Test
    public void smokeIT() throws InterruptedException {
        Thread.sleep(700);  // allow the scheduled task to add/remove animals every 100 millis
        assertThat(capture.toString(), containsString("metric captured for animal"));
    }

}
