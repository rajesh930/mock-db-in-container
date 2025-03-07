package com.ontic.perf.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

import static com.ontic.perf.tracker.PerfStats.perfStatsHolder;

/**
 * @author rajesh
 * @since 22/01/25 11:20
 */
public class Perf implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(Perf.class);

    public static final int MAX_NODES = 1000; //to protect user mistake in case not out/close is made

    static final Perf DUMMY = new Perf(null, null) {
        @Override
        public void out() {
        }

        @Override
        public void close() {
        }
    };

    final String tag;
    final Perf parent;
    LinkedHashMap<String, Perf> children;
    long lastInTime;
    long totalTimeNanos = 0;
    int calls = 0;

    Perf(String tag, Perf parent) {
        this.tag = tag;
        this.parent = parent;
    }

    void start() {
        this.lastInTime = System.nanoTime();
    }

    void end() {
        this.totalTimeNanos += (System.nanoTime() - lastInTime);
        this.calls++;
    }

    public static Perf in(String tag) {
        try {
            return _safeIn(tag);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            return DUMMY;
        }
    }

    public static Perf inDB(String dbType, String tag) {
        try {
            PerfStats perfStats = perfStatsHolder.get();
            if (perfStats == null) {
                return DUMMY;
            }
            perfStats.incDbCalls(dbType);
            return _safeIn(dbType + "_" + tag);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            return DUMMY;
        }
    }

    private static Perf _safeIn(String tag) {
        PerfStats perfStats = perfStatsHolder.get();
        if (perfStats == null || perfStats.total >= MAX_NODES) {
            return DUMMY;
        }
        Perf parent = perfStats.current;
        if (parent.children == null) {
            parent.children = new LinkedHashMap<>();
        }
        Perf in = parent.children.get(tag);
        if (in == null) {
            in = new Perf(tag, parent);
            perfStats.total++;
            parent.children.put(tag, in);
        }
        perfStats.current = in;
        in.start();
        return in;
    }


    public void out() {
        try {
            PerfStats perfStats = perfStatsHolder.get();
            if (perfStats == null) {
                return;
            }
            this.end();
            perfStats.current = this.parent;
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    @Override
    public void close() {
        out();
    }
}
