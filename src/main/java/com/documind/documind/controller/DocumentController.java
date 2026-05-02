package com.documind.documind.controller;

import com.documind.documind.model.ChatRequest;
import com.documind.documind.model.ChatResponse;
import com.documind.documind.service.ChromaService;
import com.documind.documind.service.GeminiService;
import com.documind.documind.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class DocumentController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ChromaService chromaService;

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            chromaService.deleteCollection();
            chromaService.createCollection();
            String text = pdfService.extractText(file);
            List<String> chunks = pdfService.chunkText(text);
            chromaService.storeChunks(chunks);
            return ResponseEntity.ok("PDF uploaded and processed successfully! " + chunks.size() + " chunks created.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing PDF: " + e.getMessage());
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            List<String> relevantChunks = chromaService.queryChunks(request.getQuestion());
            String answer = geminiService.generateAnswer(request.getQuestion(), relevantChunks);
            boolean isFallback = answer.startsWith("FALLBACK");
            ChatResponse response = new ChatResponse(answer, "Document", isFallback);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ChatResponse response = new ChatResponse(
                    "FALLBACK: Error processing question: " + e.getMessage(),
                    "Error", true);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("DocuMind API is running!");
    }
}