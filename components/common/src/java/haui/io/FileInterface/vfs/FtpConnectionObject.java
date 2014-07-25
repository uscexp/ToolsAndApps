/**
 * 
 */
package haui.io.FileInterface.vfs;

import haui.io.FileInterface.FtpTypeFile;

/**
 * Connection object for {@link FtpTypeFile}s.
 * 
 * @author ae
 * 
 */
public class FtpConnectionObject {

    public enum Protokoll {
        FTP, FTPS, SFTP;

        private String protokollName;
        private String uriPrefix;

        private Protokoll() {
            this.protokollName = this.name().toLowerCase();
            this.uriPrefix = protokollName + "://";
        }

        public String getProtokollName() {
            return protokollName;
        }

        public String getUriPrefix() {
            return uriPrefix;
        }
    }

    final Protokoll protokoll;
    final private String host;
    final private int port;
    final private String usr;
    final private String pwd;

    public FtpConnectionObject(String host, int port, String usr, String pwd, Protokoll protokoll) {
        super();
        this.host = host;
        this.port = port;
        this.usr = usr;
        this.pwd = pwd;
        this.protokoll = protokoll;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsr() {
        return usr;
    }

    public String getPwd() {
        return pwd;
    }

    public Protokoll getProtokoll() {
        return protokoll;
    }
}
