package nonprofitbookkeeping.ui.actions.scaledger;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TypeMap {
    private final Map<String, CategoryType> map = new HashMap<>();

    public static TypeMap fromJsonFile(Path p) throws IOException {
        TypeMap tm = new TypeMap();
        if (p == null) return tm;
        byte[] b = Files.readAllBytes(p);
        ObjectMapper om = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String,String> raw = om.readValue(b, Map.class);
        if (raw != null) {
            for (Map.Entry<String,String> e : raw.entrySet()) {
                try {
                    CategoryType ct = CategoryType.valueOf(e.getValue().trim().toUpperCase());
                    tm.map.put(e.getKey(), ct);
                } catch (Exception ex) {
                    // ignore bad enum values
                }
            }
        }
        return tm;
    }

    public CategoryType typeOf(String canonical) {
        if (canonical == null) return null;
        return map.get(canonical);
    }
}
