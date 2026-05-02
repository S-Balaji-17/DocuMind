package com.documind.documind.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class ChromaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String CHROMA_URL = "http://localhost:8000";
    private final String COLLECTION_NAME = "documind";

    public void createCollection() {
        try {
            String url = CHROMA_URL + "/api/v1/collections";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = new HashMap<>();
            body.put("name", COLLECTION_NAME);
            body.put("metadata", new HashMap<>());
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            System.out.println("Collection may already exist: " + e.getMessage());
        }
    }

    public void storeChunks(List<String> chunks) {
        try {
            String collectionId = getCollectionId();
            String url = CHROMA_URL + "/api/v1/collections/" + collectionId + "/add";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            List<String> ids = new ArrayList<>();
            List<String> documents = new ArrayList<>();
            List<List<Float>> embeddings = new ArrayList<>();

            for (int i = 0; i < chunks.size(); i++) {
                ids.add("chunk_" + i + "_" + System.currentTimeMillis());
                documents.add(chunks.get(i));
                embeddings.add(generateSimpleEmbedding(chunks.get(i)));
            }

            Map<String, Object> body = new HashMap<>();
            body.put("ids", ids);
            body.put("documents", documents);
            body.put("embeddings", embeddings);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            System.out.println("Error storing chunks: " + e.getMessage());
        }
    }

    public List<String> queryChunks(String question) {
        try {
            String collectionId = getCollectionId();
            String url = CHROMA_URL + "/api/v1/collections/" + collectionId + "/query";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("query_embeddings", List.of(generateSimpleEmbedding(question)));
            body.put("n_results", 3);
            body.put("include", List.of("documents", "distances"));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            List<List<String>> documents = (List<List<String>>) response.getBody().get("documents");
            return documents.get(0);
        } catch (Exception e) {
            System.out.println("Error querying chunks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String getCollectionId() {
        String url = CHROMA_URL + "/api/v1/collections/" + COLLECTION_NAME;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return (String) response.getBody().get("id");
    }

    private List<Float> generateSimpleEmbedding(String text) {
        List<Float> embedding = new ArrayList<>();
        String lower = text.toLowerCase();
        for (int i = 0; i < 384; i++) {
            float val = 0;
            for (int j = 0; j < lower.length(); j++) {
                val += (lower.charAt(j) * (i + 1)) % 100;
            }
            embedding.add(val / (lower.length() + 1));
        }
        return embedding;
    }

    public void deleteCollection() {
        try {
            String collectionId = getCollectionId();
            String url = CHROMA_URL + "/api/v1/collections/" + collectionId;
            restTemplate.delete(url);
        } catch (Exception e) {
            System.out.println("Error deleting collection: " + e.getMessage());
        }
    }
}