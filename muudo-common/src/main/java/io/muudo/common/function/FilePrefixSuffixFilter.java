package io.muudo.common.function;

import java.io.File;
import java.io.FilenameFilter;

public class FilePrefixSuffixFilter implements FilenameFilter {
    private String prefix;
    private String suffix;
    
    public FilePrefixSuffixFilter(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
    
    @Override
    public boolean accept(File dir, String name) {
        return name.startsWith(prefix) && name.endsWith(suffix);
    }
}
