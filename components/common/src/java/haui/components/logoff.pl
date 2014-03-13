#!/usr/bin/perl
#######################################################################
# logoff.cgi Log On Script
# Created by Andreas Eisenhauer haui@haui.cjb.net
# Copyright (c) 2000 All Rights Reserved. 
#
# This script MAY NOT be redistributed for any reason without
# the expressed written consent of the author . All copyrights reserved.
########################################################################

# Define location of passwd.txt file as created from Matt's Script Archive.
$passfile="/usr/home2/vdisk/html/.admin/.passwd";
$logfile ="/usr/home2/vdisk/html/.admin/.logon";

# Define location of current working directory file (cwd.out) as created from excmd.pl
$cwdfile = "/usr/home2/vdisk/html/.admin/cgi/cwd.out";


open(FILE,">$logfile") || die $!;
$access="No One";
print FILE "$access";
close(FILE);

# Delete working directory file
unlink( $cwdfile);

# Print out a content-type for HTTP/1.0 compatibility
print "Content-type: text/plain\n\n";
print "<SOF>\n";
print "DBasics Log In Script\n";
print "$access Logged On\n";
print "\n<EOF>";
exit;

