package com.wipro.pds.dto;

import java.math.BigDecimal;
import java.util.List;

public class AiComplaintClassificationResultDto {
    private String detectedCategory;
    private BigDecimal confidenceScore;
    private String suggestedPriority;
    private String languageDetected;
    private String sentiment;
    private List<String> extractedKeywords;
    private String recommendedAction;

    public AiComplaintClassificationResultDto() {}

    public String getDetectedCategory() { return detectedCategory; }
    public void setDetectedCategory(String detectedCategory) { this.detectedCategory = detectedCategory; }
    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }
    public String getSuggestedPriority() { return suggestedPriority; }
    public void setSuggestedPriority(String suggestedPriority) { this.suggestedPriority = suggestedPriority; }
    public String getLanguageDetected() { return languageDetected; }
    public void setLanguageDetected(String languageDetected) { this.languageDetected = languageDetected; }
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    public List<String> getExtractedKeywords() { return extractedKeywords; }
    public void setExtractedKeywords(List<String> extractedKeywords) { this.extractedKeywords = extractedKeywords; }
    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
}
