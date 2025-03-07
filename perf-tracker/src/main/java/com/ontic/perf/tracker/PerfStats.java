package com.ontic.perf.tracker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rajesh
 * @since 22/01/25 21:20
 */
public class PerfStats {
    static final ThreadLocal<PerfStats> perfStatsHolder = new ThreadLocal<>();

    private static final PerfStats ROOT = new PerfStats("_root_") {
        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }
    };

    private final PerfStats parent;

    final String heading;
    final Perf root;
    Perf current;
    short total;
    private short dbCalls;
    Map<String, Short> dbTypeVsCalls = new HashMap<>();

    private PerfStats(String heading) {
        this.heading = heading;

        PerfStats previous = perfStatsHolder.get();
        parent = previous != null ? previous : ROOT;

        root = new Perf("_root_", Perf.DUMMY);
        current = root;
    }

    public static PerfStats startNew(String heading) {
        PerfStats perfStats = new PerfStats(heading);
        perfStats.start();
        return perfStats;
    }

    void start() {
        perfStatsHolder.set(this);
        root.start();
    }

    public void stop() {
        if (parent != ROOT) {
            parent.start();
        } else {
            perfStatsHolder.remove();
        }
        root.end();
    }

    void incDbCalls(String dbType) {
        dbCalls++;
        dbTypeVsCalls.merge(dbType, (short) 1, (old, given) -> ++old);
    }

    public int dbCalls() {
        return dbCalls;
    }

    public long timeTakenMillis() {
        return root.totalTimeNanos / 1_000_000;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(heading).append(" : ").append("DB Calls=").append(dbCalls)
                .append(", Total Time=").append(root.totalTimeNanos / 1_000_000).append(" ms")
                .append(total >= Perf.MAX_NODES ? ", **Warning** too many nodes, perf stats may be wrong" : "")
                .append('\n');
        if (root.children != null) {
            for (Perf child : root.children.values()) {
                printTree(sb, child, 1);
            }
        }
        return sb.toString();
    }

    private void printTree(StringBuilder sb, Perf perf, int depth) {
        sb.append(" ".repeat(Math.max(0, depth)))
                .append(perf.tag)
                .append(" : ").append(perf.totalTimeNanos / 1_000_000).append(" ms")
                .append(" : ").append(perf.calls).append('\n');
        if (perf.children == null) {
            return;
        }
        for (Perf child : perf.children.values()) {
            printTree(sb, child, depth + 1);
        }
    }
}
