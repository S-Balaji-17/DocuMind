package com.documind.documind.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ChromaService {

    private List<String> storedChunks = new ArrayList<>();

    public void createCollection() {
        storedChunks = new ArrayList<>();
    }

    public void storeChunks(List<String> chunks) {
        storedChunks.clear();
        storedChunks.addAll(chunks);
        System.out.println("Stored " + storedChunks.size() + " chunks successfully!");
    }

    public List<String> queryChunks(String question) {
        if (storedChunks.isEmpty()) {
            return new ArrayList<>();
        }
        String questionLower = question.toLowerCase();
        List<String> relevant = new ArrayList<>();
        for (String chunk : storedChunks) {
            String chunkLower = chunk.toLowerCase();
            String[] words = questionLower.split("\\s+");
            for (String word : words) {
                if (word.length() > 3 && chunkLower.contains(word)) {
                    relevant.add(chunk);
                    break;
                }
            }
        }
        if (relevant.isEmpty()) {
            return storedChunks.subList(0, Math.min(3, storedChunks.size()));
        }
        return relevant.subList(0, Math.min(3, relevant.size()));
    }

    public void deleteCollection() {
        storedChunks = new ArrayList<>();
    }
}