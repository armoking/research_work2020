import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\User\\Desktop\\test";
        new Main(path);
    }

    Main(String path) throws IOException {
        var image = ImageLibrary.ReadImage(path);
        assert image != null;
        int[][] filteredImage = Filter.MakeFilter(image);
        ImageLibrary.ShowImage(filteredImage, path + "filtered_image.jpg");
        ArrayList<ArrayList<Common.Point>> lines = LineParser.Parse(filteredImage);
        ArrayList<ArrayList<Common.Point>> text = new ArrayList<>();
        for (var line : lines) {
            var words = WordParser.Parse(line);
            text.addAll(words);
        }
        ArrayList<int[][]> answer = new ArrayList<>();
        for (var word : text) {
            int[][] pixels = ImageLibrary.GenerateImage(word, ImageLibrary.BLACK, false);
            if (pixels == null) continue;
            answer.add(pixels);
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(path + "_result.txt"));
        int n = answer.size();
        out.write(n + "\n");

        for (int[][] arr : answer) {
            int w = arr.length;
            int h = arr[0].length;
            out.write(w + " " + h + "\n");
            for (int[] ints : arr) {
                for (int y = 0; y < h; y++) {
                    if (ints[y] == ImageLibrary.BLACK) {
                        out.write("255 ");
                    } else {
                        out.write("0 ");
                    }
                }
                out.write('\n');
            }
        }
        out.close();
    }

}
