package com.example.vibestudy;

import java.util.List;

public class SubscriptionMainBulkRequestDto {
    private List<SubscriptionMainRequestDto> items;

    public List<SubscriptionMainRequestDto> getItems() { return items; }
    public void setItems(List<SubscriptionMainRequestDto> items) { this.items = items; }
}
