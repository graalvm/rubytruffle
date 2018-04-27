/*
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 *
 * This code is modified from the Readline JRuby extension module
 * implementation with the following header:
 *
 * Version: EPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Eclipse Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/epl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Ola Bini <ola@ologix.com>
 * Copyright (C) 2006 Damian Steer <pldms@mac.com>
 * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the EPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the EPL, the GPL or the LGPL.
 */
package org.truffleruby.stdlib.readline;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.history.History;
import jline.console.history.MemoryHistory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConsoleHolder {

    private final ConsoleReader readline;
    private Completer currentCompleter;
    private final History history;
    private final FileStreamer in;
    private final FileStreamer out;

    public static ConsoleHolder update(ConsoleHolder original, int inFd, int outFd) {
        if (inFd == original.getIn().getFd() && outFd == original.getOut().getFd()) {
            return original;
        }

        return new ConsoleHolder(original, inFd, outFd);
    }

    @TruffleBoundary
    public ConsoleHolder() {
        try {
            in = new FileStreamer(0);
            out = new FileStreamer(1);
            readline = new ConsoleReader(in.getIn(), out.getOut());
        } catch (IOException e) {
            throw new UnsupportedOperationException("Couldn't initialize readline", e);
        }

        readline.setHistoryEnabled(false);
        readline.setPaginationEnabled(true);
        readline.setBellEnabled(true);

        currentCompleter = new ReadlineNodes.RubyFileNameCompleter();
        readline.addCompleter(currentCompleter);

        history = new MemoryHistory();
        readline.setHistory(history);
    }

    @TruffleBoundary
    public ConsoleHolder(ConsoleHolder copy, int inFd, int outFd) {
        in = copy.in.getFd() == inFd ? copy.in : new FileStreamer(inFd);
        out = copy.out.getFd() == outFd ? copy.out : new FileStreamer(outFd);

        try {
            readline = new ConsoleReader(in.getIn(), out.getOut());
        } catch (IOException e) {
            throw new UnsupportedOperationException("Couldn't initialize readline", e);
        }

        final ConsoleReader copyReadline = copy.getReadline();

        readline.setHistoryEnabled(copyReadline.isHistoryEnabled());
        readline.setPaginationEnabled(copyReadline.isPaginationEnabled());
        readline.setBellEnabled(copyReadline.getBellEnabled());

        currentCompleter = copy.getCurrentCompleter();
        readline.addCompleter(currentCompleter);

        history = copy.getHistory();
        readline.setHistory(history);
    }

    public ConsoleReader getReadline() {
        return readline;
    }

    public Completer getCurrentCompleter() {
        return currentCompleter;
    }

    public void setCurrentCompleter(Completer completer) {
        readline.removeCompleter(currentCompleter);
        currentCompleter = completer;
        readline.addCompleter(completer);
    }

    public History getHistory() {
        return history;
    }

    public FileStreamer getIn() {
        return in;
    }

    public FileStreamer getOut() {
        return out;
    }

}
