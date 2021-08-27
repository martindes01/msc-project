package com.bham.mld705.tasks;

import com.bham.mld705.tests.DyadicCountSketchTest;

/**
 * @author Martin de Spirlet
 */
public final class DyadicCountSketchAccuracyTask {

    private DyadicCountSketchAccuracyTask() {
        throw new AssertionError();
    }

    public static void main(String[] args) {
        new DyadicCountSketchTest(args).runAccuracyTest();
    }

}
