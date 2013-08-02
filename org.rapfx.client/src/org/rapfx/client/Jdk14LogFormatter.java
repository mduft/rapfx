/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for the Java Logger to create single-line log statements, which is much more readable.
 */
public class Jdk14LogFormatter extends Formatter {

    @Override
    public String format(LogRecord log) {
        StringWriter builder = new StringWriter();
        Date date = new Date(log.getMillis());
        builder.append(DateFormat.getTimeInstance().format(date)).append(" ")
                .append(pad(log.getLevel().getName(), 8)).append(" ")
                .append(pad(getShortClassName(log.getSourceClassName()), 25)).append(": ")
                .append(log.getMessage()).append('\n');

        Throwable thrown = log.getThrown();
        if (thrown != null) {
            thrown.printStackTrace(new PrintWriter(builder));
        }

        return builder.toString();
    }

    private String getShortClassName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private String pad(String name, int length) {
        return String.format("%1$" + length + "s", name);
    }
}
