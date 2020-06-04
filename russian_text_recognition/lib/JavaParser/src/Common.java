import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class Common {
    static class DSU {
        int n;
        int[] p;
        int[] d;

        DSU(int n) {
            this.n = n;
            this.p = new int[n];
            this.d = new int[n];
            for (int i = 0; i < n; i++) {
                p[i] = i;
                d[i] = 1;
            }
        }

        int get(int v) {
            if (p[v] == v) return v;
            p[v] = get(p[v]);
            return p[v];
        }

        void uni(int a, int b) {
            a = get(a);
            b = get(b);
            if (a == b) return;
            if (d[a] < d[b]) {
                int c = a;
                a = b;
                b = c;
            }
            d[a] += d[b];
            p[b] = a;
        }

        @Override
        public String toString() {
            return "DSU{" +
                    "n=" + n +
                    ", p=" + Arrays.toString(p) +
                    ", d=" + Arrays.toString(d) +
                    '}';
        }
    }


    static public class Point implements Comparable<Point> {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Point o) {
            if (x == o.x) return Integer.compare(y, o.y);
            return Integer.compare(x, o.x);
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    static public class Pair<T1, T2> {
        T1 a;
        T2 b;

        Pair(T1 a, T2 b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
    }
}
