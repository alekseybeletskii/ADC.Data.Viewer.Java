/*
 * ******************** BEGIN LICENSE BLOCK *********************************
 *
 * ADCDataViewer
 * Copyright (c) 2016 onward, Aleksey Beletskii  <beletskiial@gmail.com>
 * All rights reserved
 *
 * github: https://github.com/alekseybeletskii
 *
 * The ADCDataViewer software serves for visualization and simple processing
 * of any data recorded with Analog Digital Converters in binary or text form.
 *
 * Commercial support is available. To find out more contact the author directly.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *          list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *         this list of conditions and the following disclaimer in the documentation
 *         and/or other materials provided with the distribution.
 *
 * The software is distributed to You under terms of the GNU General Public
 * License. This means it is "free software". However, any program, using
 * ADCDataViewer _MUST_ be the "free software" as well.
 * See the GNU General Public License for more details
 * (file ./COPYING in the root of the distribution
 * or website <http://www.gnu.org/licenses/>)
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ******************** END LICENSE BLOCK ***********************************
 */

package adc.data.viewer.ui;


import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardWatchEventKinds.*;

public class WatchDirectory implements Runnable {


    private boolean suspendFlag;
    private Thread watcherThread;
    private Path directoryToWatch;
    private MainApp mainApp;
    private AtomicBoolean stop = new AtomicBoolean(false);

    WatchDirectory(Path directoryToWatch, MainApp mainApp, String threadname) {
        this.mainApp = mainApp;
        this.directoryToWatch = directoryToWatch;
        watcherThread = new Thread(this, threadname);
        suspendFlag = false;


    }

    public Thread getWatcherThread() {
        return watcherThread;
    }

    public boolean isStopped() {
        return stop.get();
    }

    public void stopThread() {
        stop.set(true);
    }

    public synchronized void suspendWatcher() {
        suspendFlag = true;
    }
    public synchronized void resumeWatcher() {
        suspendFlag = false;
        notifyAll();
    }

    @Override
    public void run() {

        try (final WatchService watcher = FileSystems.getDefault().newWatchService()) {

            directoryToWatch.register(watcher, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);

            while (!isStopped()) {
                final WatchKey key;
//                    System.out.println("next cycle");
                try {
                    key = watcher.take();
                    if (key == null) {
                        Thread.yield();
                        continue;
                    }

                } catch (InterruptedException e) {
                    return;
                }

                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    WatchEvent.Kind<?> watchEventKind = watchEvent.kind();
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) watchEvent;
                    Path newPath = directoryToWatch.resolve(pathEvent.context());

//                    int fullStopIndex = newPath.getFileName().toString().lastIndexOf(".");
//                    String fileExtension = newPath.getFileName().toString().substring(fullStopIndex + 1).toLowerCase();

                    if (watchEventKind == OVERFLOW) {
                        Thread.yield();
                        return;
                    }

                    if (watchEventKind == ENTRY_MODIFY&&newPath.toFile().length()!=0) {
                        List<Path> inpList = new ArrayList<>();
                        inpList.add(newPath);
                        System.out.println("newPath: " + newPath);
                        suspendFlag=true;
                        Platform.runLater(() -> {
                            mainApp.parse(inpList);
                        });
                    }

                    try {
                        Thread.sleep(1000);
                        synchronized(this) {
                            while (suspendFlag) {
                                System.out.println("waiting...");
                                wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
                Thread.yield();
            }
        } catch (IOException e) {
            e.printStackTrace();                }
    }


}
