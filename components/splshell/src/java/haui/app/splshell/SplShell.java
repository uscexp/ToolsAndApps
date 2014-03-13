
package haui.app.splshell;

import haui.app.splshell.util.ProcessStore;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.NormalFile;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.filter.WildcardFileInterfaceFilter;
import haui.util.PropertyFile;
import haui.util.PropertyStruct;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Vector;

class SplShell
{
  public final static String APPNAME = "SplShell";
  public final static String LONGNAME = "Simple Programming Language Shell Interpreter Version 0.1";
  
  protected final static String MAPPED_METHOD_FILE = "MethodAliases.def";

  public static void main( String args[])
  {
    SplShellParser parser;
    if( args.length == 1)
    {
      System.out.println( LONGNAME + ":  Reading from file " + args[0] + " . . .");
      try
      {
        parser = new SplShellParser( new FileInputStream( args[0]));
      }
      catch( FileNotFoundException e)
      {
        System.err.println( LONGNAME + ":  File " + args[0] + " not found.");
        return;
      }
    }
    else
    {
      System.out.println( LONGNAME + ":  Usage :");
      System.out.println( "         java SplShell inputfile");
      return;
    }
    try
    {
      parser.CompilationUnit();
      //Thread.sleep( 1); // asure that the date is never the same
      Date date = new Date();
      Long id = new Long( date.getTime());
      ProcessStore.getInstance( id).setArgs( args);
      registerMappedMethods( parser, id);
      parser.jjtree.rootNode().interpret( id);
    }
    catch( ParseException e)
    {
      System.err.println( LONGNAME + ":  Encountered errors during parse.");
      e.printStackTrace();
    }
    catch( Exception e1)
    {
      System.err.println( LONGNAME + ":  Encountered errors during interpretation/tree building.");
      e1.printStackTrace();
    }
  }
  
  public void eval(String expression)
  {
    if( expression != null && expression.length() > 0)
    {
      System.out.println( LONGNAME + ":  Reading from expression . . .");
      String[] args = new String[1];
      args[0] = expression ;
      InputStream inputStream = new StringBufferInputStream(expression);
      interpret(inputStream, args);
    }
    else
    {
      System.err.println( "error: no filename given");
      return;
    }
  }
  
  public void source(String fileName)
  {
    if( fileName != null && fileName.length() > 0)
    {
      System.out.println( LONGNAME + ":  Reading from file " + fileName + " . . .");
      try
      {
        String[] args = new String[1];
        args[0] = fileName ;
        InputStream inputStream = new FileInputStream( fileName);
        interpret(inputStream, args);
      }
      catch( FileNotFoundException e)
      {
        System.err.println( LONGNAME + ":  File " + fileName + " not found.");
        return;
      }
    }
    else
    {
      System.err.println( "error: no filename given");
      return;
    }
  }
  
  public void interpret(InputStream inputStream, String args[])
  {
    SplShellParser parser;
    
    if( inputStream != null)
    {
      parser = new SplShellParser( inputStream);
    }
    else
    {
      System.err.println( "error: no input stream given");
      return;
    }
    try
    {
      parser.CompilationUnit();
      //Thread.sleep( 1); // asure that the date is never the same
      Date date = new Date();
      Long id = new Long( date.getTime());
      ProcessStore.getInstance( id).setArgs(args);
      registerMappedMethods( parser, id);
      parser.jjtree.rootNode().interpret( id);
    }
    catch( ParseException e)
    {
      System.err.println( LONGNAME + ":  Encountered errors during parse.");
      e.printStackTrace();
    }
    catch( Exception e1)
    {
      System.err.println( LONGNAME + ":  Encountered errors during interpretation/tree building.");
      e1.printStackTrace();
    }
  }
  
  protected static void registerMappedMethods( SplShellParser parser, Long id) throws MalformedURLException
  {
    FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration(
        null, 0, null, null, 0, 0, APPNAME, null, true);
    FileInterface file = FileConnector.createFileInterface(NormalFile.class, ".", null, null, null, fic);
    FileInterfaceFilter filter = new WildcardFileInterfaceFilter( "*.def", APPNAME);
    FileInterface[] files = file._listFiles( filter);
    int mmdId = 100;
    int mbId = 101;
    
    for( int j = 0; j < files.length; ++j)
    {
      file = files[j];
      if( !file.exists())
        continue;
      
      PropertyFile propertyFile = new PropertyFile( file.toURL(), file.toURL());
      
      propertyFile.load();
      
      Vector vec = propertyFile.getAllElements();
      
      if( vec.size() > 0)
      {
        for( int i = 0; i < vec.size(); ++i)
        {
          ASTMappedMethodDeclaration methodDeclaration = new ASTMappedMethodDeclaration( mmdId);
  //        parser.jjtree.rootNode().jjtAddChild( methodDeclaration,
  //            parser.jjtree.rootNode().jjtGetNumChildren());
          
          PropertyStruct propertyStruct = (PropertyStruct)vec.get( i);
          
          ASTMappedBlock mappedBlock = new ASTMappedBlock( mbId);
          
          String method = propertyStruct.getStructName();
          mappedBlock.typeName = propertyStruct.stringValue( "type");
          mappedBlock.methodName = propertyStruct.stringValue( "realMethod");
          mappedBlock.statik = propertyStruct.booleanValue( "static");
          mappedBlock.paramDefs = propertyStruct.vectorValue( "realParams");
          if( mappedBlock.paramDefs == null)
            mappedBlock.paramDefs = new Vector();
          mappedBlock.returnType = propertyStruct.stringValue( "returnType");
          
          methodDeclaration.jjtAddChild( mappedBlock, methodDeclaration.jjtGetNumChildren());
          
          ProcessStore.getInstance( id).addMethod( method, methodDeclaration);
        }
      }
    }
  }
}
