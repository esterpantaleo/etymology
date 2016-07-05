#!/usr/bin/perl -w
use strict;
use utf8;
use Encode;
use URI::Escape;

use constant false => 0;
use constant true  => 1;

binmode STDOUT, ":utf8";
binmode STDERR, ":utf8";

my $numArgs = $#ARGV + 1;

if ($numArgs < 4) {
  print STDERR "Usage: extract_kernel_defs.pl lang kernel fr_extract Thesaurus MAN\n";
  print STDERR "  Where: lang in fr, en, de\n";
  exit -1;
}

my $lang = shift @ARGV;
my $kernelFile = shift @ARGV;
my $extractFile = shift @ARGV;
my $thesaurus = shift @ARGV;
my @MANFolder = @ARGV;

my $lcode = "\\#fra\\|";

if ($lang eq "en") {
  $lcode = "\\#eng\\|";
} elsif ($lang eq "de") {
  $lcode = "\\#deu\\|";
}

my %kernel;
load_kernel();
extract_kernel_defs();
display_unavailable_kernel_entries();


sub load_kernel {
  open K_FH, "<:utf8",  "$kernelFile" or die $1;
  my $i = 0;
  
    while(<K_FH>) {
        chomp;
        $i++;
        my $w = $_;
        $kernel{$w} = 1;
        # print STDOUT "Comment: $_\n";
    }
}


sub extract_kernel_defs {
  my $fname = $extractFile;
  my $i=0;
  
    open ED_FH, "<:utf8",  "$fname" or die $1;
    open XED_FH, ">:utf8", "$fname.kdefs";
    my $current_entry = "";
    my $ignoring_current_entry = true;
    
    while(<ED_FH>) {
        chomp;
        $i++;
        if (/^-O-\s*($lcode)(.*)$/) {
          my $w = $2;
          if ($kernel{$w}) {
            $ignoring_current_entry = false;
            $current_entry = $w;
            $kernel{$w} = 2;
            print XED_FH "-O- $1$2\n";
            my $mandata = read_man_files($w);
            if ($mandata ne "") {
              print XED_FH "$mandata";
            }
          } else {
            $ignoring_current_entry = true;
            $current_entry = "";
          }
        } elsif (/-O-.*/) {
            $ignoring_current_entry = true;
            $current_entry = "";
        } elsif (/^\s*-D- \#pos\|(.*)$/) {
          if (! $ignoring_current_entry) {
            my $pos = $1;
            print XED_FH "  -pos- $pos\n";
          }
        } elsif (/^\s*-D- \#def\|(.*)$/) {
            if (! $ignoring_current_entry) {
            my $def = $1;
            print XED_FH "  -def- $def\n";
          }
        }
    }
    
    close XED_FH;
    close ED_FH;
}

sub display_unavailable_kernel_entries {
  foreach my $key (keys(%kernel )) {
    if ($kernel{$key} == 1) {
      print STDERR "unavailable kernel entry : \"$key\"\n";
    }
  }
}

sub read_man_files {
  my $entry = shift;
  
  # œ is not a latin 1 character, so it cannot be url encoded in older MAN kernels.
  $entry =~ s/œ/oe/g;
  $entry =~ s/\’/_/g;
  $entry =~ s/ /_/g;
  
  my $fc = substr($entry, 0, 1);
  
  $entry = uri_escape( $entry);
  $fc = uri_escape($fc);
  
  my $content = "";
  my $p = "";
  
  $content .= read_man_file("$thesaurus/$fc/$entry", ("N", "V", "ADJ", "ADV"));

  foreach (@MANFolder) {
    $content .= read_man_file("$_/$fc/$entry", (""));
  } 
   
  
  return $content;
}

sub read_man_file {
  my ($fname, @suffixes) = @_;


  my $content = "";
  foreach my $suffix (@suffixes) {
    my $mfn = "$fname$suffix";
    if (-e $mfn) {
      $content .= "--- man: $mfn:\n";
      open MF_FH, "<:encoding(iso-8859-1)", $mfn;
      while (my $line = <MF_FH>) {
        trim($line);
        if ($line ne "") {
          #print STDERR "$line\n";
          $content .= "--- $line\n";
        }
      }
    close MF_FH;
  }
  }
  return $content;
}

sub trim {
    # Trim the string (modifying it)
    $_[0] =~ s/^\s+//;
    $_[0] =~ s/\s+$//;
    $_[0];
}