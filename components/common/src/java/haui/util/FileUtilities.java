
package haui.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FileUtilities
{
	/** Lists all files that match the filter recursively. */
	
	public static File [] listFilesRecursively (File directory, FileFilter filter, FileFilter directoryFilter)
	{
		List list = new ArrayList (50);
		listFilesRecursively(directory, filter, list, directoryFilter);
		
		return (File[])list.toArray (new File[list.size ()]);
	}
	
	/** Lists files recursively adding them to the specified collection. */
	
	public static void listFilesRecursively (File directory, FileFilter filter, Collection collection, FileFilter directoryFilter)
	{
		File [] files = directory.listFiles();
		
		if (files != null)
		{
			for (int i = 0; i < files.length; i++)
			{
				File file = files[i];
				
				if (filter.accept(file))
					collection.add(file);
				
				if (file.isDirectory() && (directoryFilter == null || directoryFilter.accept(file)))
					listFilesRecursively(file, filter, collection, directoryFilter);
			}
		}
	}
	
	/** Deletes a file recursively. If the file is a directory the contents of the
	 directory are also deleted.
	 @return true if all files were successful deleted or false if one or more weren't. */
	
	public static boolean deleteFiles (File file)
	{
		if (!file.exists ())
			return false;
		
		boolean bSuccess = true;
		
		if (file.isDirectory ())
		{
			File [] files = file.listFiles ();
			
			for (int i = 0; i < files.length; i++)
			{
				if (!deleteFiles (files[i]))
					bSuccess = false;
			}
		}
		
		if (!file.delete ())
			bSuccess = false;
		
		return bSuccess;
	}
	
	/** Gets the realtive path from the supplied current directory to the supplied file. Relative paths are
	    separated using the supplied separator.
	 	@returns a string that represents the relative path from currentDirectory to file or null if file is not
	             contained in currentDirectory. */
	
	public static String getRelativePath (File file1, File file2, String sParent, String sSeparator)
	{
		StringBuffer buffer = new StringBuffer (100);
		File file = file1;
		
		if (file.isFile())
			file = file.getParentFile();
		
		while (file != null)
		{
			if (file.equals (file2) || isParentOf(file, file2))
				break;
			
			buffer.append(sParent);
			buffer.append(sSeparator);
			file = file.getParentFile();
		}
		
		if (file == null)
			return null;
		
		boolean bAddedPath = false;
		int nInsertPos = buffer.length ();
		
		while (!file2.equals (file))
		{
			if (bAddedPath)
				buffer.insert (nInsertPos, sSeparator);
			
			buffer.insert(nInsertPos, file2.getName ());
			bAddedPath = true;
			file2 = file2.getParentFile();
		}
		
		return buffer.toString();
	}
	
	/** Gets the realtive path from the supplied current directory to the supplied file. Relative paths are
	 separated using the file separator for the current platform.
	 @returns a string that represents the relative path from currentDirectory to file or null if file is not
	             contained in currentDirectory. */
	
	public static String getRelativePath (File file1, File file2, String sParent)
	{
		return getRelativePath(file1, file2, sParent, File.separator);
	}
	
	public static String getRelativePath (File file1, File file2)
	{
		return getRelativePath(file1, file2, "..");
	}
	
	public static boolean isParentOf (File file1, File file2)
	{
		File file = file2.getParentFile();
		
		while (file != null)
		{
			if (file.equals (file1))
				return true;
			
			file = file.getParentFile();
		}
		
		 return false;
	}
	
	public static File getCommonParent (File file1, File file2)
	{
		if (isParentOf(file1, file2))
			return file1;
		
		if (isParentOf(file2, file1))
			return file2;
		
		File parent = file1.getParentFile();
		
		while (parent != null)
		{
			if (isParentOf(parent, file2))
				return parent;
			
			parent = parent.getParentFile();
		}
		
		return null;
	}
	
	public static File getFileFromRelativePath (File base, String sRelativePath, String sParent, String sCurrentDir, String sSeparator)
	{
		if (base.isFile())
			base = base.getParentFile();
		
		String [] paths = StringUtil.separateString(sRelativePath, sSeparator, false);
		File file = base;
		
		for (int i = 0; i < paths.length; i++)
		{
			String sPath = paths[i];
			
			if (i == paths.length - 1 && sPath.length() == 0)
				break;
			
			if (sPath.equals (sParent))
				file = file.getParentFile();
				
			else if (sPath.equals (sCurrentDir))
				;
			
			else file = new File (file, sPath);
		}
		
		return file;
	}
	
	public static File getFileFromRelativePath (File base, String sRelativePath, String sParent, String sCurrentDir)
	{
		return getFileFromRelativePath(base, sRelativePath, sParent, sCurrentDir, File.separator);
	}
	
	public static File getFileFromRelativePath (File base, String sRelativePath)
	{
		return getFileFromRelativePath(base, sRelativePath, "..", ".", File.separator);
	}
	
	public static void copyFile (File source, File destination)
		throws IOException
	{
		InputStream in = null;
		OutputStream out = null;
		
		try
		{
			in = new BufferedInputStream(new FileInputStream(source));
			out = new BufferedOutputStream(new FileOutputStream(destination));
			
			while (true)
			{
				int c = in.read ();
				
				if (c == -1)
					break;
				
				out.write((byte)c);
			}
		}
		
		finally
		{
			if (in != null)
				in.close ();
			
			if (out != null)
				out.close ();
		}
	}
	
	/** Checks if file names are case sensitive on the host operating system. */
	
	public static boolean areFileNamesCaseSensitive ()
	{
		// This optimization ensures we don't create new file objects every time the method
		// is called and only create them the first time. This makes the method efficient
		// if used in a loop or repetitively throughout code.
		
		if (!g_bCheckedIfFileNamesAreCaseSensitive)
		{
			g_bAreFileNamesCaseSensitive = (new File ("test").equals (new File ("TEST")) == false);
			g_bCheckedIfFileNamesAreCaseSensitive = true;
		}
		
		return g_bAreFileNamesCaseSensitive;
	}
	
	/** Gets an array of files from a string. The string can contain quoted characters
	 to specify file names with spaces in them. A wild character of '*' can be used to specify file
	 patterns. */
	
	public static File [] getFiles (String sFiles, File currentDirectory, String sFileSeparator, boolean bAllowWildChars)
	{
		List files = new LinkedList ();
		Tokenizer t = new Tokenizer (sFiles);
		
		while (t.hasMoreTokens())
		{
			String sFilePath = t.nextToken();
			int nStartOfFileName = sFilePath.lastIndexOf(sFileSeparator) + 1;
			final String sFileName = sFilePath.substring(nStartOfFileName);
			
			if (bAllowWildChars &&
				sFileName.indexOf ('*') != -1 ||
				sFileName.indexOf('?') != -1 ||
			    sFileName.indexOf('[') != -1)
			{
				String sDirectory = sFilePath.substring(0, nStartOfFileName);
				File directory = new File (sDirectory);
				
				if (!directory.isAbsolute())
					directory = new File (currentDirectory, sDirectory);
				
				File [] matchedFiles = directory.listFiles(new FileFilter ()
														   {
															   public boolean accept (File file)
															   {
																   return StringUtil.matchesPattern(file.getName (), sFileName, FileUtilities.areFileNamesCaseSensitive());
															   }
														   });
				
				for (int i = 0; i < matchedFiles.length; i++)
					files.add (matchedFiles[i]);
			}
			
			else
			{
				File f = new File (sFilePath);
				
				if (f.isAbsolute())
					files.add (f);
				
				else files.add (new File (currentDirectory, sFilePath));
			}
		}
		
		return (File [])files.toArray(new File [files.size()]);
	}
	
	public static File [] getFiles (String sFiles, File currentDirectory, String sFileSeparator)
	{
		return getFiles (sFiles, currentDirectory, sFileSeparator, true);
	}
	
	public static File [] getFiles (String sFiles, File currentDirectory)
	{
		return getFiles (sFiles, currentDirectory, File.separator, true);
	}
	
	public static File [] getFiles (String sFiles)
	{
		return getFiles (sFiles, new File (System.getProperty ("user.dir")), File.separator, true);
	}
	
	private static boolean g_bCheckedIfFileNamesAreCaseSensitive = false;
	private static boolean g_bAreFileNamesCaseSensitive;
}
