#!/usr/bin/env ruby

require 'ftools'
require 'rexml/document'
require 'xemplate'
include REXML

BASE_DIR = "."
CONTENT_DIR = File.join(BASE_DIR,"content")
SKIN_DIR = File.join(BASE_DIR,"templates")
OUTPUT_DIR = File.join(BASE_DIR,"output")

CVSWEB_ROOT = "http://cvs.xstream.codehaus.org/xstream/website/content/"

TEMPLATE = XEMPLATE::load_template( File.join(SKIN_DIR,"skin.html") )

def env( varname, default_value )
	ENV[varname] || default_value
end

$logger = $stdout

def log( message )
	$logger.puts( message )
	$logger.flush
end

def is_markup( filename )
	filename =~ /.(html|xml)$/
end

def is_cvs_data( filename )
	filename =~ /\/CVS(\/|$)/
end

def is_directory( filename )
	FileTest.directory? filename
end

def list_files dir
    Dir[File.join(dir,"*")].reject {|f| is_cvs_data(f)}
end

def skin_assets
    Dir[File.join(SKIN_DIR,"*")].reject {|f| is_cvs_data(f) or is_markup(f)}
end

def output_file( asset_file, root_asset_dir )
    File.join( OUTPUT_DIR, filename_relative_to(asset_file,root_asset_dir) )
end

def filename_relative_to( file, root_dir )
	if file[0,root_dir.length] != root_dir
		raise "#{file} is not within directory #{root_dir}"
	end
	
	root_dir_length = root_dir.length
	root_dir_length = root_dir_length + 1 if root_dir[-1] != '/'
	
	file[root_dir_length, file.length-root_dir_length]
end

def copy_to_output( asset_file, root_asset_dir )
	dest_file = output_file( asset_file, root_asset_dir )
	
  	log "#{asset_file} -> #{dest_file}"
	
    File.copy( asset_file, dest_file )
end

def make_output_directory( asset_dir, root_asset_dir )
	new_dir = output_file( asset_dir, root_asset_dir )
	
	if not File.exists? new_dir
		log "making directory #{new_dir}"
		
		Dir.mkdir( new_dir )
	end
end

def skin_content( content_dir, root_content_dir=content_dir )
    list_files(content_dir).each do |content_file|
    	if is_directory(content_file)
    		make_output_directory( content_file, root_content_dir )
    		skin_content( content_file, root_content_dir )
    	elsif is_markup(content_file)
	    	skin_content_file( content_file, root_content_dir )
    	else
			copy_to_output( content_file, root_content_dir )
		end
    end
end

def skin_content_file( content_file, root_content_dir )
    output_file = output_file( content_file, root_content_dir )
    
    config = {
        "content" => content_file,
        "isindex" => (content_file =~ /content\/index\.html$/) != nil,
        "prerelease" => "1.0-rc1",
        "release" => "1.0",
        "history" => CVSWEB_ROOT + content_file[(root_content_dir.size+1)..-1]
    }
    
    skinned_content = TEMPLATE.expand( config )
    
    # workaround for MSIE
    add_class( 
        skinned_content.elements["/html/body/div[@id='center']/div[@id='content']/*[1]"], 
        "FirstChild" )
    
    log "#{content_file} ~> #{output_file}"
    
    write_to_output( skinned_content, output_file )
end

def add_class( element, new_class )
    old_class = element.attributes["class"]
    if old_class != nil
        new_class = "#{old_class} #{new_class}"
    end
    element.add_attribute( "class", new_class )
end

def write_to_output( xhtml, output_file )
    File.open( output_file, "w" ) do |output|
        xhtml.write( output, 0, false, true )
    end
end

def build_site
    skin_content CONTENT_DIR
    skin_assets.each {|f| copy_to_output f, SKIN_DIR}
end

begin
    build_site
    $stdout.puts "done"
    $stdout.flush
rescue REXML::ParseException => ex
    $stderr.puts "parse error: " + ex.message
    $stderr.flush
    
    exit 1
end

