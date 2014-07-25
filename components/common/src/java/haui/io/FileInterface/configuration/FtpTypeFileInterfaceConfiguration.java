/**
 * 
 */
package haui.io.FileInterface.configuration;

import haui.exception.AppSystemException;
import haui.io.FileInterface.vfs.FtpConnectionObject;
import haui.util.AppProperties;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

/**
 * Configuration for FTP type file.
 * 
 * @author ae
 * 
 */
public class FtpTypeFileInterfaceConfiguration extends VfsTypeFileInterfaceConfiguration {

    /** protokoll://hostname[: port][ relative-path] */
    public static final String URI_FORMAT = "%s%s:%d/%s";

    protected FtpConnectionObject connectionObject;

    public FtpTypeFileInterfaceConfiguration(String appName, AppProperties props, boolean cached, FtpConnectionObject connectionObject) {
        super(appName, props, cached);
        this.connectionObject = connectionObject;
        StaticUserAuthenticator userAuthenticator = new StaticUserAuthenticator(connectionObject.getHost(), connectionObject.getUsr(), connectionObject.getPwd());
        try {
            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fileSystemOptions, userAuthenticator);
            FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
            FtpFileSystemConfigBuilder.getInstance().setDataTimeout(fileSystemOptions, 10000);
            FtpFileSystemConfigBuilder.getInstance().setSoTimeout(fileSystemOptions, 10000);
        } catch (FileSystemException e) {
            throw new AppSystemException(e);
        }
    }

    public String getUri(String path) {
        return String.format(URI_FORMAT, connectionObject.getProtokoll().getUriPrefix(), connectionObject.getHost(), connectionObject.getPort(), path);
    }

    public FtpConnectionObject getConnectionObject() {
        return connectionObject;
    }
}
