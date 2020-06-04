import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


public class Filter {
    final static int BORDER = 15;
    final static int DISTANCE = 1;

    public static int[][] RemoveBadPixels(int[][] a, int n, int m) {
        var dsu = new Common.DSU(n * m);
        int[][] ans = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (a[i][j] != 0) {
                    continue;
                }
                for (int dx = -DISTANCE; dx <= DISTANCE; dx++) {
                    for (int dy = -DISTANCE; dy <= DISTANCE; dy++) {
                        int x = i + dx;
                        int y = j + dy;
                        if (x < 0 || y < 0 || x >= n || y >= m) continue;
                        if (a[x][y] == 0) {
                            dsu.uni(i * m + j, x * m + y);
                        }
                    }
                }
            }
        }
        int counter = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (dsu.d[dsu.get(i * m + j)] <= BORDER || a[i][j] == 0xffffff) {
                    ans[i][j] = 0xffffff;
                    counter++;
                } else {
                    ans[i][j] = 0;
                }
            }
        }
        ans[0][0] = counter;
        return ans;
    }

    public static HashMap<Integer, Integer> KMeans(HashMap<Integer, Integer> map, int k, double precision) {
        int n = map.size();
        int[] red = new int[n];
        int[] green = new int[n];
        int[] blue = new int[n];
        int[] count = new int[n];
        int[] type = new int[n];
        int[] ansRed = new int[k];
        int[] ansGreen = new int[k];
        int[] ansBlue = new int[k];

        ArrayList<Integer>[] arr = new ArrayList[k];

        for (int i = 0; i < k; i++) {
            int color = (int) (ImageLibrary.WHITE * Math.random());
            ansRed[i] = ImageLibrary.GetR(color);
            ansGreen[i] = ImageLibrary.GetG(color);
            ansBlue[i] = ImageLibrary.GetB(color);
            arr[i] = new ArrayList();
        }

        {
            int index = 0;
            for (int color : map.keySet()) {
                count[index] = map.get(color);
                red[index] = ImageLibrary.GetR(color);
                green[index] = ImageLibrary.GetG(color);
                blue[index] = ImageLibrary.GetB(color);

                int dist = (int) 1e9;

                for (int i = 0; i < k; i++) {
                    int currentDistance = ImageLibrary.Distance(red[index] - ansRed[i], green[index] - ansGreen[i], blue[index] - ansBlue[i]);
                    if (dist > currentDistance) {
                        dist = currentDistance;
                        type[index] = i;
                    }
                }
                arr[type[index]].add(index);
                index++;
            }
        }

        int countAllPixels = 0;
        int delta = 100;
        while (delta > 3 * k) {
            delta = 0;
            countAllPixels = 0;
            for (int i = 0; i < k; i++) {
                int dr = 0;
                int dg = 0;
                int db = 0;
                int counter = 0;
                for (int index : arr[i]) {
                    dr += (red[index] - ansRed[i]) * count[index];
                    dg += (green[index] - ansGreen[i]) * count[index];
                    db += (blue[index] - ansBlue[i]) * count[index];
                    counter += count[index];
                }
                countAllPixels += counter;
                if (arr[i].isEmpty()) continue;
                ansRed[i] += dr / counter;
                ansGreen[i] += dg / counter;
                ansBlue[i] += db / counter;
                delta += (Math.abs(dr) + Math.abs(dg) + Math.abs(db)) / (3 * counter);
                arr[i].clear();
            }

            for (int index = 0; index < n; index++) {
                int dist = (int) 1e9;
                for (int i = 0; i < k; i++) {
                    int currentDistance = ImageLibrary.Distance(red[index] - ansRed[i], green[index] - ansGreen[i], blue[index] - ansBlue[i]);
                    if (dist > currentDistance) {
                        dist = currentDistance;
                        type[index] = i;
                    }
                }
                arr[type[index]].add(index);
            }
        }
        HashMap<Integer, Integer> answer = new HashMap<>();

        int[] totalCount = new int[k];
        for (int i = 0; i < n; i++) {
            totalCount[type[i]] += count[i];
        }

        for (int i = 0; i < n; i++) {
            int color = ImageLibrary.GetColor(red[i], green[i], blue[i]);
            int j = type[i];

            int resultColor = ImageLibrary.GetColor(ansRed[j], ansGreen[j], ansBlue[j]);
            if (totalCount[j] >= precision * countAllPixels) {
                resultColor = ImageLibrary.WHITE;
            }
            answer.put(color, resultColor);
        }


        return answer;
    }

    public static double[][] BlurImage(int[][] alpha, int w, int h) {

        double[][] bluer = new double[w][h];
        int delta = 30;

        int[][] sum = new int[w + delta * 2][h + delta * 2];
        for (int i = 0; i < w + delta * 2; i++) {
            for (int j = 0; j < h + delta * 2; j++) {
                int x = Math.max(0, Math.min(w - 1, i - delta));
                int y = Math.max(0, Math.min(h - 1, j - delta));
                sum[i][j] = alpha[x][y];
                if (j > 0) {
                    sum[i][j] += sum[i][j - 1];
                }
            }
            if (i > 0) {
                for (int j = 0; j < h + delta * 2; j++) {
                    sum[i][j] += sum[i - 1][j];
                }
            }
        }
        int S = (delta * 2 + 1) * (delta * 2 + 1);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int x2 = i + delta * 2;
                int y2 = j + delta * 2;
                bluer[i][j] = sum[x2][y2];
                if (i > 0) bluer[i][j] -= sum[i - 1][y2];
                if (j > 0) bluer[i][j] -= sum[x2][j - 1];
                if (i > 0 && j > 0) bluer[i][j] += sum[i - 1][j - 1];
                bluer[i][j] /= S;
            }
        }
        return bluer;
    }

    public static int CountCells(int[][] image, int w, int h) {
        int ans = 0;
        for (int i = 0; i < w - 1; i++) {
            for (int j = 0; j < h - 1; j++) {
                if (image[i][j] == image[i][j + 1] && image[i + 1][j] == image[i + 1][j + 1]) {
                    if (image[i][j] != image[i + 1][j]) {
                        ans++;
                    }
                }
            }
        }
        return ans;
    }

    public static int[][] MakeFilter(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        int[][] alpha = new int[w][h];
        int[][] image = new int[w][h];
        int brightness = 40;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                image[i][j] = img.getRGB(i, j);
                int r = Math.min(255, ImageLibrary.GetR(image[i][j]) + brightness);
                int g = Math.min(255, ImageLibrary.GetG(image[i][j]) + brightness);
                int b = Math.min(255, ImageLibrary.GetB(image[i][j]) + brightness);
                image[i][j] = ImageLibrary.GetColor(r, g, b);
                alpha[i][j] = (r + g + b) / 3;
            }
        }

        double[][] coefficients = BlurImage(alpha, w, h);

        HashMap<Integer, Integer> map = new HashMap<>();
        int[][] pixels2 = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int color = image[x][y];
                int r = Math.min(255, (int) (ImageLibrary.GetR(color) * 200 / coefficients[x][y]));
                int g = Math.min(255, (int) (ImageLibrary.GetG(color) * 200 / coefficients[x][y]));
                int b = Math.min(255, (int) (ImageLibrary.GetB(color) * 200 / coefficients[x][y]));

                pixels2[x][y] = ImageLibrary.GetColor(r, g, b);
                map.put(pixels2[x][y], map.getOrDefault(pixels2[x][y], 0) + 1);
            }
        }

        int ansCount = (int) 1e9;
        int[][] answer = pixels2.clone();
        int k = 7;
        int present = 15;
        for (int iteration = 0; iteration < 10; iteration++) {
            HashMap<Integer, Integer> result = KMeans(map, k, present * 0.01);
            int[][] pixels = new int[w][h];

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    pixels[x][y] = result.getOrDefault(pixels2[x][y], 0);

                    if (pixels[x][y] != ImageLibrary.WHITE) {
                        pixels[x][y] = ImageLibrary.BLACK;
                    }

                }
            }

            int value = CountCells(pixels, w, h);
            if (ansCount > value) {
                ansCount = value;
                answer = pixels.clone();
            }
        }

        answer = RemoveBadPixels(answer, w, h);
        return answer;
    }
}

