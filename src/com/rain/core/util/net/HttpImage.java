package com.rain.core.util.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpImage {

	public static enum ImageMsg{
		ERROR_IMG_LINK("IMG-404","不存在的图片超链接"),NET_CONNECT_FAIL("NET-500","网络不通，无法下载网络图片!"),
		IMAGE_CREATE_SUCCESS("IMG-200","图片创建成功"),IMAGE_CREATE_FAIL("IMG-500","图片创建失败");
		
		private String msg;
		private String code;
		private ImageMsg(String code,String msg){
			this.msg = msg;
		}
		public String getMsg(){
			return msg;
		}
		public String getCode(){
			return code;
		}
	}
	
	public static ImageMsg downLoadHttpImage(String imageUrl,String path,String imageName){
		
		byte[] imageData = null;
		try {
			imageData = getImageFromNetByUrl(imageUrl);
		} catch (Exception e) {
			return ImageMsg.NET_CONNECT_FAIL;
		}
		if (imageData == null)
			return ImageMsg.ERROR_IMG_LINK;
		
		boolean flag = createImage(imageData,path,imageName);
		return flag ? ImageMsg.IMAGE_CREATE_SUCCESS : ImageMsg.IMAGE_CREATE_FAIL;
	}
	
	/**
	 * 创建图片文件
	 * @param imageData
	 * @param path
	 * @param imageName
	 * @return
	 */
	public static boolean createImage(byte[] imageData,String path,String imageName){
		File image = new File(path,imageName);
		
		try{
			OutputStream os = new FileOutputStream(image);
		    os.write(imageData);
		    os.flush();
		    os.close();
		    return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * 从网络上读取流数据
	 * @param imageUrl
	 * @return
	 * @throws Exception 
	 */
	public static byte[] getImageFromNetByUrl(String imageUrl) throws Exception{
		try{
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10*1000);
			InputStream  is = conn.getInputStream();
			byte[] imageData = readInputStream(is);
			return imageData;
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}
	
	  /** 
     * 从输入流中获取数据 
     * @param inStream 输入流 
     * @return 
     * @throws Exception 
     */  
    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        byte[] data =  outStream.toByteArray(); 
        outStream.close();
        return data;
    }  
}  
	
	

