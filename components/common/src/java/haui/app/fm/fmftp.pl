#!/usr/bin/perl
use lib "./www_ftp";
use strict 'vars';
use vars qw($q $ftp @msg $info @fields @list_fields $action $path $param @retVals $upload_files $work_dir $temp_dir $session_lifetime $session_dir $s $data_http);

use CGI;
$CGI::HEADERS_ONCE=1;
#use CGI::Carp qw(fatalsToBrowser);
use Net::FTP;
use wftpSession;

######################################################################################
# WWW FTP Client
# Script file name
my $this_name = "fmftp.pl";

# Version 1.0.0
my $ver = '1.0.0';
# Last updated: 04.08.2002
# 2002 Copyright by Andreas Eisenhauer
# http://???.???.???/
#
# HERE ARE CONFIGURATION VARIABLES
$upload_files = 6;			#number of upload fields
$work_dir = "www_ftp";
$temp_dir = "$work_dir/temp";		#directory for uploads and editing
$session_lifetime = 300;		#session lifetime in seconds
$session_dir = "$work_dir/sessions"; #sessions directory

#My own variables
$data_http = 'http://thunder.prohosting.com/~myservic/.admin/cgi';
my $retVal = 0;

#############################################################
@fields = qw(site user password); #names of user data fields to connect to FTP server
@list_fields = qw(mode digit owner group size month day time name); #names of fields returned by FTP server for dirs
######################################################################################

$q = new CGI;
load_msg();
blRet = set_temp_dir();

$action = $q->param('action');
$path = $q->param('path');
$param = $q->param('param');
my $sess_id = $q->param('sess_id');

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

if( $path eq ".")
{
  $path = &getCurrentPath();
}

if($sess_id)
{
	$s = new wftpSession(
			lifetime => $session_lifetime,
			path => $session_dir.'/',
			id => $sess_id,
			);
	if($s->start())
  {
		$info = $s->get_data();
		if ($info->{'site'})
    {
			$info->{'dir'} = $path;
			$ftp = Net::FTP->new($info->{'site'});
			$ftp = login_site($ftp, $info);
		}
		else
    {
			#error_here('session_retrieve');
      $retVal = 0;
      print "$retVal\n";
			exit;
		}
	}
	else
  {
		#error_here('session_expired');
    $retVal = 0;
    print "$retVal\n";
		exit;
	}
}
elsif($q->param('user'))
{
	$info = grab_data($q, \@fields);
	if( $ftp = Net::FTP->new($info->{'site'}) )
  {
		$ftp = login_site($ftp, $info);
		$s = new wftpSession(
				lifetime => $session_lifetime,
				path => $session_dir.'/',
				);
		$s->start();
		$sess_id = $s->id();
		$s->save_data($info);
	}
	else
  {
		#error_here("bad_site");
    $retVal = 0;
    print "$retVal\n";
    exit;
	}
}
else
{
	#login_form();
  $retVal = 0;
  print "$retVal\n";
	exit;
}

