import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import protocol.TokenRing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class LoadTest {
    public static Writer output;

    @BeforeAll
    public static void init() throws IOException {
        output = new FileWriter(new File("report/messagesInRing.csv"));
        output.write("Messages;Value;Err\n");
    }

    @ParameterizedTest
    @ValueSource(ints = {0,10,100,1000,10000,100000,1000000,10000000})
    public void test(int messagesForLoad) throws InterruptedException, IOException {
        TokenRing<String> tokenRing = new TokenRing<>(8);
        tokenRing.start();
        for(int i = 0; i < messagesForLoad; i++) {
            tokenRing.requestFromRandomNodeWithPayload("PAYLOAD");
        }
        for(int i = 0; i < 1000; i++) {
            tokenRing.messageRequest().from(1L).to(0L).with("PAYLOAD").execute();
            Thread.sleep(50);
        }
        Thread.sleep(5000);
        tokenRing.close();
        System.out.println(tokenRing.getMetricsList().size());
        List<Long> metrics = tokenRing.getMetricsList();
        double value = metrics.stream().mapToDouble(i->i).sum() / metrics.size();
        double err = metrics.stream().mapToDouble(i -> i*i - value*value).sum() / metrics.size();
        output.write(messagesForLoad + ";" + value + ";" + err +"\n");
    }

    @AfterAll
    public static void finish() throws IOException {
        output.close();
    }
}
