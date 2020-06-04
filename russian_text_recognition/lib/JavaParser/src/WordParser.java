import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class WordParser {

    static final int BORDER = 50;
    static final int[][] steps = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

    private static Common.Pair<Common.Pair<Common.Point, Common.Point>, ArrayList<Common.Point>> maxDistance(boolean[][] g, boolean[][] used, int w, int h, int x, int y) {
        Common.Point max = new Common.Point(x, y);
        Common.Point min = new Common.Point(x, y);
        ArrayDeque<Common.Point> deque = new ArrayDeque<>();
        deque.add(new Common.Point(x, y));
        used[x][y] = true;
        ArrayList<Common.Point> visited = new ArrayList<>();
        while (!deque.isEmpty()) {
            var point = deque.pollFirst();
            visited.add(point);
            used[point.x][point.y] = true;
            max.x = Math.max(max.x, point.x);
            max.y = Math.max(max.y, point.y);
            min.x = Math.min(min.x, point.x);
            min.y = Math.min(min.y, point.y);

            for (int[] step : new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}}) {
                int nx = point.x + step[0];
                int ny = point.y + step[1];
                if (nx < 0 || ny < 0 || nx >= w || ny >= h || used[nx][ny] || !g[nx][ny]) continue;
                used[nx][ny] = true;
                deque.add(new Common.Point(nx, ny));
            }
        }
        return new Common.Pair<>(new Common.Pair<>(min, max), visited);
    }

    private static void RemovePixelsFromAnotherLines(ArrayList<Common.Point> line) {
        int minx = line.get(0).x;
        int miny = line.get(0).y;
        int maxx = minx;
        int maxy = miny;
        for (var point : line) {
            maxx = Math.max(point.x, maxx);
            maxy = Math.max(point.y, maxy);
            minx = Math.min(point.x, minx);
            miny = Math.min(point.y, miny);
        }
        int w = maxx - minx + 1;
        int h = maxy - miny + 1;
        for (var point : line) {
            point.x -= minx;
            point.y -= miny;
        }
        boolean[][] arr = new boolean[w][h];
        boolean[][] used = new boolean[w][h];
        for (var point : line) {
            arr[point.x][point.y] = true;
        }
        ArrayList<Common.Point> remove = new ArrayList<>();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (Math.min(y, h - 1 - y) * 1.0 / h > 0.05) continue;
                if (!used[x][y] && arr[x][y]) {
                    var minmax = maxDistance(arr, used, w, h, x, y);
                    var minPoint = minmax.a.a;
                    var maxPoint = minmax.a.b;
                    var visited = minmax.b;
                    if (maxPoint.y - minPoint.y < h / 2) {
                        remove.addAll(visited);
                    }
                }
            }
        }
        for (var point : remove) {
            line.removeIf(a -> (a.x == point.x && a.y == point.y));
        }
    }

    public static ArrayList<ArrayList<Common.Point>> Parse(ArrayList<Common.Point> line) {
        RemovePixelsFromAnotherLines(line);
        Collections.sort(line);
        int n = line.size();
        var dsu = new Common.DSU(n);
        var map = new TreeMap<Common.Point, Integer>();
        {
            int index = 0;
            for (var point : line) {
                map.put(point, index++);
            }
        }

        for (int i = 0; i < n; i++) {
            var point = line.get(i);
            for (int[] step : steps) {
                var nextPoint = new Common.Point(step[0] + point.x, step[1] + point.y);
                if (map.containsKey(nextPoint)) {
                    int nextIndex = map.get(nextPoint);
                    dsu.uni(nextIndex, i);
                }
            }
        }

        var lettersMap = new TreeMap<Integer, ArrayList<Common.Point>>();

        for (int i = 0; i < n; i++) {
            int parent = dsu.get(i);
            var currentPixels = lettersMap.getOrDefault(parent, new ArrayList<>());
            currentPixels.add(line.get(i));
            lettersMap.put(parent, currentPixels);
        }

        ArrayList<ArrayList<Common.Point>> letters = new ArrayList<>();
        for (var parent : lettersMap.keySet()) {
            var letter = lettersMap.get(parent);
            if (letter.size() >= BORDER) {
                letters.add(letter);
            }
        }

        int m = letters.size();
        if (m == 0) {
            return new ArrayList<>();
        }
        ArrayList<Integer> distances = new ArrayList<>();
        int[] distToNext = new int[m - 1];
        for (int i = 1; i < m; i++) {
            int dist = (int) 1e9;
            for (var cur : letters.get(i)) {
                for (var pre : letters.get(i - 1)) {
                    dist = Math.min(dist, (cur.x - pre.x) * (cur.x - pre.x) + (cur.y - pre.y) * (cur.y - pre.y));
                }
            }
            distToNext[i - 1] = dist;
            distances.add(dist);
        }
        Collections.sort(distances);

        int distanceBetweenWords = 0;
        for (int i = 0; i < distances.size() - 1; i++) {
            if (distances.get(i) * 3 <= distances.get(i + 1)) {
                distanceBetweenWords = distances.get(i) + 1;
                break;
            }
        }

        ArrayList<ArrayList<Common.Point>> answer = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            answer.add(letters.get(i));
            if (i + 1 < m && distToNext[i] >= distanceBetweenWords) {
                answer.add(new ArrayList<>());
            }
        }
        return answer;

    }
}
