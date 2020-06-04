import java.io.IOException;
import java.util.*;

public class LineParser {
    public static ArrayList<ArrayList<Common.Point>> Parse(int[][] pixels) {
        int w = pixels.length;
        int h = pixels[0].length;
        int[] counter = new int[h];
        for (int[] pixel : pixels) {
            for (int j = 0; j < h; j++) {
                if (pixel[j] != ImageLibrary.WHITE) {
                    counter[j]++;
                }
            }
        }

        int present = 5;
        boolean[] isLine = new boolean[h];
        for (int i = 0; i < h; i++) {
            isLine[i] = counter[i] >= w * 0.01 * present;
        }

        ArrayList<Integer> lines = new ArrayList<>();
        int pre = h;
        int border = 16;
        for (int i = 0; i < h; i++) {
            if (isLine[i]) {
                if (pre == h) {
                    pre = i;
                }
            } else {
                if (i - pre >= border) {
                    lines.add((i + pre) / 2);
                }
                pre = h;
            }
        }
        if (lines.size() == 0) {
            return new ArrayList<>();
        } else if (lines.size() == 1) {
            ArrayList<Common.Point> ans = new ArrayList<>();

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    if (pixels[x][y] != ImageLibrary.WHITE) {
                        ans.add(new Common.Point(x, y));
                    }
                }
            }
            return new ArrayList<>(Collections.singleton(ans));
        }
        int cnt = lines.size() - 1;
        int sum = 0;
        for (int i = 0; i < cnt; i++) {
            sum += lines.get(i + 1) - lines.get(i);
        }
        int avg = sum / cnt / 2;
        ArrayList<ArrayList<Common.Point>> ans = new ArrayList<>();
        for (int y : lines) {
            ArrayList<Common.Point> cur = new ArrayList<>();
            for (int cy = Math.max(0, y - avg); cy <= Math.min(y + avg, h - 1); cy++) {
                for (int x = 0; x < w; x++) {
                    if (pixels[x][cy] != ImageLibrary.WHITE) {
                        cur.add(new Common.Point(x, cy));
                    }
                }
            }
            ans.add(cur);
        }
        return ans;
    }
}
