package com.adriansevelin.pdfmodule;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfScriptFunctions {
    public Map<String, String> parsePDF(String path, Map<String, List<String>> values) throws IOException {
        String pdfText = extractAndNormalizeText(path);
        Map<String, String> output = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : values.entrySet()) {
            String outputKey = entry.getKey();
            List<String> pair = entry.getValue();
            if (pair == null || pair.size() < 2) {
                throw new IllegalArgumentException(
                    "Each values entry must be [label, nextLabel]. Bad key: " + outputKey
                );
            }

            String label = pair.get(0);
            String nextLabel = pair.get(1);
            String parsedValue = extractBetween(pdfText, label, nextLabel);
            output.put(outputKey, parsedValue);
        }

        return output;
    }

    private String extractAndNormalizeText(String path) throws IOException {
        PDDocument document = PDDocument.load(new File(path));
        String text;
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } finally {
            document.close();
        }

        return collapseWhitespace(text);
    }

    private String extractBetween(String text, String label, String nextLabel) {
        int start = text.indexOf(label);
        if (start < 0) {
            return "";
        }

        start += label.length();
        int end = text.indexOf(nextLabel, start);

        if (end < 0) {
            return text.substring(start).trim();
        }

        return text.substring(start, end).trim();
    }

    private String collapseWhitespace(String value) {
        StringBuilder out = new StringBuilder();
        boolean previousWasWhitespace = false;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isWhitespace(ch)) {
                if (!previousWasWhitespace) {
                    out.append(' ');
                    previousWasWhitespace = true;
                }
            } else {
                out.append(ch);
                previousWasWhitespace = false;
            }
        }

        return out.toString().trim();
    }
}
