import java.util.*;
import java.nio.file.*;
import java.net.*;
import java.util.stream.*;

class CountJRT {

    public static void main(String[] args) {
	    readFromJRT();
    }

    public static void readFromJRT() {
        int count = 0;
        for (Path s : getJRTPath("/modules/java.base/", true)) {
            // System.out.println(s);
            if (s.toString().endsWith(".class")) {
                count++;
            }
        }
        System.out.println(count);
    }

    public static Set<Path> getJRTPath(String path, boolean rec) {
        try {
            FileSystem fs = FileSystems.newFileSystem(URI.create("jrt:/"), Collections.emptyMap());
            if (rec) {
                try (Stream<Path> stream = Files.walk(fs.getPath(path), FileVisitOption.FOLLOW_LINKS).filter(Files::isRegularFile)) {
                    return stream.collect(Collectors.toSet());
                }
            } else {
                try (Stream<Path> stream = Files.list(fs.getPath(path))) {
                    return stream.collect(Collectors.toSet());
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

}
