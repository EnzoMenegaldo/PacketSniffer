package com.packetsniffer.emenegal.packetsniffer.api.strategy;


import com.packetsniffer.emenegal.packetsniffer.api.strategy.strategy.ICollectionStrategy;


public class StrategyManager {
    public static final String TAG = StrategyManager.class.getSimpleName();

    public static final StrategyManager INSTANCE = new StrategyManager();

    private ICollectionStrategy strategy;

    public void setStrategy(ICollectionStrategy strategy) {
        this.strategy = strategy;
    }

    public ICollectionStrategy getStrategy() {
        return strategy;
    }

}