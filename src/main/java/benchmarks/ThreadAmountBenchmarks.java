package benchmarks;

import org.openjdk.jmh.annotations.*;
import protocol.TokenRing;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class ThreadAmountBenchmarks {

    //@Param({"2","4","6","8","10","12","14","16","18","20","25","32"})
    /*@Param({"2", "30"})
    public int nodesNum;*/

    public TokenRing<String> tokenRing;

    /*@Setup(Level.Iteration)
    public void prepare() {
        tokenRing = new TokenRing<>(nodesNum);
        tokenRing.start();
        for(int i = 0; i < 1000000; i++) {
            tokenRing.requestFromRandomNodeWithPayload("PAYLOAD");
        }
    }*/

    public int yuvu() {
        int result = 0;
        for(int i = 0; i < 20; i++) {
            Thread thread = new Thread(() -> {
                int j = 0;
                while(j < 100) {
                    j++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            result++;
        }
        return result;
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    //@OutputTimeUnit(TimeUnit.SECONDS)
    public void timeBench() {
        tokenRing = new TokenRing<>(10);
        tokenRing.start();
        for(int i = 0; i < 1000000; i++) {
            tokenRing.requestFromRandomNodeWithPayload("PAYLOAD");
        }
    }

   /* @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void throughputBench() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    /*@TearDown(Level.Iteration)
    public void finish() {
        tokenRing.close();
    }*/

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ThreadAmountBenchmarks.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                //.threadGroups(30)
                //.output("threadAmountBenchmarks.txt")
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
