package pa.am.scipioutils_android.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class: SocketHandler
 *  传统socket，相关操作的装饰者类
 * Description: 传统socket是阻塞式的
 * Author: Alan Min
 * Createtime: 2018/7/17
 */
public class BioSocketHandler {

    private Socket socket;

    private BufferedReader reader;
    private PrintWriter writer;

    public BioSocketHandler(Socket socket){
        this.socket=socket;
    }

    //================================================================================

    /**
     *  发送数据
     * @param isNeedShutdown 发送后是否立即关闭输出流（注意，不关闭则无法接着接收）
     */
    public void send(boolean isNeedShutdown,String... messages) throws IOException {
        doSend(socket,isNeedShutdown,messages);
    }

    //默认发送一次，直接关闭输出流
    public void send(String... messages) throws IOException {
        doSend(socket,true,messages);
    }

    /**
     *  接收数据
     * @param isNeedShutdown 发送后是否立即关闭输入流（注意，不关闭则无法接着发送）
     */
    public String receive(boolean isNeedShutdown) throws IOException {
        return doReceive(socket,isNeedShutdown);
    }

    //默认接受一次，直接关闭输入流
    public String receive() throws IOException {
        return doReceive(socket,true);
    }

    //================================================================================

    /**
     * 关闭所有
     */
    public void close() throws IOException {
        if(reader!=null)
            reader.close();
        if(writer!=null)
            writer.close();

        shutdownInput();
        shutdownOutput();
        socket.close();
    }

    /**
     *关闭输入流
     */
    public void shutdownInput() throws IOException {
        if(!socket.isInputShutdown())
            socket.shutdownInput();
    }

    /**
     *关闭输出流
     */
    public void shutdownOutput() throws IOException {
        if(!socket.isOutputShutdown())
            socket.shutdownOutput();
    }

    //================================================================================

    /**
     * 发送的实现
     * @param socket socket对象
     * @param isNeedShutdown 是否需要立即关闭输入流
     * @param messages 要发送的消息
     */
    private void doSend(Socket socket,boolean isNeedShutdown,String... messages) throws IOException {
        if(messages==null)
            throw new RuntimeException("[Error]parameter messages is null");
        if(socket==null)
            throw new RuntimeException("[Error]socket is null");

        OutputStream os;
        //获取输出流
        if( (os=socket.getOutputStream()) ==null)
            throw new RuntimeException("[Error]the OutputStream which get from socket is null");

        writer=new PrintWriter(os);
        //开始输出
        for(String msg:messages)
        {
            writer.write(msg);
            writer.flush();
        }
        if(isNeedShutdown)
            socket.shutdownOutput();
    }

    /**
     * 接收的实现
     * @param socket socket对象
     * @param isNeedShutdown 是否需要立即关闭输入流
     */
    private String doReceive(Socket socket,boolean isNeedShutdown) throws IOException {
        InputStream is;
        StringBuilder strBuilder;
        if(socket==null)
            throw new RuntimeException("[Error]socket is null");
        //获取输入流
        if( (is=socket.getInputStream()) ==null)
            throw new RuntimeException("[Error]the OutputStream which get from socket is null");

        //开始读取输入流中的数据
        strBuilder=new StringBuilder();
        reader=new BufferedReader(new InputStreamReader(is));
        String str;
        while( (str=reader.readLine()) != null )
        {
            strBuilder.append(str);
        }

        if(isNeedShutdown)
            socket.shutdownInput();
        return strBuilder.toString();
    }

}