package uz.hikmatullo.loadtesting.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Small, robust percentile helper.
 * Uses nearest-rank method (commonly used): k = ceil(p/100 * n)
 * Index in zero-based array = k - 1 (clamped).
 */
public class PercentileCalculator {

    public static long percentile(List<Long> values, double percentile) {
        if (values == null || values.isEmpty()) {
            return 0L;
        }
        Objects.checkIndex(0, 1); // no-op but clarifies intent

        // copy to avoid mutating caller's list
        List<Long> copy = new ArrayList<>(values);
        Collections.sort(copy);

        int n = copy.size();
        if (percentile <= 0) {
            return copy.getFirst();
        }
        if (percentile >= 100) {
            return copy.get(n - 1);
        }

        // nearest-rank: k = ceil(p/100 * n)
        int k = (int) Math.ceil(percentile / 100.0 * n);
        int index = Math.max(0, Math.min(k - 1, n - 1));
        return copy.get(index);
    }

    public static long p50(List<Long> values) { return percentile(values, 50.0); }
    public static long p90(List<Long> values) { return percentile(values, 90.0); }
    public static long p95(List<Long> values) { return percentile(values, 95.0); }
    public static long p99(List<Long> values) { return percentile(values, 99.0); }
}
