package pa.am.scipioutils_android.socket.demo;

import java.util.Scanner;

/**
 * Class: NioServerSelector
 * Description:
 * Author: Alan Min
 * Create Date: 2019/9/18
 */
public class NioControlThread implements Runnable {

    private NioSocketServer server;
    private NioSocketClient client;

    private String outputPrefix="";//输出到控制台的前缀
    private String clientCloseCmd;//客户端通知服务器要关闭的字符串

    public NioControlThread(NioSocketServer server) {
        this.server = server;
    }

    public NioControlThread(NioSocketClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println(outputPrefix+"控制台子线程启动");
        try {
            Scanner scanner=new Scanner(System.in);
            while (true)
            {
                String inputStr=scanner.next();
                //停止命令
                if( inputStr.toLowerCase().equals("stop") )
                {
                    if(server!=null)
                    {
                        server.close();
                    }
                    else if(client!=null)//客户端关闭需要先向服务器发送消息，收到反馈了再关闭
                    {
                        client.setWriteMsg(clientCloseCmd);
                    }
                    break;
                }
                //向另一端输出消息
                else if( !inputStr.equals("") )
                {
                    if(server!=null)
                        server.setWriteMsg(inputStr);
                    else if(client!=null)
                        client.setWriteMsg(inputStr);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(outputPrefix+"控制台子线程结束");
    }//end of run()

    /**
     * 设置输出控制台语句的前缀
     */
    public void setOutputPrefix(String outputPrefix){
        this.outputPrefix=outputPrefix;
    }

    public void setClientCloseCmd(String clientCloseCmd){
        this.clientCloseCmd=clientCloseCmd;
    }

}
