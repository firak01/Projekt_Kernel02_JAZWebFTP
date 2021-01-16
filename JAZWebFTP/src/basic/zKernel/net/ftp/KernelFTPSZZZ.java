/*
 * Created on 14.01.2005
 * 
 */
package basic.zKernel.net.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zKernel.IKernelLogZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

/**
 * @author Lindhauer
 * This class is used as a wrapper arround the org.apache.commons.net.ftpclient - class
 * Therefore the jakarta.commons.net and the jakarta.orp - API must be in the classpath
 */
public class KernelFTPSZZZ extends KernelUseObjectZZZ {
	public static final String sPROTOCOL="SSL";
	
	private FTPSClient objFTPClient=null;
	private String sServer=new String("");
	private String sUser=new String("");
	private String sPassword=new String("");
	
	public KernelFTPSZZZ(IKernelZZZ objKernel, IKernelLogZZZ objLog, String[] saFlagControl) throws ExceptionZZZ {
		super(objKernel);
		KernelFTPnew_(objLog, saFlagControl);
	}
	
	private boolean KernelFTPnew_(IKernelLogZZZ objLog, String[] saFlagUsed) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
		String stemp; boolean btemp; String sLog;
		
		//setzen der übergebenen Flags
		if(saFlagUsed!=null) {
		  for(int iCount = 0;iCount<=saFlagUsed.length-1;iCount++){
			  stemp = saFlagUsed[iCount];
			  if(!StringZZZ.isEmpty(stemp)){
				  btemp = setFlag(stemp, true);
				  if(btemp==false){
					  sLog = "the flag '" + stemp + "' is not available.";
					  this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
					  ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName()); 
					  throw ez;		 
				  }
			  }
		  }
			if(this.getFlag("INIT")==true){
				bReturn = true;
				break main; 
			}	
		}
	}//end main:
	return bReturn;
}//end constructor


/**
 * @return FTPClient, from jakarta.commons.net
 */
public FTPSClient getClientObject(){
	if(this.objFTPClient==null) {
		this.objFTPClient = new FTPSClient(KernelFTPSZZZ.sPROTOCOL, false);
	}
	return this.objFTPClient;	
}

/**
 * @param objFTPClient, from jakarta.commons.net
 */
public void setClientObject(FTPSClient objFTPClient){
	this.objFTPClient=objFTPClient;
}

/**
 * Make a connection. Use Server-, User-, Password - Property. This properties must be initialized before. 
 * @return boolean, indicating the success of the method
 */
public boolean makeConnection() throws ExceptionZZZ{
	String sServer = this.getServer();
	String sUser = this.getUser();
	String sPassword = this.getPassword();
	return makeConnection_(sServer, sUser, sPassword);
}//end connectionMake()

/**
 * close a connection
 * @return boolean, indicating the success of the method
 * @throws ExceptionZZZ
 */
public boolean closeConnection() throws ExceptionZZZ{
	boolean bReturn=false;
	main:{
		String stemp;String sLog;
		try{
		
		FTPSClient objFTPCLient = this.getClientObject();
		if(objFTPClient!=null){
			objFTPClient.disconnect();
		}
		}catch(IOException ioe){
			stemp=ioe.getMessage();
			sLog = "IOException: '" + stemp + "'";
			this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), ioe); 
			throw ez;	
		}
	}//end main:
	return bReturn;
}

/**
 * @param sServer
 * @param sUser
 * @param sPassword
 * @return boolean, indicating the success of the method
 */
public boolean makeConnection(String sServer, String sUser, String sPassword) throws ExceptionZZZ{
	return makeConnection_(sServer, sUser, sPassword);
}

/**
 * @param sServer
 * @param sUser
 * @param sPassword
 * @return boolean, indicating the success of the method
 */
