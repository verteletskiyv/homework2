package part2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class ViolationsToXmlSummary {
    private static final ObjectMapper JSON_MAPPER;
    private static final XmlMapper XML_MAPPER;

    static {
        JSON_MAPPER = new ObjectMapper();
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        XML_MAPPER = xmlMapper;
    }

    public static void main(String[] args) {
        File directory = new File("src/main/resources/part2/input");
        Map<ViolationType, Double> fineSummary = getFineSummaryFromFolder(directory);
        sortAndWriteSummaryToXml(fineSummary, new File("src/main/resources/part2/output/fine_summary.xml"));
    }

    private static Map<ViolationType, Double> getFineSummaryFromFolder(File folder) {
        File[] files = folder.listFiles(pathname -> pathname.getName().endsWith(".json"));
        Map<ViolationType, Double> violationsTypeDouble = new HashMap<>();
        for (File fileName : Objects.requireNonNull(files)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName));
                 JsonParser jsonParser = JSON_MAPPER.getFactory().createParser(reader)) {
                if (jsonParser.nextToken() != JsonToken.START_ARRAY)
                    throw new IllegalStateException("Expected content to be an array");

                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    Violation tmp = JSON_MAPPER.readValue(jsonParser, Violation.class);
                    violationsTypeDouble.merge(tmp.getType(), tmp.getFineAmount(), Double::sum);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return violationsTypeDouble;
    }

    private static void sortAndWriteSummaryToXml(Map<ViolationType, Double> summaryMap, File outPath) {
        summaryMap = summaryMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        try {
            XML_MAPPER.writer().withRootName("ViolationsFineSummary").writeValue(outPath, summaryMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}