package com.example.vibestudy;

/**
 * @deprecated Use {@link IdGenerator} instead
 */
@Deprecated
public final class WfIdGenerator {

    private WfIdGenerator() {}

    public static String generate(String prefix) {
        return IdGenerator.generate(prefix);
    }
}
