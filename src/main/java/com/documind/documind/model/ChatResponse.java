package com.documind.documind.model;

public class ChatResponse {
    private String answer;
    private String source;
    private boolean fallback;

    public ChatResponse(String answer, String source, boolean fallback) {
        this.answer = answer;
        this.source = source;
        this.fallback = fallback;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }
}