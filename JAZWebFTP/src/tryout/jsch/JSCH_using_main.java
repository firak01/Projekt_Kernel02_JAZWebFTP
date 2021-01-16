package tryout.jsch;

import java.io.File;
import java.nio.file.Paths;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class JSCH_using_main {
	private static String username = "fgl@fgl.homepage.t-online.de";
	private static String password = "1fgl2fgl";
	private static String remoteHost = "hosting.telekom.de";
	
	//ssh-keyscan -H -t rsa REMOTE_HOSTNAME >> known_hosts
	
	//merke:
	//ssh-keyscan is a command from Linux/Unix. 
	//In order to execute it on Windows, you need a version that has been modified to run on Windows. 
	//Easiest is to install git-for-windows(download) and open Git Bash. 
	//Inside this console you can use the ssh-keyscan command.
	//
	//also:
	//GitBash starten
	//ssh-keyscan -H -t rsa hosting.telekom.de >> .ssh\\known_hosts
	
	
	public static void main(String[] args) {
		try {
			whenUploadFileUsingJsch_thenSuccess();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void whenUploadFileUsingJsch_thenSuccess() throws JSchException, SftpException {
	    ChannelSftp channelSftp = setupJsch();
	    channelSftp.connect();
	 
	    String localFile = "src/file/debugTestContent.txt";
	    String remoteDir = "/home/www/debug/";
	 
	    channelSftp.put(localFile, remoteDir + "debugTestContent.txt");
	 
	    channelSftp.exit();
	}

	private static ChannelSftp setupJsch() throws JSchException {
	    JSch jsch = new JSch();
 
	    //jsch.setKnownHosts("Dokumente und Einstellungen/Users/lindhaueradmin/.ssh/known_hosts");
	    String HOME = System.getProperty("user.home");
        String knownHostsFileName = Paths.get(HOME, ".ssh", "known_hosts").toString();
        System.out.println("knownHostsFileName: " + knownHostsFileName);

        if (knownHostsFileName != null && new File(knownHostsFileName).exists()) {
            jsch.setKnownHosts(knownHostsFileName);
            System.out.println("KnownHostsFile added");
        }
        
	    Session jschSession = jsch.getSession(username, remoteHost,22);
	    jschSession.setPassword(password);
	    jschSession.connect();
	    return (ChannelSftp) jschSession.openChannel("sftp");
	}
	
}
