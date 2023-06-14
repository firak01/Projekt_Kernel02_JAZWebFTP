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
import java.nio.file.Paths;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.character.CharZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.machine.EnvironmentZZZ;
import basic.zKernel.IKernelLogZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

/**
 * @author Lindhauer
 * This class is used as a wrapper arround the org.apache.commons.net.ftpclient - class
 * Therefore the jakarta.commons.net and the jakarta.orp - API must be in the classpath
 */
public class KernelSFTPZZZ extends KernelUseObjectZZZ {
	public static final String sPROTOCOL="sftp";
		
	 private JSch objFTPClient = null;
	 private Session objSession = null;
	
	private String sServer=new String("");
	private String sUser=new String("");
	private String sPassword=new String("");
	private String sRootPath=new String("");
	private int iPort=22;
	
	private char cDirectorySeparatorLocal = StringZZZ.string2Char(FileEasyZZZ.sDIRECTORY_SEPARATOR);
	private char cDirectorySeparatorRemote = StringZZZ.string2Char(FileEasyZZZ.sDIRECTORY_SEPARATOR_UNIX);
	
	public KernelSFTPZZZ(IKernelZZZ objKernel, IKernelLogZZZ objLog, String[] saFlagControl) throws ExceptionZZZ {
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
 * @return FTPClient
 */
public JSch getClientObject() throws ExceptionZZZ, SftpException {
	if(this.objFTPClient==null) {
		//https://stackoverflow.com/questions/34413/why-am-i-getting-a-noclassdeffounderror-in-java		
		try {
			this.objFTPClient = new JSch();//Darum hier eine Exception abfangen, wg. des statischen Konstruktors.
		}catch(Throwable t) {				
			t.printStackTrace();
			
			String sLog = t.getMessage();			  
			this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName()); 
			throw ez;	
		}
	}
	return this.objFTPClient;	
}

/**
 * @param objFTPClient
 */
public void setClientObject(JSch objFTPClient){
	this.objFTPClient=objFTPClient;
}

public Session getSession() {
	return this.objSession;
}
public void setSession(Session objSession) {
	this.objSession = objSession;
}


public File getKnownHostFile() throws ExceptionZZZ {
	 File objReturn = null;
	 main:{
		 String HOME = EnvironmentZZZ.getUserHome(); //System.getProperty("user.home");
		//jsch.setKnownHosts("Dokumente und Einstellungen/Users/lindhaueradmin/.ssh/known_hosts");
		 String knownHostsFileName = Paths.get(HOME, ".ssh", "known_hosts").toString();
		 System.out.println(ReflectCodeZZZ.getPositionCurrent() +": knownHostsFileName='" + knownHostsFileName + "'");
		 if (knownHostsFileName != null) {
			 objReturn = new File(knownHostsFileName);			 
		 }
	 }//end main:
	 return objReturn;
}

/**Hier wird der Pfad zur knownHosts Datei übergeben, die normalerweise im Benutzerordner, Unterordner .ssh liegt.
 * Damit aber der Host darin bekannt ist, folgendes unter Windows Git-Bash (von GitGui) ausführen:
 * ssh-keyscan -H -t rsa hosting.telekom.de >> known_hosts
 * Das erzeugt einen HashString in der Datei für den genannten Server.
 * @return
 * @throws ExceptionZZZ
 * @author Fritz Lindhauer, 15.01.2021, 12:11:57
 */
public boolean setKnownHost() throws ExceptionZZZ{
	boolean bReturn = false;
	main:{
		String sLog;
		try {
			File knownHostsFile = this.getKnownHostFile();
			if(FileEasyZZZ.exists(knownHostsFile)) {
				 this.getClientObject().setKnownHosts(knownHostsFile.getAbsolutePath());
				 sLog = "KnownHostsFile added";
				 System.out.println(ReflectCodeZZZ.getPositionCurrent() +": " + sLog );
				 bReturn = true;
			 }
		}catch(JSchException je) {
			sLog = "JSchException: " +je.getMessage();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() +": " + sLog );
			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, KernelSFTPZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName(),je);
			throw ez;
		} catch (SftpException e) {	
			sLog = "JSchException: " +e.getMessage();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() +": " + sLog );
			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, KernelSFTPZZZ.class.getName(), ReflectCodeZZZ.getMethodCurrentName(),e);
			throw ez;
		}
	}//End main:
	return bReturn;
}


/**
 * Make a connection. Use Server-, User-, Password - Property. This properties must be initialized before. 
 * @return boolean, indicating the success of the method
 */
public boolean makeConnection() throws ExceptionZZZ{
	String sServer = this.getServer();
	String sUser = this.getUser();
	String sPassword = this.getPassword();
	int iPort = this.getPort();
	return makeConnection_(sServer, sUser, sPassword, iPort);
	
	
	
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
//		try{
		
		//JSch objFTPCLient = this.getFTPClientObject();
		Session objSession = this.getSession();
		if(objSession!=null){
			if(objSession.isConnected()) {
				objSession.disconnect();
			}
		}
//		}catch(IOException ioe){
//			stemp=ioe.getMessage();
//			sLog = "IOException: '" + stemp + "'";
//			this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
//			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), ioe); 
//			throw ez;	
//		}
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
	int iPort = this.getPort();
	return makeConnection_(sServer, sUser, sPassword, iPort);
}

