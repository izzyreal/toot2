/* Copyright (C) 2005 Steve Taylor (toot.org.uk) */

package uk.org.toot.misc;

import java.io.PrintStream;
import java.util.ArrayList;

public class Log
{
    private static ArrayList<PrintStream> streams =
        new ArrayList<PrintStream>();

    public static void addStream(PrintStream os) {
        streams.add(os);
    }

    private static void println(String message) {
        for ( PrintStream stream : streams ) {
            stream.println(message);
        }
    }

    public static void exception(Exception e, String message) {
        println(message);
        exception(e);
    }

    public static void exception(Exception e) {
        println(e.toString());
        StackTraceElement[] stackTrace = e.getStackTrace();
        if ( stackTrace.length == 0 ) return;
        for ( int i = 0; i < stackTrace.length; i++ ) {
            println(stackTrace[i].toString());
        }
    }

    public static void debug(String message) {
		println(message);
    }

    public static void error(String message) {
        println(message);
    }
}
