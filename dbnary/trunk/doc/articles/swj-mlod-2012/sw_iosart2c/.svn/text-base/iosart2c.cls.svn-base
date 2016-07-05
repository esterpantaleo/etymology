%% This is file `iosart2c.cls'
%%
%% LaTeX 2e class file for articles in the journals:
%%   Journal of Ambient Intelligence and Smart Environments (JAISE),
%%   Web Intelligence and Agent Systems: An International Journal (WIAS)
%%   Semantic Web (SW)
%% published by IOS Press, The Netherlands 
%%
%% Macros written by Vytas Statulevicius, VTeX, Lithuania
%% for IOS Press, The Netherlands
%% Please submit bugs or your comments to vytas@vtex.lt
%%
%% You are free to use this class file as you see fit, provided 
%% that you do not make changes to the file. 
%% If you DO make changes, you are required to rename this file.
%%
%% It may be distributed under the terms of the LaTeX Project Public
%% License, as described in lppl.txt in the base LaTeX distribution.
%% Either version 1.0 or, at your option, any later version.
%%
%% \CharacterTable
%%  {Upper-case    \A\B\C\D\E\F\G\H\I\J\K\L\M\N\O\P\Q\R\S\T\U\V\W\X\Y\Z
%%   Lower-case    \a\b\c\d\e\f\g\h\i\j\k\l\m\n\o\p\q\r\s\t\u\v\w\x\y\z
%%   Digits        \0\1\2\3\4\5\6\7\8\9
%%   Exclamation   \!     Double quote  \"     Hash (number) \#
%%   Dollar        \$     Percent       \%     Ampersand     \&
%%   Acute accent  \'     Left paren    \(     Right paren   \)
%%   Asterisk      \*     Plus          \+     Comma         \,
%%   Minus         \-     Point         \.     Solidus       \/
%%   Colon         \:     Semicolon     \;     Less than     \<
%%   Equals        \=     Greater than  \>     Question mark \?
%%   Commercial at \@     Left bracket  \[     Backslash     \\
%%   Right bracket \]     Circumflex    \^     Underscore    \_
%%   Grave accent  \`     Left brace    \{     Vertical bar  \|
%%   Right brace   \}     Tilde         \~}
%%
%%
%% Bug fixes and changes:
%% 2009.05.21 - doi command added
%% 2010.01.04 - jaice price changed
%% 2010.10.26 - new journal sw
%%            - new command \review{Editor}{Solicited review}{Open review}
%% 2010.10.28 - pakeistas review sriftas

\NeedsTeXFormat{LaTeX2e}[2000/01/26]
\ProvidesClass{iosart2c}
              [2010/10/28 v1.0, IOS Press]

\newif\if@restonecol \@restonecolfalse
\newif\if@openright
\newif\if@mainmatter \@mainmattertrue

\DeclareOption{draft}{\setlength\overfullrule{5pt}}
\DeclareOption{final}{\setlength\overfullrule{0pt}}
\DeclareOption{openright}{\@openrighttrue}
\DeclareOption{openany}{\@openrightfalse}
\DeclareOption{onecolumn}{\@twocolumnfalse\@restonecoltrue}
\DeclareOption{twocolumn}{\@twocolumntrue}
\DeclareOption{leqno}{\input{leqno.clo}}
\DeclareOption{fleqn}{\input{fleqn.clo}}%
\DeclareOption{crcready}{\AtBeginDocument{\gdef\rtitleheadline{}\gdef\@copyright{}%
\pagestyle{empty}\relax
}}


% Numbering switches:
\newif\if@seceqn   \@seceqnfalse   \DeclareOption{seceqn}{\@seceqntrue}
\newif\if@secfloat \@secfloatfalse \DeclareOption{secfloat}{\@secfloattrue}
\newif\if@secthm                   \DeclareOption{secthm}{\@secthmtrue}

% Information about journal

\gdef\@issn{0000-0000}
\gdef\@price{00.00}
\gdef\@journal{Undefined}
\def\@publisher{IOS Press}
\def\@copyrightowner{ -- IOS Press and the authors. All rights reserved}


\DeclareOption{wias}{%
  \gdef\@issn{1570-1263}
  \gdef\@price{27.50}
  \gdef\@journal{Web Intelligence and Agent Systems: An International Journal}}

\DeclareOption{jaise}{%
  \gdef\@issn{1876-1364}
  \gdef\@price{27.50}
  \gdef\@journal{Journal of Ambient Intelligence and Smart Environments}}

\DeclareOption{sw}{%
  \gdef\@issn{1570-0844}
  \gdef\@price{27.50}
  \gdef\@journal{Semantic Web}}



%**************** options to amsmath
\PassOptionsToPackage{fleqn}{amsmath}


\ExecuteOptions{twocolumn,twoside,final,openright,fleqn}
\ProcessOptions

%************************* FONTS

% Fonts:
\typeout{Ten point}
\renewcommand\normalsize{%
   \@setfontsize\normalsize\@xpt{12pt plus .5\p@ minus .1\p@}%
   \abovedisplayskip 12\p@ \@plus2pt \@minus2pt%
   \abovedisplayshortskip\abovedisplayskip%
   \belowdisplayshortskip\abovedisplayskip%
   \belowdisplayskip \abovedisplayskip%
   \let\@listi\@listI}

\newcommand\small{%
   \@setfontsize\small\@ixpt\@xipt%
   \abovedisplayskip 11\p@ \@plus3pt%
   \abovedisplayshortskip 11\p@ \@plus1pt \@minus1pt%
   \belowdisplayshortskip 11\p@ \@plus1pt \@minus1pt%
   \def\@listi{\leftmargin\leftmargini
               \topsep 5\p@ \@plus2\p@ \@minus2\p@
               \parsep \z@ \itemsep \parsep}%
   \belowdisplayskip \abovedisplayskip%
}
\newcommand\footnotesize{%
   \@setfontsize\footnotesize\@viiipt\@xpt%
   \abovedisplayskip 10\p@ \@plus3pt%
   \abovedisplayshortskip 10\p@ \@plus1pt \@minus1pt%
   \belowdisplayshortskip 10\p@ \@plus1pt \@minus1pt%
   \def\@listi{\leftmargin\leftmargini
               \topsep 4\p@ \@plus2\p@ \@minus2\p@
               \parsep \z@ \itemsep \parsep}%
   \belowdisplayskip \abovedisplayskip%
}


\newcommand\scriptsize{\@setfontsize\scriptsize\@viipt\@viiipt}
\newcommand\tiny{\@setfontsize\tiny\@vipt\@viipt}
\newcommand\large{\@setfontsize\large\@xipt{13}}
\newcommand\Large{\@setfontsize\Large\@xivpt{18}}
\newcommand\LARGE{\@setfontsize\LARGE\@xviipt{22}}
\newcommand\huge{\@setfontsize\huge\@xxpt{25}}
\newcommand\Huge{\@setfontsize\Huge\@xxvpt{30}}
\normalsize

% Customization of fonts
\renewcommand\sldefault{it}
\renewcommand\bfdefault{b}
\let\slshape\itshape

% ********************* DIMENSIONS:

% TEXT DIMENSIONS
\setlength\parindent{10\p@}
\setlength\textwidth{460\p@} %162mm

\setlength\textheight{50\baselineskip}
\advance\textheight by \topskip

\setlength\columnsep{26\p@}  %9mm 
\setlength\columnwidth{217\p@}
\setlength\columnseprule{0\p@}

%% Centering on A4 page
\hoffset=-1in
\voffset=-1.15mm
\def\paper@width{210mm}
\def\paper@height{297mm}
\@tempdima=\paper@width
\advance\@tempdima by-\textwidth
\divide\@tempdima  by2




% HEADS:
\setlength\headheight{12\p@}
\setlength\headsep   {15\p@}
\setlength\topskip   {10\p@}
\setlength\footskip  {25\p@}
\setlength\maxdepth  {.5\topskip}

% SIDE MARGINS
\setlength\oddsidemargin   {\@tempdima}
\setlength\evensidemargin  {\@tempdima}
\setlength\topmargin       {10mm}%

% TEXT PARAMETERS
\setlength\lineskip{1\p@}
\setlength\normallineskip{1\p@}
\renewcommand\baselinestretch{}
\setlength\parskip{0\p@}

% BREAKS
\setlength\smallskipamount{6\p@ \@plus 1\p@ \@minus 1\p@}
\setlength\medskipamount{12\p@ \@plus 2\p@ \@minus 3\p@} %%DG
\setlength\bigskipamount{24pt \@plus 6\p@ \@minus 6\p@}  

% PAGE-BREAKING PENALTIES
\clubpenalty=10000
\widowpenalty=10000
\displaywidowpenalty=50
\pretolerance=100
\tolerance=800
\hbadness=800
\emergencystretch=3\p@
\doublehyphendemerits=80000
\hfuzz=1\p@

\doublehyphendemerits=0
\adjdemerits=0
\brokenpenalty=0
\interlinepenalty=0

\if@twocolumn
 \setlength\marginparsep {10\p@}
\else
  \setlength\marginparsep{7\p@}
\fi
\setlength\marginparpush{5\p@}

% FOOTNOTES
\setlength\footnotesep{6.65\p@}
\setlength{\skip\footins}{18\p@ \@plus 6\p@}

% FLOATS
\setlength\floatsep    {15\p@ \@plus 10\p@ \@minus 4\p@}
\setlength\textfloatsep{12\p@ \@plus 6\p@ \@minus 4\p@}
\setlength\intextsep   {12\p@ \@plus 6\p@ \@minus 4\p@}
\setlength\dblfloatsep    {15\p@ \@plus 10\p@ \@minus 4\p@}
\setlength\dbltextfloatsep{12\p@ \@plus 12\p@ \@minus 4\p@}

%  For floats on a separate float page or column:
\setlength\@fptop{0\p@ \@plus 1fil}
\setlength\@fpsep{8\p@ \@plus 1000fil}
\setlength\@fpbot{0\p@ \@plus 1fil}
\setlength\@dblfptop{0\p@ \@plus 1fil}
\setlength\@dblfpsep{8\p@ \@plus 1000fil}
\setlength\@dblfpbot{0\p@ \@plus 1fil}

\setcounter{topnumber}{5}
\renewcommand\topfraction{.90}
\setcounter{bottomnumber}{5}
\renewcommand\bottomfraction{.90}
\setcounter{totalnumber}{10}
\renewcommand\textfraction{.10}
\renewcommand\floatpagefraction{.9}
\setcounter{dbltopnumber}{5}
\renewcommand\dbltopfraction{.99}
\renewcommand\dblfloatpagefraction{.8}

% PENALTIES
\@lowpenalty   51
\@medpenalty  151
\@highpenalty 301
\@beginparpenalty -\@lowpenalty
\@endparpenalty   -\@lowpenalty
\@itempenalty     -\@lowpenalty

% LISTS
\setlength\partopsep{0\p@}
\def\@listI{\leftmargin\leftmargini
  \topsep 6pt plus 3pt minus 3pt            % 9pt +3 -3
  \partopsep 3pt plus 1pt minus 2pt
  \itemsep 0pt plus 0.5pt minus 1pt
\parsep0pt}
\let\@listi\@listI
\@listi
\def\@listii {\leftmargin\leftmarginii
              \labelwidth\leftmarginii
              \advance\labelwidth-\labelsep
              \topsep    4\p@ \@plus2\p@ \@minus\p@
              \parsep    0\p@ \@plus1\p@  \@minus\p@
              \itemsep   \parsep}
\def\@listiii{\leftmargin\leftmarginiii
              \labelwidth\leftmarginiii
              \advance\labelwidth-\labelsep
              \topsep    2\p@ \@plus\p@\@minus\p@
              \parsep    \z@
              \partopsep \p@ \@plus\z@ \@minus\p@
              \itemsep   \topsep}
\def\@listiv {\leftmargin\leftmarginiv
              \labelwidth\leftmarginiv
              \advance\labelwidth-\labelsep}
\def\@listv  {\leftmargin\leftmarginv
              \labelwidth\leftmarginv
              \advance\labelwidth-\labelsep}
\def\@listvi {\leftmargin\leftmarginvi
              \labelwidth\leftmarginvi
              \advance\labelwidth-\labelsep}

\DeclareMathSizes{\@xivpt}{\@xivpt}{\@xpt}{\@viiipt}
\DeclareMathSizes{12}{12}{\@viiipt}{\@viipt}


% ******************** HEADINGS
% normal heading
\def\ps@headings{%
  \let\@oddfoot\@empty\let\@evenfoot\@empty
  \def\@evenhead{\footnotesize\rlap{\thepage}\hfill\textit{\leftmark}\hfill}
  \def\@oddhead{\footnotesize\hfill\textit{\leftmark}\hfill\llap{\thepage}}
}

% empty RH
\def\ps@empty{\let\@mkboth\@gobbletwo
     \def\@oddhead{\hfill}\def\@oddfoot{}
\let\@evenhead\@oddhead\let\@evenfoot\@oddfoot}

% RH  with pagenumber at bottom
\def\ps@plain{\let\@mkboth\@gobbletwo
     \def\@oddhead{\hfill}\def\@oddfoot{}
\let\@evenhead\@oddhead
  \def\@oddfoot{\hfill\footnotesize\thepage\hfill}
  \let\@evenfoot\@oddfoot
}

\def\ps@copyright{\let\@mkboth\@gobbletwo
  \def\@evenhead{\parbox[t]{\textwidth}{\ltitleheadline}}%
  \def\@oddhead{\parbox[t]{\textwidth}{\rtitleheadline}}%
  \def\@oddfoot{\parindent 0pt
    \parbox{\textwidth}{\vskip10pt\footnotesize\@copyright}}%
  \let\@evenfoot\@oddfoot
}


\def\rtitleheadline{\scriptsize\@journal\ \@volume\ (\the\@pubyear)\ \@pagerange
\hfill{\footnotesize\thepage}\break
\@ifundefined{@doi}{}{DOI \@doi\\}%
\@publisher}
\let\ltitleheadline\rtitleheadline

\def\@copyright{\footnotesize\@issn/\the@copyear/\$\@price\ \copyright@sign\
\the\@pubyear\@copyrightowner}%

% ************************ FOOTNOTE
% footnoterule
\def\footnoterule{\kern-3\p@
  \hrule width\columnwidth
  \kern 2.6\p@}

\newcommand\@makefntext[1]{%
    \parindent1em\@makefnmark #1}
\def\@makefnmark{\@textsuperscript{\normalfont\@thefnmark}}%

% ************************ Counters
\setcounter{secnumdepth}{3}
\newcounter {section}
\newcounter {subsection}[section]
\newcounter {subsubsection}[subsection]
\newcounter {paragraph}[subsubsection]
\newcounter {subparagraph}[paragraph]
\renewcommand \thesection {\@arabic\c@section}
\renewcommand\thesubsection   {\thesection.\@arabic\c@subsection}
\renewcommand\thesubsubsection{\thesubsection .\@arabic\c@subsubsection}
\renewcommand\theparagraph    {\thesubsubsection.\@arabic\c@paragraph}
\renewcommand\thesubparagraph {\theparagraph.\@arabic\c@subparagraph}

% ******************** Sectioning commands
\def\no@harm{\let\thanks=\@gobble \let\\=\@empty}

\def\nohyphen{\pretolerance=10000 \tolerance=10000
\hyphenpenalty=10000 \exhyphenpenalty=10000}
\newcommand\section{\@startsection {section}{1}{\z@}%
                                   {\bigskipamount}%
                                   {\medskipamount}%
                                   {\normalsize\bfseries\nohyphen\raggedright}}
\newcommand\subsection{\@startsection {subsection}{2}{\z@}%
                                   {\medskipamount}%
                                   {\medskipamount}%
                                   {\normalsize\itshape\nohyphen\raggedright}}
\newcommand\subsubsection{\@startsection{subsubsection}{3}{\z@}%
                                     {\smallskipamount}%
                                     {0.1\p@}%
                                     {\normalsize\itshape\nohyphen\raggedright}}
\newcommand\paragraph{\@startsection{paragraph}{4}{\z@}%
                                    {\smallskipamount}%
                                    {-1em}%
                                    {\normalsize\itshape}}
\newcommand\subparagraph{\@startsection{subparagraph}{5}{\z@}%
                                       {0.1pt}%
                                       {-1em}%
                                       {\normalsize\itshape}}
% Format for the counter:
\def\@seccntformat#1{\csname the#1\endcsname.\enspace}

\def\appendix{\par
   \section*{Appendix}
   \setcounter{section}{0}%
   \setcounter{subsection}{0}%
   \def\thesection{\Alph{section}}}

\def\acknowledgements{\section*{\acknowledgementsname}%
  \typeout{\acknowledgementsname}}

\def\notes{\section*{Notes}\footnotesize}
\def\endnotes{\par \vskip 6pt plus12pt minus2pt\relax}

%****************** LISTS
\if@twocolumn
  \setlength\leftmargini  {2em}
\else
  \setlength\leftmargini  {2.5em}
\fi
\leftmargin  \leftmargini
\setlength\leftmarginii  {2.2em}
\setlength\leftmarginiii {1.87em}
\setlength\leftmarginiv  {1.7em}
\if@twocolumn
  \setlength\leftmarginv  {.5em}
  \setlength\leftmarginvi {.5em}
\else
  \setlength\leftmarginv  {1em}
  \setlength\leftmarginvi {1em}
\fi
\setlength  \labelsep  {.4em}
\setlength  \labelwidth{\leftmargini}
\addtolength\labelwidth{-\labelsep}

\renewcommand\theenumi{\@arabic\c@enumi}
\renewcommand\theenumii{\@alph\c@enumii}
\renewcommand\theenumiii{\@roman\c@enumiii}
\renewcommand\theenumiv{\@Alph\c@enumiv}
\newcommand\labelenumi{\theenumi.}
\newcommand\labelenumii{(\theenumii)}
\newcommand\labelenumiii{\theenumiii.}
\newcommand\labelenumiv{\theenumiv.}
\renewcommand\p@enumii{\theenumi}
\renewcommand\p@enumiii{\theenumi(\theenumii)}
\renewcommand\p@enumiv{\p@enumiii\theenumiii}

\def\setenumlabel#1{\gdef\max@enumlabel{#1}}
\setenumlabel{1.}

\def\enumerate{\@ifnextchar[{\enumerate@}{\enumerate@[\max@enumlabel]}}

\def\enumerate@[#1]{\ifnum \@enumdepth >4 \@toodeep\else
 \advance\@enumdepth \@ne
 \edef\@enumctr{enum\romannumeral\the\@enumdepth}%
 \list {\csname label\@enumctr\endcsname}%
 {\usecounter{\@enumctr}\def\makelabel##1{{\hfill\rm ##1}}
\settowidth{\labelwidth}{#1}
\advance\labelwidth by\parindent \labelsep=0.5em
 \leftmargin\z@ \rightmargin\z@ \itemindent=\labelwidth
        \advance\itemindent\labelsep
        \leftmargin=\the\itemindent\itemindent=\z@
        \partopsep\z@ \topsep\smallskipamount \parsep\z@ \itemsep\z@ %\@rightskip\z@ plus 1fil
 \listparindent\z@}\fi\setenumlabel{1.}}

%%%%%%%%%%%%%%%%%%%%%% ITEMIZE
\newcommand\labelitemi{\normalfont\bfseries \textendash}
\newcommand\labelitemii{\textasteriskcentered}
\newcommand\labelitemiii{\textasteriskcentered}
\newcommand\labelitemiv{\textperiodcentered}

\let\@itemize@indent\parindent

\def\itemize{\@ifnextchar[{\itemize@}{\itemize@[]}}
\def\itemize@[#1]{\ifnum \@itemdepth >4 \@toodeep\else
  \advance\@itemdepth \@ne
  \edef\@itemitem{labelitem\romannumeral\the\@itemdepth}%
  \if.#1. \else\def\@@tempa{#1}\edef\@itemitem{@@tempa}\fi\list
{\csname\@itemitem\endcsname}{\settowidth{\labelwidth}
                {\csname\@itemitem\endcsname}
                \def\makelabel##1{##1}\labelsep=0.5em%ST
        \itemindent=\labelwidth \advance\itemindent\labelsep
                \advance\itemindent\@itemize@indent
        \leftmargin\the\itemindent \itemindent=\z@
                \partopsep\z@ \topsep\smallskipamount \parsep\z@ %\@rightskip\z@ plus 1fil
        \itemsep\z@ \listparindent\z@} \fi}

\newenvironment{description}
               {\list{}{\labelwidth\z@ \itemindent-\leftmargin
                        \let\makelabel\descriptionlabel}}
               {\endlist}
\newcommand*\descriptionlabel[1]{\hspace\labelsep
                                \normalfont\bfseries #1}
\newenvironment{verse}
               {\let\\\@centercr
                \list{}{\itemsep      \z@
                        \itemindent   -1.5em%
                        \listparindent\itemindent
                        \rightmargin  \leftmargin
                        \advance\leftmargin 1.5em}%
                \item\relax}
               {\endlist}

\newenvironment{quotation}
               {\list{}{\small\listparindent2mm%
                        \itemindent\z@   %
                        \rightmargin\z@   \leftmargin\parindent%
                        \partopsep\z@ \topsep\smallskipamount \parsep\z@%
                        }%
                \item[\Q@strut]\relax}
               {\endlist}
\def\Q@strut{\leavevmode\hbox{\vrule height9pt depth1pt width0pt}}

\newenvironment{quote}
               {\list{}{\listparindent\z@%
                        \itemindent    \listparindent%
                        \rightmargin\z@   \leftmargin 1.5em%
                        \partopsep\z@ \topsep6pt \parsep\z@%
                        }%
                \item[\Q@strut]\relax}
               {\endlist}

%************************** TABULAR
\let\savehline\hline
   \def\thline{\noalign{\vskip3pt}\savehline\noalign{\vskip3pt}}%
   \def\fhline{\noalign{\vskip1pt}\savehline\noalign{\vskip7pt}}%
   \def\bhline{\noalign{\vskip3pt}\noalign{\global\arrayrulewidth=1\p@}\savehline\noalign{\global\arrayrulewidth=.5\p@}\noalign{\vskip3pt}}%
   \def\lhline{\noalign{\vskip3pt}\noalign{\global\arrayrulewidth=.3\p@}\savehline\noalign{\global\arrayrulewidth=.5\p@}\noalign{\vskip3pt}}

%************************** MATH SETTINGS
\setlength\mathindent{20\p@}
\setlength\arraycolsep{1.2\p@}
\setlength\tabcolsep{6\p@}
\setlength\arrayrulewidth{.4\p@}
\setlength\doublerulesep{2\p@}
\setlength\tabbingsep{\labelsep}
\setlength\jot{6\p@}
\skip\@mpfootins = \skip\footins
\setlength\fboxsep{3\p@}
\setlength\fboxrule{.4\p@}

\if@seceqn
  \@addtoreset {equation}{section}
  \renewcommand\theequation{\thesection.\@arabic\c@equation}
\else
  \renewcommand\theequation{\@arabic\c@equation}
\fi

%******* TABLES, FIGURES, ALGORITHM
\newcounter{figure}
\if@secfloat
 \@addtoreset{figure}{section}
 \renewcommand \thefigure {\thesection.\@arabic\c@figure}
\else
 \renewcommand \thefigure {\@arabic\c@figure}
\fi

\def\fps@figure{tbp}
\def\ftype@figure{1}
\def\ext@figure{lof}
\def\fnum@figure{\figurename~\thefigure.}

\newenvironment{figure}
               {\let\@makecaption\@makefigurecaption\let\@floatboxreset\@figureboxreset\@float{figure}}
               {\end@float}
\newenvironment{figure*}
               {\let\@makecaption\@makefigurecaption\let\@floatboxreset\@figureboxreset\@dblfloat{figure}}
               {\end@dblfloat}

\def\@figureboxreset{%
        \reset@font%
        \centering%
        \@setnobreak%
        \@setminipage%
}


\long\def\@makefigurecaption#1#2{\footnotesize%
 \vskip\abovecaptionskip
\setbox\@tempboxa\hbox{#1 #2}%
  \ifdim \wd\@tempboxa >\hsize
    \unhbox\@tempboxa\par
  \else
    \hbox to\hsize{\hfil\box\@tempboxa\hfil}%
  \fi}

% TABLE
\newcounter{table}
\if@secfloat
  \@addtoreset{table}{section}
\renewcommand \thetable{\thesection.\@arabic\c@table}
\else
  \renewcommand \thetable{\@arabic\c@table}
\fi
\def\fps@table{tbp}
\def\ftype@table{2}
\def\ext@table{lot}
\def\fnum@table{\tablename~\thetable}

\newenvironment{table}
               {\let\@makecaption\@maketablecaption%
               \let\@floatboxreset\@tableboxreset\@float{table}}
               {\end@float}
\newenvironment{table*}
               {\let\@makecaption\@maketablecaption%
               \let\@floatboxreset\@tableboxreset\@dblfloat{table}}
               {\end@dblfloat}

\def\@tableboxreset{%
        \reset@font%
        \centering\footnotesize%
        \def\arraystretch{1.2}
        \@setnobreak%
        \@setminipage%
}

\newlength\abovecaptionskip
\newlength\belowcaptionskip
\setlength\abovecaptionskip{8\p@}
\setlength\belowcaptionskip{3\p@}

\newdimen\tablewidth \tablewidth\textwidth
\newdimen\saved@tablewidth \saved@tablewidth\textwidth

\long\def\@maketablecaption#1#2{\begingroup\footnotesize
 \global\setbox\@tempboxa\hbox{#2}\endgroup\centering
 \hbox to \hsize{\footnotesize\hss#1\hss}\par
 \ifdim \wd\@tempboxa>\tablewidth \parbox[t]{\tablewidth}{\footnotesize%
 #2\vphantom{Ay}\par} \else
 \hbox to\hsize{\hfill\box\@tempboxa\vphantom{Ay}\hfill}\fi%
 \global\saved@tablewidth\tablewidth%
 \global\tablewidth\hsize\vskip\belowcaptionskip}

%%****************** Algorithm
\newcounter{algorithm}
\if@secfloat
  \@addtoreset{algorithm}{section}
\renewcommand \thealgorithm{\thesection.\@arabic\c@algorithm}
\else
  \renewcommand \thealgorithm{\@arabic\c@algorithm}
\fi
\def\fps@algorithm{tbp}
\def\ftype@algorithm{4}
\def\ext@algorithm{loa}
\def\fnum@algorithm{\algorithmname~\thealgorithm.}

\newenvironment{algorithm}
               {\let\@makecaption\@makealgorithmcaption%
               \let\@floatboxreset\@algorithmboxreset\@float{algorithm}}
               {\end@float}
\newenvironment{algorithm*}
               {\let\@makecaption\@makealgorithmcaption%
               \let\@floatboxreset\@algorithmboxreset\@dblfloat{algorithm}}
               {\end@dblfloat}

\def\@algorithmboxreset{%
        \reset@font%
        \centering
        \@setnobreak%
        \@setminipage%
}

\long\def\@makealgorithmcaption#1#2{\vskip 2ex \small
  \hbox to \hsize{\parbox[t]{\hsize}{{\bf #1} #2}}}

%%%% Program Code:
\def\programcode{%
\let\@makealgorithmcaption\@makefigurecaption
\def\algorithmname{Program Code}}


%********************* COMPATIBILITY WITH OLD LATEX:
\DeclareOldFontCommand{\rm}{\normalfont\rmfamily}{\mathrm}
\DeclareOldFontCommand{\sf}{\normalfont\sffamily}{\mathsf}
\DeclareOldFontCommand{\tt}{\normalfont\ttfamily}{\mathtt}
\DeclareOldFontCommand{\bf}{\normalfont\bfseries}{\mathbf}
\DeclareOldFontCommand{\it}{\normalfont\itshape}{\mathit}
\let\sl\it
\DeclareOldFontCommand{\sc}{\normalfont\scshape}{\@nomath\sc}
\DeclareRobustCommand*\cal{\@fontswitch\relax\mathcal}
\DeclareRobustCommand*\mit{\@fontswitch\relax\mathnormal}

\if@secthm
 \@addtoreset{thm}{section}
 \def\thethm{\thesection.\arabic{thm}}
\else
 \def\thethm{\arabic{thm}}
\fi

%***************************** BIBLIOGRAPHY

\newenvironment{thebibliography}[1]
     {\section*{\refname}\footnotesize\rmfamily\upshape%
      \list{\@biblabel{\@arabic\c@enumiv}}%
           {\settowidth\labelwidth{\@biblabel{#1}}%
            \leftmargin\labelwidth
            \advance\leftmargin\labelsep
            \usecounter{enumiv}%
            \let\p@enumiv\@empty
            \renewcommand\theenumiv{\@arabic\c@enumiv}}%
      \sloppy
      \clubpenalty4000
      \@clubpenalty \clubpenalty
      \widowpenalty4000%
      \sfcode`\.\@m}
     {\def\@noitemerr
       {\@latex@warning{Empty `thebibliography' environment}}%
      \endlist}

\newcommand\newblock{\hskip .11em\@plus.33em\@minus.07em}

\def\@citex[#1]#2{%
  \let\@citea\@empty
  \@cite{\@for\@citeb:=#2\do
    {\@citea\def\@citea{,\penalty\@m\hskip.1pt}%
     \edef\@citeb{\expandafter\@firstofone\@citeb}%
     \if@filesw\immediate\write\@auxout{\string\citation{\@citeb}}\fi
     \@ifundefined{b@\@citeb}{\mbox{\reset@font\bfseries ?}%
       \G@refundefinedtrue
       \@latex@warning
         {Citation `\@citeb' on page \thepage \space undefined}}%
       {\hbox{\csname b@\@citeb\endcsname}}}}{#1}}


% If natbib loaded:

\AtBeginDocument{\@ifpackageloaded{natbib}{\set@natbib}{}}

\def\set@natbib{%
  \def\set@NAT@space{\def\NAT@space{\hskip.1\p@}}
  \g@addto@macro\NAT@set@cites\set@NAT@space
  \let\bibfont\footnotesize
  \bibpunct{[}{]}{,}{n}{}{,}
  \setlength\bibsep{0pt}}


%****************************** FRONTMATTER *

\newtoks\t@glob@notes
\newtoks\t@loc@notes
\newcount\note@cnt
\newcounter{author}
\newcount\n@author
\def\n@author@{}
\newcounter{address}

\newdimen\sv@mathsurround
\newcount\sv@hyphenpenalty

\newcount\prev@elem \prev@elem=0
\newcount\cur@elem  \cur@elem=0
\chardef\e@pretitle=1
\chardef\e@title=1
\chardef\e@subtitle=1
\chardef\e@author=2
\chardef\e@address=3

\newif\if@newelem
\newif\if@firstauthor
\newif\if@preface
\newif\if@hasabstract
\newif\if@haskeywords

\newbox\fm@box
\newdimen\fm@size
\newbox\t@abstract
\newbox\t@keywords

\def\add@tok#1#2{\global#1\expandafter{\the#1#2}}
\def\add@xtok#1#2{\begingroup
  \no@harm
  \xdef\@act{\global\noexpand#1{\the#1#2}}\@act
\endgroup}

\def\tailthanksref[#1]#2{\noexpand\pthanksref{#1}}
\def\pthanksref#1{\global\advance\note@cnt\@ne\ifnum\note@cnt>\@ne
\global\t@loc@notes\expandafter{\the\t@loc@notes\note@sep}\fi
\global\t@loc@notes\expandafter{\the\t@loc@notes#1}}

\def\beg@elem{\global\t@loc@notes={}\global\note@cnt\z@}
\def\@xnamedef#1{\expandafter\xdef\csname #1\endcsname}
\def\no@harm{%
  \let\\=\relax  \let\rm\relax
  \let\ss=\relax \let\ae=\relax \let\oe=\relax
  \let\AE=\relax \let\OE=\relax
  \let\o=\relax  \let\O=\relax
  \let\i=\relax  \let\j=\relax
  \let\aa=\relax \let\AA=\relax
  \let\l=\relax  \let\L=\relax
  \let\d=\relax  \let\b=\relax \let\c=\relax
  \let\bar=\relax
  \def\protect{\noexpand\protect\noexpand}}

\def\proc@elem#1#2{\begingroup
    \no@harm
    \def\thanks##1##{\@gobble}%
    \def\thanksref##1##{\@gobble}%
    \@xnamedef{@#1}{#2}%
  \endgroup
  \prev@elem=\cur@elem
  \cur@elem=\csname e@#1\endcsname
  \expandafter\elem@nothanksref#2\thanksref\relax%
  \expandafter\elem@nothanks#2\thanks\relax}

\def\elem@nothanksref#1\thanksref{\futurelet\@peektok\elem@thanksref}
\def\elem@thanksref{\ifx\@peektok\relax
  \else \expandafter\elem@morethanksref \fi}
\def\elem@morethanksref[#1]#2{\add@thanks{#1}\elem@nothanksref}

\def\elem@nothanks#1\thanks{\futurelet\@peektok\elem@thanks}
\def\elem@thanks{\ifx\@peektok\relax
  \else \ifx\@peektok[ \expandafter\expandafter\expandafter\elem@morethankse
  \else \expandafter\expandafter\expandafter\elem@morethanks \fi\fi}

\def\elem@morethankse[#1]#2{\thanks@optarg[#1]{#2}\add@thanks{#1}\elem@nothanks}
\def\elem@morethanks#1{\thanks@optarg[]{#1}\add@thanks{}\elem@nothanks}

\def\add@thanks#1{%
  \global\advance\note@cnt\@ne
    \ifnum\note@cnt>\@ne \add@xtok\t@loc@notes{\note@sep}\fi
    \ifx.#1.\add@xtok\t@loc@notes{\thefootnote}\else
        \add@xtok\t@loc@notes{#1}\fi%
}
\def\add@addressref#1{%
  \global\advance\note@cnt\@ne
    \ifnum\note@cnt>\@ne \add@xtok\t@loc@notes{\note@sep}\fi
    \add@tok\t@loc@notes{\ref{#1}}%
}
\def\note@sep{,}

\def\thanks@optarg[#1]#2{%
    \ifx.#1.\add@tok\t@glob@notes{\footnotetext}%
    \else\add@tok\t@glob@notes{\freefootnotetext}\fi%
    \refstepcounter{footnote}%
    \ifx.#1.\add@xtok\t@glob@notes{[\the\c@footnote]}%
    \else\add@xtok\t@glob@notes{[#1]}\fi%
    \add@tok\t@glob@notes{{#2}}%
        \ignorespaces}%

% FRONTMATTER

\def\artty#1{\gdef\@artty{#1}}
\gdef\@artty{RA} % Default Research Article
\gdef\@RA{RA}%     Research Article
\gdef\@RV{RV}%     Review Article
\gdef\@XX{XX}%     Miscelaneous Article
\gdef\@AI{AI}%     Author Index

\newdimen\a@title@skip \a@title@skip=12\p@
\newskip\b@section@skip \b@section@skip=36\p@ plus6\p@ minus6\p@%
\newskip\b@pretitle@skip \b@pretitle@skip\z@

\def\make@AI{%
\global\a@title@skip=16\p@
}

\def\frontmatter{%
  \ifx\@artty\@AI \make@AI\fi
  \let\review\ios@review
  \let\@corresp@note\relax
  \global\t@glob@notes={}\global\c@author\z@
  \global\c@address\z@
  \sv@mathsurround\mathsurround \m@th
  \global\n@author=0\n@author@\relax
  \global\advance\n@author\m@ne
  \global\@firstauthortrue
  \global\@hasabstractfalse
  \global\@prefacefalse
  \newcount\c@sv@footnote
  \global\c@sv@footnote=\c@footnote
  \parindent\z@
  \open@fm \ignorespaces}

\def\preface{\@prefacetrue}


% ENDFRONTMATTER

\def\endfrontmatter{%
  \global\c@footnote=\c@sv@footnote
  \global\n@author=\c@author \@writecount
  \global\@topnum\z@
  \ifx\@firstpage\@lastpage
    \gdef\@pagerange{\@firstpage}
  \else
    \gdef\@pagerange{\@firstpage--\@lastpage}
  \fi
  \thispagestyle{copyright}%
  \newcount\c@sv@footnote
  \global\c@sv@footnote=\c@footnote
  \if@twocolumn\else\output@glob@notes\fi
  \output@glob@notes
  \if@preface
    \@hasabstractfalse
  \fi
  \if@hasabstract
    \normal@text
    \vskip 48\p@
    \unvbox\t@abstract
    \vskip 12\p@
    \unvbox\t@keywords
  \fi
  \close@fm
  \if@twocolumn\output@glob@notes\fi
  \markboth{\@runauthor\@runtitle}{\@runauthor\@runtitle}%
  \global\@prefacefalse
  \global\leftskip\z@
  \global\@rightskip\z@
  \global\rightskip\@rightskip
  \global\c@footnote=\c@sv@footnote
  \global\mathsurround\sv@mathsurround
  \let\title\relax       \let\author\relax
  \let\address\relax
  \let\frontmatter\relax \let\endfrontmatter\relax
  \let\@maketitle\relax  \let\@@maketitle\relax
  \normal@text}
%
% Dvieju koloneliu zurnale per visa lapo ploti eina
% tik pretitle, title ir subtitle. Tam ivedame komanda
% \maketitle, kuri uzdaro box'a

  \def\two@c@maketitle{%
    \global\let\close@fm\relax%
    \vskip\b@section@skip%
    \par \egroup
    \emergencystretch=1pc \twocolumn[\unvbox\fm@box]}

\if@restonecol
  \let\maketitle\relax
\else
  \let\maketitle\two@c@maketitle
\fi


\newdimen\t@xtheight
\def\init@settings{
\splittopskip=\topskip \splitmaxdepth=\maxdepth
\t@xtheight\textheight \advance\t@xtheight-\splittopskip}

\def\open@fm{
  \global\setbox\fm@box=\vbox\bgroup
  \hsize=\textwidth
  \raggedright
  \sv@hyphenpenalty\hyphenpenalty
  \hyphenpenalty\@M}

\def\close@fm{%
  \vskip\b@section@skip%
  \par \egroup
  \fm@size=\dp\fm@box \advance\fm@size by \ht\fm@box
  \@whiledim\fm@size>\t@xtheight \do{%
    \global\setbox\@tempboxa=\vsplit\fm@box to \t@xtheight
    \unvbox\@tempboxa \newpage
    \fm@size=\dp\fm@box \advance\fm@size by \ht\fm@box}
  \if@twocolumn
    \emergencystretch=1pc \twocolumn[\unvbox\fm@box]
  \else
    \unvbox\fm@box
  \fi}

\def\output@glob@notes{\bgroup
  \the\t@glob@notes
  \egroup}

\def\justify@off{\let\\=\@normalcr
  \leftskip\z@ \@rightskip\@flushglue \rightskip\@rightskip}
\def\justify@on{\let\\=\@normalcr
  \leftskip\z@ \@rightskip\z@ \rightskip\@rightskip}

\def\normal@text{\global\let\\=\@normalcr
  \global\leftskip\z@ \global\@rightskip\z@ \global\rightskip\@rightskip
  \global\parfillskip\@flushglue}

\def\@writecount{\write\@mainaux{\string\global
  \string\@namedef{n@author@}{\the\n@author}}%
}

% TITLE
\def\pretitle#1{%
\vspace*{\b@pretitle@skip}\pretitle@size#1\par\vskip6\p@\hrule
\vskip12\p@}

\newcounter{titlefootnote}
\def\title#1{%
  \beg@elem
  \title@note@fmt
  \add@tok\t@glob@notes
    {\title@note@fmt}%
  \proc@elem{title}{#1}%
  \def\title@notes{\the\t@loc@notes}%
  \title@fmt{\@title}{\title@notes}%
  \setcounter{footnote}{0}%
  \ignorespaces}

\newdimen\@@topskip \@@topskip=24\p@


\def\title@fmt#1#2{%
  \vspace*{\@@topskip}
  {\title@size #1\,\hbox{$^{#2}$}\par}%
  \vskip\a@title@skip%
  }


\def\subtitle#1{%
  \beg@elem
  \proc@elem{subtitle}{#1}%
  \def\title@notes{\the\t@loc@notes}%
  \subtitle@fmt{\@subtitle}{\title@notes}%
  \ignorespaces}

\def\subtitle@fmt#1#2{%
  {\subtitle@size #1\,\hbox{$^{\mathrm{#2}}$}\par}%
  \vskip\a@title@skip%
  }


\def\title@note@fmt{\def\thefootnote{\arabic{footnote}}}

% \ios@review#1#2#3

\def\ios@review#1#2#3{{%
  \footnotesize\textnormal
  \noindent
  \ifx.#1.\else\textbf{Editor(s):} #1\\\fi
  \ifx.#2.\else\textbf{Solicited review(s):} #2\\\fi
  \ifx.#3.\else\textbf{Open review(s):} #3\fi
  \par
  \vskip\a@title@skip%
}}

% AUTHOR

\newdimen\b@author@skip
\b@author@skip 8\p@

\def\author{\@ifnextchar[{\author@optarg}{\author@optarg[]}}

\def\author@optarg[#1]#2{\stepcounter{author}%
  \beg@elem\def\degs##1{##1}\def\fnms##1{##1}\def\inits##1{##1}%
        \def\snm##1{##1}\def\roles##1{##1}%
  \if@firstauthor%
  \first@author \global\@firstauthorfalse \fi%
  \@for\@tempa:=#1\do{\expandafter\add@addressref\expandafter{\@tempa}}%
  \proc@elem{author}{#2}%
  \author@fmt{\the\c@author}{\the\t@loc@notes}{\@author}}%

\def\author@fmt#1#2#3{\@newelemtrue
  \ifnum\prev@elem=\e@author \global\@newelemfalse \fi
  \if@newelem \author@fmt@init \fi
  \edef\@tempb{#2}\ifx\@tempb\@empty
    \hbox{#3}\else
    \hbox{#3\,$^{\mathrm{#2}}$}%
  \fi}

\def\first@author{\author@note@fmt%
  \add@tok\t@glob@notes%
    {\author@note@fmt}}%

\def\author@fmt@init{%
  \par
  \vskip \b@author@skip
  \authors@size\raggedright
  \leavevmode}

\def\and{\unskip~and~}

\def\author@note@fmt{%
  \def\thefootnote{\sxarabic{footnote}}}

\def\sxarabic#1{%
        \expandafter\ifcase\value{#1} \or *\or **\or *** \or **** \or *****\fi
}


\def\syarabic#1{%
        \expandafter\ifcase\value{#1} \or \zstar\or \zstar\zstar\or \zstar\zstar\zstar \or \zstar\zstar\zstar\zstar \or \zstar\zstar\zstar\zstar\zstar\fi
}
\def\zstarshape{\fontencoding{U}\fontfamily{pzd}\fontseries{m}\fontshape{n}\selectfont}
\DeclareRobustCommand{\zstar}{{\fontencoding{U}\fontfamily{pzd}\fontseries{m}\fontshape{n}\selectfont I}}


% ADDRESS

\def\email#1{{e-mail:\ #1}}

\def\address{\@ifstar{\address@star}%
  {\@ifnextchar[{\address@optarg}{\address@noptarg}}}

\def\address@optarg[#1]#2{\refstepcounter{address}%
  \beg@elem
  \proc@elem{address}{#2}%
  \address@fmt{\the\c@address}{\the\t@loc@notes}{\@address}\label{#1}%
  \ignorespaces}

\def\address@noptarg#1{\refstepcounter{address}%
  \beg@elem
  \proc@elem{address}{#1}%
  \address@fmt{\z@}{\the\t@loc@notes}{\@address}%
  \ignorespaces}

\def\address@star#1{%
  \beg@elem
  \proc@elem{address}{#1}%
  \address@fmt{\m@ne}{\the\t@loc@notes}{\@address}%
  \ignorespaces}

\def\theaddress{\alph{address}}

\def\address@fmt#1#2#3{\@newelemtrue
  \ifnum\prev@elem=\e@address \@newelemfalse \fi
  \if@newelem \address@fmt@init \fi
  \bgroup\parskip\z@\noindent\raggedright \address@size
  \ifnum#1=\z@
    #3\,$^{\mathrm{#2}}$\space%
  \else
    \ifnum#1=\m@ne
      $^{\phantom{\mathrm{\theaddress}}\,}$#3\,$^{\mathrm{#2}}$%
    \else
      $^{\mathrm{\theaddress}\,}$#3\,$^{\mathrm{#2}}$%
    \fi
  \fi
  \par\egroup}

\def\address@fmt@init{%
        \def\@currentlabel{\theaddress}
  \par
  \vskip 2\p@ plus 1\p@ minus 1\p@}

% ABSTRACT

\def\abstract{\@ifnextchar[{\@abstract}{\@abstract[]}}
\def\@abstract[#1]{%
  \global\@hasabstracttrue
  \hyphenpenalty\sv@hyphenpenalty
  \global\setbox\t@abstract=\vbox\bgroup\linewidth\textwidth
  \justify@on\abstract@size\parindent 1em
  \abstract@indent\abstractname\ignorespaces}
\def\endabstract{\par\egroup}

% KEYWORDS
\def\sep{\unskip, }
\global\@haskeywordsfalse
\newdimen\dp@t@keywords
\def\keyword{\global\@haskeywordstrue%
  \global\setbox\t@keywords=\vbox\bgroup%
  \justify@on\abstract@size\parindent 0\p@
  \keywordsname:\ \ignorespaces
  }
\def\endkeyword{\par\egroup\global\dp@t@keywords=\dp\t@keywords}
\def\keywords#1{\begin{keyword}#1\end{keyword}}

% JORNAL, VOLUME, ISSUE

% Running title
\def\runningtitle#1{\gdef\@runtitle{#1}}   \def\@runtitle{}
\def\runningauthor#1{{\def\etal{et al.}\gdef\@runauthor{#1\@runsep}}} \def\@runauthor{}
\def\runningsep#1{\gdef\@runsep{#1}}
\def\@runsep{\ /\ }

\def\journal#1{\gdef\@journal{#1}}     \@ifundefined{@journal}{\gdef\@journal{Journal not defined}}{}
\def\volume#1{\gdef\@volume{#1}}       \def\@volume{0}
\def\issue#1{\gdef\@issue{#1}}         \def\@issue{0}

\newcount\@pubyear
\newcount\@copyear

\@copyear\@pubyear \advance\@copyear-1900
\def\pubyear#1{\global\@pubyear#1
  \global\@copyear\@pubyear \global\advance\@copyear-1900
  \ignorespaces}

\def\pubyear#1{\global\@pubyear#1
  \global\@copyear\@pubyear 
\ifnum\@copyear<2000 \global\advance\@copyear-1900 \else% 
  \global\advance\@copyear-2000\fi%
  \ignorespaces}

\pubyear{\number\year}

\def\firstpage#1{\def\@tempa{#1}\ifx\@tempa\@empty\else
  \gdef\@firstpage{#1}\gdef\@lastpage{#1}%
  \global\c@page=#1 \ignorespaces\fi
  }

\def\the@copyear{\ifnum\@copyear<10 0\fi\the\@copyear}

\def\@firstpage{1}
\def\lastpage#1{\def\@tempa{#1}\ifx\@tempa\@empty\else
  \gdef\@lastpage{#1}\ignorespaces\fi}
\def\@lastpage{0}
\def\@pagerange{1--0}

% SGML
\long\def\convertas#1#2{#2}
\def\sday#1{#1}\def\smonth#1{#1}\def\syear#1{#1}
\def\aid#1{\gdef\@aid{#1}}

\def\SSDI#1{\gdef\@ssdi{#1}} \def\@ssdi{000000-00}
\def\issn#1{\gdef\@issn{#1}}
\def\price#1{\gdef\@price{#1}}

\def\date#1{\gdef\@date{#1}}    \def\@date{\today}


\def\empty@data{\@nil}

% \doi:
\def\doi#1{\gdef\@doi{#1}}
\def\doi@base{http://dx.doi.org/}
\let\@doi\relax


%***************** BACKMATTER
\newcommand\backmatter{\goodbreak}

%**************** INICIALIZATION
\newcommand\refname{References}
\newcommand\figurename{Fig.}
\newcommand\tablename{Table}
\newcommand\algorithmname{Algorithm}
\newcommand\appendixname{Appendix}
\newcommand\abstractname{{\bfseries Abstract.\ }}
\newcommand\keywordsname{Keywords}
\def\acknowledgementsname{Acknowledgements}

\def\copyright@sign{\copyright}

% DIMENSIONS
\def\@articletypesize{\large}
\def\pretitle@size{\LARGE}
\def\title@size{\Huge}
\def\subtitle@size{\large\itshape}
\def\authors@size{\large}
\def\abstract@size{\small}
\def\abstract@indent{\noindent}
\def\address@size{\normalsize\itshape}

% Block preparation of contents:
\def\addcontentsline#1#2#3{}
\long\def\addtocontents#1#2{}

\newcommand\today{}
\edef\today{\ifcase\month\or
  January\or February\or March\or April\or May\or June\or
  July\or August\or September\or October\or November\or December\fi
  \space\number\day, \number\year}

\@twosidetrue
\pagenumbering{arabic}
\frenchspacing
\init@settings

\if@twocolumn
 \RequirePackage{flushend}%
\fi

\if@twocolumn\setlength\tablewidth{\columnwidth}
\else\setlength\tablewidth{\textwidth}\fi
\pagestyle{headings}

\endinput
%%
%% End of file `iosart2c.cls'.