/**
 * @param sServer
 * @param sUser
 * @param sPassword
 * @return boolean, indicating the success of the method
 */
private boolean makeConnection_(String sServer, String sUser, String sPassword, int iPort) throws ExceptionZZZ {
	boolean bReturn=false;
	main:{
		String stemp; String sLog;
		try{
		    JSch objFTPClient = this.getClientObject();		  
			if(objFTPClient==null){				
				stemp = "FTPClientObject'";				
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
			//Setze zuerst die "knownHosts"
			this.setKnownHost();
			
			
			//Mache die Verbindung
			//20210111: DAS MUSS JETZT FTPSClient sein, der Server der Telekom hat sich geändert	
			//ABER, Fehlermeldung: Could not parse response code. Server Reply: SSH-2.0-OpenSSH_8.4
			//Zumindest der Port ist richtig. Aber FTPS ist scheinbar nicht SFTP.
			//Also brauche ich einen SFTP - Client, der SSH-2.0 versteht ..... 
		    Session jschSession = objFTPClient.getSession(sUser, sServer, iPort);
		    jschSession.setPassword(sPassword);
		    jschSession.connect();
		    this.setSession(jschSession);
		    bReturn = true;
		    
		} catch (JSchException je) {
			stemp= je.getMessage();
			sLog = "JScheException: '" + stemp + "'";
			this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), je); 
			throw ez;				
		} catch (SftpException e) {
			stemp= e.getMessage();
			sLog = "SftpException: '" + stemp + "'";
			this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), e); 
			throw ez;				
		}
//		} catch (SocketException se) {
//			stemp= se.getMessage();
//			sLog = "SocketException: '" + stemp + "'";
//			this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
//			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), se); 
//			throw ez;					
//		} catch (IOException ioe) {
//			stemp=ioe.getMessage();
//			sLog = "IOException: '" + stemp + "'";
//			this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
//			ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), ioe); 
//			throw ez;	
//		}
 
	}//end main
	return bReturn;
}//end method 'ConnctionMake_"


/**
 * Loads a file by FTP to the server. An established connection is necessary.
 * @param objFile, the sourceFile-Object
 * @param sFileTarget, if null or the empty String, the filename of the sourceFile-Object will be used
 * @return boolean, indicating the success of the method
 * @throws JSchException 
 */
public boolean uploadFile(File objFile, String sFilePathTarget, String sFileNameTarget) throws ExceptionZZZ, JSchException{
	return uploadFile_(objFile, sFilePathTarget, sFileNameTarget);	
}

/**
 * Loads a file by FTP to the server. An established connection is necessary.
 * @param objFile, the sourceFile-Object
 * @param sFileTarget, if null or the empty String, the filename of the sourceFile-Object will be used
 * @return boolean, indicating the success of the method
 * @throws JSchException 
 */
public boolean uploadFile(File objFile, String sFilePathTarget) throws ExceptionZZZ, JSchException{
	return uploadFile_(objFile, sFilePathTarget, "");	
}
private boolean uploadFile_(File objFile, String sDirTargetIn, String sFileNameTargetIn) throws ExceptionZZZ, JSchException{
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
			
			String sFileNameTarget;
			if(StringZZZ.isEmpty(sFileNameTargetIn)) {
				sFileNameTarget = objFile.getName();
			}else {
				sFileNameTarget = sFileNameTargetIn;
			}
			
			if(FileEasyZZZ.isDirectory(objFile)) {
				stemp = objFile.getAbsolutePath();
				sLog="File-Object --> File is a directory: '" + stemp + "'";				
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;
			}
			
			String sDirTarget;
			if(StringZZZ.isEmpty(sDirTargetIn)){
				sDirTarget = objFile.getName();		
			}else{
				sDirTarget=sDirTargetIn;
			}
			
			Session objSession = this.getSession();
			if(objSession==null){
				stemp = "SessionObject'";				
				sLog = "Missing: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PROPERTY_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName()); 
				throw ez;	
			}
			
			 ChannelSftp objChannel = (ChannelSftp) objSession.openChannel(KernelSFTPZZZ.sPROTOCOL);
			 objChannel.connect();

			 	String localFile = objFile.getAbsolutePath();
			 	
			 	//Hier auch ein FileEasyZZZ.join... machen, aber mit "/" als Verzeichnistrenner.
			 	//In der Konfiguration ggfs. sicherstellen, das ein führender "/" vorangestellt ist.
			 	//ABER: Der lokale Root-Eintrag (z.B. in Eclipse 'src' darf nicht vorangestellt werden, darum 'remote'=true.
			 	String sRemoteRoot = this.getRootPath();
			 	String sRemoteDirPathTotal = FileEasyZZZ.joinFilePathNameForRemote(sRemoteRoot, sDirTarget, this.getDirectorySeparatorRemote());
			 	String sRemoteFilePathTotal = FileEasyZZZ.joinFilePathNameForRemote(sRemoteDirPathTotal, sFileNameTarget, this.getDirectorySeparatorRemote());
			 	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": sRemoteFilePathTotal='"+sRemoteFilePathTotal+"'");
			 	this.mkdirs(sDirTarget); //Das sind Pfade unterhalb des Server-RootPfads
			 	
			 	//wichtig: Der Remote-Pfad auf dem Server muss mit Slash / anfangen!!!
			 	objChannel.put(localFile, sRemoteFilePathTotal);
			    objChannel.exit();
			    bReturn = true;    						   
