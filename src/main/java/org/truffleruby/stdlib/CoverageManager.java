/*
 * Copyright (c) 2013, 2016 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.truffleruby.stdlib;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.EventBinding;
import com.oracle.truffle.api.instrumentation.ExecutionEventNode;
import com.oracle.truffle.api.instrumentation.Instrumenter;
import com.oracle.truffle.api.instrumentation.SourceSectionFilter;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import org.truffleruby.RubyContext;
import org.truffleruby.RubyLanguage;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLongArray;

public class CoverageManager {

    public class LineTag {
    }

    public static final long NO_CODE = -1;

    private final Instrumenter instrumenter;
    private EventBinding<?> binding;
    private final Map<Source, AtomicLongArray> counters = new ConcurrentHashMap<>();
    private final Set<Source> coveredSources = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private boolean enabled;

    public CoverageManager(RubyContext context, Instrumenter instrumenter) {
        this.instrumenter = instrumenter;

        if (context.getOptions().COVERAGE_GLOBAL) {
            enable();
        }
    }

    public synchronized void loadingSource(Source source) {
        if (enabled) {
            coveredSources.add(source);
        }
    }

    private int lineToIndex(int line) {
        return line - 1;
    }

    public void setLineHasCode(Source source, int line) {
        if (coveredSources.contains(source)) {
            final AtomicLongArray counters = getCounters(source);
            counters.set(lineToIndex(line), 0);
        }
    }

    private boolean getLineHasCode(Source source, int line) {
        final AtomicLongArray counters = getCounters(source);
        return counters.get(lineToIndex(line)) != NO_CODE;
    }

    @TruffleBoundary
    public synchronized void enable() {
        if (enabled) {
            return;
        }

        binding = instrumenter.attachFactory(SourceSectionFilter.newBuilder()
                .mimeTypeIs(RubyLanguage.MIME_TYPE)
                .sourceIs((source) -> coveredSources.contains(source))
                .tagIs(LineTag.class)
                .build(), eventContext -> new ExecutionEventNode() {

                    @CompilationFinal private boolean configured;
                    @CompilationFinal private int lineNumber;
                    @CompilationFinal private AtomicLongArray counters;

                    @Override
                    protected void onEnter(VirtualFrame frame) {
                        if (!configured) {
                            CompilerDirectives.transferToInterpreterAndInvalidate();
                            final SourceSection sourceSection = eventContext.getInstrumentedSourceSection();

                            if (getLineHasCode(sourceSection.getSource(), sourceSection.getStartLine())) {
                                lineNumber = lineToIndex(sourceSection.getStartLine());
                                counters = getCounters(sourceSection.getSource());
                            }
                            configured = true;
                        }

                        if (counters != null) {
                            counters.incrementAndGet(lineNumber);
                        }
                    }

                });

        enabled = true;
    }

    @TruffleBoundary
    public synchronized void disable() {
        if (!enabled) {
            return;
        }

        binding.dispose();
        counters.clear();
        coveredSources.clear();

        enabled = false;
    }
    
    private synchronized AtomicLongArray getCounters(Source source) {
        if (source.getName() == null) {
            return null;
        }

        AtomicLongArray c = counters.get(source);

        if (c == null) {
            long[] initialValues = new long[source.getLineCount()];
            Arrays.fill(initialValues, NO_CODE);
            c = new AtomicLongArray(initialValues);
            counters.put(source, c);
        }

        return c;
    }

    public synchronized Map<Source, long[]> getCounts() {
        if (!enabled) {
            return null;
        }

        final Map<Source, long[]> counts = new HashMap<>();

        for (Map.Entry<Source, AtomicLongArray> entry : counters.entrySet()) {
            final long[] array = new long[entry.getValue().length()];

            for (int n = 0; n < array.length; n++) {
                array[n] = entry.getValue().get(n);
            }

            counts.put(entry.getKey(), array);
        }

        return counts;
    }

    public synchronized void print(PrintStream out) {
        final int maxCountDigits = Long.toString(getMaxCount()).length();

        final String countFormat = "%" + maxCountDigits + "d";

        final char[] noCodeChars = new char[maxCountDigits];
        Arrays.fill(noCodeChars, ' ');
        noCodeChars[maxCountDigits - 1] = '-';
        final String noCodeString = new String(noCodeChars);

        for (Map.Entry<Source, AtomicLongArray> entry : counters.entrySet()) {
            out.println(entry.getKey().getName());

            for (int n = 0; n < entry.getValue().length(); n++) {
                // TODO CS 5-Sep-17 can we keep the line as a CharSequence rather than using toString?
                String line = entry.getKey().getCharacters(n + 1).toString();

                if (line.length() > 60) {
                    line = line.substring(0, 60);
                }

                out.print("  ");
                final long c = entry.getValue().get(n);
                if (c == NO_CODE) {
                    out.print(noCodeString);
                } else {
                    out.printf(countFormat, c);
                }
                out.printf("  %s%n", line);
            }
        }
    }

    private long getMaxCount() {
        long max = 0;

        for (Map.Entry<Source, AtomicLongArray> entry : counters.entrySet()) {
            for (int n = 0; n < entry.getValue().length(); n++) {
                max = Math.max(max, entry.getValue().get(n));
            }
        }

        return max;
    }

    public synchronized boolean isEnabled() {
        return enabled;
    }

}
