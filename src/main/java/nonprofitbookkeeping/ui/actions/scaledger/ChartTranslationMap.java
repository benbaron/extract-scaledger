package nonprofitbookkeeping.ui.actions.scaledger;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChartTranslationMap {
    private final Map<String,String> rawToCanonical;

    public ChartTranslationMap(Map<String,String> rawToCanonical) {
        this.rawToCanonical = new LinkedHashMap<>(rawToCanonical);
    }

    public String translate(String raw) {
        if (raw == null) return null;
        return rawToCanonical.get(raw);
    }

    public Map<String,String> asMap() { return Collections.unmodifiableMap(rawToCanonical); }

    public static ChartTranslationMap fromJsonFile(Path jsonFile) throws IOException {
        byte[] bytes = Files.readAllBytes(jsonFile);
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String,String> parsed = mapper.readValue(bytes, Map.class);
        return new ChartTranslationMap(parsed);
    }
}
