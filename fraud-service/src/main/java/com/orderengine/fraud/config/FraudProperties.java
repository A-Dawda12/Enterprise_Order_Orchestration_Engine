package com.orderengine.fraud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "orderengine.fraud")
public class FraudProperties {

    private int reviewScoreThreshold = 80;

    private BigDecimal highValueThreshold = new BigDecimal("100.00");

    private int newAccountPoints = 45;
    private int highValuePoints = 45;

    private List<String> newAccountPrefixes = new ArrayList<>(List.of("new-"));

    public int getReviewScoreThreshold() {
        return reviewScoreThreshold;
    }

    public void setReviewScoreThreshold(int reviewScoreThreshold) {
        this.reviewScoreThreshold = reviewScoreThreshold;
    }

    public BigDecimal getHighValueThreshold() {
        return highValueThreshold;
    }

    public void setHighValueThreshold(BigDecimal highValueThreshold) {
        this.highValueThreshold = highValueThreshold;
    }

    public int getNewAccountPoints() {
        return newAccountPoints;
    }

    public void setNewAccountPoints(int newAccountPoints) {
        this.newAccountPoints = newAccountPoints;
    }

    public int getHighValuePoints() {
        return highValuePoints;
    }

    public void setHighValuePoints(int highValuePoints) {
        this.highValuePoints = highValuePoints;
    }

    public List<String> getNewAccountPrefixes() {
        return newAccountPrefixes;
    }

    public void setNewAccountPrefixes(List<String> newAccountPrefixes) {
        this.newAccountPrefixes = newAccountPrefixes;
    }

}
