package com.ai.geminiAPI.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a part of the content in a Gemini API request or response.
 * Can contain either text or inlineData (for images).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Part {
    private String text;
    private InlineData inlineData;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InlineData getInlineData() {
        return inlineData;
    }

    public void setInlineData(InlineData inlineData) {
        this.inlineData = inlineData;
    }
}