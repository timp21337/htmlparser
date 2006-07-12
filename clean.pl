#!/usr/bin/perl -w 
use strict;

open (FILE, "<:encoding(utf-8)", "Copy_en.xhtml");
binmode STDOUT, ":utf8";
while (<FILE>) { 
    s|</p>|</p>\n|g;
    s|</span>|</span>\n|g;
    s|</p>|</p>\n|g;
    s|\x2018|"|g;
    s|\x2019|"|g;
    s|’|\&quote;|g;
    s|é|\&eacute;|g;
    s|ô|\&ocirc;|g;
    s|à|\&agrave;|g;
    s|û|\&ucirc|g;
    print;
}
