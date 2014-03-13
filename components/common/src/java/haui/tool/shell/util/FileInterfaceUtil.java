package haui.tool.shell.util;

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.filter.WildcardFileInterfaceFilter;
import haui.tool.shell.engine.JShellEnv;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Module:      FileInterfaceUtil.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\util\\FileInterfaceUtil.java,v $
 *<p>
 * Description: FileInterface utility.<br>
 *</p><p>
 * Created:     27.04.2004  by AE
 *</p><p>
 * @history     27.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: FileInterfaceUtil.java,v $
 * Revision 1.1  2004-08-31 16:03:20+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:52+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\util\\FileInterfaceUtil.java,v 1.1 2004-08-31 16:03:20+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class FileInterfaceUtil
{
  public static Vector getSourceFileInterfaces( FileInterface fi, JShellEnv jse, String strCurPath, String strArg)
  {
    Vector vecFi = new Vector();
    String strPath = strArg;
    FileInterface fiRet[] = null;
    FileInterface fiSource = null;
    StringBuffer strbufSource = new StringBuffer( strCurPath);
    strbufSource.append( fi.separatorChar());
    strbufSource.append( strArg);
    fiSource = FileConnector.createFileInterface( strbufSource.toString(),
      strCurPath, false, fi.getFileInterfaceConfiguration());
    if( !fiSource.exists())
    {
      FileInterface fiTmp = null;
      try
      {
        if( !fiSource.isAbsolutePath( strArg))
          strPath = fiSource.getParent() + fiSource.separatorChar() + strArg;
        fiTmp = FileConnector.createFileInterface( strPath, null,
          false, fi.getFileInterfaceConfiguration());
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        /*
        strPath = fiSource.getParent() + fiSource.separatorChar() + strArg;
        fiTmp = FileConnector.createFileInterface( strPath, null,
          false, null, jse.getAppPropperties(), true);
        */
      }
      if( fiTmp.exists())
      {
        fiSource = fiTmp;
        vecFi.add( fiSource);
      }
      else
      {
        String strName = fiSource.getName();
        String strDir = fiSource.getParent();
        strPath = fiSource.getAbsolutePath();
        String strParentPath = strDir;
        FileInterface fiDir = FileConnector.createFileInterface( strParentPath, null, false, fi.getFileInterfaceConfiguration());
        if( fiDir != null /* && fiDir.exists() //because FtpFile always returns false */ && fiDir.isDirectory())
        {
          WildcardFileInterfaceFilter wfif = new WildcardFileInterfaceFilter( strName, jse.getAppName() );
          fiRet = fiDir._listFiles( wfif );
          if( fiRet != null)
          {
            for( int i = 0; i < fiRet.length; ++i )
            {
              vecFi.add( fiRet[i] );
            }
          }
        }
        else
        {
          strName = fiTmp.getName();
          strDir = fiTmp.getParent();
          strPath = fiTmp.getAbsolutePath();
          strParentPath = strDir;
          fiDir = FileConnector.createFileInterface( strParentPath, null, false,
            fi.getFileInterfaceConfiguration());
          if( fiDir != null && fiDir.exists() && fiDir.isDirectory())
          {
            WildcardFileInterfaceFilter wfif = new WildcardFileInterfaceFilter( strName, jse.getAppName() );
            fiRet = fiDir._listFiles( wfif );
            if( fiRet != null)
            {
              for( int i = 0; i < fiRet.length; ++i )
              {
                vecFi.add( fiRet[i] );
              }
            }
          }
        }
      }
    }
    else
    {
      vecFi.add( fiSource);
    }
    return vecFi;
  }

  public static void copy( JShellEnv jse, Vector vecFi, FileInterface fiTarget, boolean blTargetIsFile, boolean blForce, boolean blRecursive)
  {
    for( int i = 0; i < vecFi.size(); ++i)
    {
      FileInterface fiSource = ((FileInterface)vecFi.elementAt(i));
      String strTargetFile = fiTarget.getAbsolutePath();
      if( !blTargetIsFile)
        strTargetFile += fiTarget.separatorChar() + fiSource.getName();
      FileInterface fiTargetFile = FileConnector.createFileInterface( strTargetFile, null,
        false, fiTarget.getFileInterfaceConfiguration());
      if( fiSource.exists())
      {
        if( fiSource.isDirectory())
        {
          if( !fiTargetFile.exists() )
          {
            fiTargetFile.mkdir();
          }

          if( blRecursive )
          {
            Vector vecFiSub = new Vector();
            FileInterface[] fiSources = fiSource._listFiles();
            if( fiSources != null)
            {
              for( int ii = 0; ii < fiSources.length; ++ii )
              {
                vecFiSub.add( fiSources[ii] );
              }
            }
            copy( jse, vecFiSub, fiTargetFile, false, blForce, blRecursive );
          }
        }
        else
        {
          try
          {
            boolean blCopy = blForce;
            if(!blCopy && fiTargetFile.exists())
            {
              jse.getOut().print( "Override existing file: ");
              jse.getOut().print( fiTargetFile.getAbsolutePath());
              jse.getOut().print( " (y/n)? ");
              jse.setInputMode( true);
              String strResp = jse.getNextLine();
              if( strResp.equalsIgnoreCase( "y"))
                blCopy = true;
            }
            else
              blCopy = true;
            if( blCopy)
            {
              BufferedOutputStream bo = fiTargetFile.getBufferedOutputStream( fiTargetFile.
                getAbsolutePath() );

              fiSource.copyFile( bo );
              bo.close();
            }
          }
          catch( IOException ex )
          {
            ex.printStackTrace();
          }
        }
      }
    }
  }
}