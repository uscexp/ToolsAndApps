#!/usr/bin/perl
#######################################################################
# excmd.cgi excecute command script
# Created by Andreas Eisenhauer haui@haui.cjb.net
# Copyright (c) 2000 All Rights Reserved. 
#
# This script MAY NOT be redistributed for any reason without
# the expressed written consent of the author . All copyrights reserved.
########################################################################

# Snippet for password protection
$logfile ="/usr/home2/vdisk/html/.admin/.logon";
open(FILE,"$logfile") || die $!;
$logon=<FILE>;
close(FILE);
if ($ENV{'REMOTE_ADDR'} ne $logon) { exit; }
# End of Snippet

# output file name
$outfile = "/usr/home2/vdisk/html/.admin/cgi/tmp.out";

# current working directory file
$cwdfile = "/usr/home2/vdisk/html/.admin/cgi/cwd.out";

# home directory
$homedir = "/usr/home2/vdisk/html";

# Get the input
read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});
if ($ENV{'QUERY_STRING'})
{
	$buffer = "$buffer\&$ENV{'QUERY_STRING'}";
}
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
 
if ($ENV{'REQUEST_METHOD'} eq "POST")
{
	# Print out a content-type for HTTP/1.0 compatibility
	print "Content-type: text/plain\n\n";
	
  print "<SOF>\n";
	
	# Read current working directory
	$cwd = "$homedir";
	if( open(CWDFILE, "< $cwdfile"))
	{
		if( defined( $line= <CWDFILE>))
		{
			$line =~ s/\n//;
			$cwd = $line;
		}
		close( $cwdfile);
	}

	$cmd = $FORM{'cmd'};
	if( $cmd =~ /cd/)
	{
		$cmd = sprintf("cd $cwd;%s > $outfile;pwd > $cwdfile", $cmd);
		$pwd = 1;
	}
	else
	{
		$cmd = sprintf("cd $cwd;%s > $outfile", $cmd);
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
	print "\n<EOF>";
	$ok = ( $rc == 0);
	exit;
}
else
{

  # Print out a content-type for HTTP/1.0 compatibility
  print "Content-type: text/plain\n\n";

  print "excmd Script\n";
  print "failed\n";
	print "\n<EOF>";
  exit;
}

# END OF SCRIPT
