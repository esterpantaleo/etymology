#!/usr/bin/perl -w
use strict;
use utf8;
use Encode;
use URI::Escape;
use File::Find;


use constant false => 0;
use constant true  => 1;

binmode STDOUT, ":utf8";
binmode STDERR, ":utf8";

my $numArgs = $#ARGV + 1;

if ($numArgs != 1) {
  print STDERR "Usage: kernel_dir_file.pl MAN\n";
  exit -1;
}

my $manFolder = shift @ARGV;

find(\&print_content, $manFolder);

sub print_content() {
  my $entry = $_;
    
  $entry = uri_unescape($entry);
  
  print STDOUT "$entry : \n";
  
  open MF_FH, "<:encoding(iso-8859-1)", $File::Find::name;
  while (my $line = <MF_FH>) {
    trim($line);
    if ($line ne "") {
      print STDOUT "    $line\n";
    }
  }
close MF_FH;
}

# sub read_man_files {
#   my $entry = shift;
#   
#   # œ is not a latin 1 character, so it cannot be url encoded in older MAN kernels.
#   $entry =~ s/œ/oe/g;
#   $entry =~ s/\’/_/g;
#   $entry =~ s/ /_/g;
#   
#   my $fc = substr($entry, 0, 1);
#   
#   $entry = uri_escape( $entry);
#   $fc = uri_escape($fc);
#   
#   my $content = "";
#   my $p = "";
#   
#   $content .= read_man_file("$thesaurus/$fc/$entry", ("N", "V", "ADJ", "ADV"));
# 
#   foreach (@MANFolder) {
#     $content .= read_man_file("$_/$fc/$entry", (""));
#   } 
#    
#   
#   return $content;
# }
# 
# sub read_man_file {
#   my $fname = shift;
#   my @suffixes = shift;
# 
#   my $content = "";
#   foreach my $suffix (@suffixes) {
#     my $mfn = "$fname$suffix";
#     if (-e $mfn) {
#       $content .= "--- man: $mfn:\n";
#       open MF_FH, "<:encoding(iso-8859-1)", $mfn;
#       while (my $line = <MF_FH>) {
#         trim($line);
#         if ($line ne "") {
#           #print STDERR "$line\n";
#           $content .= "--- $line\n";
#         }
#       }
#     close MF_FH;
#   }
#   }
#   return $content;
# }

sub trim {
    # Trim the string (modifying it)
    $_[0] =~ s/^\s+//;
    $_[0] =~ s/\s+$//;
    $_[0];
}