/*
 * Created on 14.01.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package custom.zKernel.net.ftp;

import org.apache.commons.net.ftp.FTPClient;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelLogZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.net.ftp.KernelFTPSZZZ;
import basic.zKernel.net.ftp.KernelFTPZZZ;
import basic.zKernel.net.ftp.KernelSFTPZZZ;


/**
 * @author Lindhauer
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SFTPZZZ extends KernelSFTPZZZ {

	/**
	 * @param objKernel
	 * @param objLog
	 * @param saFlagControl
	 * @throws ExceptionZZZ
	 */
	public SFTPZZZ(IKernelZZZ objKernel, IKernelLogZZZ objLog, String[] saFlagControl) throws ExceptionZZZ {
		super(objKernel, objLog, saFlagControl);
	}
 
}//end class FTPZZZ
