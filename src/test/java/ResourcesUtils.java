import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class ResourcesUtils {

    private ResourcesUtils() {
    }

    public static <T> T loadResource(Class theClass, String filePath, Class<T> returnedClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String content = loadResourceAsString(theClass, filePath);
        return mapper.readValue(content, returnedClass);
    }

    public static byte[] loadResourceAsBytes(Class theClass, String filePath) throws IOException {
        try (InputStream is = theClass.getResourceAsStream(filePath)) {
            return IOUtils.toByteArray(is);
        }
    }

    public static String loadResourceAsString(Class theClass, String filePath) throws IOException {
        return new String(loadResourceAsBytes(theClass, filePath), Charset.forName("UTF-8"));
    }
}