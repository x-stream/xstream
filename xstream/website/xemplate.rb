# XEMPLATE: a simple XML templating engine
# 
# Copyright (c) 2003 Nat Pryce, all rights reserved
# Licensed under the GNU GPL v2 or above (see the file COPYING).

require 'rexml/document'
require 'rexml/xpath'

module XEMPLATE
    NAMESPACE = "http://www.b13media.com/xemplate/1.0"
    
    WHEN = "when"
    INCLUDE = "include"
    TEXT = "text"
    ATTR = "attr"
    DYNAMIC = "dynamic"
    
    SRC = "src"
    ELEMENTS = "elements"
    VAR = "var"
    NAME = "name"
    VALUE = "value"
    
    class TemplateException < Exception
    end
    
    def XEMPLATE.load_template( filename )
        Template.new( load_xml(filename), filename )
    end
    
    def XEMPLATE.load_xml( file )
        File.open(file) do |input|
            begin
                Document.new(input)
            rescue ParseException => ex
                raise ParseException,
                      file + ", line " + ex.line.to_s + ": " + ex.message, 
                      ex.backtrace
            end
        end
    end
    
    
    class Template
        def initialize( template_document, filename )
            @template = template_document
            @filename = filename
            @include_cache = {}
        end
        
        def Template.load( filename )
            new( XEMPLATE::load_xml(filename), filename )
        end
        
        def expand( bindings )
            expand_document( @template, bindings )
        end
        
        private
        
        def expand_document( template_doc, bindings )
            expanded_doc = Document.new
            expand_children( expanded_doc, template_doc, bindings )
            return expanded_doc
        end
        
        def expand_children( expanded_parent, template_element, bindings )
            template_element.each do |child|
                if child.kind_of? REXML::Element
                    expand_element( expanded_parent, child, bindings )
                else
                    expanded_parent.add( deep_clone_node(child) )
                end
            end
        end
        
        def expand_element( expanded_parent, template_element, bindings )
            if is_template_directive?( template_element )
                expand_directive( expanded_parent, template_element, bindings )
            else
                expanded_element = clone_element(template_element)
                expanded_parent.add( expanded_element )
                expand_children( expanded_element, template_element, bindings )
            end
        end
        
        def is_template_directive?( template_element )
            template_element.namespace == NAMESPACE
        end
        
        def expand_directive( expanded_parent, template_element, bindings )
            begin
                action = method("expand_#{template_element.name}_element")
                action.call( expanded_parent, template_element, bindings )
            rescue NameError
                raise TemplateException,
                      "unknown template directive '#{template_element.name}'",
                      caller
            end
        end
        
        def expand_when_element( expanded_parent, template_element, bindings )
            if is_when_directive_active?(template_element,bindings)
                expand_children( expanded_parent, template_element, bindings )
            end
        end
        
        def is_when_directive_active?( template_element, bindings )
            variable = direct_attribute_value( template_element, VAR )
            guard_value = template_attribute( template_element, VALUE, bindings )
            actual_value = bindings[variable]
            
            if actual_value.nil?
                return false
            elsif guard_value.nil?
                return actual_value
            else
                return actual_value.to_s == guard_value
            end
        end
        
        def expand_include_element( expanded_parent, template_element, bindings )
            filename = template_attribute(template_element,SRC,bindings) \
                do |value,is_indirect|
                    if is_indirect
                        value
                    else
                        expand_path_relative_to_template(value)
                    end
                end
            selector = template_attribute(template_element,ELEMENTS,bindings) \
                or "/*/*"
            
            include_doc = load_xml( filename, bindings )
            included_elements = REXML::XPath.match( include_doc, selector )
            
            add_children( expanded_parent, included_elements )
        end
        
        def expand_dynamic_element( expanded_parent, template_element, bindings )
            variable = direct_attribute_value( template_element, VAR )
            add_children( expanded_parent, [bindings[variable]] )
        end
        
        def add_children( parent, children )
            children.each do |child|
                parent.add( deep_clone_node(child) )
            end
        end
        
        def expand_text_element( expanded_parent, template_element, bindings )
            variable = direct_attribute_value( template_element, VAR )
            text = bindings[variable].to_s
            expanded_parent.add( Text.new(text) )
        end
        
        def expand_attr_element( expanded_parent, template_element, bindings )
        	attr_name = template_attribute( template_element, NAME, bindings )
        	attr_value = template_attribute( template_element, VALUE, bindings )
        	
        	expanded_parent.attributes[attr_name] = attr_value
        end
        
        def template_attribute( element, attr, bindings )
            value = indirect_attribute_value(element,attr,bindings)
            is_indirect = value != nil
            value = direct_attribute_value(element,attr) unless is_indirect
            
            if block_given?
                yield value, is_indirect
            else
                value
            end
        end
        
        def indirect_attribute_value( element, attr, bindings )
            varname = direct_attribute_value( element, "#{attr}#{VAR}" );
            if varname
                value = bindings[varname]
            else
                nil
            end
        end
        
        def direct_attribute_value( element, attr )
            element.attributes[attr]
        end
        
        def is_variable_reference?( value )
            return (value != nil) && value[0..0] == "$"
        end
        
        def value_to_variable( value )
            return value[1..-1]
        end
        
        def dereference_variable( attr_value, bindings )
            var_name = value_to_variable(attr_value)
            var_value = bindings[var_name]
            if var_value.nil?
                raise TemplateException,
                      "no value for variable #{var_name}",
                      caller
            end
            var_value
        end
        
        def clone_element( template_element )
            cloned_element = Element.new( template_element.expanded_name )
            copy_attributes( template_element, cloned_element )
            cloned_element
        end
        
        def copy_attributes( template_element, expanded_element )
          template_element.attributes.each_attribute do |attribute|
            if should_copy(attribute)
                expanded_element.attributes.add( attribute.clone )
            end
          end
        end
        
        def should_copy( attribute )
            attribute.namespace != NAMESPACE && \
                attribute.prefix != "xmlns" && \
                attribute.value != NAMESPACE
        end
        
        def expand_path_relative_to_template( filename )
            File.expand_path( filename, File.dirname(@filename) )
        end
        
        def load_xml( filename, bindings )
            doc = @include_cache[filename]
            if doc.nil?
                doc = XEMPLATE.load_template(filename).expand(bindings)
                @include_cache[filename] = doc
            end
            doc
        end
        
        # Yuck!  But REXML forces us to do this.
        def deep_clone_node( node )
            if node.kind_of? Parent
                node.deep_clone
            else
                node.clone
            end
        end
    end
end
