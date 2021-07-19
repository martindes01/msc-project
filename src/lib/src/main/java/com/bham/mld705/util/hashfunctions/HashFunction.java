package com.bham.mld705.util.hashfunctions;

@FunctionalInterface
public interface HashFunction {

    public abstract int apply(int key);

}
