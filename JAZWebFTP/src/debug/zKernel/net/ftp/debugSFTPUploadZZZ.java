/*
 * Created on 14.01.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package debug.zKernel.net.ftp;

import java.io.File;
import java.io.IOException;

import com.jcraft.jsch.JSchException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.character.CharZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zKernel.IKernelConfigSectionEntryZZZ;
import basic.zKernel.IKernelLogZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelSingletonZZZ;
import basic.zKernel.KernelZZZ;
import custom.zKernel.file.ini.FileIniZZZ;
import custom.zKernel.net.ftp.FTPSZZZ;
import custom.zKernel.net.ftp.FTPZZZ;
import custom.zKernel.net.ftp.SFTPZZZ;


/**
 * @author Lindhauer
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class debugSFTPUploadZZZ {

	public static void main(String[] args) {
		System.out.println("Start");
			main:{
				boolean btemp;
				try {

			//1. Erstellen das Z-Kernel Objekt							
			IKernelZZZ objKernel = KernelSingletonZZZ.getInstance("FGL", "01", "", "ZKernelConfigFTP_test.ini",(String[]) null);
			
			//2. Protokoll
			IKernelLogZZZ objLog = objKernel.getLogObject();

			//3. FTPZZZ-Objekt, als Wrapper um jakarta.commons.net.ftpclient
			SFTPZZZ objFTP = new SFTPZZZ(objKernel, objLog, (String[]) null);
			char c = StringZZZ.string2Char(FileEasyZZZ.sDIRECTORY_SEPARATOR_UNIX);
			objFTP.setDirectorySeparatorRemote(c);
						
			//4. Konfiguration auslesen
			//Hier werden Informationen �ber die IP-Adressdatei ausgelesen, etc.
			FileIniZZZ objFileIniIPConfig = objKernel.getFileConfigIniByAlias("FTPDebug");
   								
			IKernelConfigSectionEntryZZZ entryServer = objKernel.getParameterByProgramAlias(objFileIniIPConfig, "FTPModul","Server");
			String sServer = entryServer.getValue();
			
			IKernelConfigSectionEntryZZZ entryUser = objKernel.getParameterByProgramAlias(objFileIniIPConfig, "FTPModul","User");
			String sUser = entryUser.getValue();
			
			IKernelConfigSectionEntryZZZ entryPassword = objKernel.getParameterByProgramAlias(objFileIniIPConfig, "FTPModul","Password");
			String sPassword = entryPassword.getValue();
			
			System.out.println("Page Transfer - Login detail read from file: "+sServer + " ("+sUser+" - "+sPassword+")");
  
			//5. Login
			btemp = objFTP.makeConnection(sServer, sUser, sPassword);
			if (btemp==true) {
				System.out.println("Connection - successfull, now transfering file");
			}else {
				System.out.println("Connection - NOT successfull, end program");
				break main;
			}
			
			//6. Datei ermitteln und �bertragen
			//TODO Mit eine @Z-Formel in der Konfiguration DIESES Programms auslesen, die auf den wert in der konfiguration eines anderen Programms hinweist.
			//Hier die Konfiguration direkt auslesen
			/*
				 * TargetDirectory=c:\temp
TargetFile=testpage.html
				 */
			
			
			IKernelConfigSectionEntryZZZ entryDirSource=objKernel.getParameterByProgramAlias(objFileIniIPConfig, "FTPModul","SourceDirectory");
			String sDirSource = entryDirSource.getValue();
			
			//Also eigentlich objKernel.getParameterByProgramAlias(objFileIniIPConfgi, "ProgFTP","SourceDirectory");
			IKernelConfigSectionEntryZZZ entryFile = objKernel.getParameterByProgramAlias(objFileIniIPConfig, "FTPModul","SourceFile");
			String sFile = entryFile.getValue();
			
			//Also eigentlich objKernel.getParameterByProgramAlias(objFileIniIPConfgi, "ProgFTP","SourceFile");
			String sFilePath = FileEasyZZZ.joinFilePathName(sDirSource, sFile);
			File objFile = new File(sFilePath);
			if(!FileEasyZZZ.exists(objFile)){
				System.out.println("File not found '"+sFilePath);
			}else{
				IKernelConfigSectionEntryZZZ entryDirTarget=objKernel.getParameterByProgramAlias(objFileIniIPConfig, "FTPModul","TargetDirectory");
				String sDirTarget = entryDirTarget.getValue();
				StringZZZ.replace(sDirTarget, FileEasyZZZ.sDIRECTORY_SEPARATOR_WINDOWS, CharZZZ.toString(objFTP.getDirectorySeparatorRemote()));
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": DirTarget='"+sDirTarget+"'");
				
				//Dateiname bleibt gleich, also nicht extra auslesen.
				//IKernelConfigSectionEntryZZZ entryFileTarget = objKernel.getParameterByProgramAlias(objFileIniIPConfig, "FTPModul","TargetFile");
				//String sFileTarget = entryFileTarget.getValue();				
				String sFileTargetTotal = FileEasyZZZ.joinFilePathName(sDirTarget, sFile, objFTP.getDirectorySeparatorRemote(), true); //Merke: Bei dem Remote-Pfad soll sichergestellt sein, dass kein src-Root Ordner voranagestellt ist.
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FileTargetName: "+sFileTargetTotal);
																
				btemp = objFTP.uploadFile(objFile, sDirTarget);
			}
			
			
			//7. Verbindung schliessen
			if (btemp==true){
				System.out.println("Transfer - successfull, now disconnecting"); 
			}else{
				 System.out.println("Transfer - NOT successfull, now disconnecting");					
			}
			objFTP.closeConnection();

			
			} catch (ExceptionZZZ ez) {
					System.out.println(ez.getDetailAllLast());
			} catch (JSchException jsche) {
				System.out.println(jsche.getMessage());
			}
//			} catch (IOException e) {					
//				e.printStackTrace();
//			} 
			System.out.println("Ende");
		}//end main:
	}//end method 
}//end class
