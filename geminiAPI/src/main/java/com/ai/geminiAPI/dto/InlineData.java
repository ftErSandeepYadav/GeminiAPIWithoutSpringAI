package com.ai.geminiAPI.dto;

/**
 * Represents the inline data block, used for returning base64 encoded image data.
 */
public class InlineData {
    private String mimeType;
    private String data; // Base64 encoded string

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}