import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import protocol.TokenRing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DistributionTest {
    public static Writer output;

    @BeforeAll
    public static void init() throws IOException {
        output = new FileWriter(new File("report/messagesDistr.csv"));
        output.write("Index;Node-0;Node-1;Node-2;Node-3;Node-4;Node-5;Node-6;Node-7;\n");
    }

    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9})
    public void test(int index) throws InterruptedException, IOException {
        TokenRing<String> tokenRing = new TokenRing<>(8);
        tokenRing.start();
        for(int i = 0; i < 1000000; i++) {
            tokenRing.requestFromRandomNodeWithPayload("PAYLOAD");
        }
        Thread.sleep(5000);
        tokenRing.close();
        output.write(index + ";" + tokenRing.getNodeDistr() +"\n");
    }

    @AfterAll
    public static void finish() throws IOException {
        output.close();
    }
}
