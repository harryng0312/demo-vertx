package org.harryng.demo.vertx.mutiny;

import io.smallrye.mutiny.Context;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.core.file.AsyncFile;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FileSystemVertx {
    static Logger logger = LoggerFactory.getLogger(FileSystemVertx.class);
    Vertx vertx = null;

    private void initVertx() {
        var vertxOpts = new VertxOptions();
        vertx = Vertx.vertx(vertxOpts);
    }

    public Vertx getVertx() {
        if (vertx == null) {
            initVertx();
        }
        return vertx;
    }

    public void readFile() {
        getVertx().fileSystem()
                .readFile("./files/test.txt")
                .map(buffer -> {
                    logger.info("file content:"
                            + new String(buffer.getBytes(), StandardCharsets.UTF_8));
                    return buffer;
                })
                .onFailure().transform(err -> {
                    logger.info("", err);
                    return err;
                });
    }

    public void readBigFile() {
        var srcPath = "files/BadgeChainDemo.mp4";
//        var srcPath = "files/test2.txt";
        int buffSize = 1024 * 1024;
        getVertx().fileSystem().open(srcPath, new OpenOptions().setRead(true))
                .map(asyncFile -> {
                    logger.info("file read");
                    return asyncFile.setReadBufferSize(buffSize)
                            .handler(buffer -> {
                                logger.info("Buffsize: " + buffer.length());
                            })
//                            .endHandler(() -> logger.info( "end file!"))
                            .endHandler(() -> {
                                logger.info("end file!");
                                asyncFile.closeAndForget();
                            });
//                    return asyncFile;
                })
//                .flatMap(asyncFile -> {
//                    logger.info( "file closed");
//                    return asyncFile.close();
//                })
                .onFailure().transform(err -> {
                    logger.info("", err);
                    return err;
                })
                .subscribe().with(
                        item -> {
                        },//logger.info( "subscribe: " + item),
                        ex -> logger.info("error: ", ex)
                );
//                .await().indefinitely();
    }

    public void writeFile() {
        final var path = "files/test2.txt";
        final var data = "this is string data big";
        getVertx().fileSystem()
                .exists(path)
                .flatMap(existed -> {
                    if (!existed) {
                        getVertx().fileSystem().createFile(path);
                    }
                    return Uni.createFrom().voidItem();
                })
                .flatMap(v ->
                        getVertx().fileSystem()
                                .open(path, new OpenOptions()
                                        .setWrite(true))
                )
                .flatMap(asyncFile -> {
                    var buffer = Buffer.buffer(data, "utf-8");
                    return asyncFile.write(buffer).map(v -> asyncFile);
//                    var pump = Pump.pump(, asyncFile);
//                    return asyncFile;
                })
//                .flatMap(asyncFile -> asyncFile.flush().map(v -> asyncFile))
//                .flatMap(AsyncFile::close)
                .map(AsyncFile::flushAndForget)
                .map(asyncFile -> {
                    asyncFile.closeAndForget();
                    return asyncFile;
                })
                .onFailure().transform(Unchecked.function(err -> err))
                .await().indefinitely();

    }

    public void copyFileMulti() {
        var srcPath = "files/BadgeChainDemo.mp4";
        var destPath = "files/BadgeChainDemo_1.mp4";
        int buffSize = 256 * 1024;
        getVertx().fileSystem()
                .exists(srcPath)
                .flatMap(Unchecked.function(srcPathExisting -> {
                    if (srcPathExisting) {
                        return getVertx().fileSystem()
                                .open(srcPath, new OpenOptions()
                                        .setCreate(false)
                                        .setRead(true));
                    } else {
                        throw new FileNotFoundException(srcPath);
                    }
                }))
                .flatMap(Unchecked.function(srcAsyncFile -> getVertx().fileSystem().exists(destPath)))
                .flatMap(Unchecked.function(destPathExisting -> !destPathExisting ?
                        getVertx().fileSystem().createFile(destPath)
                        : getVertx().fileSystem().delete(destPath)))
                .flatMap(Unchecked.function(v -> getVertx().fileSystem().open(srcPath, new OpenOptions().setRead(true))))
                .onItem().transformToMulti(srcFile -> Multi.createFrom().<Buffer>emitter(multiEmitter ->
                        srcFile.setReadBufferSize(buffSize)
                                .handler(buffer -> {
                                    logger.info("read buffer size: " + buffer.length());
                                    multiEmitter.emit(buffer);
                                })
                                .endHandler(() -> {
                                    logger.info("Read file finished!");
                                    srcFile.closeAndForget();
                                })))
                .onItem().transformToMultiAndConcatenate(
                        buffer -> getVertx().fileSystem().open(destPath, new OpenOptions().setAppend(true))
                                .toMulti()
                                .flatMap(destFile -> {
                                    logger.info("write buffer size: " + buffer.length());
                                    return destFile.write(buffer)
//                                                .map(v -> destFile.flushAndForget())
                                            .toMulti()
                                            .map(v -> destFile.flushAndForget())
                                            .map(v -> destFile);
                                }))
//                .merge()
                .toUni()
//                .flatMap(itm -> itm)
                .map(destFile -> {
                    logger.info("Dest file size: " + destFile.sizeBlocking());
                    destFile.closeAndForget();
                    return destFile;
                })
                .onFailure().invoke(ex -> logger.info("Exception: ", ex))
                .subscribe().with(
                        item -> logger.info("read size: " + item.sizeBlocking()),
                        failure -> System.out.println("Failed with " + failure)//,
//                        () -> System.out.println("Completed")
                );
    }

    public void copyFileMulti2() {
//        var srcPath = "/mnt/working/downloads/film/Rat Disaster 2021 ViE 1080p WEB-DL DD2.0 H.264 (Thuyet Minh - Sub Viet).mkv";
        var srcPath = "/mnt/working/downloads/film/Stay.Alive.2006.KSTE.avi";
        var destPath = "files/testbig.avi";
        int buffSize = 1024 * 1024;

        getVertx().fileSystem()
                .exists(srcPath)
                .flatMap(Unchecked.function(srcPathExisting -> {
                    if (srcPathExisting) {
                        return getVertx().fileSystem()
                                .open(srcPath, new OpenOptions()
                                        .setCreate(false)
                                        .setRead(true));
                    } else {
                        throw new FileNotFoundException(srcPath);
                    }
                }))
                .flatMap(Unchecked.function(srcAsyncFile -> getVertx().fileSystem().exists(destPath)))
                .flatMap(Unchecked.function(destPathExisting -> !destPathExisting ?
                        getVertx().fileSystem().createFile(destPath)
                        : getVertx().fileSystem().delete(destPath)))
                .flatMap(Unchecked.function(v -> getVertx().fileSystem().open(srcPath, new OpenOptions().setRead(true))))
                .onItem().transformToMulti(srcFile -> Multi.createFrom().<Buffer>emitter(multiEmitter -> {
                    var srcIndex = new AtomicInteger();
                    srcFile.setReadBufferSize(buffSize).handler(buffer -> {
//                                logger.info( "read:" + new String(buffer.getBytes()) + "|");
                                srcIndex.getAndIncrement();
                                multiEmitter.emit(buffer);
                            })
                            .endHandler(() -> {
                                logger.info("Read file finished! " + srcIndex.intValue());
                                srcFile.closeAndForget();
                            });
                }, buffSize * 16))
                .concatMap(buffer -> getVertx().fileSystem().open(destPath, new OpenOptions().setAppend(true))
                        .toMulti()
                        .flatMap(destFile -> {
//                            logger.info( "write:" + new String(buffer.getBytes()) + "|");
                            return destFile.write(buffer)
                                    .map(v -> destFile.flushAndForget()).toMulti();
//                                            .map(v -> destFile).toMulti();
//                                            .toMulti().map(v -> destFile);
                        }))
                .onItem().transformToUniAndConcatenate(destFile -> {
//                    logger.info( "Write count: " + destIndex.intValue());
                    return Uni.createFrom().item(destFile);
                })
                .map(asyncFile -> {
                    logger.info("Dest file is closing!");
                    asyncFile.closeAndForget();
                    return asyncFile;
                })
                .onFailure().invoke(ex -> logger.info("Exception: ", ex))
                .subscribe().with(
                        item -> {
                        },//logger.info( "Read size: " + item.sizeBlocking()),
                        failure -> logger.info("Failed with " + failure, failure)//,
//                        () -> System.out.println("Completed")
                );
    }

    public void copyFile() {
        var srcPath = "files/BadgeChainDemo.mp4";
        var destPath = "files/BadgeChainDemo_1.mp4";
        int buffSize = 1024 * 1024;
        int buffElement = 32;

        Map<String, Object> contextVars = new HashMap<>();
        contextVars.put("srcCount", 0);
        contextVars.put("destCount", 0);

        var context = Context.from(contextVars);

        getVertx().fileSystem()
                .exists(srcPath)
                .flatMap(Unchecked.function(srcPathExisting -> {
                    if (srcPathExisting) {
                        return getVertx().fileSystem()
                                .open(srcPath, new OpenOptions()
                                        .setCreate(false)
                                        .setRead(true));
                    } else {
                        throw new FileNotFoundException(srcPath);
                    }
                }))
                .flatMap(Unchecked.function(srcAsyncFile -> getVertx().fileSystem().exists(destPath)))
                .flatMap(Unchecked.function(destPathExisting -> !destPathExisting ?
                        getVertx().fileSystem().createFile(destPath)
                        : getVertx().fileSystem().delete(destPath)))
                .flatMap(Unchecked.function(v -> getVertx().fileSystem().open(srcPath, new OpenOptions().setRead(true))))
                .attachContext()
                .map(srcFileWithCtx -> {
                    AsyncFile srcFile = srcFileWithCtx.get();
                    srcFileWithCtx.context().put("srcFileGlobal", srcFile);
                    return srcFile;
                })
                .attachContext()
                .onItem().transformToMulti(srcFileWithCtx -> Multi.createFrom().<Buffer>emitter(multiEmitter -> {
                    AsyncFile srcFile = srcFileWithCtx.get();
                    srcFile.setReadBufferSize(buffSize)
                            .handler(buffer -> {
                                multiEmitter.emit(buffer);
                                var srcCount = srcFileWithCtx.context()
                                        .<Integer>get("srcCount");
                                var srcFileGlobal = srcFileWithCtx.context()
                                        .<AsyncFile>get("srcFileGlobal");
                                var sc = ++srcCount;
                                if (sc % buffElement == 0) {
                                    srcFileGlobal.pause();
                                    logger.info("Src pause!");
                                }
                            })
                            .endHandler(() -> {
                                srcFile.closeAndForget();
                                srcFileWithCtx.context().delete("srcFileGlobal");
                                multiEmitter.complete();
                                logger.info("Src file is closed!");
                            });
                }))
                .concatMap(buffer -> getVertx().fileSystem().open(destPath, new OpenOptions().setAppend(true))
                        .attachContext()
                        .map(destFileWithCtx -> {
                            AsyncFile destFile = destFileWithCtx.get();
                            destFileWithCtx.context().put("destFileGlobal", destFile);
                            return destFile;
                        })
                        .toMulti()
                        .flatMap(destFile -> {
                            destFile.writeAndForget(buffer);
                            return Multi.createFrom().item(destFile.flushAndForget());
                        }))
                .attachContext()
                .map(destFileWithCtx -> {
                    var srcCount = destFileWithCtx.context()
                            .<Integer>get("srcCount");
                    var destCount = destFileWithCtx.context()
                            .<Integer>get("destCount");
                    var dc = ++destCount;
                    AsyncFile destFile = destFileWithCtx.get();
                    if (dc.equals(srcCount) && destFileWithCtx.context().contains("srcFileGlobal")) {
                        var srcFileGlobal = destFileWithCtx.context().<AsyncFile>get("srcFileGlobal");
                        srcFileGlobal.resume();
                        logger.info("Src resume!");
                    }
                    return destFile;
                })
                .collect().last().invoke(destFile -> {
                    logger.info("Dest file is closed!");
                    destFile.closeAndForget();
                })
                .onFailure().invoke(ex -> logger.info("Exception: ", ex))
                .subscribe().with(context, item -> {
                    logger.info("Copied done!");
                }, failure -> {
                    logger.error("", failure);
                });
    }
}
