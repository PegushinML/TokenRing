import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import protocol.TokenRing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class NodesAmountTest {

    public static Writer output;
    public TokenRing<String> tokenRing;

    @BeforeAll
    public static void init() throws IOException {
        output = new FileWriter(new File("report/nodesAmount.csv"));
        output.write("NumNodes;Value;Err\n");
    }

    @ParameterizedTest
    @ValueSource(ints = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35})
    public void test(int numNodes) throws InterruptedException, IOException {
        TokenRing<String> tokenRing = new TokenRing<>(numNodes);
        tokenRing.start();
        for(int i = 0; i < 10000; i++) {
            tokenRing.requestFromRandomNodeWithPayload("PAYLOAD");
        }
        for(int i = 0; i < 1000; i++) {
            tokenRing.messageRequest().from(1L).to(0L).with("PAYLOAD").execute();
        }
        Thread.sleep(5000);
        tokenRing.close();
        System.out.println(tokenRing.getMetricsList().size());
        List<Long> metrics = tokenRing.getMetricsList();
        double value = metrics.stream().mapToDouble(i->i).sum() / metrics.size();
        double err = metrics.stream().mapToDouble(i -> i*i - value*value).sum() / metrics.size();
        output.write(numNodes + ";" + value + ";" + err +"\n");

    }

    @AfterAll
    public static void finish() throws IOException {
        output.close();
    }
}
