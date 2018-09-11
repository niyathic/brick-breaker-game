import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

public class InfoReader {
    private int[] info;

    public InfoReader(String filename) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader(filename));
        String line = r.readLine();
        if (line == null) {
            JOptionPane.showMessageDialog(null, "No game saved!");
        }
        else {
            // get String array from csv
            String[] infoStrings = line.split(",");
            r.close();
            System.out.println(infoStrings.length);

            // convert String array to int array
            info = new int[infoStrings.length];
            for (int i = 0; i < infoStrings.length; i++) {
                info[i] = Integer.parseInt(infoStrings[i]);
            }
        }
    }

    public int[] getInfo() {
        return info;
    }


}
