(The XStream website is modelled on the jMock.org website, created by Nat Pryce).

STRUCTURE

The directories should be self explanatory:
  ./src/templates:   contains the skin HTML and style sheet. Other templates might go here.
  ./src/content:     contains the content in simple HTML.
  ./src/java:        the code for actually generating the website.
  ./build/website:   is where the resulting website is created.
  ./lib:             the libraries supporting the code.
  ./build.xml:       the Ant build file, that has a 'website' target for kicking it off.


EDITING THE CONTENT OF THE SITE

1) Edit the files in ./src/content. These are plain HTML files and can be edited using any old HTML editor
   (including Mozilla Composer or Word).

   Supporting resources that are relevant to the content (such as images) can be placed in this directory.

2) If the structure of the navigation requires a change (necessary if adding a new page), update ./src/content/website.xml.

3) Run the site builder from the parent XStream directory using Ant:

   % ant website

   The new site will be deployed in ./build/website.

4) To deploy the site, commit your changes to CVS.  The continuous integration system
   will build the site and deploy it onto xstream.codehaus.org


EDITING THE LOOK AND FEEL OF THE SITE

1) Edit the file ./src/templates/skin.html. This is a FreeMarker template that is applied to each page using SiteMesh.

   Supporting resources that are relevant to the look and feel (such as images or CSS) can be placed in this directory.



DESIGN PRINCIPLES

The ideas behind site design are as follows:

1) It implements the design that resulted from the analysis of stereotypical users

The menu is designed to put what our users most want right up front.  Other, less
important information can be linked from subpages.

2) It completely separates content, navigation and style

This makes it easy to change the "skin" of the site or the style of that skin.

3) The navigation "skin" is designed to be convenient in browsers that do not
   process CSS.

The banner appears first, then the main page content, followed by any side panels on
the page.  Thus, users of less capable browsers get the most important information
first and less important information later.

4) All content is simple HTML.

No styles required, although any styles and other media used by the content pages
get merged into the final site by the templater.  This makes it easy to create
content in WYSIWYG editors.
