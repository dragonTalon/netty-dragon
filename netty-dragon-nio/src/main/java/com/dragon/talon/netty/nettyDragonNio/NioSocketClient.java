package com.dragon.talon.netty.nettyDragonNio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioSocketClient {
    public static final String LOCALHOST = "127.0.0.1"; 
    public static void main(String[] args) {
        new Thread(new TimeClientHandle(),"client").start();
    }
}
class TimeClientHandle implements Runnable{
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;
    
    public TimeClientHandle(){
        this.host = NioSocketClient.LOCALHOST;
        this.port = NioSocketService.PORT;
        try{
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void run() {
        try{
            doConnect();
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop){
            try{
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                
                while (iterator.hasNext()){
                    key = iterator.next();
                    iterator.remove();
                    try{
                        handleInput(key);
                    }catch (Exception e){
                        key.cancel();
                        if (key.channel() != null){
                            key.channel().close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        if (selector != null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()){
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()){
                if (sc.finishConnect()){
                    sc.register(selector,SelectionKey.OP_READ);
                    doWrite(sc);
                }else {
                    System.exit(1);
                }
            }
            if (key.isReadable()){
                 ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                final int read = sc.read(readBuffer);
                if (read>0){
                    readBuffer.flip();
                    final byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"utf-8");
                    System.out.println("now is :" + body);
                    this.stop = true;

                }else if (read <0){
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    private void doConnect() throws IOException {
        //如果连接成功，则注册到多路复用器上，发送请求消息
        if (socketChannel.connect(new InetSocketAddress(host,port))){
            socketChannel.register(selector,SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else {
            socketChannel.register(selector,SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel socketChannel) throws IOException {
        byte[] req = "QUERY TIME ORDER".getBytes();
        final ByteBuffer allocate = ByteBuffer.allocate(req.length);
        allocate.put(req);
        allocate.flip();
        socketChannel.write(allocate);
        if (!allocate.hasRemaining()){
            System.out.println("Send order 2 server succeed.");
        }
    }


}