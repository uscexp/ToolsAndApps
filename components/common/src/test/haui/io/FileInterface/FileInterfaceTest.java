package haui.io.FileInterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Date;

import org.junit.Test;

public abstract class FileInterfaceTest {

    protected String currentPath = new File(".").getAbsolutePath();
    protected String testFile = "test.ext";
    protected FileInterface sut;

    @Test
    public void testGetTempDirectory() throws Exception {
        String tempDirectory = sut.getTempDirectory();

        String expectedTmpDir = new File(".").getAbsolutePath();

        assertEquals(expectedTmpDir, tempDirectory);
    }

    @Test
    public void testGetBufferedInputStream() throws Exception {
        BufferedInputStream bufferedInputStream = sut.getBufferedInputStream();

        assertNotNull(bufferedInputStream);
        assertTrue(bufferedInputStream.available() > 0);

        bufferedInputStream.close();
    }

    public void testGetBufferedOutputStream(String testFilepath) throws Exception {

        BufferedOutputStream bufferedOutputStream = sut.getBufferedOutputStream(testFilepath);

        assertNotNull(bufferedOutputStream);

        byte[] b = { 1, 2, 3, 4 };
        bufferedOutputStream.write(b);

        bufferedOutputStream.flush();
        bufferedOutputStream.close();

        sut.getFileInterfaceConfiguration().setCached(false);

        assertEquals(4, sut.length());
    }

    @Test
    public void testDuplicate() throws Exception {
        FileInterface duplicate = sut.duplicate();

        assertEquals(sut, duplicate);
    }

    @Test
    public void testGetFileInterfaceConfiguration() throws Exception {
        FileInterfaceConfiguration fileInterfaceConfiguration = sut.getFileInterfaceConfiguration();

        assertNotNull(fileInterfaceConfiguration);
    }

    @Test
    public void testSetFileInterfaceConfiguration() throws Exception {
        sut.setFileInterfaceConfiguration(null);

        assertNull(sut.getFileInterfaceConfiguration());
    }

    public void test_listRoots(FileInterface[] expected) throws Exception {
        FileInterface[] roots = sut._listRoots();

        assertEquals(expected, roots);
    }

    public void testEquals(FileInterface expected) throws Exception {
        assertEquals(expected, sut);
    }

    public void testIsCached(boolean expected) throws Exception {
        assertEquals(expected, sut.isCached());
    }

    public void testSeparatorChar(char expected) throws Exception {
        assertEquals(expected, sut.separatorChar());
    }

    public void testPathSeparatorChar(char expected) throws Exception {
        assertEquals(expected, sut.pathSeparatorChar());
    }

    @Test
    public void testCanRead() throws Exception {
        assertTrue(sut.canRead());
    }

    @Test
    public void testCanWrite() throws Exception {
        assertTrue(sut.canWrite());
    }

    public void testIsAbsolutePath(String strPath) throws Exception {
        assertTrue(sut.isAbsolutePath(strPath));
    }

    @Test
    public void testIsAbsolute() throws Exception {
        assertTrue(sut.isAbsolute());
    }

    @Test
    public abstract void testIsDirectory() throws Exception;

    @Test
    public abstract void testIsArchive() throws Exception;

    @Test
    public abstract void testIsFile() throws Exception;

    @Test
    public void testIsHidden() throws Exception {
        assertFalse(sut.isHidden());
    }

    @Test
    public void testIsRoot() throws Exception {
        assertFalse(sut.isRoot());
    }

    @Test
    public void testGetCanonicalPath() throws Exception {
        String canonicalPath = sut.getCanonicalPath();

        assertNotNull(canonicalPath);
    }

    @Test
    public void testGetCanonicalFile() throws Exception {
        FileInterface canonicalFile = sut.getCanonicalFile();

        assertEquals(sut, canonicalFile);
    }

    @Test
    public void testToURL() throws Exception {
        URL url = sut.toURL();

        assertNotNull(url);
    }

    @Test
    public void testLength() throws Exception {
        long length = sut.length();

        assertTrue(length > 0);
    }

    public void testGetId(String expected) throws Exception {
        String id = sut.getId();

        assertNotNull(id);
        assertEquals(expected, id);
    }

    public void testGetHost(String expected) throws Exception {
        String host = sut.getHost();

        assertNotNull(host);
        assertEquals(expected, host);
    }

    public void testGetName(String expected) throws Exception {
        String name = sut.getName();

        assertNotNull(name);
        assertEquals(expected, name);
    }

    public void testGetAbsolutePath(String expected) throws Exception {
        String absolutePath = sut.getAbsolutePath();

        assertNotNull(absolutePath);
        assertEquals(expected, absolutePath);
    }

