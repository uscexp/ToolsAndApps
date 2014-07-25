package haui.io.FileInterface;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.filter.WildcardFileInterfaceFilter;

import java.io.File;
import java.io.FileFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NormalFileTest extends FileInterfaceTest {

    private File tempDir = null;
    private FileInterfaceConfiguration fileInterfaceConfiguration;
    private String filename;

    @Before
    public void setup() throws Exception {
        tempDir = new File(System.getProperty("java.io.tmpdir"));
        fileInterfaceConfiguration = FileConnector.createFileInterfaceConfiguration(FileInterface.LOCAL, 0, null, null, 0, 0, "test", null, true);
        filename = tempDir.getAbsoluteFile() + File.separator + "testfile";
        File file = new File(filename);
        file.createNewFile();
        sut = (NormalFile) FileConnector.createFileInterface(file.getAbsolutePath(), null, false, fileInterfaceConfiguration);
    }

    @After
    public void cleanup() {
        if (sut.exists()) {
            sut.delete();
        }
    }

    private File setSutForDirectory() {
        File dir = new File(tempDir.getAbsoluteFile() + File.separator + "testdir");

        assertTrue(dir.mkdir());

        sut = (NormalFile) FileConnector.createFileInterface(dir.getAbsolutePath(), null, false, fileInterfaceConfiguration);

        return dir;
    }

    @Override
    public void testIsDirectory() throws Exception {
        assertFalse(sut.isDirectory());

        setSutForDirectory();

        assertTrue(sut.isDirectory());
    }

    @Override
    public void testIsArchive() throws Exception {
        assertFalse(sut.isArchive());
    }

    @Override
    public void testIsFile() throws Exception {
        assertTrue(sut.isFile());
    }

    @Override
    public void testExec() throws Exception {
        // TODO Auto-generated method stub

    }

    @Test
    public void testGetBufferedOutputStream() throws Exception {
        String testFilepath = sut.getAbsolutePath();
        super.testGetBufferedOutputStream(testFilepath);
    }

    @Test
    public void test_listRoots() throws Exception {
        FileInterface[] expected = null;
        super.test_listRoots(expected);
    }

    @Test
    public void testEquals() throws Exception {
        FileInterface expected = null;
        super.testEquals(expected);
    }

    @Test
    public void testIsCached() throws Exception {
        boolean expected = true;
        super.testIsCached(expected);
    }

    @Test
    public void testSeparatorChar() throws Exception {
        char expected = File.separatorChar;
        super.testSeparatorChar(expected);
    }

    @Test
    public void testPathSeparatorChar() throws Exception {
        char expected = File.pathSeparatorChar;
        super.testPathSeparatorChar(expected);
    }

    @Test
    public void testIsAbsolutePath() throws Exception {
        String strPath = sut.getAbsolutePath();
        super.testIsAbsolutePath(strPath);
    }

    @Test
    public void testGetId() throws Exception {
        String expected = FileInterface.LOCAL;
        super.testGetId(expected);
    }

    @Test
    public void testGetHost() throws Exception {
        String expected = FileInterface.LOCAL;
        super.testGetHost(expected);
    }

    @Test
    public void testGetName() throws Exception {
        String expected = "testfile";
        super.testGetName(expected);
    }

    @Test
    public void testGetAbsolutePath() throws Exception {
        String expected = filename;

        super.testGetAbsolutePath(expected);
    }

    @Test
    public void testGetInternalPath() throws Exception {
        String expected = null;
        super.testGetInternalPath(expected);
    }

    @Test
    public void testGetParent() throws Exception {
        String expected = tempDir.getAbsolutePath();
        super.testGetParent(expected);
    }

    @Test
    public void testGetRootFileInterface() throws Exception {
        File[] listRoots = File.listRoots();
        String path = sut.getAbsolutePath();
        File rootFile = null;

        for (File file : listRoots) {
            String root = file.getAbsolutePath();

            if (path.startsWith(root)) {
                rootFile = file;
                break;
            }
        }
        FileInterface expected = FileConnector.createFileInterface(rootFile.getAbsolutePath(), null, false, fileInterfaceConfiguration);

        super.testGetRootFileInterface(expected);
    }

    @Test
    public void testGetAppName() throws Exception {
        String expected = "test";
        super.testGetAppName(expected);
    }

    @Test
    public void testList() throws Exception {
        File dir = setSutForDirectory();
        String[] expected = dir.list();

        super.testList(expected);
    }

    @Test
    public void test_listFilesFileInterfaceFilter() throws Exception {
        File directory = setSutForDirectory();
        FileInterfaceFilter filter = new WildcardFileInterfaceFilter("tes*", "test");
        boolean dontShowHidden = false;
        File[] listFiles = directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(File paramFile) {
                boolean result = false;

                if (paramFile.getName().startsWith("tes"))
                    result = true;

                return result;
            }
        });

        FileInterface[] expected = new NormalFile[listFiles.length];

        for (int i = 0; i < expected.length; i++) {
            expected[i] = FileConnector.createFileInterface(listFiles[i].getAbsolutePath(), null, false, fileInterfaceConfiguration);
        }

        super.test_listFilesFileInterfaceFilter(filter, dontShowHidden, expected);
    }

    @Test
    public void test_listFiles() throws Exception {
        File directory = setSutForDirectory();
        File[] listFiles = directory.listFiles();

        FileInterface[] expected = new NormalFile[listFiles.length];

        for (int i = 0; i < expected.length; i++) {
            expected[i] = FileConnector.createFileInterface(listFiles[i].getAbsolutePath(), null, false, fileInterfaceConfiguration);
        }
        super.test_listFiles(expected);
    }

    @Test
    public void testRenameTo() throws Exception {
        FileInterface file = FileConnector.createFileInterface(tempDir.getAbsoluteFile() + File.separator + "rntestdir", null, false, fileInterfaceConfiguration);
        super.testRenameTo(file);
    }

}
