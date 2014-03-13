#!/usr/bin/perl
#######################################################################
# filemanager.cgi File Manager interface script
# Created by Andreas Eisenhauer haui@haui.cjb.net
# Copyright (c) 2000 All Rights Reserved.
#
# This script MAY NOT be redistributed for any reason without
# the expressed written consent of the author. All copyrights reserved.
########################################################################

use File::stat;
use File::Path;
use File::Copy;

# Define location of passwd.txt file as created from Matt's Script Archive.
$passfile="/usr/home2/myservic/html/.admin/.passwd";
$logfile ="/usr/home2/myservic/html/.admin/.logon";

# output file name
$outfile = "/usr/home2/myservic/html/.admin/cgi/tmp.out";

# current working directory file
$cwdfile = "/usr/home2/myservic/html/.admin/cgi/cwd.out";
$cwdfmfile = "/usr/home2/myservic/html/.admin/cgi/cwdfm.out";

# temp path file for uploading
$pathfile = "/usr/home2/myservic/html/.admin/cgi/path.out";

# temp dir
$tempdir = "/usr/home2/myservic/html/.admin/cgi/temp";

# home directory
$homedir = "/usr/home2/myservic/html";

# path seperator
$seperator = "/";

# binary file read buffer size
$bufferSize = 32768;

# temp input filename
$timestamp = &Timestamp();
$tmpinfile = "input$timestamp";

$CONTENT_TYPE = $ENV{'CONTENT_TYPE'};
$CONTENT_LENGTH = $ENV{'CONTENT_LENGTH'};

local( $buffer);
local( $ident, $i);

open( INFILE, ">$tempdir/$tmpinfile") || die $!;

$blRet = 0;

for( $i = 0; read( STDIN, $buffer, $bufferSize) > 0; ++$i)
{
  print INFILE "$buffer";
  if( $i == 0)
  {
    $ident = substr( $buffer, 0, 7);
  }
}
close( "$tempdir/$tmpinfile");

if( $ENV{'REQUEST_METHOD'} ne "POST")
{

  # Print out a content-type for HTTP/1.0 compatibility
  print "Content-type: text/plain\n\n";

  unlink( "$tempdir/$tmpinfile");
  
  print "<SOF>\n";
  print "filemanager script\n";
  print "failed\n";
  print "\n<EOF>\n";
  exit;
}

