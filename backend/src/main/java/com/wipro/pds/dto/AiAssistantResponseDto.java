package com.wipro.pds.dto;

import java.util.List;
import java.util.Map;

public class AiAssistantResponseDto {
    private String answer;
    private String queryIntent;
    private List<Map<String, Object>> tabularData;
    private List<String> keyInsights;
    private String safetyNotice = "AI Advisory indicator for authorized civil supplies officer review only. No automated updates performed.";

    public AiAssistantResponseDto() {}

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getQueryIntent() { return queryIntent; }
    public void setQueryIntent(String queryIntent) { this.queryIntent = queryIntent; }
    public List<Map<String, Object>> getTabularData() { return tabularData; }
    public void setTabularData(List<Map<String, Object>> tabularData) { this.tabularData = tabularData; }
    public List<String> getKeyInsights() { return keyInsights; }
    public void setKeyInsights(List<String> keyInsights) { this.keyInsights = keyInsights; }
    public String getSafetyNotice() { return safetyNotice; }
    public void setSafetyNotice(String safetyNotice) { this.safetyNotice = safetyNotice; }
}
