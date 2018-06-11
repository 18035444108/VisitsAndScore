package com.lx.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.golaxy.main.SingleScoreRequest;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException; 
public class BatchRequest {
	/**log*/  
	private static final Logger log = Logger.getLogger(SingleScoreRequest.class);
  
    public static final String NO_FILE = "No such file";  
  
    private ChannelSftp sftp = null;  
  
    private Session sshSession = null;  
  
    private String username;  
  
    private String password;  
  
    private String host;  
  
    private int port;  
  
    public BatchRequest(String username, String password, String host, int port) {  
        this.username = username;  
        this.password = password;  
        this.host = host;  
        this.port = port;  
    }  
  
    /** 
     * 连接sftp服务器 
     * @return ChannelSftp sftp类型 
     * @throws GoPayException 
     */  
    public ChannelSftp connect()  {  
    	System.out.println("---------");
        log.info("FtpUtil-->connect--ftp连接开始>>>>>>host=" + host + ">>>port" + port + ">>>username=" + username);  
        JSch jsch = new JSch();  
        try {  
            jsch.getSession(username, host, port);  
            sshSession = jsch.getSession(username, host, port);  
            log.info("ftp---Session created.");  
            sshSession.setPassword(password);  
            Properties properties = new Properties();  
            properties.put("StrictHostKeyChecking", "no");  
            sshSession.setConfig(properties);  
            sshSession.connect();  
            log.info("ftp---Session connected.");  
            Channel channel = sshSession.openChannel("sftp");  
            channel.connect();  
            log.info("Opening Channel.");  
            sftp = (ChannelSftp) channel;  
            log.info("ftp---Connected to " + host);  
        }  
        catch (JSchException e) {  
//            throw new GoPayException("FtpUtil-->connect异常" + e.getMessage());  
        }  
        return sftp;  
    }  
  
    /** 
     * 载单个文件 
     * @param directory       ：远程下载目录(以路径符号结束) 
     * @param remoteFileName  FTP服务器文件名称 如：xxx.txt ||xxx.txt.zip 
     * @param localFile       本地文件路径 如 D:\\xxx.txt 
     * @return 
     * @throws GoPayException 
     */  
    public File downloadFile(String directory, String remoteFileName,String localFile)  {  
        log.info(">>>>>>>>FtpUtil-->downloadFile--ftp下载文件"+remoteFileName+"开始>>>>>>>>>>>>>");  
        connect();  
        File file = null;  
        OutputStream output = null;  
        try {  
            file = new File(localFile);  
            if (file.exists()){  
                file.delete();  
            }  
            file.createNewFile();  
            sftp.cd(directory);  
            output = new FileOutputStream(file);  
            sftp.get(remoteFileName, output);  
            log.info("===DownloadFile:" + remoteFileName + " success from sftp.");  
        }  
        catch (SftpException e) {  
            if (e.toString().equals(NO_FILE)) {  
                log.info(">>>>>>>>FtpUtil-->downloadFile--ftp下载文件失败" + directory +remoteFileName+ "不存在>>>>>>>>>>>>>");  
                System.out.println("FtpUtil-->downloadFile--ftp下载文件失败" + directory +remoteFileName + "不存在");  
            }  
            System.out.println("ftp目录或者文件异常，检查ftp目录和文件" + e.toString()); 
        }  
        catch (FileNotFoundException e) {  
            System.out.println("本地目录异常，请检查" + file.getPath() +"    "+ e.getMessage());  
        }  
        catch (IOException e) {  
        	System.out.println("创建本地文件失败" + file.getPath() + e.getMessage());
        }  
        finally {  
            if (output != null) {  
                try {  
                    output.close();  
                }  
                catch (IOException e) {  
                	System.out.println("Close stream error."+ e.getMessage());
                }  
            }  
            disconnect();  
        }  
  
        log.info(">>>>>>>>FtpUtil-->downloadFile--ftp下载文件结束>>>>>>>>>>>>>");  
        return file;  
    }  
  
    /** 
     * 上传单个文件 
     * @param directory      ：远程下载目录(以路径符号结束) 
     * @param uploadFilePath 要上传的文件 如：D:\\test\\xxx.txt 
     * @param fileName       FTP服务器文件名称 如：xxx.txt ||xxx.txt.zip 
     * @throws GoPayException 
     */  
    public void uploadFile(String directory, String uploadFilePath, String fileName)  
             {  
        log.info(">>>>>>>>FtpUtil-->uploadFile--ftp上传文件开始>>>>>>>>>>>>>");  
        FileInputStream in = null;  
        connect();  
        try {  
            sftp.cd(directory);  
        }  
        catch (SftpException e) {  
            try {  
                sftp.mkdir(directory);  
                sftp.cd(directory);  
            }  
            catch (SftpException e1) {  
               System.out.println("ftp创建文件路径失败，路径为" + directory);
            }  
  
        }  
        File file = new File(uploadFilePath);  
        try {  
            in = new FileInputStream(file);  
            sftp.put(in, fileName);  
        }  
        catch (FileNotFoundException e) {  
            System.out.println("文件不存在-->" + uploadFilePath);  
        }  
        catch (SftpException e) {  
        	System.out.println("sftp异常-->" + e.getMessage());
        }  
        finally {  
            if (in != null){  
                try {  
                    in.close();  
                }  
                catch (IOException e) {  
                    System.out.println("Close stream error."+ e.getMessage());  
                }  
            }  
            disconnect();  
        }  
        log.info(">>>>>>>>FtpUtil-->uploadFile--ftp上传文件结束>>>>>>>>>>>>>");  
    }  
  
//    private synchronized static File certTempEmptyile() {  
//        String dirPath = SystemConfig.getProperty("down_settle_file.temp_path");  
//        FileUtil.mkDir(dirPath);  
//        String newFileName = System.currentTimeMillis() + ".txt";  
//        String filePath = dirPath + File.separator + newFileName;  
//        File file = new File(filePath);  
//        return file;  
//    }  
  
    /** 
     * 关闭连接 
     */  
    public void disconnect() {  
        if (this.sftp != null) {  
            if (this.sftp.isConnected()) {  
                this.sftp.disconnect();  
                this.sftp = null;  
                log.info("sftp is closed already");  
            }  
        }  
        if (this.sshSession != null) {  
            if (this.sshSession.isConnected()) {  
                this.sshSession.disconnect();  
                this.sshSession = null;  
                log.info("sshSession is closed already");  
            }  
        }  
    }  
    
    public static void main(String[] args) {
    	BatchRequest batchRequest = new BatchRequest("cuftp", "cuftp2018", "202.108.211.109", 21013);
    	batchRequest.connect();
    	batchRequest.uploadFile("/request/1","/home/lixiang/req_1_20180522150344_.json.gz", "req_1_20180522150344_.json.gz");
    	System.out.println("上传完成！");
    	try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	batchRequest.downloadFile("/response/1", "*.json.gz", "D:/resp_1_20180524113200.json.gz");
    	System.out.println("下载完成！");
    	
	}
}
