package org.wanja.kexi;

import java.io.PrintStream;
import java.io.File;

import picocli.CommandLine.ITypeConverter;

public class PrintStreamConverter implements ITypeConverter<PrintStream> {

    @Override
    public PrintStream convert(String value) throws Exception {
        return new PrintStream(new File(value));
    }
    
    
}
