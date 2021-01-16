package tryout.sshj;

import java.io.IOException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

/** This example demonstrates downloading of a file over SFTP from the SSH server. */
public class SFTPDownload {

    public static void main(String[] args){
    	String sLog;
    	try {
        final SSHClient ssh = new SSHClient();
        //ssh.loadKnownHosts();
        ssh.authPassword("fgl@fgl.homepage.t-online.de", "1fgl2fgl");
        ssh.connect("hosting.telekom.de");
        try {
            //ssh.authPublickey(System.getProperty("user.name"));
        	ssh.authPublickey("fgl@fgl.homepage.t-online.de");
            final SFTPClient sftp = ssh.newSFTPClient();
            try {
                sftp.get("test_file", new FileSystemFile("/tmp"));
            } finally {
                sftp.close();
            }
        } finally {
            ssh.disconnect();
        }
    	}catch(IOException ioe) {
    		sLog = ioe.getMessage();
    		System.out.println("Error: " + sLog);
    	}
    }

}