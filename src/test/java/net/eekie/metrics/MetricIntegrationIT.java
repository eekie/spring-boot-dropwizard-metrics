package net.eekie.metrics;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@IntegrationTest({"scheduledtasks.addremoveanimals.fixedRate:100", "logging.level.net.eekie.metrics: DEBUG"})
public class MetricIntegrationIT {

    @Rule
    public OutputCapture capture = new OutputCapture();

    @Test
    public void smokeIT() throws InterruptedException {
        Thread.sleep(700);  // allow the scheduled task to add/remove animals every 100 millis
        assertThat(capture.toString(), containsString("metric captured for animal"));
    }

}
