package com.ai.geminiAPI.dto;

import java.util.List;

/**
 * Represents the response payload from the Gemini API.
 */
public class GeminiResponse {
    private List<Candidate> candidates;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public static class Candidate {
        private Content content;
        private String finishReason;
        private int index;
        private List<SafetyRating> safetyRatings;

        // Getters and Setters for all fields

        public Content getContent() { return content; }
        public void setContent(Content content) { this.content = content; }
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public List<SafetyRating> getSafetyRatings() { return safetyRatings; }
        public void setSafetyRatings(List<SafetyRating> safetyRatings) { this.safetyRatings = safetyRatings; }
    }

    public static class Content {
        private List<Part> parts;
        private String role;

        // Getters and Setters
        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class SafetyRating {
        private String category;
        private String probability;

        // Getters and Setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getProbability() { return probability; }
        public void setProbability(String probability) { this.probability = probability; }
    }
}

