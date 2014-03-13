#!/usr/bin/perl
#######################################################################
# writefile.cgi excecute command script
# Created by Andreas Eisenhauer haui@haui.cjb.net
# Copyright (c) 2000 All Rights Reserved. 
#
# This script MAY NOT be redistributed for any reason without
# the expressed written consent of the author . All copyrights reserved.
########################################################################


# output file name
$outfile = "app.ppr";

# Get the input
read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});
 
if ($ENV{'REQUEST_METHOD'} eq "POST")
{
	# Print out a content-type for HTTP/1.0 compatibility
	print "Content-type: text/plain\n\n";
	
	open(OUTFILE, "> $outfile") || die $!;
	print OUTFILE "$buffer";
	close( $outfile);
	print "properties written\n<EOF>";
	$ok = ( $rc == 0);
	exit;
}
else
{

  # Print out a content-type for HTTP/1.0 compatibility
  print "Content-type: text/plain\n\n";

  print "Write File Script\n";
  print "failed\n";
	print "\n<EOF>";
  exit;
}

# END OF SCRIPT
