package part1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLNameSurnameMerging {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/part1/input.xml"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/part1/output.xml"))) {
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
                if (line.endsWith(">")) {
                    writer.write(mergeTags(builder.toString()));
                    builder = new StringBuilder();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String mergeTags(String tag) {
        // Groups to find: (1) `name="Name"` => (2)`Name` OR (3)`surname="Surname"` => (4)`Surname`;
        Pattern nameOrSurname = Pattern.compile("(\\bname\\b\\s*=\\s*\"([а-щА-ЩЬьЮюЯяЇїІіЄєҐґ]+)\")"
                                            +"|(\\bsurname\\b\\s*=\\s*\"([а-щА-ЩЬьЮюЯяЇїІіЄєҐґ]+)\"\\s)");

        Matcher matcher = nameOrSurname.matcher(tag);
        String foundNameTag = null, foundSurnameTag = null, foundName = null, foundSurname = null;
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                foundNameTag = matcher.group(1);
                foundName = matcher.group(2);
            }
            if (matcher.group(3) != null) {
                foundSurnameTag = matcher.group(3);
                foundSurname = matcher.group(4);
            }
            if (foundNameTag != null && foundSurnameTag != null)
                tag = tag
                        .replaceAll(foundNameTag, "name=\"" + foundName + " " + foundSurname + "\"")
                        .replaceAll(foundSurnameTag, "");
        }
        return tag;
    }
}