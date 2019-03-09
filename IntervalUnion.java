import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class IntervalUnion {

    /** Inner class to represent a single interval. */
    private static class Interval {
        final int start;
        final int end;

        public Interval(int start, int end) {
            if (start > end)
                throw new IllegalArgumentException();
            this.start = start;
            this.end = end;
        }

        /** Checks for overlap. */
        public boolean overlaps(Interval other) {
            return (start <= other.end && other.start <= end);
        }

        /** Checks for adjacency. */
        public boolean adjacent(Interval other) {
            return (this.end + 1 == other.start) ||
                   (other.end + 1 == this.start);
        }

        /** Returns the union of two overlapping/adjacent intervals. */
        public Interval union(Interval other) {
            if (!overlaps(other) && !adjacent(other))
                throw new IllegalArgumentException();
            if (start <= other.start && end >= other.end)
                return this;
            if (start >= other.start && end <= other.end)
                return other;
            return new Interval(Math.min(start, other.start),
                                Math.max(end, other.end));
        }

        /** Returns the intersection of two intervals. */
        public Interval intersection(Interval other) {
            if (!overlaps(other))
                throw new IllegalArgumentException();
            if (start <= other.start && end >= other.end)
                return other;
            if (start >= other.start && end <= other.end)
                return this;
            return new Interval(Math.max(start, other.start),
                                Math.min(end, other.end));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Interval)) return false;
            Interval i = (Interval) o;
            return (start == i.start && end == i.end);
        }

        @Override
        public int hashCode() {
            return 43 * start + end;
        }

        @Override
        public String toString() {
            if (start == end)
                return "" + start;
            return start + "-" + end;
        }
    }


    /** Comparator instance that considers overlapping intervals equals. */
    private static final Comparator<Interval> cmp = (o1, o2) -> {
        if (o1.overlaps(o2))
            return 0;
        return Integer.compare(o1.start, o2.start);
    };


    /** List of sorted disjoint intervals contained in this IntervalUnion. */
    private final ArrayList<Interval> intervals;


    private IntervalUnion(ArrayList<Interval> intervals) {
        this.intervals = intervals;
    }


    private IntervalUnion(Interval interval) {
        this(new ArrayList<>());
        this.intervals.add(interval);
    }


    private IntervalUnion() {
        this(new ArrayList<>());
    }


    /** Run a O(log n) binary search for a point interval containing x. */
    public boolean contains(int x) {
        Interval pointInterval = new Interval(x, x);
        int index = Collections.binarySearch(intervals, pointInterval, cmp);
        return index >= 0;
    }


    /** The union runs in a similar way to the merge step of mergesort.
      * The intervals are extracted from both IntervalUnion object in order,
      * and merged when necessary. */
    public IntervalUnion union(IntervalUnion other) {
        IntervalUnion result = new IntervalUnion(new ArrayList<>());
        int i1 = 0, i2 = 0;
        Interval current = null;

        while (i1 < intervals.size() || i2 < other.intervals.size()) {
            Interval next1 = (i1 < intervals.size()
                    ? intervals.get(i1) : null);
            Interval next2 = (i2 < other.intervals.size()
                    ? other.intervals.get(i2) : null);
            Interval next;
            if (next1 == null) {
                next = next2;
                i2++;
            } else if(next2 == null) {
                next = next1;
                i1++;
            } else {
                if (next2.start < next1.start) {
                    next = next2;
                    i2++;
                } else {
                    next = next1;
                    i1++;
                }
            }

            /* If there is no current interval, next becomes it.
             * If there is a current interval, then:
             * - If it overlaps with next, merge them.
             * - If not, add current to the result and set it to next. */
            if (current == null) {
                current = next;
            } else if (current.overlaps(next) || current.adjacent(next)) {
                current = current.union(next);
            } else {
                result.intervals.add(current);
                current = next;
            }
        }

        if (current != null)
           result.intervals.add(current);

        return result;
    }


    /** The intersection is calculated running through both IntervalUnion's
      * intervals in parallel. When the two intervals intersect, their
      * intersection is added to the result; at each iteration, the interval
      * that ends sooner is advanced to the next in its union. */
    public IntervalUnion intersection(IntervalUnion other) {
        IntervalUnion result = new IntervalUnion(new ArrayList<>());
        int i1 = 0, i2 = 0;

        while (i1 < intervals.size() && i2 < other.intervals.size()) {
            Interval next1 = intervals.get(i1);
            Interval next2 = other.intervals.get(i2);

            if (next1.overlaps(next2)) {
                result.intervals.add(next1.intersection(next2));
            }

            if (next1.end >= next2.end)
                i2++;
            else
                i1++;
        }

        return result;
    }


    public int getPieceCount() {
        return intervals.size();
    }


    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < intervals.size(); i++) {
            sb.append(intervals.get(i));
            if (i < intervals.size() - 1)
                sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }


    @Override public boolean equals(Object other) {
        if (other == null || !(other instanceof IntervalUnion))
            return false;
        IntervalUnion union = (IntervalUnion) other;
        return (intervals.equals(union.intervals));
    }


    @Override public int hashCode() {
        return intervals.hashCode();
    }


    public static IntervalUnion create(int start, int end) {
        return new IntervalUnion(new Interval(start, end));
    }


    public static String getAuthorName() {
        return "Khatter, Joy";
    }


    public static String getRyersonID() {
        return "500866988";
    }

}
