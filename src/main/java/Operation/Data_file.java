package Operation;

import java.io.*;

/**
 * @author xuyang
 * 2019.12.29
 * 文档接口
 */
public class Data_file {


    /**
     * 打开文件，返回文件流string
     * @param filePath
     * @return
     */
   public static String open_file(String filePath){
        File f = new File(filePath);
        InputStream in =null;
        String fileStream="";

        try {

            if( f.isFile() &&  f.exists()){ //判断文件是否存在
                // 一次读多个字节

                BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(f),"utf-8"));
                String line="";
                while((line=br.readLine())!=null) {
//                    System.out.println(line.length());
                    fileStream+=line+"\n";
                }
            }else{
                System.out.println("找不到指定的文件,请确认文件路径是否正确");
            }



        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }

        return fileStream;
    }


    /**
     * 读取文件，返回操作结果
     * @param filePath
     * @param str
     * @return
     */
    public static boolean writeFile(String filePath, String str) {
        FileWriter fw;
        try {

            fw = new FileWriter(filePath);
            PrintWriter out = new PrintWriter(fw);
            out.write(str);
            out.println();
            fw.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static String open_file_line(String filePath){
        File f = new File(filePath);
        InputStream in =null;
        String fileStream="";

        try {

            if( f.isFile() &&  f.exists()){ //判断文件是否存在
                // 一次读多个字节

                BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(f),"utf-8"));

                while((fileStream=br.readLine())!=null) {
//                    System.out.println(line.length());
                }
            }else{
                System.out.println("找不到指定的文件,请确认文件路径是否正确");
            }



        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }

        return fileStream;
    }

}
