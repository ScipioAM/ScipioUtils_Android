package pa.am.scipioutils_android.io;

import java.io.*;
import java.nio.channels.FileChannel;

import pa.am.scipioutils_android.http.HttpConst;
import pa.am.scipioutils_android.http.HttpStreamUtil;

/**
 * Class: FileUtil
 * Description: v1.7
 *  文件操作工具
 * Author: Alan Min
 * Create Time: 2018/5/3
 */
public class FileUtil {

    private FileUtil(){}

    /**
     * 拷贝文件
     * param sourcePath: 源路径
     * param targetPath: 目标路径
     * return: 拷贝结果：0成功，-1源文件不存在，-2目标文件已存在
     */
    public static Long copyFile(String sourcePath, String targetPath) {
        long result=0L;
        File sourceFile=new File(sourcePath);
        File targetFile=new File(targetPath);

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            if(targetFile.exists())
            {
                result=-2L;
                return result;
            }
            if(!sourceFile.exists())
            {
                result=-1L;
                return result;
            }
            fi = new FileInputStream(sourceFile);
            fo = new FileOutputStream(targetFile);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            result=in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(fi!=null)
                    fi.close();
                if(in!=null)
                    in.close();
                if(fo!=null)
                    fo.close();
                if(out!=null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//end of finally
        return result;
    }

    /**
     *删除指定文件夹及其所有子文件夹
     */
    public static Boolean delAllFiles(File dir) {
        System.out.println(dir.getParent());
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            if(children==null)
                return true;
            for (String child:children)//递归删除子目录下的所有文件
            {
                boolean success = delAllFiles(new File(dir, child));
                if (!success) {
                    return false;
                }
            }//end of for
        }//end of outside if
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     *删除指定文件夹下的所有文件(本身不删)
     */
    public static Boolean delFiles(String dirPath) {
        boolean isSuccess=true;
        File dir=new File(dirPath);
        if(dir.isDirectory())
        {
            File[] children=dir.listFiles();
            if(children==null)
                return true;
            for (File f : children) {
                if(!f.delete())
                {
                    isSuccess=false;
                    break;
                }
            }//end of for
        }
        return isSuccess;
    }

    public static void writeBytes(File file, byte[] data) throws IOException {
        OutputStream fos = null;
        OutputStream os = null;
        try {
            fos = new FileOutputStream(file);
            os = new BufferedOutputStream(fos);
            os.write(data);
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if (os != null)
                os.close();
            if (fos != null)
                fos.close();
        }//end of finally
    }//end of writeBytes()

    /**
     * 检查文件夹下是否有文件（不包括子文件夹里）
     * param dirPath 文件夹路径
     * return 子文件个数
     */
    public static Integer checkDirectory(String dirPath)
    {
        File dir=new File(dirPath);
        if(!dir.exists())
            return 0;
        if(!dir.isDirectory())
            return 0;
        String[] childNames=dir.list();
        if(childNames==null)
            return 0;
        else
            return childNames.length;
    }

    /**
     * 从网上保存文件到本地
     * param imgUrl：源文件url
     * param savePath：文件保存路径
     * param fileName：文件名
     * return 是否成功
     */
    public static Boolean saveFileFromNet(String fileUrl,String savePath,String fileName) {
        try {
            HttpStreamUtil httpStreamUtil=new HttpStreamUtil();
            InputStream is=  httpStreamUtil.requestStream(fileUrl, HttpConst.REQUEST_GET);
            File saveFile=new File(savePath+File.separatorChar+fileName);
            if(!saveFile.exists())
            {
                if(!saveFile.createNewFile())
                {
                    System.out.println("****** 创建本地文件失败!!! ******");
                    return false;
                }
            }
            else
            {
                if(!saveFile.delete())
                {
                    System.out.println("****** 原有本地文件删除失败!!! ******");
                    return false;
                }
                if(!saveFile.createNewFile())
                {
                    System.out.println("****** 创建本地文件失败!!! ******");
                    return false;
                }
            }
            DataInputStream dis=new DataInputStream(is);
            DataOutputStream dos=new DataOutputStream(new FileOutputStream(saveFile));
            int SIZE=1024;//缓冲区尺寸
            byte[] b = new byte[SIZE];
            int n;
            while((n=dis.read(b))!=-1){
                dos.write(b, 0, n);
            }
            dis.close();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }//end of catch
        return true;
    }

    /**
     * 将字符串内容写入一份文件里
     * param filePath: 文件路径（不包括文件名）
     * param filePath: 文件名（包括后缀）
     * param content: 写入内容
     * param isNeedNewLine: 是否需要新起一行写入文本
     */
    public static boolean writeTxtFile( String filePath, String fileName, String content, boolean isNeedNewLine) {
        File parentDir=new File(filePath);
        File file=new File(filePath+File.separatorChar+fileName);
        BufferedWriter writer;
        try {
            if(!parentDir.exists())//父目录不存在
            {
                if(!parentDir.mkdirs())//创建父目录
                    throw new RuntimeException("Error: file's parent directories created failed!");
                if(file.createNewFile())//创建文件（父目录不存在，则文件肯定不存在）
                {
                    writer=new BufferedWriter(new FileWriter(file));
                    writer.write(content);
                }
                else
                    throw new RuntimeException("Error: file created failed!");
            }
            else//父目录存在
            {
                if(file.exists())//文件存在
                {
                    writer=new BufferedWriter(new FileWriter(file,true));
                    if(isNeedNewLine)
                        writer.newLine();
                    writer.write(content);
                }
                else//文件不存在
                {
                    if(!file.createNewFile())//创建文件
                        throw new RuntimeException("Error: file created failed!");
                    writer=new BufferedWriter(new FileWriter(file));
                    writer.write(content);
                }
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据文件地址和给定的输出流，将本地文件输出
     * @param filePath 本地文件地址
     * @param os 输出流
     */
    public static void outPutLocalFile(String filePath,OutputStream os)
    {
        File imageFile = new File(filePath);//获取本地文件对象
        if (imageFile.exists()) { //检查这个文件是否存在
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imageFile);//打开本地文件输入流，也就是开始读取本地文件
                int count;
                byte[] buffer = new byte[1024 * 8];//每次读取的缓冲区，其大小决定了读取速度
                while ((count = fis.read(buffer)) != -1) {  //read()方法
                    os.write(buffer, 0, count);//把读出的每一块，向客户端输出
                    os.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fis!=null)//写完了不等于本身是null
                        fis.close();
                    if(os!=null)
                        os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//end of finally
        }//end of if
    }

}
