package pers.es.rebuild;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("all")
public class ReadFileUtil {
	
	/**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     */
    public static List<String> readTxtFile(String filePath){
    	List<String> list= new ArrayList<String>();
        try {
                String encoding=getCharset(filePath);
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                       
                        list.add(lineTxt);
                    }
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
		return list;
     
    }
	/**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     */

    public static String readTxtFileALL(String filePath){
        StringBuffer stringBuffer=new StringBuffer("");
        try {
                String encoding=getCharset(filePath);
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){

                        stringBuffer.append(lineTxt);
                    }
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
		return stringBuffer.toString();

    }

	public static String getCharset(String fileName) throws IOException{  
        
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));    
        int p = (bin.read() << 8) + bin.read();    
          
        String code = null;    
          
        switch (p) {    
            case 15476:    
                code = "UTF-8";    
                break;    
            case 59061:    
            	code = "UTF-8";    
            	break;    
            case 0xfffe:    
                code = "Unicode";    
                break;    
            case 0xfeff:    
                code = "UTF-16BE";    
                break;    
            default:    
                code = "GBK";    
        }    
        bin.close();
        return code;  
} 
     
    public static void main(String argv[]){

        File file=new File(".");
        String filePath = file.getAbsoluteFile().getParent()+"\\SpringBootES\\src\\main\\resources\\template\\mapping.txt";
/*
        List<String> readTxtFile = readTxtFile(filePath);
        for (String line : readTxtFile) {
        	line=line.replaceAll("\\t", "");
        	line=line.replaceAll("\"", "\\\\\"");
        	System.out.println("\""+line.trim()+"\"+");
		}*/
        String s = readTxtFileALL(filePath);
        System.out.println(s);
    }

}