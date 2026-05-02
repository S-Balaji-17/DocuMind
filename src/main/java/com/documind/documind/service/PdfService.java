package com.documind.documind.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.Loader;

@Service
public class PdfService {

    private static final int CHUNK_SIZE = 200;
    private static final int OVERLAP = 20;

    public String extractText(MultipartFile file) throws IOException {
        PDDocument document = Loader.loadPDF(file.getBytes());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    public List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        int i = 0;
        while (i < words.length) {
            StringBuilder chunk = new StringBuilder();
            int end = Math.min(i + CHUNK_SIZE, words.length);
            for (int j = i; j < end; j++) {
                chunk.append(words[j]).append(" ");
            }
            chunks.add(chunk.toString().trim());
            i += CHUNK_SIZE - OVERLAP;
        }
        return chunks;
    }
}