if( &isDirectory() && $path =~ /^\//)
{
  open(CWDFILE, "> $cwdfile") || die $!;
  print CWDFILE "$path";
  close( $cwdfile);
}
  
change_dir($path) if $path;

#list_dir($ftp, $info);
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
  local($retVal, $cwdfile, $pwd, $rc, $line);
  
	if( $cmd =~ /cd/)
	{
		$cmd = sprintf("cd $path;%s > $outfile;pwd > $cwdfile", $cmd);
		$pwd = 1;
	}
	else
	{
		$cmd = sprintf("cd $path;%s > $outfile", $cmd);
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

  $cmd = sprintf("pwd > $cwdfile");
  &systemExec( $cmd);
  
  if( open(CWDFILE, "< $cwdfile"))
  {
    if( defined( $line= <CWDFILE>))
    {
      $line =~ s/\n//;
      $cwd = $line;
    }
    close( $cwdfile);
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

##########################
sub action_logout
{
	$s->destroy();
  print 1;
	exit;
}

###############################################
#       SUBROUTINES FOR RENAME ACTION         #
###############################################
sub action_rename {
	return unless($q->param("name"));

	start_form($info, \@fields);
	rename_header();
	my $count = 0;
	foreach($q->param("name")){
		show_rename($_, $count++);
		}
	print_action_footer("confirm_rename", 77);
	end_html();
	exit;
	}

sub action_confirm_rename {
	foreach my $name($q->param("name")){
		my $new_name = trim_spaces($q->param("new_name_$name"));
		next unless $new_name;
		unless(check_name($new_name)){
			error_here("bad_name", $name);
			next;
			}
		if($ftp->rename("$name", "$new_name")){
			success_here("renamed", $name, $new_name);
			}
		 else{
		 	error_here("not_renamed", $name, $new_name);
		 	next;
		 	}
		}
	}

sub show_rename {
	my($name, $count) = @_;
	my $bg = ($count%2) ? "#EEEEEE" : "#FFFFFF";
	print_start_table($name, $bg);
	print <<"EOT";
	<TD WIDTH=250>$name</TD>
	<TD><INPUT SIZE=30 TYPE=text NAME="new_name_$name" VALUE="$name"></TD>
	</TR>
	</TABLE>
EOT
	}

sub rename_header {
	all_header();
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=525>
	<TR CLASS="header"><TD WIDTH=25>&nbsp;</TD><TD WIDTH=250>$msg[22]</TD><TD>$msg[23]</TD></TR>
	</TABLE>
EOT
	}

#################################################
#        SUBROUTINES FOR CHMOD ACTION           #
#################################################
sub action_chmod {
	return unless ($q->param("name"));
	start_form($info, \@fields);
	chmod_header();
	my $count = 0;
	foreach($q->param("name")){
		show_chmod($_, $count++);
		}
	print_action_footer("confirm_chmod", 78);
	end_html();
	exit;
	}

sub action_confirm_chmod {
	foreach my $name($q->param("name")){
		my $mode = $q->param("mode_$name");
		#site method returns most significant digit of the response code,
		#so if it is not 2 the command went wrong
		my $response=($ftp->site("CHMOD $mode $name"));
		if($response == 2){
			success_here("chmodded",  $name, $mode);
			}
		else{
			error_here("not_chmodded", $name, $mode);
			next;
			}
		}
	}

sub show_chmod {
	my($name, $count) = @_;
	my $bg = ($count%2) ? "#EEEEEE" : "#FFFFFF";
	print_start_table($name, $bg);
	print <<"EOT";
	<TD WIDTH=300>$name</TD>
	<TD><INPUT SIZE=4 MAXLENGTH=3 TYPE=text NAME="mode_$name" VALUE=""></TD>
	</TR>
	</TABLE>
EOT
	}

sub chmod_header {
	all_header();
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=525>
	<TR CLASS="header"><TD WIDTH=25>&nbsp;</TD><TD WIDTH=300>$msg[19]</TD><TD>$msg[20]</TD></TR>
	</TABLE>
EOT
	}

###############################################
#     SUBROUTINES FOR DELETE_FILE ACTION      #
###############################################
sub action_delete_file {
	return unless($q->param("name"));

	start_form($info, \@fields);
	delete_file_header();
	my $count = 0;
	foreach($q->param("name")){
		show_delete_file($_, $count++);
		}
	print_action_footer("confirm_delete_file", 79);
	end_html();
	exit;
	}

sub action_confirm_delete_file {
	foreach my $name($q->param("name")){
		if($ftp->delete("$name")){
			success_here("file_deleted", $name);
			}
		 else{
		 	error_here("file_not_deleted", $name);
		 	next;
		 	}
		}
	}

sub show_delete_file {
	my($name, $count) = @_;
	my $bg = ($count%2) ? "#EEEEEE" : "#FFFFFF";
	print_start_table($name, $bg);
	print <<"EOT";
	<TD>$name</TD>
	</TR>
	</TABLE>
EOT
	}

sub delete_file_header {
	all_header();
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=525>
	<TR CLASS="header"><TD WIDTH=25>&nbsp;</TD><TD>$msg[19]</TD></TR>
	</TABLE>
EOT
	}

###############################################
#      SUBROUTINES FOR DELETE_DIR ACTION      #
###############################################
sub action_delete_dir {
	return unless($q->param("name"));

	start_form($info, \@fields);
	delete_file_header();
	my $count =0;
	foreach($q->param("name")){
		show_delete_file($_, $count++);
		}
	print_action_footer("confirm_delete_dir", 80);
	end_html();
	exit;
	}

sub action_confirm_delete_dir {
	foreach my $name($q->param("name")){
		#if we put a second arg = true for rmdir, then it will remove recursive
		$ftp->rmdir("$name", 1);
		if($ftp->status == 2){
			success_here("dir_deleted", $name);
			}
		else {
		 	error_here("generic", $ftp->message);
		 	next;
			}
		}
	}

###############################################
#       SUBROUTINES FOR NEW_DIR ACTION        #
###############################################
sub action_new_dir {
	my $new_dir_name = trim_spaces($q->param("new_dir_name"));
	return unless $new_dir_name;
	if($ftp->mkdir("$new_dir_name")){
		success_here("dir_created", $new_dir_name);
		}
	 else{
	 	error_here("dir_not_created", $new_dir_name);
	 	}
	}

###############################################
#     SUBROUTINES FOR DOWNLOAD_FILE ACTION      #
###############################################
sub action_download_files {
	return unless($q->param("name"));

	start_form($info, \@fields);
	download_header();
	my $count = 0;
	foreach my $name($q->param("name")){
		show_download_field( $name, $count++);
		}
	print_action_footer("confirm_download", 95);
	end_html();
	exit;
	}

sub action_confirm_download {
	my $temp_name;
	my @chars = ('a' .. 'z', 0 ..9);

	my(@to_get, @to_get_temp, @modes, @to_get_short_name);
  my $count = 0;
	foreach my $name($q->param("name")){
		my $mode = $q->param("mode_$count");
		#next unless $_;
		(my $short_name = $_) =~ s/.*[\/\\](.*)$/$1/;

		#getting temp name
		for(;;){
			$temp_name = join('', @chars[ map { rand @chars}(1 .. 8)]);
			(! -e "$temp_dir/$temp_name") && last;
			}

    push(@to_get, $name);
    #push(@to_get_temp, $temp_name);
    push(@to_get_short_name, $short_name);
    push(@modes, $mode);
    $count++;
    }
    
  $count = 0;
  foreach my $name(@to_get){
    if ($modes[$count] eq "binary"){
      $ftp->binary();
      }
    else {
      $ftp->ascii();
      }
    $temp_name = $ftp->get("$name", "$temp_dir/$name");
    push(@to_get_temp, $name);
    $count++;
    }
  
  start_form($info, \@fields);
  download_file_header();
	$count = 0;
	foreach my $name(@to_get){
    show_download_file( "$name", $count++);
    }
	print_action_footer("delete_temp_files", 96);
	end_html();
  exit;
	}

sub action_delete_temp_files {
	return unless($q->param("name"));

	my $count = 0;
	foreach my $name($q->param("name")){
		if(unlink ("$temp_dir/$name")){
			success_here("file_deleted", $name);
			}
		else {
		 	error_here("file_not_deleted", $name);
			}
		}
	}

sub show_download_file {
	my($name, $count) = @_;
	my $bg = ($count%2) ? "#EEEEEE" : "#FFFFFF";
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=525>
	<TR BGCOLOR="$bg">
	<TD ALIGN=center WIDTH=70></TD>
	<TD ALIGN=left><INPUT TYPE=checkbox NAME="name" VALUE="$name" CHECKED><A HREF="$data_http/$temp_dir/$name" TARGET="_new10">$name</A></TD>
	</TR>
	</TABLE>
EOT
	}

sub download_file_header {
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=525>
	<TR CLASS="header"><TD ALIGN=center WIDTH=70>&nbsp;</TD><TD>$msg[19]</TD></TR>
	</TABLE>
EOT
	}

sub show_download_field {
	my($name, $count) = @_;
	my $bg = ($count%2) ? "#EEEEEE" : "#FFFFFF";
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=525>
	<TR BGCOLOR="$bg">
	<TD ALIGN=center WIDTH=70><SELECT NAME="mode_$count"><OPTION VALUE="binary">Binary<OPTION VALUE="ascii">ASCII</SELECT></TD>
	<TD ALIGN=left><INPUT TYPE=checkbox NAME="name" VALUE="$name" CHECKED>$name</TD>
	</TR>
	</TABLE>
EOT
	}

sub download_header {
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=525>
	<TR CLASS="header">
	<TD ALIGN=center WIDTH=70>Mode:
	<TD ALIGN=left>$msg[19]</TD>
	</TR>
	</TABLE>
EOT
	}

###############################################
#     SUBROUTINES FOR UPLOAD_FILES ACTION     #
###############################################
sub action_upload_files {
	start_form($info, \@fields);
	upload_header();
	for (my $i = 1; $i <= $upload_files; $i++) {
		show_upload_field($i);
		}
	print_action_footer("confirm_upload", 82);
	end_html();
	exit;
	}

sub action_confirm_upload {
	my $temp_name;
	my @chars = ('a' .. 'z', 0 ..9);

	my(@to_put, @to_put_temp, @modes);
	for (my $i=1; $i<=$upload_files; $i++){
		my $file = $q->param("file_$i");
		my $mode = $q->param("mode_$i");
		next unless $file;
		(my $short_name = $file) =~ s/.*[\/\\](.*)$/$1/;

		#getting temp name
		for(;;){
			$temp_name = join('', @chars[ map { rand @chars}(1 .. 8)]);
			(! -e "$temp_dir/$temp_name") && last;
			}

		if(open (OUTFILE, ">$temp_dir/$temp_name")){
	 		if($mode eq "binary"){
				binmode(OUTFILE);
				my ($bytesread, $buffer);
				while($bytesread = read($file, $buffer, 1024)){
					print OUTFILE $buffer;
					}
				}
			else {
				while(<$file>){
					chomp;
					s/\r$//;
					print OUTFILE "$_\n";
					}
				}
			unless(close(OUTFILE)){
				print "$msg[27]: $!.<BR>";
				unlink ("$temp_dir/$temp_name") or print "$msg[28]: $!";
				print "$msg[92]: <B>$short_name</B>.<BR>";
				next;
				}

			push(@to_put, $short_name);
			push(@to_put_temp, $temp_name);
			push(@modes, $mode);
			}
		else {
			print "$msg[91]: $!.<BR>";
			print "$msg[92]: <B>$short_name</B>.<BR>";
			next;
			}
		}

	my $count = 0;
	foreach my $name(@to_put){
		if ($modes[$count] eq "binary"){
			$ftp->binary();
			}
		else {
			$ftp->ascii();
			}
		if ($ftp->put("$temp_dir/$to_put_temp[$count]", "$name")){
			success_here("file_uploaded", $name);
			}
		else{
			error_here("bad_upload", $name);
			}
		unlink ("$temp_dir/$to_put_temp[$count]");
		$count++;
		}
	}

sub show_upload_field {
	my($index) = @_;
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=400>
	<TR>
	<TD ALIGN=center WIDTH=70><SELECT NAME="mode_$index"><OPTION VALUE="binary">Binary<OPTION VALUE="ascii">ASCII</SELECT></TD>
	<TD ALIGN=left><INPUT SIZE=35 TYPE=FILE NAME="file_$index" VALUE=""></TD>
	</TR>
	</TABLE>
EOT
	}

sub upload_header {
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=400>
	<TR>
	<TD ALIGN=center WIDTH=70>Mode:
	<TD ALIGN=left>$msg[29]</TD>
	</TR>
	</TABLE>
EOT
	}

#################################################
#         SUBROUTINES FOR MOVING AROUND         #
#################################################
sub action_change_dir {
	my $name = $q->param("name") if defined $q->param("name");
	my $new_dir = $info->{'dir'};
	$new_dir .= "/" unless ($new_dir eq "/");
	$new_dir .= $name;
	change_dir($new_dir);
	}

sub change_dir {
	my $new_dir = shift;
	if($ftp->cwd($new_dir)){
		$info->{"dir"} = $new_dir;
		}
	else {
		error_here("bad_dir", $new_dir);
		}
	}

sub action_up {
	$ftp->cdup();
	$info->{dir} = $ftp->pwd();
#	$info->{dir} = "" if $info->{dir} == "/";
	}

#################################################
#       SUBROUTINES FOR DIRECTORY LISTINGS      #
#################################################
sub list_dir {
	my($ftp, $info) = @_;
	my(@files, @dirs);
	my $list = read_site($ftp);

	#composing code that will be eval'ed to fill $node_info by parsing
	#$ftp->dir() output. we'll get something like this:
	#($node_info->{"user"},$node_info->{"group"})=split(" ");
	my $code = '(';
	foreach(@list_fields){
		$code .= '$node_info->{"' . $_ . '"}, ';
		}
	$code .= ') = split(" ", $_, 9);';

	foreach(@$list){
		next if /\.{1,2}$/ || /^total/;
		my $node_info;
		eval $code;
		(/^d/) ? push(@dirs, $node_info) : push(@files, $node_info);
		}
	show_dirs(\@dirs);
	show_files(\@files);
	}

sub show_dirs {
	my($dirs) = @_;
	start_dirs_table();
	my $count = 0;
	my $dirs_num = scalar @$dirs;
	my $rows_num = ($dirs_num%2) ? int($dirs_num/2) + 1: int($dirs_num/2);

	for(my $i = 0; $i < $rows_num; $i++){
		show_2_dir($dirs->[$i], $dirs->[$i + $rows_num], $i);
		}
	print qq(<TABLE BORDER=0 WIDTH=700><TR><TD><I>$msg[17]</I></TD></TR></TABLE>) unless $dirs_num;
	}

sub show_2_dir {
	my($dir_info1, $dir_info2, $count) = @_;
	my $bg = ($count%2) ? "#EEEEEE" : "#FFFFFF";
	unless($count){
		print <<"EOT";
		<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=700><TR>
		<TD WIDTH=25>&nbsp;</TD><TD WIDTH=225>$msg[19]</TD><TD WIDTH=100>$msg[20]</TD>
		<TD WIDTH=25>&nbsp;</TD><TD WIDTH=225>$msg[19]</TD><TD WIDTH=100>$msg[20]</TD>
		</TR></TABLE>
EOT
		}
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=0 CELLSPACING=0 WIDTH=700 ALIGN=center>
	<TR BGCOLOR="$bg">
	<TD WIDTH=350>
		<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=100% ALIGN=center><TR>
		<TD WIDTH=25><INPUT TYPE=checkbox NAME="name" VALUE="$dir_info1->{name}"</TD>
		<TD WIDTH=225><B>$dir_info1->{name}</B></TD>
		<TD WIDTH=100>$dir_info1->{mode}</TD>
		</TR></TABLE>
	</TD><TD WIDTH=350>
EOT
	if($dir_info2){
		print <<"EOT";
		<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=100% ALIGN=center><TR>
			<TD WIDTH=25><INPUT TYPE=checkbox NAME="name" VALUE="$dir_info2->{name}"</TD>
			<TD WIDTH=225><B>$dir_info2->{name}</B></TD>
			<TD WIDTH=100>$dir_info2->{mode}</TD>
		</TR></TABLE>
EOT
		}
	else {
		print qq(&nbsp;);
		}
	print qq(</TD></TR></TABLE>);
	}

sub show_files {
	my($files) = @_;
	my $i;
	start_files_table();
	my $files_num = scalar @$files;
	my $rows_num = ($files_num%2) ? int($files_num/2) + 1: int($files_num/2);
	for(my $i = 0; $i < $rows_num; $i++){
		show_2_file($files->[$i], $files->[$i + $rows_num], $i);
		}
	print qq(<TABLE BORDER=0 WIDTH=700><TR><TD><I>$msg[18]</I></TD></TR></TABLE>) unless $files_num;
	}

sub show_2_file {
	my($file_info1, $file_info2, $count) = @_;
	my $bg = ($count%2) ? "#EEEEEE" : "#FFFFFF";
	unless($count){
		print <<"EOT";
		<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=700><TR>
		<TD WIDTH=25></TD><TD WIDTH=170>$msg[19]</TD><TD WIDTH=45>$msg[21]</TD><TD WIDTH=100>$msg[20]</TD>
		<TD WIDTH=25></TD><TD WIDTH=170>$msg[19]</TD><TD WIDTH=45>$msg[21]</TD><TD WIDTH=100>$msg[20]</TD>
		</TR></TABLE>
EOT
		}
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=0 CELLSPACING=0 WIDTH=700 ALIGN=center>
	<TR BGCOLOR="$bg">
	<TD WIDTH=350>
		<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=100% ALIGN=center><TR>
		<TD WIDTH=25><INPUT TYPE=checkbox NAME="name" VALUE="$file_info1->{name}"</TD>
		<TD WIDTH=170>$file_info1->{name}</TD>
		<TD WIDTH=45>$file_info1->{size}</TD>
		<TD WIDTH=100>$file_info1->{mode}</TD>
		</TR></TABLE>
	</TD><TD WIDTH=350>
EOT
	if($file_info2){
		print <<"EOT";
		<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=100% ALIGN=center><TR>
			<TD WIDTH=25><INPUT TYPE=checkbox NAME="name" VALUE="$file_info2->{name}"</TD>
			<TD WIDTH=170>$file_info2->{name}</TD>
			<TD WIDTH=45>$file_info2->{size}</TD>
			<TD WIDTH=100>$file_info2->{mode}</TD>
		</TR></TABLE>
EOT
		}
	else {
		print qq(&nbsp;);
		}
	print qq(</TD></TR></TABLE>);
	}

sub start_dirs_table {
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=700>
	<TR CLASS="header"><TD COLSPAN=6>$msg[15]:</TD></TR>
	</TABLE>
EOT
	}

sub start_files_table {
	print <<"EOT";
	<BR>
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=700>
	<TR CLASS="header"><TD COLSPAN=8>$msg[16]:</TD></TR>
	</TABLE>
EOT
	}

#################################################
#       LOGGING TO AND READING FROM FTP         #
#################################################
sub login_site()
{
	my($ftp, $info) = @_;
	$ftp->login($info->{"user"}, $info->{"password"}) or error_here("bad_login");
#	action_change_dir($info->{"dir"});
	change_dir($info->{'dir'});
	return $ftp;
}

sub read_site()
{
	my($ftp) = @_;
	my @list = $ftp->dir('-a'); #to show hidden files
	$ftp->quit();
	return \@list;
}

#################################################
# COMPOSING FORM HIDDEN FIELDS AND GETTING THEM #
#################################################
sub grab_data {
	my($q, $fields) = @_;
	my $data;
	foreach(@$fields){
		$data->{$_} = $q->param($_);
		}
	return $data;
	}

#################################################
#              MISC SUBROUTINES                 #
#################################################
sub parse_action_name {
	local($_) = @_;
	$_ = lc(trim_spaces($_));
	s/\s+/_/g;
	return $_;
	}

sub check_name {
	local($_) = @_;
	$_ = trim_spaces($_);
	(/^[\w.+]+$/) ? 1 : 0;
	}

sub trim_spaces {
	local($_) = @_;
	s/^\s+//;
	s/\s+$//;
	return $_;
	}

sub all_header {
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=525>
	<TR><TD ALIGN=center>$msg[30]</TD></TR>
	</TABLE>
EOT
	}

sub login_form {
	print <<"EOT";
	<FORM NAME=login_form METHOD=POST>
	<TABLE BORDER=1 CELLPADDING=3 CELLSPACING=0>
	<TR><TD>Host address:<TD><INPUT TYPE=text NAME=site>
	<TR><TD>User:<TD><INPUT TYPE=text NAME=user>
	<TR><TD>Password:<TD><INPUT TYPE=password NAME=password>
	<TR><TD>Start directory:<TD><INPUT TYPE=text NAME=start_dir>
	<TR><TD><TD><INPUT TYPE=submit VALUE="Login"><INPUT TYPE=reset VALUE="Clear">
	</TABLE>
	</FORM>
EOT
	end_html();
	}

sub print_action_footer {
	my($action, $msg_id) = @_;
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=425>
	<TR ALIGN=center><TD><INPUT CLASS="submitbutton" TYPE=submit NAME="action_$action" VALUE="$msg[$msg_id]"><INPUT CLASS="submitbutton" TYPE=submit VALUE="$msg[83]"></TD></TR>
	</TABLE>
EOT
	}

sub print_start_table {
	my($name, $bg) = @_;
	print <<"EOT";
	<TABLE BORDER=0 CELLPADDING=2 CELLSPACING=0 WIDTH=525>
	<TR BGCOLOR="$bg">
	<TD WIDTH=25><INPUT TYPE=checkbox NAME="name" VALUE="$name" CHECKED></TD>
EOT
	}

#################################################
#  SUBROUTINES FOR SUCCESS AND ERROR MESSAGES   #
#################################################
sub success_here {
	my($reason, @param) = @_;
	print $q->header();
	if ($reason eq "renamed"){
		print "<B>$param[0]</B> $msg[37] <B>$param[1]</B>.<BR>";
		}
	if ($reason eq "chmodded"){
		print "<B>$param[0]</B> $msg[38] <B>$param[1]</B>.<BR>";
		}
	if ($reason eq "dir_deleted"){
		print "$msg[39] <B>$param[0]</B> $msg[40].<BR>";
		}
	if ($reason eq "file_deleted"){
		print "$msg[41] <B>$param[0]</B> $msg[40].<BR>";
		}
	if ($reason eq "file_modified"){
		print "$msg[41] <B>$param[0]</B> $msg[42].<BR>";
		}
	if ($reason eq "dir_created"){
		print "$msg[39] <B>$param[0]</B> $msg[43].<BR>";
		}
	if ($reason eq "file_uploaded"){
		print "$msg[41] <B>$param[0]</B> $msg[44].<BR>";
		}
	if ($reason eq "file_downloaded"){
		print "$msg[41] <B>$param[0]</B> $msg[44].<BR>";
		}
	}

sub error_here {
	my($reason, @param) = @_;
	print $q->header();
	if ($reason eq "bad_login"){
		print "$msg[45]";
		exit;
		}
	if ($reason eq "bad_site"){
		print "$msg[46]";
		exit;
		}
	if ($reason eq "not_renamed"){
		print "$msg[47] <B>$param[0]</B> => <B>$param[1].</B><BR>";
		}
	if ($reason eq "chmod_not_supported"){
		print "$msg[48]<BR>";
		}
	if ($reason eq "not_chmodded"){
		print "$msg[49] <B>$param[0]</B> => <B>$param[1]</B><BR>";
		}
	if ($reason eq "dir_not_deleted"){
		print "$msg[50] <B>$param[0]</B>.<BR>";
		}
	if ($reason eq "file_not_deleted"){
		print "$msg[51] <B>$param[0]</B>.<BR>";
		}
	if ($reason eq "bad_edit"){
		print "$msg[52] <B>$param[0]</B>.<BR>";
		}
	if ($reason eq "file_not_got"){
		print "$msg[53]: <B>$param[0]</B><BR>";
		}
	if ($reason eq "dir_not_created"){
		print "$msg[54] <B>$param[0]</B>.<BR>";
		}
	if ($reason eq "bad_dir"){
		print "$msg[55] <B>$param[0]</B>.<BR>";
		}
	if ($reason eq "bad_upload"){
		print "$msg[56] <B>$param[0]</B>.<BR>";
		}
	if ($reason eq "bad_download"){
		print "$msg[56] <B>$param[0]</B>.<BR>";
		}
	if ($reason eq "bad_name"){
		print "$msg[57] <B>$param[0]</B>. $msg[58]<BR>";
		}
	if ($reason eq "session_expired"){
		print "$msg[93]<BR><A HREF='$this_name'>Login</A><BR>";
		}
	if ($reason eq "session_retrieve"){
		print "$msg[94]<BR><A HREF='$this_name'>Login</A><BR>";
		}
	if ($reason eq "generic"){
		print "$msg[59]: ", $param[0];
		}
	}

sub set_temp_dir
{
	unless (-e "$temp_dir")
  {
		unless(mkdir("$temp_dir", 0777))
    {
			return 0;
		}
	}
  return 1;
}

sub load_msg {
	@msg = ("","Site","Current Directory","Site Manager","Logout","New Dir","New Text File",
"Upload Files","Change Dir","  Up  ","Rename","Chmod","Edit File","Delete File","Delete Dir",
"Directories","Files","No directories","No files","Name","Mode","Size","Old name","New name",
"Cannot get file","Edit","Cannot open file","Cannot close file","Cannot delete file",
"Please select files to <B>upload</B>.","Please make sure you are not making a fatal mistake.<BR>Only checked files(directories) will be processed.",
"Host address","User","Password","Start directory","Login","Clear","has been renamed to","has been chmodded to",
"Directory","has been deleted","File","has been modifed","has been created","has been uploaded",
"Cannot login to host.<BR>Either the host is unreachable or you entered the wrong data.",
"Cannot connect to host.<BR>Host seems not to answer.","Cannot rename</B>",
"<B>chmod</B> command not supported on this FTP server.","Cannot chmod</B>.","Cannot delete directory",
"Cannot delete file","Cannot modify file","Cannot get file from FTP server","Cannot create directory",
"Cannot move to","Cannot upload file","Illegal new name for","Please use only alphanumerics, '.' and '+'.",
"FTP error occured","FTP Site Label","Host Address","Initial Remote Directory","Save","Edit",
"Delete","Connect","Change Password","Old password","New password","Confirm new password","Confirm Change Password",
"Enter Password","Sorry, wrong old password.","Sorry, passwords differ.","Password has been changed.",
"New Site","Confirm Rename","Confirm Chmod","Confirm Delete File","Confirm Delete Dir","Save File",
"Confirm Upload","Cancel","Change Language","Confirm changing language","Cannot open directory",
"Cannot close directory","Change Interface Language","Download","File has not been modifed",
"Cannot save temporary file on local server","File has not been uploaded","Your session has expired, you have to login again.",
"Cannot retrieve session data. Try to login again.","Confirm Download","Delete Temp Files",
);
	}
