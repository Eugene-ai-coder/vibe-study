package com.example.vibestudy;

public class MenuMoveRequestDto {
    private String newParentId;   // null이면 최상위로 이동
    private int newSortOrder;     // 도착지에서의 위치 (1-based)

    public String getNewParentId() { return newParentId; }
    public void setNewParentId(String newParentId) { this.newParentId = newParentId; }
    public int getNewSortOrder() { return newSortOrder; }
    public void setNewSortOrder(int newSortOrder) { this.newSortOrder = newSortOrder; }
}
