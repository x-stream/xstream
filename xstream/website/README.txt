(Courtesy of Nat Pryce)

STRUCTURE

The directories should be self explanatory:
  ./templates:   contains the skin XML/XHTML and style sheet. Other templates might go here.
  ./content:     contains the content in simple XHTML
  ./output:      is where the templater builds the skinned pages
  ./skinner.rb:  is the program that builds the site
  ./xemplate.rb: is the templating engine used by the skinner

To run the skinner you will need Ruby 1.8.  It might work with older versions of Ruby
if you have installed REXML.


HOW TO UPDATE THE SITE

1) Edit the content or templates.  New content files and subdirectories are picked up 
   automatically by the skinner.
   
2) Run the skinner from within the website/ directory, using the command:

   % ruby skinner.rb

3) To deploy the site, commit your changes to CVS.  The continuous integration system
   will build the site and deploy it onto xstream.codehaus.org



DESIGN PRINCIPLES

The ideas behind site design are as follows:

1) It implements the design that resulted from the analysis of stereotypical users

The menu is designed to put what our users most want right up front.  Other, less
important information can be linked from subpages.

2) It completely separates content, navigation and style

This makes it easy to change the "skin" of the site or the style of that skin.

Skinning is implemented by a simple XML templater that I knocked up in Ruby.  
I "spiked" it very quickly:  it works but there are no tests apart from the site 
looks ok.  Perhaps it should be reimplemented in Java with tests etc. and used
as a jMock example.

3) The navigation "skin" is designed to be convenient in browsers that do not 
   process CSS.

The banner appears first, then the main page content, followed by any side panels on 
the page.  Thus, users of less capable browsers get the most important information
first and less important information later.

4) All content is simple XHTML.

No styles required, although any styles and other media used by the content pages 
get merged into the final site by the templater.  This should make it easy to create
content in WYSIWYG editors.