if( $CONTENT_TYPE eq "application/octet-stream" || $ident ne "action=")
{
  local( $strNewPath);
  local( $blRet);

  print "Content-type: text/plain\n\n";
  print "<SOF>\n";
  
  open( FILE, "< $pathfile") || die $!;
  $strNewPath = <FILE>;
  close( $pathfile);
  
  $blRet = 0;
  
  $path = "$tempdir/$tmpinfile";
  $blRet = moveTo( $strNewPath);
  print "$blRet\n";

  print "<EOF>\n";
  $ok = 1;
  exit;
}
else
{
  # Get the input
  open( INFILE, "$tempdir/$tmpinfile") || die $!;
  read(INFILE, $buffer, $ENV{'CONTENT_LENGTH'});
  if ($ENV{'QUERY_STRING'})
  {
    $buffer = "$buffer\&$ENV{'QUERY_STRING'}";
  }
  close( "$tempdir/$tmpinfile");
  unlink( "$tempdir/$tmpinfile");

  # Split the name-value pairs
  @pairs = split(/&/, $buffer);
  
  foreach $pair (@pairs)
  {
    ($name, $value) = split(/=/, $pair);
  
    # Un-Webify plus signs and %-encoding
    $value =~ tr/+/ /;
    $value =~ s/%([a-fA-F0-9][a-fA-F0-9])/pack("C", hex($1))/eg;
  
    $FORM{$name} = $value;
  }
  
  local( $action);
  local( $retVal);
  local( @retVals);
  @strListNames = ();

  $action = $FORM{'action'};
  $path = $FORM{'path'};
  $param = $FORM{'param'};
  
  if( $action eq "logon")
  {
    # Print out a content-type for HTTP/1.0 compatibility
    print "Content-type: text/plain\n\n";
  
    print "<SOF>\n";
    
    open(PASSWD,"$passfile") || die $!;
    $passwd_line = <PASSWD>;
    chop($passwd_line) if $passwd_line =~ /\n$/;
    close(PASSWD);
  
    ($username,$passwd) = split(/:/,$passwd_line);
  
    $test_passwd = crypt($path, substr($passwd, 0, 2));
    if (!($test_passwd eq $passwd && $param eq $username)) {
      $access="Not";
    } else {
      open(FILE,">$logfile") || die $!;
      $access=$ENV{'REMOTE_ADDR'};
      print FILE "$access";
      close(FILE);
    }
  
    print "DBasics Log In Script\n";
    print "$access Logged On\n";
    print "\n<EOF>";
    exit;
  }
  elsif( $action eq "logoff")
  {
    open(FILE,">$logfile") || die $!;
    $access="No One";
    print FILE "$access";
    close(FILE);
    
    # Print out a content-type for HTTP/1.0 compatibility
    print "Content-type: text/plain\n\n";
    print "<SOF>\n";
    print "DBasics Log In Script\n";
    print "$access Logged On\n";
    print "\n<EOF>";
    exit;
  }

  # Snippet for password protection
  #open(FILE,"$logfile") || die $!;
  #$logon=<FILE>;
  #close(FILE);
  #if ($ENV{'REMOTE_ADDR'} ne $logon) { exit; }
  # End of Snippet

  # Print out a content-type for HTTP/1.0 compatibility
  if( $action ne "copyfiletolocal")
  {
    print "Content-type: text/plain\n\n";
    print "<SOF>\n";
  }
  else
  {
    print "Content-type: application/octet-stream\n\n";
  }
  
  if( $action ne "exec")
  {
    if( $path eq ".")
    {
      $path = &getCurrentPath();
    }
    
    open( FILE, "< $path") || die $!;
    
    if( &isDirectory() && $path =~ /^\//)
    {
      open(CWDFMFILE, "> $cwdfmfile") || die $!;
      print CWDFMFILE "$path";
      close( $cwdfmfile);
    }
  }
  
  if( $action eq "init")
  {
    &init();
  }
  elsif( $action eq "initFileInfoArray")
  {
    &initFileInfoArray();
  }
  elsif( $action eq "canRead")
  {
    $retVal = &canRead();
    print "$retVal\n";
  }
  elsif( $action eq "canWrite")
  {
    $retVal = &canWrite();
    print "$retVal\n";
  }
  elsif( $action eq "isDirectory")
  {
    $retVal = &isDirectory();
    print "$retVal\n";
  }
  elsif( $action eq "isFile")
  {
    $retVal = &isFile();
    print "$retVal\n";
  }
  elsif( $action eq "isHidden")
  {
    $retVal = &isHidden();
    print "$retVal\n";
  }
  elsif( $action eq "lastModified")
  {
    $retVal = &lastModified();
    print "$retVal\n";
  }
  elsif( $action eq "length")
  {
    $retVal = &lengthFile();
    print "$retVal\n";
  }
  elsif( $action eq "exists")
  {
    $retVal = &existsFile( $param);
    print "$retVal\n";
  }
  elsif( $action eq "getName")
  {
    $retVal = &getName();
    print "$retVal\n";
  }
  elsif( $action eq "getPath")
  {
    $retVal = &getPath();
    print "$retVal\n";
  }
  elsif( $action eq "getAbsolutePath")
  {
    $retVal = &getAbsolutePath();
    print "$retVal\n";
  }
  elsif( $action eq "getParent")
  {
    $retVal = &getParent();
    print "$retVal\n";
  }
  elsif( $action eq "listNames")
  {
    @retVals = &listNames();
    &sendArray( @retVals);
  }
  elsif( $action eq "list")
  {
    @retVals = &list();
    &sendArray( @retVals);
  }
  elsif( $action eq "listRoots")
  {
    @retVals = &listRoots();
    &sendArray( @retVals);
  }
  elsif( $action eq "delete")
  {
    $retVal = &deleteFile();
    print "$retVal\n";
  }
  elsif( $action eq "deletedir")
  {
    $retVal = &deleteDir( $path);
    print "$retVal\n";
  }
  elsif( $action eq "exec")
  {
    $retVal = &execCmd( $param);
  }
  elsif( $action eq "mkdir")
  {
    $retVal = &mkDir( $param);
  }
  elsif( $action eq "renameTo")
  {
    $retVal = &renameTo( $param);
  }
  elsif( $action eq "move")
  {
    $retVal = &moveTo( $param);
  }
  elsif( $action eq "copy")
  {
    $retVal = &copyTo( $path, $param);
  }
  elsif( $action eq "copydir")
  {
    $retVal = &copyDirs( $path, $param);
  }
  elsif( $action eq "copytolocal")
  {
    $retVal = &copyToLocal();
  }
  elsif( $action eq "copyfiletolocal")
  {
    $retVal = &copyFileToLocal();
  }
  elsif( $action eq "preparecopy")
  {
    $retVal = &prepareCopy();
  }
  elsif( $action eq "copytoremote")
  {
    $retVal = &copyToRemote( $param);
    print "$retVal\n";
  }
  elsif( $action eq "setpath")
  {
    $retVal = &setPathForRemoteCopy( $param);
    print "$retVal\n";
  }
  close( $path);
  
  if( $action ne "copyfiletolocal")
  {
    print "<EOF>\n";
  }
  $ok = 1;
  exit;
}

#############################################################
# subroutines
#############################################################

#
# send an array
#
# @param array: array of values
#
sub sendArray
{
  local( @array) = @_;
  local( $count);
  
  $count = $#array+1;
  
  print "$count\n";
  foreach $value ( @array)
  {
    print "$value\n";
  }
}

#
# Read the information
#
sub init
{
  local( $blRead, $blWrite, $blDirectory, $blFile, $blHidden, $lLength, $strName, $strParent, $strPath, $strAbsolutePath, @strRoots, $lModified);
  
  $strPath = &getPath();
  $blRead = &canRead();
  $blWrite = &canWrite();
  $blDirectory = &isDirectory();
  $blFile = &isFile();
  $blHidden = &isHidden();
  $lLength = &lengthFile();
  $strName = &getName();
  $strAbsolutePath = &getAbsolutePath();
  $strParent = &getParent();
  @strRoots = &listRoots();
  $lModified = &lastModified();
  
  print "$strPath\n";
  print "$seperator\n";
  print "$blRead\n";
  print "$blWrite\n";
  print "$blDirectory\n";
  print "$blFile\n";
  print "$blHidden\n";
  print "$lLength\n";
  print "$strName\n";
  print "$strAbsolutePath\n";
  print "$strParent\n";
  &sendArray( @strRoots);
  print "$lModified\n";
  
  return;
}

#
# Read all FileInfos for the diractory
#
sub initFileInfoArray
{
  local( @blRead, @blWrite, @blDirectory, @blFile, @blHidden, @lLength, @strName, @strParent, @strPath, @strAbsolutePath, @strRoots, @lModified);
  local( $i, $count);
  local( $oldPath);
  
  $oldPath = $path;
  $i = 0;
  if( &isDirectory())
  {
    @strName = &listNames();
    @strPath = &list();
    @strAbsolutePath = @strPath;
  }
  @strRoots = &listRoots();
  close( $path);
  foreach $path ( @strPath)
  {
    open( FILE, "< $path") || die $!;
    $blRead[$i] = &canRead();
    $blWrite[$i] = &canWrite();
    $blDirectory[$i] = &isDirectory();
    $blFile[$i] = &isFile();
    $blHidden[$i] = &isHidden();
    $lLength[$i] = &lengthFile();
    $strParent[$i] = &getParent();
    $lModified[$i] = &lastModified();
    $i++;
    close( $path);
  }
  
  $count = $#strPath+1;
  
  print "$count\n";
  
  $i = 0;
  foreach $path ( @strPath)
  {
    $value = $path;
    print "$value\n";
    print "$seperator\n";
    $value = $blRead[$i];
    print "$value\n";
    $value = $blWrite[$i];
    print "$value\n";
    $value = $blDirectory[$i];
    print "$value\n";
    $value = $blFile[$i];
    print "$value\n";
    $value = $blHidden[$i];
    print "$value\n";
    $value = $lLength[$i];
    print "$value\n";
    $value = $strName[$i];
    print "$value\n";
    $value = $strAbsolutePath[$i];
    print "$value\n";
    $value = $strParent[$i];
    print "$value\n";
    &sendArray( @strRoots);
    $value = $lModified[$i];
    print "$value\n";
    $i++;
  }

  $path = $oldPath;
  open( FILE, "< $path") || die $!;
  return;
}

#
# Tests whether the application can read the file
#
# @return true if the file is readable; false otherwise
#
sub canRead
{
  local( $blRet);
  
  $blRet = -r FILE;
  if( $blRet eq "")
  {
    $blRet = 0;
  }
  return $blRet;
}

#
# Tests whether the application can write the file
#
# @return true if the file is writeable; false otherwise
#
sub canWrite
{
  local( $blRet);
  
  $blRet = -w FILE;
  if( $blRet eq "")
  {
    $blRet = 0;
  }
  return $blRet;
}

#
# Checks if file is a directory
#
# @return true if the file is a directory
#
sub isDirectory
{
  local( $blRet);
  
  $blRet = -d FILE;
  if( $blRet eq "")
  {
    $blRet = 0;
  }
  return $blRet;
}

#
# Checks if file is a file
#
# @return true if the file is a file
#
sub isFile
{
  local( $blRet);
  
  $blRet = -f FILE;
  if( $blRet eq "")
  {
    $blRet = 0;
  }
  return $blRet;
}

# NOT IMPLEMENTED always false
# Tests whether the file is hidden
#
# @return true if the file is hidden; false otherwise
#
sub isHidden
{
  local( $blRet);
  
  $blRet = 0;
  if( $blRet eq "")
  {
    $blRet = 0;
  }
  return $blRet;
}

#
# get date of last modification of the file
#
# @return date of last modification
#
sub lastModified
{
  local( $lRet);
  
  $lRet = -M FILE;
  if( $blRet eq "")
  {
    $blRet = 0;
  }
  return $lRet;
}

#
# lenght of the file
#
# @return lenght of the file
#
sub lengthFile
{
  local( $lRet);
  
  $lRet = -s FILE;
  if( $blRet eq "")
  {
    $blRet = 0;
  }
  return $lRet;
}

#
# Tests wether the file exists
#
# @param strFile: path (name) of the file
#
# @return true if the file exists; false otherwise
#
sub existsFile
{
  local($strFile) = @_;
  local( $blRet);
  
  $blRet = chdir( $path);
  if( $blRet)
  {
    $blRet = -e $strFile;
  }
  if( $blRet eq "")
  {
    $blRet = 0;
  }
  return $blRet;
}

#
# Ececutes a command string
#
# @param cmd: command string to execute
#
sub execCmd
{
  local($cmd) = @_;
  local($retVal, $pwd, $rc, $line);
  
  if( $path eq "")
  {
    $path = "$homedir";
    if( open(CWDFILE, "< $cwdfile"))
    {
      if( defined( $line= <CWDFILE>))
      {
        $line =~ s/\n//;
        $path = $line;
      }
      close( $cwdfile);
    }
  }
  
	if( $cmd =~ /cd/)
	{
		$cmd = sprintf("cd $path;%s 1> $outfile 2>&1;pwd > $cwdfile", $cmd);
		$pwd = 1;
	}
	else
	{
		$cmd = sprintf("cd $path;%s 1> $outfile 2>&1", $cmd);
		$pwd = 0
	}
	#print "command: $cmd\n";
	
	$rc = 0xffff & system $cmd;
	if( $rc == 0)
	{
		;
	}
	elsif( $rc == 0xff00)
	{
		print "command failed: $!\n";
	}
	elsif( ( $rc & 0xff) == 0)
	{
		$rc >>= 8;
		print "ran with non-zero exit status $rc\n";
	}
	else
	{
		print "ran with ";
		if( $rc & 0x80)
		{
			$rc &= ~0x80;
			print "coredump from ";
		}
		print "signal $rc\n";
	}
	
	open(OUTFILE, "< $outfile") || die $!;
	while( defined( $line= <OUTFILE>))
	{
		print "$line";
	}
	close( $outfile);
	if( $pwd == 1 && open(CWDFILE, "< $cwdfile"))
	{
		if( defined( $line= <CWDFILE>))
		{
			print "$line";
		}
		close( $cwdfile);
	}
  $retVal = "";
  return $retVal;
}

#
# get name of the file
#
# @return name the file
#
sub getName
{
  local( $strRet);

  @parts = split( /$seperator/, $path);
  
  foreach $part ( reverse @parts)
  {
    $strRet = $part;
    last;
  }

  return $strRet;
}

#
# get path of the file
#
# @return path of the file
#
sub getPath
{
  local( $strRet);

  $strRet = $path;

  return $strRet;
}

#
# get absoluts path of the file
#
# @return absolote path of the file
#
sub getAbsolutePath
{
  local( $strRet);

  $strRet = $path;

  return $strRet;
}

#
# get parent path of the current path
#
# @return parent path
#
sub getParent
{
  local( $strRet);
  local( $i, $first);

  @parts = split( //, $path);
  $i = 0;
  $first = 0;
  foreach $part ( reverse @parts)
  {
    $i++;
    if( $i == 1)
    {
      next;
    }
    if( $first == 0 && $part eq $seperator)
    {
      $first = 1;
      next;
    }
    elsif( $first == 0)
    {
      next;
    }
    $strRet = "$part$strRet";
  }
  
  
  if( $strRet eq "")
  {
    $strRet = "..";
  }
  return $strRet;
}

#
# Gives all names of files in current path
#
# @return names of all files in current path
#
sub listNames
{
  local( @strArrRet);
  local( $i, $file);

  if( &isDirectory())
  {
    if( $#strListNames > 0)
    {
      @strArrRet = @strListNames;
    }
    else
    {
      opendir(DIR, $path) || die $!;
      for( $i = 0; defined( $file = readdir(DIR)); $i++)
      {
        $strListNames[$i] = $file;
        $strArrRet[$i] = $file;
      }
      closedir(DIR);
    }
  }
  
  return @strArrRet;
}

#
# Gives all files in current path
#
# @return pathnames of all files in current path
#
sub list
{
  local( @strArrRet);
  local( $i, $file);

  if( &isDirectory())
  {
    if( $#strListNames <= 0)
    {
      &listNames();
    }
    $i = 0;
    foreach $file ( @strListNames)
    {
      $strArrRet[$i] = "$path$seperator$file";
      $i++;
    }
  }
  
  return @strArrRet;
}

#
# List the available filesystem roots
#
# @return filesystem roots
#
sub listRoots
{
  local( @strArrRet);

  $strArrRet[0] = $seperator;
  
  return @strArrRet;
}

#
# delete file
#
# @return true if the file or directory is successfully deleted; false otherwise
#
sub deleteFile
{
  local( $blRet);

  $blRet = 1;
  if( &isFile())
  {
    if( !unlink( $path))
    {
      $blRet = 0;
    }
  }
  else
  {
    if( &isDirectory())
    {
      if( !rmdir( $path))
      {
        $blRet = 0;
      }
    }
    else
    {
      $blRet = 0;
    }
  }
  
  return $blRet;
}

#
# delete dir recursively
#
# @return true if the file or directory is successfully deleted; false otherwise
#
sub deleteDir
{
  local( $strPath) = @_;
  local( $blRet, $dir);

  $blRet = 0;
  open( INPUT, "<$strPath") || die $!;
  
  $dir = -d INPUT;
  if( $dir eq "")
  {
    $dir = 0;
  }

  close( $strPath);
  $blRet = &deleteFiles( $strPath);
  if( $dir)
  {
    $blRet = &deleteDirs( $strPath);
  }
  if( $blRet eq "")
  {
    $dir = 0;
  }
  
  return $blRet;
}

#
# delete dir recursively (part one)
#
# @return true if the file or directory is successfully deleted; false otherwise
#
sub deleteFiles
{
  local( $strPath) = @_;
  local( $blRet);
  local( $length, $i);
  local( $dir, $file, $name, $listName, $part, $compName);
  local( @strListN, @strListD, @parts);

  $blRet = 0;
  open( INPUT, "<$strPath") || die $!;
  
  $dir = -d INPUT;
  if( $dir eq "")
  {
    $dir = 0;
  }

  close( $strPath);
  if( $dir)
  {
    @parts = split( /$seperator/, $strPath);
    
    foreach $part ( reverse @parts)
    {
      $name = $part;
      last;
    }
    if( $name eq "." || $name eq "..")
    {
      return 1;
    }
    opendir(DIR, $strPath) || die $!;
    for( $i = 0; defined( $file = readdir(DIR)); $i++)
    {
      $strListN[$i] = $file;
    }
    $i = 0;
    foreach $file ( @strListN)
    {
      $strListD[$i] = "$strPath$seperator$file";
      $i++;
    }
    closedir(DIR);
    $i = 0;
    foreach $file ( @strListD)
    {
      print "ForEach: $file\n";
      $blRet = &deleteFiles( $file);
      $i++;
    }
  }
  else
  {
    print "Del: $strPath";
    $blRet = unlink( $strPath);
    print ", $blRet\n";
  }
  if( $blRet eq "")
  {
    $dir = 0;
  }
  
  return $blRet;
}

#
# delete dir recursively (part two)
#
# @return true if the file or directory is successfully deleted; false otherwise
#
sub deleteDirs
{
  local( $strPath) = @_;
  local( $blRet);
  local( $length, $i);
  local( $dir, $file, $name, $listName, $part, $compName);
  local( @strListN, @strListD, @parts);

  $blRet = 1;
  open( INPUT, "<$strPath") || die $!;
  
  $dir = -d INPUT;
  if( $dir eq "")
  {
    $dir = 0;
  }

  close( $strPath);
  if( $dir)
  {
    @parts = split( /$seperator/, $strPath);
    
    foreach $part ( reverse @parts)
    {
      $name = $part;
      last;
    }
    if( $name eq "." || $name eq "..")
    {
      return 1;
    }
    opendir(DIR, $strPath) || die $!;
    for( $i = 0; defined( $file = readdir(DIR)); $i++)
    {
      $strListN[$i] = $file;
    }
    $i = 0;
    foreach $file ( @strListN)
    {
      $strListD[$i] = "$strPath$seperator$file";
      $i++;
    }
    closedir(DIR);
    $i = 0;
    foreach $file ( @strListD)
    {
      print "ForEach: $file\n";
      $blRet = &deleteDirs( $file);
      $i++;
    }
    print "RmDir: $strPath";
    $blRet = rmdir( $strPath);
    print ", $blRet\n";
  }
  if( $blRet eq "")
  {
    $dir = 0;
  }
  
  return $blRet;
}

#
# make directory
#
# @param strDir: path (name) of the directory
#
# @return true if the directory is successfully created; false otherwise
#
sub mkDir
{
  local( $strDir) = @_;
  local( $blRet);
  local( $strParent);

  $blRet = 1;
  $blRet = chdir( $path);
  if( $blRet)
  {
    $blRet = mkdir( $strDir, oct( "0755"));
  }
  
  print "$blRet\n";
  
  if( $strDir =~ /$seperator/)
  {
    print "$strDir\n";
  }
  else
  {
    $strParent = &getParent();
    print "$path$seperator$strDir\n";
  }
  
  return $blRet;
}

#
# rename file
#
# @param strNew: new file name
#
# @return true if the file or directory is successfully renamed; false otherwise
#
sub renameTo
{
  local( $strNew) = @_;
  local( $blRet);
  local( $lModified);
  local( $part, @parts);
  local( $strName);
  local( $strParent);

  $strParent = &getParent();
  $blRet = 1;
  $blRet = chdir( $strParent);
  if( $blRet)
  {
    $blRet = move( $path, $strNew);
  }
  
  print "$blRet\n";
  
  if( $strNew =~ /$seperator/)
  {
    @parts = split( /$seperator/, $strNew);
    
    foreach $part ( reverse @parts)
    {
      $strName = $part;
      last;
    }
    print "$strNew\n";
    print "$strName\n";
    print "$lModified\n";
  }
  else
  {
    $strParent = &getParent();
    $lModified = &lastModified();
    print "$strParent$seperator$strNew\n";
    print "$strNew\n";
    print "$lModified\n";
  }
  
  return $blRet;
}

#
# move file
#
# @param strNewPath: new file path
#
# @return true if the file or directory is successfully moved; false otherwise
#
sub moveTo
{
  local( $strNewPath) = @_;
  local( $blRet);
  
  $blRet = move( $path, $strNewPath);
  
  print "$blRet\n";

  return $blRet;
}

#
# copy file
#
# @param strNewPath: new file path
#
# @return true if the file or directory is successfully copied; false otherwise
#
sub copyTo
{
  local( $strPath, $strNewPath) = @_;
  local( $blRet);
  local( $buffer);
  local( $length);
  
  $blRet = 0;
  
  open( INPUT, "<$strPath") || die $!;
  $length = -s INPUT;

  open( OUTPUT, ">$strNewPath") || die $!;
  
  if( $length eq "")
  {
    $length = 0;
  }
  
  print "$length\n";
  
  while( read( INPUT, $buffer, $bufferSize) > 0)
  {
    print OUTPUT "$buffer";
  }
  
  print "<CEOF>\n";
  $blRet = 1;
  print "$blRet\n";

  close( $strPath);
  close( $strNewPath);
  
  return $blRet;
}

#
# save path for later remote copy
#
# @return true if the file is successfully written;
#
sub setPathForRemoteCopy
{
  local( $strPath) = @_;
  local( $blRet);
  
  open(PATHFILE, "> $pathfile") || die $!;
  print PATHFILE "$strPath";
  close( $pathfile);
  
  $blRet = 1;
  
  return $blRet;
}

#
# copy file
#
# @param strNewPath: new file path
#
# @return true if the file or directory is successfully copied; false otherwise
#
sub copyToRemote
{
  local( $strNewPath) = @_;
  local( $blRet);
  local( $buffer);
  local( $length);
  
  $blRet = 0;
  
  open( OUTPUT, ">$strNewPath") || die $!;
  
  while( read( STDIN, $buffer, $bufferSize) > 0)
  {
    print OUTPUT "$buffer";
  }
  
  $blRet = 1;
  print "$blRet\n";

  close( $strNewPath);
  
  return $blRet;
}

#
# copy dir
#
# @param strNewPath: new file path
#
# @return true if the directory is successfully copied; false otherwise
#
sub copyDirs
{
  local( $strPath, $strNewPath) = @_;
  local( $blRet);
  local( $length, $i);
  local( $dir, $file, $name, $listName, $part, $compName);
  local( @strListN, @strListD, @parts);
  
  $blRet = 0;
  
  open( INPUT, "<$strPath") || die $!;
  
  $dir = -d INPUT;
  if( $dir eq "")
  {
    $dir = 0;
  }

  close( $strPath);
  if( $dir)
  {
    @parts = split( /$seperator/, $strPath);
    
    foreach $part ( reverse @parts)
    {
      $name = $part;
      last;
    }
    if( $name eq "." || $name eq "..")
    {
      return 1;
    }
    $blRet = -e "$strNewPath";
    if( $blRet eq "")
    {
      $blRet = 0;
    }
    if( !$blRet)
    {
      $blRet = mkdir( "$strNewPath", oct( "0755"));
    }
    if( $blRet)
    {
      opendir(DIR, $strPath) || die $!;
      for( $i = 0; defined( $file = readdir(DIR)); $i++)
      {
        $strListN[$i] = $file;
      }
      $i = 0;
      foreach $file ( @strListN)
      {
        $strListD[$i] = "$strPath$seperator$file";
        $i++;
      }
      closedir(DIR);
      $i = 0;
      foreach $file ( @strListD)
      {
        $listName = $strListN[$i];
        $compName = "$strNewPath$seperator$listName";
        
        $blRet = &copyDirs( $file, $compName);
        $i++;
      }
    }
  }
  else
  {
    $blRet = &copyTo( $strPath, $strNewPath);
  }
  
  return $blRet;
}

#
# copy file to the local machine
#
# @return true if the file or directory is successfully copied; false otherwise
#
sub copyToLocal
{
  local( $blRet);
  local( $buffer);
  local( $length);
  
  $blRet = 0;
  
  $length = &lengthFile();
  
  print "$length\n";
  
  while( read( FILE, $buffer, $bufferSize) > 0)
  {
    print "$buffer";
  }
  
  print "<CEOF>\n";
  $blRet = 1;
  print "$blRet\n";
  
  return $blRet;
}

#
# copy file to the local machine
#
# @return true if the file or directory is successfully copied; false otherwise
#
sub prepareCopy
{
  local( $blRet);
  local( $length);
  
  $blRet = 0;
  
  $length = &lengthFile();
  
  print "$length\n";
}

#
# copy file to the local machine
#
# @return true if the file or directory is successfully copied; false otherwise
#
sub copyFileToLocal
{
  local( $blRet);
  
  while( read( FILE, $buffer, $bufferSize) > 0)
  {
    print "$buffer";
  }
  
  $blRet = 1;
  
  return $blRet;
}

#
# get current path (".")
#
# @return current path
#
sub getCurrentPath
{
  local( $strRet, $cmd, $cwd);

  $cmd = sprintf("pwd > $cwdfmfile");
  &systemExec( $cmd);
  
  if( open(CWDFMFILE, "< $cwdfmfile"))
  {
    if( defined( $line= <CWDFMFILE>))
    {
      $line =~ s/\n//;
      $cwd = $line;
    }
    close( $cwdfmfile);
  }

  $strRet = $cwd;

  return $strRet;
}

#
# executes a command with the OS
#
# @param cmd: command
#
sub systemExec
{
  local( $cmd) = @_;
  local( $rc);
	$rc = 0xffff & system $cmd;
	if( $rc == 0)
	{
		;
	}
	elsif( $rc == 0xff00)
	{
		print "command failed: $!\n";
	}
	elsif( ( $rc & 0xff) == 0)
	{
		$rc >>= 8;
		print "ran with non-zero exit status $rc\n";
	}
	else
	{
		print "ran with ";
		if( $rc & 0x80)
		{
			$rc &= ~0x80;
			print "coredump from ";
		}
		print "signal $rc\n";
	}
}

sub Timestamp {
  local($timestamp);
  
  $timestamp= `date '+%Y%m%d%H%M%S'`;
  $timestamp=~ s/\n//;
  
  return $timestamp;
}

# END OF SCRIPT
