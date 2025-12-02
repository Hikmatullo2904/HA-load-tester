package uz.hikmatullo.loadtesting.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PercentileCalculator {

    /**
     * Nearest-rank percentile:
     * p in [0,100]
     */
    public static long percentile(List<Long> values, double p) {
        if (values == null || values.isEmpty()) {
            return 0L;
        }

        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        int n = sorted.size();

        // Clamp percentile to [0,100]
        if (p <= 0.0) return sorted.getFirst();
        if (p >= 100.0) return sorted.get(n - 1);

        // nearest-rank: k = ceil(p/100 * n)
        int k = (int) Math.ceil((p / 100.0) * n);
        int index = k - 1;

        return sorted.get(index);
    }

    public static long p50(List<Long> values)  { return percentile(values, 50); }
    public static long p90(List<Long> values)  { return percentile(values, 90); }
    public static long p95(List<Long> values)  { return percentile(values, 95); }
    public static long p99(List<Long> values)  { return percentile(values, 99); }
}
