package com.example.vibestudy;

public final class WfStatus {

    private WfStatus() {}

    // Process/Task instance statuses
    public static final String ACTIVE = "ACTIVE";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";
    public static final String PENDING = "PENDING";
    public static final String IN_PROGRESS = "IN_PROGRESS";

    // State types
    public static final String STATE_START = "START";
    public static final String STATE_END = "END";

    // Assignee types
    public static final String ASSIGNEE_USER = "USER";
    public static final String ASSIGNEE_FIELD = "FIELD";

    // Task results
    public static final String RESULT_APPROVED = "APPROVED";
    public static final String RESULT_REJECTED = "REJECTED";
    public static final String RESULT_DONE = "DONE";

    // Transition codes
    public static final String TRANSITION_APPROVE = "APPROVE";
    public static final String TRANSITION_REJECT = "REJECT";
    public static final String TRANSITION_SUBMIT = "SUBMIT";
}
