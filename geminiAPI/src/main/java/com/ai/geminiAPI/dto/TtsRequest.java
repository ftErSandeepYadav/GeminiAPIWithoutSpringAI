package com.ai.geminiAPI.dto;
import java.util.List;

/**
 * Represents the request payload for the Gemini Text-to-Speech API.
 */
public class TtsRequest {
    private List<Content> contents;
    private GenerationConfig generationConfig;
    private String model;

    // Getters and Setters
    public List<Content> getContents() { return contents; }
    public void setContents(List<Content> contents) { this.contents = contents; }
    public GenerationConfig getGenerationConfig() { return generationConfig; }
    public void setGenerationConfig(GenerationConfig generationConfig) { this.generationConfig = generationConfig; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }


    public static class Content {
        private List<Part> parts;
        // Getters and Setters
        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }

    public static class GenerationConfig {
        private List<String> responseModalities;
        private SpeechConfig speechConfig;
        // Getters and Setters
        public List<String> getResponseModalities() { return responseModalities; }
        public void setResponseModalities(List<String> responseModalities) { this.responseModalities = responseModalities; }
        public SpeechConfig getSpeechConfig() { return speechConfig; }
        public void setSpeechConfig(SpeechConfig speechConfig) { this.speechConfig = speechConfig; }
    }

    public static class SpeechConfig {
        private VoiceConfig voiceConfig;
        // Getters and Setters
        public VoiceConfig getVoiceConfig() { return voiceConfig; }
        public void setVoiceConfig(VoiceConfig voiceConfig) { this.voiceConfig = voiceConfig; }
    }

    public static class VoiceConfig {
        private PrebuiltVoiceConfig prebuiltVoiceConfig;
        // Getters and Setters
        public PrebuiltVoiceConfig getPrebuiltVoiceConfig() { return prebuiltVoiceConfig; }
        public void setPrebuiltVoiceConfig(PrebuiltVoiceConfig prebuiltVoiceConfig) { this.prebuiltVoiceConfig = prebuiltVoiceConfig; }
    }

    public static class PrebuiltVoiceConfig {
        private String voiceName;
        // Getters and Setters
        public String getVoiceName() { return voiceName; }
        public void setVoiceName(String voiceName) { this.voiceName = voiceName; }
    }
}