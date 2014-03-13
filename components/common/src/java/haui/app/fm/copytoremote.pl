#!/usr/bin/perl
#######################################################################
# copytoremote.cgi File Manager upload script
# Created by Andreas Eisenhauer haui@haui.cjb.net
# Copyright (c) 2001 All Rights Reserved.
#
# This script MAY NOT be redistributed for any reason without
# the expressed written consent of the author. All copyrights reserved.
########################################################################

use File::stat;
use File::Path;
use File::Copy;

# Snippet for password protection
#$logfile ="/usr/home2/vdisk/html/.admin/.logon";
#open(FILE,"$logfile") || die $!;
#$logon=<FILE>;
#close(FILE);
#if ($ENV{'REMOTE_ADDR'} ne $logon) { exit; }
# End of Snippet

# output file name
$outfile = "/usr/home2/vdisk/html/.admin/cgi/tmp.out";

# current working directory file
$cwdfile = "/usr/home2/vdisk/html/.admin/cgi/cwd.out";

# temp path file for uploading
$pathfile = "/usr/home2/vdisk/html/.admin/cgi/path.out";

# home directory
$homedir = "/usr/home2/vdisk/html";

# path seperator
$seperator = "/";

# binary file read buffer size
$bufferSize = 32768;

# Get the input
#read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});

if ($ENV{'REQUEST_METHOD'} eq "POST")
{
  local( $strNewPath);
  local( $blRet);
  local( $buffer);
  local( $length);

  print "Content-type: text/plain\n\n";
  print "<SOF>\n";
	
	open( FILE, "< $pathfile") || die $!;
  $strNewPath = <FILE>;
  close( $pathfile);
	
  $blRet = 0;
  
  open( OUTPUT, ">$strNewPath") || die $!;
  
  while( read( STDIN, $buffer, $bufferSize) > 0)
  {
    print OUTPUT "$buffer";
  }
  
  $blRet = 1;
  print "$blRet\n";

  close( $strNewPath);
  
  print "<EOF>\n";
	$ok = 1;
	exit;
}
else
{

  # Print out a content-type for HTTP/1.0 compatibility
  print "Content-type: text/plain\n\n";

  print "<SOF>\n";
  print "copytoremote Script\n";
  print "failed\n";
	print "\n<EOF>\n";
  exit;
}

#############################################################
# subroutines
#############################################################


# END OF SCRIPT
