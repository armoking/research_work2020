import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

public class ImageLibrary {
    public static int BLACK = 0x000000;
    public static int WHITE = 0xffffff;

    public static BufferedImage ReadImage(String filename) {
        BufferedImage img = null;
        File f;
        for (String format : new String[]{"", ".png ", ".jpg ", ".jpeg ", ".bmp "}) {
            try {
                String fullPath = filename + format;
                f = new File(fullPath);
                return ImageIO.read(f);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static int[][] GenerateImage(ArrayList<Common.Point> points, int color, boolean force) {
        if (points.size() == 0) {
            if (force) {
                int[][] ans = new int[10][10];
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        ans[i][j] = WHITE;
                    }
                }
                return ans;
            }
            return null;
        }
        int minX = points.get(0).x;
        int minY = points.get(0).y;
        for (int i = 1; i < points.size(); i++) {
            minX = Math.min(minX, points.get(i).x);
            minY = Math.min(minY, points.get(i).y);
        }
        int maxX = 0;
        int maxY = 0;
        for (Common.Point value : points) {
            maxX = Math.max(maxX, value.x - minX);
            maxY = Math.max(maxY, value.y - minY);
        }
        int[][] ans = new int[maxX + 1][maxY + 1];
        for (int i = 0; i <= maxX; i++) {
            for (int j = 0; j <= maxY; j++) {
                ans[i][j] = WHITE;
            }
        }
        for (Common.Point point : points) {
            ans[point.x - minX][point.y - minY] = color;
        }
        return ans;
    }

    public static void ShowImage(int[][] pixels, String filename) throws IOException {
        if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            return;
        }
        int w = pixels.length;
        int h = pixels[0].length;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                image.setRGB(x, y, pixels[x][y]);
            }
        }

        File answer = new File(filename);
        ImageIO.write(image, "jpg", answer);
    }
    public static void ShowImage(BufferedImage image, String filename) throws IOException {
        File answer = new File(filename);
        ImageIO.write(image, "jpg", answer);
    }

    public static int GetR(int x) {
        return (x >> 16) & 0xff;
    }

    public static int GetG(int x) {
        return (x >> 8) & 0xff;
    }

    public static int GetB(int x) {
        return (x & 0xff);
    }

    public static int Distance(int dr, int dg, int db) {
        return dr * dr + dg * dg + db * db;
    }

    public static int GetColor(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }
}
