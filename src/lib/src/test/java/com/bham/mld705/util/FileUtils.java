package com.bham.mld705.util;

/**
 * @author Martin de Spirlet
 */
public final class FileUtils {

    public static final String CSV_DELIMITER = ",";
    public static final String CSV_EXTENSION = ".csv";
    public static final String TSV_DELIMITER = "\t";
    public static final String TSV_EXTENSION = ".tsv";
    public static final String OTHER_EXTENSION = ".txt";

    private FileUtils() {
        throw new AssertionError();
    }

    public static String getDelimitedFileExtension(String delimiter) {
        switch (delimiter.replace(TSV_DELIMITER, "").strip()) {
            case CSV_DELIMITER:
                return CSV_EXTENSION;

            case "":
                return TSV_EXTENSION;

            default:
                return OTHER_EXTENSION;
        }
    }

}
