package com.dragon.talon.netty.nettyDragonNio;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NioSocketService {
    public static final Integer PORT = 8080;

    public static void main(String[] args) throws IOException {
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(PORT);
/*
        final Thread thread = new Thread(timeServer, "NIO_MultipexrtimeServer-001");
        thread.start();
        thread.isAlive();*/
        final ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        System.out.println(threadGroup.getName());
    }
}

class MultiplexerTimeServer implements Runnable {
    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            final ServerSocketChannel servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("the time server is start in port" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    public void run() {
        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //多路复用关闭后，所有注册的channel和pipe会被自动去注册并关闭，所以不需要重复释放资源
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //处理新接入的请求消息
            if (key.isAcceptable()) {
                //Accept the new connection
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "utf-8");
                    System.out.println("The time Server receive order : " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis
                            ()).toString() : "BAD ORDER";
                    doWrite(sc, currentTime);
                } else if (readBytes < 0) {
                    //对端链路关闭
                    key.cancel();
                    sc.close();
                }

            }
        }
    }

    private void doWrite(SocketChannel sc, String currentTime) throws IOException {
        if (currentTime != null && currentTime.trim().length() > 0) {
            byte[] bytes = currentTime.getBytes();
            ByteBuffer allocate = ByteBuffer.allocate(bytes.length);
            allocate.put(bytes);
            allocate.flip();
            sc.write(allocate);
        }
    }
}