//			} catch (JSchException je) {
//				stemp = je.getMessage();
//				sLog = "JSchException: '" + stemp + "'";
//				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
//				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), je); 
//				throw ez;					
			} catch (SftpException sftpe) {
				stemp = sftpe.getMessage();
				sLog = "SftpException: '" + stemp + "'";
				this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_RUNTIME, this,  ReflectCodeZZZ.getMethodCurrentName(), sftpe); 
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
	 * @param string
	 */
	public void setPassword(String string) {
		sPassword = string;
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
	public void setUser(String string) {
		sUser = string;
	}

	
	/**
	 * @return String
	 */
	public String getRootPath() {
		return this.sRootPath;
	}


	/**
	 * @param string
	 */
	public void setRootPath(String sRootPath) {
		this.sRootPath = sRootPath;
	}
	
	public int getPort() {
		return this.iPort;
	}
	
	public void setPort(int iPort) {
		this.iPort = iPort;
	}
	
	/**
	 * @return String
	 */
	public String getServer() {
		return sServer;
	}


	/**
	 * @param string
	 */
	public void setServer(String string) {
		sServer = string;
	}

	public char getDirectorySeparatorRemote() {
		return this.cDirectorySeparatorRemote;
	}
	public void setDirectorySeparatorRemote(char c) {
		this.cDirectorySeparatorRemote = c;
	}
	
	public char getDirectorySeparatorLocal() {
		return this.cDirectorySeparatorLocal;
	}
	public void setDirectorySeparatorLocal(char c) {
		this.cDirectorySeparatorLocal = c;
	}
	
	/** Erstelle alle Verzeichnisse auf dem FTP Server.
	 *  Zu beachten, ist, dass ggfs. auf den Verzeichnissen keine Rechte existieren, dann würden Sie ebenfalls neu erstellt.
	 *  Beispiel:
	 *  Das ist z.B. für den Server-Root-Pfad der Fall. z.B. T-Online: /home/www/
	 *  Wenn man nun einen Pfad /home/www/bla/bub übergibt, wird letztendlich folgender Verzeichnispfad erstellt:
	 *  /home/www/home/www/bla/bub
	 * @param sDirectoryPath
	 * @return
	 * @author Fritz Lindhauer, 06.02.2021, 17:19:56
	 * siehe: https://stackoverflow.com/questions/12838767/use-jsch-to-put-a-file-to-the-remote-directory-and-if-the-directory-does-not-exi
	 */
	public boolean mkdirs(String sDirectoryPath) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			String stemp; String sLog;
			try {
				if(StringZZZ.isEmpty(sDirectoryPath)) break main;
				
				if(FileEasyZZZ.isPathAbsolut(sDirectoryPath)) {
					sLog = "Pfad ist absolut. Solch ein Pfad kann auf dem FTP Server nicht erstellt werden '" + sDirectoryPath + "'.";
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": "+sLog);
					ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PARAMETER_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName()); 
					throw ez;	
				}
				
				Session objSession = this.getSession();
				if(objSession==null){
					stemp = "SessionObject'";				
					sLog = "Missing: '" + stemp + "'";
					this.logLineDate(ReflectCodeZZZ.getMethodCurrentName() + ": " + sLog);
					ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PROPERTY_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName()); 
					throw ez;	
				}
				
				 ChannelSftp objChannel = (ChannelSftp) objSession.openChannel(KernelSFTPZZZ.sPROTOCOL);
				 objChannel.connect();
				
				String[] folders = sDirectoryPath.split( CharZZZ.toString(this.cDirectorySeparatorRemote) );
				for ( String folder : folders ) {
						if ( folder.length() > 0 ) {
							try {
								objChannel.cd(folder);
								}
							catch ( SftpException e ) {
								try {
									objChannel.mkdir( folder );
									objChannel.cd( folder );
								} catch (SftpException sftpe) {
									sLog="Unable to create folder '" + folder + "' for total Path '"+ sDirectoryPath + "'";
									ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PROPERTY_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName(), sftpe); 
									throw ez;
								}
								
							}
						}
				}	
				
				bReturn = true;
			} catch (JSchException jsche) {
				sLog="Error creating folders for total Path '"+ sDirectoryPath + "'";
				ExceptionZZZ ez = new ExceptionZZZ(sLog, iERROR_PROPERTY_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName(), jsche); 
				throw ez;
			}
		}
		return bReturn;
	}

}