private boolean makeConnection_(String sServer, String sUser, String sPassword) throws ExceptionZZZ {
	boolean bReturn=false;
	main:{
		String stemp; String sLog;
		try{
			if(this.getClientObject()==null){				
				stemp = "FTPSClientObject'";				
				sLog = "Missing: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PROPERTY_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;					   
			}
			if(sServer==null){
				stemp = "Servername";
				sLog = "Missing: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;
			}else if(sServer.equals("")){
				stemp = "Servername";
				sLog = "Empty: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PROPERTY_EMPTY, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;
			}
			if(sUser==null){
				stemp = "User";
				sLog = "Missing: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;
			}else if(sUser.equals("")) {
				stemp = "User";
				sLog = "Empty: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_EMPTY, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;				
			}
					
			if(sPassword==null){
				stemp = "Password";
				sLog = "Missing: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;
			}else if(sPassword.equals("")) {
				stemp = "Password";
				sLog = "Empty: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_EMPTY, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;
			}
													
						
			///##################
			//Mache die Verbindung
			//20210111: DAS MUSS JETZT FTPSClient sein, der Server der Telekom hat sich geändert	
			//ABER, Fehlermeldung: Could not parse response code. Server Reply: SSH-2.0-OpenSSH_8.4
			//Zumindest der Port ist richtig. Aber FTPS ist scheinbar nicht SFTP.
			//Also brauche ich einen SFTP - Client, der SSH-2.0 versteht ..... 
			FTPSClient objFTPClient = this.getClientObject();
			objFTPClient.connect(sServer,22);
			objFTPClient.login(sUser,sPassword);
				
			int ireply = objFTPClient.getReplyCode();
			//Merke: Hier wird FTPReply verwendet um die Erfolgsmeldung zu checken
			if(!FTPReply.isPositiveCompletion(ireply)){
				//Merke: Verwende hier fuer den ExceptionZZZ-Fehlercode einen anderen als bei den technischen Fehlern
				objFTPClient.disconnect();
				
				sLog = "FTP server '"+sServer+"' refused connection for user '"+sUser+"'";
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());				
				throw ez;
			}

		} catch (SocketException se) {
			stemp= se.getMessage();
			sLog = "SocketException: '" + stemp + "'";
			this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), se); 
			throw ez;					
		} catch (IOException ioe) {
			stemp=ioe.getMessage();
			sLog = "IOException: '" + stemp + "'";
			this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), ioe); 
			throw ez;	
		}
		bReturn = true;
	}//end main
	return bReturn;
}//end method 'ConnctionMake_"

/**
 * Loads a file by FTP to the server. An established connection is necessary.
 * @param objFile, the sourceFile-Object
 * @param sFileTarget, if null or the empty String, the filename of the sourceFile-Object will be used
 * @return boolean, indicating the success of the method
 */
public boolean uploadFile(File objFile, String sFileTarget) throws ExceptionZZZ{
	return uploadFile_(objFile, sFileTarget);	
}
private boolean uploadFile_(File objFile, String sFileTargetIn) throws ExceptionZZZ{
	boolean bReturn = false;
		main:{
			String stemp; String sLog;
			try{
	
			if(objFile==null){
				stemp = "File-Object'";
				sLog = "Missing: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;
			}else if(!FileEasyZZZ.exists(objFile)){
				stemp = objFile.getAbsolutePath();
				sLog="File-Object --> File does not exist: '" + stemp + "'";				
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;
			}
			
			if(FileEasyZZZ.isDirectory(objFile)) {
				stemp = objFile.getAbsolutePath();
				sLog="File-Object --> File is a directory: '" + stemp + "'";				
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;
			}
			
			String sFileTarget;
			if(StringZZZ.isEmpty(sFileTargetIn)){
				sFileTarget = objFile.getName();		
			}else{
				sFileTarget=sFileTargetIn;
			}
			
			FTPClient objFTP = this.getClientObject();
			if(objFTP==null){
				stemp = "FTPClientObject'";				
				sLog = "Missing: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PROPERTY_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;	
			}
			
			//################################
			FileInputStream objFIS = new FileInputStream(objFile);
			//System.out.println("aaaa#"+sFileTarget+"    bbbb#"+objFile.getPath());
			//WARUM GEHT DAS NUR MIT KONSTANTEN FILENAMEN ??????????
			
			//bReturn = objFTP.storeFile("TEST.html", objFIS);
			//bReturn = objFTP.storeUniqueFile(sFileTarget, objFIS);
			//stemp = "."+File.separator+sFileTarget;
		
			//bReturn = objFTP.deleteFile(sFileTarget );
			bReturn = objFTP.storeFile(sFileTarget, objFIS);
			objFIS.close();
			
			}catch(FileNotFoundException fnfe){
				//Kann eigentlich wg. des expliziten Parameterchecks nicht auftreten.
				stemp = fnfe.getMessage();
				sLog = "FileNotFoundException: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), fnfe); 
				throw ez;
			} catch (IOException ioe) {
				stemp = ioe.getMessage();
				sLog = "IOException: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), ioe); 
				throw ez;
			}
			
			
		}//end main:
		return bReturn;
}


/**
	 * @return String
	 */
	public String getPassword() {
		return sPassword;
	}

	/**
	 * @return String
	 */
	public String getServer() {
		return sServer;
	}

	/**
	 * @return String
	 */
	public String getUser() {
		return sUser;
	}

	/**
	 * @param string
	 */
	public void setPassword(String string) {
		sPassword = string;
	}

	/**
	 * @param string
	 */
	public void setServer(String string) {
		sServer = string;
	}

	/**
	 * @param string
	 */
	public void setUser(String string) {
		sUser = string;
	}

}
