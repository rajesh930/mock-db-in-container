package com.ontic.perf.test;

import com.ontic.perf.aspect.Track;
import com.ontic.perf.tracker.Perf;
import com.ontic.perf.tracker.PerfStats;
import org.junit.jupiter.api.Test;

/**
 * @author rajesh
 * @since 23/01/25 11:26
 */
public class PerfTest {

    @Test
    public void testPerfStats() throws Exception {
        PerfStats perfStats = PerfStats.startNew("Testing");
        try {
            method1("aefsd");
            methodLight();
            method2("aefsd", "knfkl", "jfbjkfb");
            method2("aefsd", "knfkl", "jfbjkfb");
            method2("aefsd", "knfkl", "jfbjkfb");
            method2("aefsd", "knfkl", "jfbjkfb");
            try (Perf ignored = Perf.inDB("mongo", "db_collection")) {
                Thread.sleep(100);
            }
            try (Perf ignored = Perf.inDB("es", "index")) {
                Thread.sleep(120);
            }
            try (Perf ignored = Perf.inDB("mongo", "db_collection")) {
                Thread.sleep(100);
            }
            try (Perf ignored = Perf.inDB("es", "index")) {
                Thread.sleep(120);
            }
            try (Perf ignored = Perf.inDB("mongo", "db_collection")) {
                Thread.sleep(100);
            }
            try (Perf ignored = Perf.inDB("es", "es_index")) {
                Thread.sleep(120);
            }
            nested(1);
        } finally {
            perfStats.stop();
        }
        if (perfStats.dbCalls() > 2 || perfStats.timeTakenMillis() > 1000) {
            System.out.print(perfStats);
        }
        System.out.println("done");
    }

    @Track("m1")
    private void method1(String asd) throws Exception {
        System.out.println(asd);
        method3("m3", "afljf", "sldjkd");
    }

    @Track
    private void methodLight() {
    }

    @Track
    private void method2(String a, String b, String c) throws Exception {
        System.out.println("method2 " + a + " " + b + " " + c);
        method3("m3", "afljf", "sldjkd");
        Thread.sleep(100);
    }

    @Track("m3")
    private void method3(String a, String b, String c) throws Exception {
        System.out.println("method2 " + a + " " + b + " " + c);
        Thread.sleep(100);
    }

    @Track()
    private void nested(int i) {
        if (i <= 993) {
            nested(i + 1);
        }
    }
}
