#!/usr/bin/perl
#######################################################################
# logon.cgi Log On Script
# Created by Andreas Eisenhauer haui@haui.cjb.net
# Copyright (c) 2000 All Rights Reserved. 
#
# This script MAY NOT be redistributed for any reason without
# the expressed written consent of the author . All copyrights reserved.
########################################################################

# Define location of passwd.txt file as created from Matt's Script Archive.
$passfile="/usr/home2/vdisk/html/.admin/.passwd";
$logfile ="/usr/home2/vdisk/html/.admin/.logon";

# Get the input
read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});

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
 
if ($ENV{'REQUEST_METHOD'} eq "POST") {

  # Print out a content-type for HTTP/1.0 compatibility
  print "Content-type: text/plain\n\n";

  print "<SOF>\n";
	
  open(PASSWD,"$passfile") || die $!;
  $passwd_line = <PASSWD>;
  chop($passwd_line) if $passwd_line =~ /\n$/;
  close(PASSWD);

  ($username,$passwd) = split(/:/,$passwd_line);

  $test_passwd = crypt($FORM{'password'}, substr($passwd, 0, 2));
  if (!($test_passwd eq $passwd && $FORM{'username'} eq $username)) {
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
else
{

  # Print out a content-type for HTTP/1.0 compatibility
  print "Content-type: text/plain\n\n";

  print "DBasics Log In Script\n";
  print "failed\n";
	print "\n<EOF>";
  exit;
}

# END OF SCRIPT