    @Test
    public void testGetPath() throws Exception {
        String path = sut.getPath();

        assertNotNull(path);
    }

    public void testGetInternalPath(String expected) throws Exception {
        String internalPath = sut.getInternalPath();

        assertNotNull(internalPath);
        assertEquals(expected, internalPath);
    }

    @Test
    public void testGetDirectAccessFileInterface() throws Exception {
        FileInterface directAccessFileInterface = sut.getDirectAccessFileInterface();

        assertNotNull(directAccessFileInterface);
        assertEquals(FileInterface.LOCAL, directAccessFileInterface.getHost());
        assertEquals(sut.getName(), directAccessFileInterface.getName());
        assertEquals(sut.length(), directAccessFileInterface.length());
    }

    public void testGetParent(String expected) throws Exception {
        String parent = sut.getParent();

        assertNotNull(parent);
        assertEquals(expected, parent);
    }

    @Test
    public void testGetParentFileInterface() throws Exception {
        FileInterface parent = sut.getParentFileInterface();

        assertNotNull(parent);
    }

    public void testGetRootFileInterface(FileInterface expected) throws Exception {
        FileInterface rootFileInterface = sut.getRootFileInterface();

        assertNotNull(rootFileInterface);
        assertEquals(expected, rootFileInterface);
    }

    @Test
    public void testGetAppProperties() throws Exception {
        AppProperties appProperties = sut.getAppProperties();

        assertNotNull(appProperties);
    }

    public void testGetAppName(String expected) throws Exception {
        String appName = sut.getAppName();

        assertNotNull(appName);
        assertEquals(expected, appName);
    }

    @Test
    public void testLastModified() throws Exception {
        long lastModified = sut.lastModified();

        assertTrue(lastModified > 0);
    }

    @Test
    public void testSetLastModified() throws Exception {
        long time = new Date().getTime();

        sut.setLastModified(time);

        sut.getFileInterfaceConfiguration().setCached(false);
        long lastModified = sut.lastModified();

        assertEquals(time, lastModified);
    }

    public void testList(String[] expected) throws Exception {
        String[] list = sut.list();

        assertNotNull(list);
        assertEquals(expected, list);
    }

    public void test_listFilesFileInterfaceFilter(FileInterfaceFilter filter, boolean dontShowHidden, FileInterface[] expected) throws Exception {
        FileInterface[] listFiles = sut._listFiles(filter, dontShowHidden);

        assertNotNull(listFiles);
        assertEquals(expected, listFiles);
    }

    public void test_listFiles(FileInterface[] expected) throws Exception {
        FileInterface[] listFiles = sut._listFiles();

        assertNotNull(listFiles);
        assertEquals(expected, listFiles);
    }

    public void testRenameTo(FileInterface file) throws Exception {
        boolean result = sut.renameTo(file);

        assertTrue(result);
        assertEquals(file.getName(), sut.getName());
    }

    @Test
    public void testDelete() throws Exception {
        boolean result = sut.delete();

        assertTrue(result);
        assertFalse(sut.exists());
    }

    @Test
    public void testMkdir() throws Exception {
        boolean result = sut.mkdir();

        assertTrue(result);
        assertTrue(sut.exists());
    }

    @Test
    public void testMkdirs() throws Exception {
        boolean result = sut.mkdirs();

        assertTrue(result);
        assertTrue(sut.exists());
    }

    @Test
    public void testExists() throws Exception {
        boolean result = sut.exists();

        assertTrue(result);
    }

    @Test
    public void testLogon() throws Exception {
        throw new RuntimeException("not yet implemented");
    }

    @Test
    public void testCreateNewFile() throws Exception {
        boolean result = sut.createNewFile();

        assertTrue(result);
        assertTrue(sut.exists());
    }

    @Test
    public void testSetReadOnly() throws Exception {
        boolean result = sut.setReadOnly();

        assertTrue(result);
        assertFalse(sut.canRead());
    }

    @Test
    public abstract void testExec() throws Exception;

    public void testGetConnObj(Object expected) throws Exception {
        Object connObj = sut.getConnObj();

        assertEquals(expected, connObj);
    }

    @Test
    public void testCopyFileBufferedOutputStream() throws Exception {
        File newFile = new File("target");
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newFile));

            boolean result = sut.copyFile(bufferedOutputStream);

            assertTrue(result);
            assertTrue(newFile.exists());
            assertEquals(sut.length(), newFile.length());
        } finally {
            if (newFile != null && newFile.exists())
                newFile.delete();
        }
    }

    @Test
    public void testCompareTo() throws Exception {
        FileInterface duplicate = sut.duplicate();

        int compareTo = sut.compareTo(duplicate);

        assertEquals(0, compareTo);
    }

}
