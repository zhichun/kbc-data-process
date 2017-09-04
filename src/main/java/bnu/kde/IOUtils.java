package bnu.kde;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {

    public static List<String> readList(String inpath) throws IOException {
        File file = new File(inpath);
        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        List<String> result = new ArrayList<String>();
        try {
            while (it.hasNext()) {
                result.add(it.nextLine());
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        it.close();
        return result;
    }
}
