package part2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;
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
        Map<ViolationType, Double> violationsTypeInteger = new HashMap<>();
        if (files != null) {
            for (File fileName : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        builder.append(line).append(System.lineSeparator());
                        if (line.endsWith("}") || line.endsWith("},")) {
                            String jObj = builder.toString().replaceAll("(\\[)|(])", "");
                            builder = new StringBuilder();

                            Violation tmp = JSON_MAPPER.readValue(jObj, Violation.class);
                            violationsTypeInteger.merge(tmp.getType(), tmp.getFineAmount(), Double::sum);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return violationsTypeInteger;
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