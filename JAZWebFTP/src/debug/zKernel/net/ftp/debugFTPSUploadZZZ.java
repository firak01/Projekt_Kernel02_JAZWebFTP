/*
 * Created on 14.01.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package debug.zKernel.net.ftp;

import java.io.File;
import java.io.IOException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zKernel.IKernelConfigSectionEntryZZZ;
import basic.zKernel.IKernelLogZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelSingletonZZZ;
import basic.zKernel.KernelZZZ;
import custom.zKernel.file.ini.FileIniZZZ;
import custom.zKernel.net.ftp.FTPSZZZ;
import custom.zKernel.net.ftp.FTPZZZ;


/**
 * @author Lindhauer
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class debugFTPSUploadZZZ {

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
			FTPSZZZ objFTP = new FTPSZZZ(objKernel, objLog, (String[]) null);
			
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
			if (btemp==true) System.out.println("Connction - successfull, now transfering file");
			
			//6. Datei ermitteln und �bertragen
			//TODO Mit eine @Z-Formel in der Konfiguration DIESES Programms auslesen, die auf den wert in der konfiguration eines anderen Programms hinweist.
			//Hier die Konfiguration direkt auslesen
			/*
				 * TargetDirectory=c:\temp
TargetFile=testpage.html
				 */
			IKernelConfigSectionEntryZZZ entryDir=objKernel.getParameterByProgramAlias(objFileIniIPConfig, "ProgPage","TargetDirectory");
			String sDir = entryDir.getValue();
			
			//Also eigentlich objKernel.getParameterByProgramAlias(objFileIniIPConfgi, "ProgFTP","SourceDirectory");
			IKernelConfigSectionEntryZZZ entryFile = objKernel.getParameterByProgramAlias(objFileIniIPConfig, "ProgPage","TargetFile");
			String sFile = entryFile.getValue();
			
			//Also eigentlich objKernel.getParameterByProgramAlias(objFileIniIPConfgi, "ProgFTP","SourceFile");
			String sFilePath = FileEasyZZZ.joinFilePathName(sDir, sFile);
			File objFile = new File(sFilePath);
			if(!FileEasyZZZ.exists(objFile)){
				System.out.println("File not found '"+sFilePath);
			}else{
				IKernelConfigSectionEntryZZZ entryFileTarget = objKernel.getParameterByProgramAlias(objFileIniIPConfig, "ProgFTP","TargetFile");
				String sFileTarget = entryFileTarget.getValue();
				System.out.println("FileTargetName: "+sFileTarget);
				btemp = objFTP.uploadFile(objFile, sFileTarget);
			}
			
			
			//7. Verbindung schliessen
			if (btemp==true){
				System.out.println("Transfer - successfull, now disconnecting"); 
			}else{
				 System.out.println("Transfer - NOT successfull, now disconnecting");					
			}
			objFTP.getClientObject().disconnect();

			
			} catch (ExceptionZZZ ez) {
					System.out.println(ez.getDetailAllLast());
			} catch (IOException e) {					
				e.printStackTrace();
			} 
			System.out.println("Ende");
		}//end main:
	}//end method main
}//end class
