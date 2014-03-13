#!/usr/bin/perl
#######################################################################
# readfile.cgi excecute command script
# Created by Andreas Eisenhauer haui@haui.cjb.net
# Copyright (c) 2000 All Rights Reserved. 
#
# This script MAY NOT be redistributed for any reason without
# the expressed written consent of the author . All copyrights reserved.
########################################################################


# output file name
$infile = "app.ppr";

# Print out a content-type for HTTP/1.0 compatibility
print "Content-type: text/plain\n\n";

open(INFILE, "> $infile") || die $!;
while( defined( $line= <INFILE>))
{
	print "$line";
}
close( $outfile);
print "<EOF>";
$ok = ( $rc == 0);
exit;

# END OF SCRIPT
