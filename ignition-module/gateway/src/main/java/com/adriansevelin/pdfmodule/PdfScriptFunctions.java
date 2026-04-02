package com.adriansevelin.pdfmodule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfScriptFunctions 
{
    public String downloadPDF(String url, String destination) throws IOException
    {
        Path requestedPath = Path.of(destination);
        Path parent = requestedPath.getParent();
        if (parent != null)
        {
            Files.createDirectories(parent);
        }

        Path resolvedPath = getUniqueDestination(requestedPath);
        try (InputStream inputStream = new URL(url).openStream())
        {
            Files.copy(inputStream, resolvedPath);
        }

        return resolvedPath.toString();
    }

    public String downloadAndRead(String url, String destination) throws IOException
    {
        return readPDF(downloadPDF(url, destination));
    }

    public String readPDF(String location) throws IOException
    {
        return extractAndNormalizeText(new File(location));
    }

    public List<String> listPDF(String location) throws IOException
    {
        Path directory = Path.of(location);
        if (!Files.isDirectory(directory))
        {
            throw new IllegalArgumentException("location must be an existing directory: " + location);
        }

        try (Stream<Path> pathStream = Files.list(directory))
        {
            return pathStream
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".pdf"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
                .map(Path::toString)
                .collect(Collectors.toList());
        }
    }

    public String parseBetween(String source, String label, String nextLabel) throws IOException
    {
        return extractBetween(resolvePdfText(source), label, nextLabel);
    }

    public Map<String, String> parsePDF(String source, Map<String, List<String>> values) throws IOException 
    {
        String pdfText = resolvePdfText(source);
        Map<String, String> output = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : values.entrySet()) 
        {
            String outputKey = entry.getKey();
            List<String> pair = entry.getValue();
            if (pair == null || pair.size() < 2) 
            {
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

    private String resolvePdfText(String source) throws IOException
    {
        if (source == null)
        {
            throw new IllegalArgumentException("source cannot be null");
        }

        File sourceFile = new File(source);
        if (sourceFile.isFile())
        {
            return extractAndNormalizeText(sourceFile);
        }

        return collapseWhitespace(source);
    }

    private String extractAndNormalizeText(File file) throws IOException 
    {
        String text;
        try (PDDocument document = PDDocument.load(file))
        {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        }

        return collapseWhitespace(text);
    }

    private Path getUniqueDestination(Path requestedPath)
    {
        if (!Files.exists(requestedPath))
        {
            return requestedPath;
        }

        String fileName = requestedPath.getFileName().toString();
        String baseName = fileName;
        String extension = "";
        int extensionIndex = fileName.lastIndexOf('.');

        if (extensionIndex > 0)
        {
            baseName = fileName.substring(0, extensionIndex);
            extension = fileName.substring(extensionIndex);
        }

        Path parent = requestedPath.getParent();
        int duplicateIndex = 1;
        while (true)
        {
            String candidateName = baseName + " (" + duplicateIndex + ")" + extension;
            Path candidatePath = parent == null ? Path.of(candidateName) : parent.resolve(candidateName);
            if (!Files.exists(candidatePath))
            {
                return candidatePath;
            }

            duplicateIndex++;
        }
    }

    private String extractBetween(String text, String label, String nextLabel) 
    {
        int start = text.indexOf(label);
        if (start < 0) 
        {
            return "";
        }

        start += label.length();
        int end = text.indexOf(nextLabel, start);

        if (end < 0) 
        {
            return text.substring(start).trim();
        }

        return text.substring(start, end).trim();
    }

    private String collapseWhitespace(String value) 
    {
        StringBuilder out = new StringBuilder();
        boolean previousWasWhitespace = false;

        for (int i = 0; i < value.length(); i++) 
        {
            char ch = value.charAt(i);
            if (Character.isWhitespace(ch)) 
            {
                if (!previousWasWhitespace) 
                {
                    out.append(' ');
                    previousWasWhitespace = true;
                }
            } 
            else 
            {
                out.append(ch);
                previousWasWhitespace = false;
            }
        }
        
        return out.toString().trim();
    }
}
