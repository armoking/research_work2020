import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GenerateData {
    public static class ImageWithClass {
        int classOfImage;
        short[][] image;

        ImageWithClass(short[][] image, int classOfImage) {
            this.image = image;
            this.classOfImage = classOfImage;
        }

        @Override
        public String toString() {
            return "ImageWithClass{" +
                    "classOfImage=" + classOfImage +
                    ", w=" + image.length +
                    ", h=" + image[0].length +
                    '}';
        }
    }

    public static short[][] SimplifyImage(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        var result = new short[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int color = image.getRGB(x, y) & ImageLibrary.WHITE;
                color = 255 - (ImageLibrary.GetR(color) + ImageLibrary.GetG(color) + ImageLibrary.GetB(color)) / 3;
                result[x][y] = (short) color;
            }
        }
        return result;
    }

    public static short[][] Compress(short[][] image, int n, int m) {
        int w = image.length;
        int h = image[0].length;
        short[][] currentImage = image.clone();
        while (w < n || h < m) {
            var nextImage = new short[w * 2][h * 2];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int nx : new int[]{2 * x, 2 * x + 1}) {
                        for (int ny : new int[]{2 * y, 2 * y + 1}) {
                            nextImage[nx][ny] = currentImage[x][y];
                        }
                    }
                }
            }
            currentImage = nextImage.clone();
            w *= 2;
            h *= 2;
        }
        var result = new short[n][m];
        var count = new short[n][m];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int x = i * n / w;
                int y = j * m / h;
                count[x][y]++;
                result[x][y] += currentImage[i][j];
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] /= Math.max(1, count[i][j]);
            }
        }
        return result;
    }

    public static short[][] MoveBorders(short[][] image) {
        int w = image.length;
        int h = image[0].length;
        int maxX = 0;
        int maxY = 0;
        int minX = w;
        int minY = h;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (image[x][y] != 0) {
                    maxX = Math.max(x, maxX);
                    maxY = Math.max(y, maxY);
                    minX = Math.min(x, minX);
                    minY = Math.min(y, minY);
                }
            }
        }
        int newW = maxX - minX + 1;
        int newH = maxY - minY + 1;
        short[][] result = new short[newW][newH];
        for (int i = minX; i <= maxX; i++) {
            if (maxY + 1 - minY >= 0) System.arraycopy(image[i], minY, result[i - minX], 0, maxY + 1 - minY);
        }
        return result;
    }

    public static void CheckDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            var result = dir.mkdir();
        }
    }

    public static void main(String[] args) throws IOException {
        String mainPath = "C:\\Users\\User\\Desktop\\github\\university\\educational_and_research_work\\";
        String[] folders = new File(mainPath + "images").list();
        var data = new ArrayList<ImageWithClass>();
        assert folders != null;
        for (int classOfImage = 0; classOfImage < folders.length; classOfImage++) {
            String path = mainPath + "images\\" + folders[classOfImage] + "\\";
            String[] files = new File(path).list();
            assert files != null;
            for (String file : files) {
                var image = ImageLibrary.ReadImage(path + file);
                assert image != null;
                var pixels = SimplifyImage(image);
                data.add(new ImageWithClass(Compress(pixels, 50, 50), classOfImage));
            }
            System.out.println(classOfImage + folders[classOfImage]);
        }

        int dataSize = data.size() * 3;
        ImageWithClass[] dataWithAngles = new ImageWithClass[dataSize];
        int index = 0;
        for (var item : data) {
            var currentImage = item.image;
            int w = currentImage.length;
            int h = currentImage[0].length;
            int midx = w / 2;
            int midy = h / 2;

            for (int j : new int[]{-10, 0, 5}) {
                double angle = Math.PI * j / 180;
                double sin = Math.sin(angle);
                double cos = Math.cos(angle);

                short[][] tiltedImage = new short[w][h];
                short[][] count = new short[w][h];
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        int nx = (int) ((x - midx) * cos - (y - midy) * sin) + midx;
                        int ny = (int) ((x - midx) * sin + (y - midy) * cos) + midy;
                        nx = Math.max(0, nx);
                        ny = Math.max(0, ny);
                        nx = Math.min(nx, w - 1);
                        ny = Math.min(ny, h - 1);
                        count[nx][ny]++;
                        tiltedImage[nx][ny] += currentImage[x][y];
                    }
                }
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        tiltedImage[x][y] /= Math.max(1, count[x][y]);
                    }
                }
                dataWithAngles[index++] = new ImageWithClass(Compress(MoveBorders(tiltedImage), 32, 32), item.classOfImage);
            }
        }
        System.out.println("changed" +
                "");

        BufferedWriter out = new BufferedWriter(new FileWriter(mainPath + "data_set.txt"));
        out.write("" + dataWithAngles.length + "\n");
        for (var image : dataWithAngles) {
            var img = image.image;
            out.write(image.classOfImage + "\n");
            for (int x = 0; x < 32; x++) {
                for (int y = 0; y < 32; y++) {
                    out.write("" + img[x][y] + " ");
                }
                out.write('\n');
            }
        }
        out.close();
    }
}

