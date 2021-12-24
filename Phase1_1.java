/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication0;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Stack;
import javafx.scene.control.TextField;

/**
 *
 * @author yousef
 */
public class Phase1_1 {
    // Data members

    // the XML file itself as a BufferedReader
    private BufferedReader xml_file;

    // the XML file as a single string with no spaces
    public String xml_text = "";

    // xml Declaration (if any)
    public String xmlDeclaration = "";

    // ArrayList of the XML file line by line
    private ArrayList<String> lines = new ArrayList<String>();

    // Tree that contains the XML file, the tree is initialized with a trivial node
    // (to be escapped later)
    public Tree xmlTree = new Tree("HEAD");

    // formatted XML
    public String formatted = "";

    // Json text
    public String JSONified = "";

    // ArrayList of all errors
    private ArrayList<String> errors = new ArrayList<String>();

    // Stack for validation
    Stack<String> stack = new Stack<String>();

    String allerrors = "";

    // Constructor
    public Phase1_1(BufferedReader xml_file) {
        this.xml_file = xml_file;
        fileReader();
        xml_parser();

        // Part1
        xml_validator();
        // ....
        tree_creator();
        // Part2
        formatting_maker();
        // Part3
        JSONIfy();
    }

    // Reading the xml_file line by line into the ArrayList (lines)
    public void fileReader() {
        try {
            String line;
            while ((line = xml_file.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            System.out.println("Error in file reading");
            return;
        }
    }

    // Saving the XML file as a single string with no spaces
    public void xml_parser() {

        // checking if we're reading a value
        boolean value = false;
        // checking if we're reading an tag (used once for attributes)
        boolean tag = false;
        // iterating through the xml file line by line

        for (int lineindx = 0; lineindx < lines.size(); lineindx++) {
            // iterating through each line
            for (int i = 0; i < lines.get(lineindx).length(); i++) {
                // checking if we're in a tag (opening or closing)
                if (lines.get(lineindx).charAt(i) == '<') {
                    value = false;
                    tag = true;
                }

                // check if the tag is closed.
                else if (lines.get(lineindx).charAt(i) == '>') {
                    tag = false;
                    try {
                        // checking if there's a value
                        if (lines.get(lineindx).charAt(i + 1) != ' ') {
                            value = true;
                        }
                    } catch (Exception e) {
                        System.out.print("");
                    }
                }

                if (tag) {
                    if (lines.get(lineindx).charAt(i) == ' ') {
                        xml_text += ' ';
                        // setting the tag to false to neglect extra unnecessary spaces before the
                        // attribute
                        tag = false;
                    }
                }
                // remove the sapces if we're reading a tag
                if (!value && lines.get(lineindx).charAt(i) != ' ') {
                    xml_text += lines.get(lineindx).charAt(i);
                }

                // add the spaces to the tag
                else if (value) {
                    xml_text += lines.get(lineindx).charAt(i);
                }
            }
        }
    }

    // Validating XML, gettint the errors and filling xml_text
    public String xml_validator() {
        // Checking if there's (any) error
        boolean error = false;

        // iterating the ArrayList of the lines by index (by index to indicate the lines
        // with errors)
        for (int lineindx = 0; lineindx < lines.size(); lineindx++) {

            // ignoring spaces
            try {
                while (lines.get(lineindx).charAt(0) == ' ') {
                    lines.set(lineindx, lines.get(lineindx).substring(1));
                }
            } catch (Exception e) {
                System.out.print("");
            }

            // Neglect the declaration
            try {
                if (lines.get(lineindx).substring(0, 2).compareTo("<?") == 0) {
                    System.out.println("Version");
                    allerrors += "Version\n";
                }

                // Neglect the comments
                else if (lines.get(lineindx).substring(0, 2).compareTo("<!") == 0) {
                    System.out.println("Comment");
                    allerrors += "Comment\n";
                }

                else {
                    // By default: the text is not (opening nor closing) tag
                    boolean readingopen = false;
                    boolean readingclose = false;

                    // initializing the tag with empty value
                    String tag = "";

                    // iterating through the chars of a single line
                    for (int i = 0; i < lines.get(lineindx).length(); i++) {

                        // checking if it's a/n (oepning or closing) tag
                        if (lines.get(lineindx).charAt(i) == '<') {

                            // check if it's a closing tag
                            if (lines.get(lineindx).charAt(i + 1) == '/') {
                                i++;
                                readingclose = true;
                                readingopen = false;

                                // resetting the value to empty

                                tag = "";
                            }

                            // check if it's an opening tag
                            else {
                                readingopen = true;
                                readingclose = false;

                                tag = "";
                            }
                        }
                        // check the end of a tag (opening or closing)
                        else if (lines.get(lineindx).charAt(i) == '>') {

                            // if it's the end of an opening tag: push
                            if (readingopen) {
                                stack.push(tag);
                                // xml_text += ("<" + tag + ">");
                                System.out.println(tag + " is pushed!");
                                allerrors += tag + " is pushed! \n";
                            }

                            // if it's the end of a closing tag: pop
                            else if (readingclose) {

                                // Checking of the stack is empty: ERROR!
                                if (stack.empty()) {
                                    errors.add("line: " + lineindx + ": stack is empty! the tag " + tag
                                            + " has no opening tag!");
                                    error = true;

                                }
                                // Checking if the stack has the correspoding opening tag : no error
                                else if (stack.peek().compareTo(tag) == 0) {
                                    stack.pop();
                                    System.out.println(tag + " is popped!");
                                    allerrors += tag + " is popped! \n";
                                }
                                // Checking if the stack doesn't have to correspoding opening tag: ERROR!
                                else {
                                    errors.add("line " + lineindx + ": " + tag
                                            + " is here but stack.top == " + stack.peek());
                                    error = true;
                                }
                            }
                            readingopen = false;
                            readingclose = false;
                        }
                        // If it's a tag: write into tag
                        else if (readingopen || readingclose) {
                            tag += lines.get(lineindx).charAt(i);
                        }
                    }
                }
            }

            catch (Exception e) {
                System.out.print("");
            }
        }
        // If the stack isn't empty: print the tag that hasn't been closed
        if (!stack.empty()) {
            System.out.println("The tag " + stack.peek() + " hasn't been closed");
            allerrors += "The tag " + stack.peek() + " hasn't been closed\n";
        }
        // If there're any errors: print them
        else if (error) {

            for (String errorinerrors : errors) {
                System.out.println(errorinerrors);
                allerrors += errorinerrors;
            }

        }
        // If not
        else {
            System.out.println("No Errors 👍");
            allerrors += "No Errors 👍";
        }
        return allerrors;
    }

    public void tree_creator() {
        // initializing empty tag and value
        String tag_buffer = "";
        String value_buffer = "";

        // initalizing nodes
        Node lastnode = xmlTree.root;
        Node current_node = xmlTree.root;

        // Initilazing attribute data
        String attributeName_buffer = "";
        String attributeValue_buffer = "";
        boolean attribute = false;

        // iterating through the xml_text (no spaces)
        for (int i = 0; i < xml_text.length(); i++) {

            // Checking the XML declaration
            if (xml_text.charAt(i) == '<' && xml_text.charAt(i + 1) == '?') {
                while (xml_text.charAt(i) != '>') {
                    xmlDeclaration += xml_text.charAt(i);
                    i++;
                }
                xmlDeclaration += '>';
            }

            // checking comments
            else if (xml_text.charAt(i) == '<' && xml_text.charAt(i + 1) == '!') {
                String comment_buffer = "";
                while (xml_text.charAt(i) != '>') {
                    comment_buffer += xml_text.charAt(i);
                    i++;
                }
                comment_buffer += '>';
                // adding the comment as a tag
                xmlTree.add_node(current_node, new Node(comment_buffer));
            }

            // checking closing tags
            else if (xml_text.charAt(i) == '<' && xml_text.charAt(i + 1) == '/') {
                // saving the lastest value into the current node's value
                current_node.value = value_buffer;

                // resetting the tag and the value buffers
                value_buffer = "";
                tag_buffer = "";

                // incrementing with 2 to escape "</"
                i += 2;

                // saving the current tag's name into the tag buffer
                while (xml_text.charAt(i) != '>') {
                    tag_buffer += xml_text.charAt(i);
                    i++;
                }
                // if the tag is equal to the current node's tag, return to the parent
                if (tag_buffer.compareTo(current_node.tag) == 0) {
                    current_node = current_node.parent;
                }

            }
            // checking opening tags
            else if (xml_text.charAt(i) == '<') {

                // making a new child and setting it into the current node
                lastnode = current_node;
                current_node = new Node();
                xmlTree.add_node(lastnode, current_node);

                // restting the buffers
                tag_buffer = "";
                attributeName_buffer = "";
                attributeValue_buffer = "";
                attribute = false;

                // escaping the "<"
                i++;

                while (xml_text.charAt(i) != '>' && xml_text.charAt(i) != ' ') {
                    // saving the tag's name
                    tag_buffer += xml_text.charAt(i);
                    i++;
                }
                // Checking if there's an attribute, this occurs after saving the tag into tag
                // buffer
                if (xml_text.charAt(i) == ' ') {
                    attribute = true;

                    // saving the attribute name

                    // escaping the space
                    i++;
                    while (xml_text.charAt(i) != '=' && attribute) {
                        attributeName_buffer += xml_text.charAt(i);
                        i++;
                    }
                    // escaping the "=""
                    i += 2;
                    // saving the attribute value
                    while (xml_text.charAt(i) != '\"') {
                        attributeValue_buffer += xml_text.charAt(i);
                        i++;
                    }
                    attribute = false;
                    // escaping the ">"
                    i++;
                }
                // saving the values into the current node
                current_node.tag = tag_buffer;
                current_node.attributeName = attributeName_buffer;
                current_node.attributeValue = attributeValue_buffer;
            }
            // Checking value
            else {
                value_buffer += xml_text.charAt(i);
            }
        }
        // since we've started with a trivial root, escape it to the real first node
        xmlTree.root = xmlTree.root.children.get(0);
    }

    // calling the recursive function
    public void formatting_maker() {
        // checking for declaration, if there's add it first
        if (xmlDeclaration.compareTo("") != 0) {
            formatted += xmlDeclaration + "\n";
        }
        formatted_traversal(xmlTree.root);

    }

    // the formatting recursive function, uses preorder traversal
    public void formatted_traversal(Node node) {
        // indenting with tag's depth
        for (int i = 0; i < node.depth; i++) {
            formatted += "\t";
        }
        // if the node is comment, add it in a new line
        if (node.tag.charAt(0) == '<' && node.tag.charAt(1) == '!') {
            formatted += node.tag + "\n";
        }
        // adding the tag, new line of it's a parent
        else {
            formatted += "<" + node.tag;
            if (node.attributeName.compareTo("") != 0) {
                formatted += " " + node.attributeName + "=\"" + node.attributeValue + "\"";
            }
            if (node.children.size() != 0) {
                formatted += ">\n";
            }
            // adding the tag in a single line if there's a leaf
            else {
                formatted += ">";
            }
        }
        // adding the value, if there's
        if (node.value != "") {
            formatted += node.value;
        }
        for (int i = 0; i < node.children.size(); i++) {
            formatted_traversal(node.children.get(i));
        }
        if (node.children.size() != 0) {
            for (int i = 0; i < node.depth; i++) {
                formatted += "\t";
            }
        }
        // if the node is comment, don't add it's closing!
        if (node.tag.charAt(0) == '<' && node.tag.charAt(1) == '!') {
            formatted += "";
        } else {
            formatted += "</" + node.tag + ">\n";
        }

    }

    public void JSONIfy() {
        Node lastNode;
        boolean last = false;

        // first node (root)
        JSONified += "{\n";
        JSONified += "\t\"" + xmlTree.root.tag + "\": {\n";

        if (xmlTree.root.value.compareTo("") != 0) {
            JSONified += "\"" + xmlTree.root.value + "\"";
        }

        for (int i = 0; i < xmlTree.root.children.size(); i++) {
            // Making last node is the parent for the first node, else the lastest brother
            if (i == 0) {
                lastNode = xmlTree.root;
            } else {
                lastNode = xmlTree.root.children.get(i - 1);
            }
            // flag the last node for ","
            if (i == xmlTree.root.children.size() - 1) {
                last = true;
            }
            JSONify_traversal(xmlTree.root.children.get(i), lastNode, last, i);
        }
        JSONified += "\t}\n";
        JSONified += "\n}";
    }

    public void JSONify_traversal(Node node, Node lastNode, boolean last, int order) {
        boolean square = false;

        // If comment, neglect it
        if (node.tag.charAt(0) == '<' && node.tag.charAt(1) == '!') {
            // JSONified += "//" + node.tag.substring(2, node.tag.length() - 2) + "\n";
            return;
        }

        // if the node's tag equals lastest brother's tag (twin)
        if (node.tag.compareTo(lastNode.tag) == 0) {
            // if it has children
            if (node.children.size() > 0) {
                for (int i = 0; i < node.depth + 2; i++) {
                    JSONified += "\t";
                }
                JSONified += "{\n";
                for (int i = 0; i < node.children.size(); i++) {
                    if (i == 0) {
                        lastNode = node;
                    } else {
                        lastNode = node.children.get(i - 1);
                    }
                    if (i == node.children.size() - 1) {
                        last = true;
                    } else {
                        last = false;
                    }
                    JSONify_traversal(node.children.get(i), lastNode, last, i);
                }
                for (int i = 0; i < node.depth + 2; i++) {
                    JSONified += "\t";
                }
                JSONified += "}";
            }
            // if it's a leaf
            else {
                for (int i = 0; i < node.depth + 2; i++) {
                    JSONified += "\t";
                }
                JSONified += "\"" + node.value + "\"";
            }
            JSONified += '\n';
            Node parent = node.parent;
            if (order == parent.children.size() - 1) {
                // indentation
                for (int i = 0; i < node.depth + 1; i++) {
                    JSONified += "\t";
                }
                JSONified += "]";

                if (!last) {
                    JSONified += ",";
                }
                JSONified += '\n';
            }
            boolean lastone = false;
            try {
                parent.children.get(order + 1);
            } catch (Exception e) {
                lastone = true;
            }
            if (!lastone) {
                if (parent.children.get(order + 1).tag.compareTo(node.tag) != 0) {
                    // indentation
                    for (int i = 0; i < node.depth + 1; i++) {
                        JSONified += "\t";
                    }
                    JSONified += "]";

                    if (!last) {
                        JSONified += ",";
                    }
                    JSONified += '\n';
                }
            }
            return;

        }

        // First child: check if there're twin brothers
        node.get_brothers();
        for (int i = 0; i < node.brothers.size(); i++) {
            if (node.brothers.get(i).tag.compareTo(node.tag) == 0 && i != order) {
                square = true;
                break;
            }
        }
        if (square) {
            // indentation
            for (int i = 0; i < node.depth + 1; i++) {
                JSONified += "\t";
            }
            JSONified += "\"" + node.tag + "\": [\n";
            // check if it has children
            if (node.children.size() > 0) {
                // indentation
                for (int i = 0; i < node.depth + 2; i++) {
                    JSONified += "\t";
                }
                // adding the "{", calling the children
                JSONified += "{\n";
                for (int i = 0; i < node.children.size(); i++) {
                    if (i == 0) {
                        lastNode = node;
                    } else {
                        lastNode = node.children.get(i - 1);
                    }
                    if (i == node.children.size() - 1) {
                        last = true;
                    } else {
                        last = false;
                    }

                    JSONify_traversal(node.children.get(i), lastNode, last, i);
                }
                // closing
                for (int i = 0; i < node.depth + 2; i++) {
                    JSONified += "\t";
                }
                JSONified += "},";
            } else {
                // indentation
                for (int i = 0; i < node.depth + 2; i++) {
                    JSONified += "\t";
                }
                JSONified += "\"" + node.value + "\"";
            }
            if (!last) {
                JSONified += ",";
            }
            JSONified += '\n';
            square = false;
            return;
        }

        // no twins
        else {
            // check if it has children
            if (node.children.size() > 0) {
                // indentation
                for (int i = 0; i < node.depth + 1; i++) {
                    JSONified += "\t";
                }
                // adding the "{", calling the children
                JSONified += "\"" + node.tag + "\": {\n";
                for (int i = 0; i < node.children.size(); i++) {
                    if (i == 0) {
                        lastNode = node;
                    } else {
                        lastNode = node.children.get(i - 1);
                    }
                    if (i == node.children.size() - 1) {
                        last = true;
                    } else {
                        last = false;
                    }
                    JSONify_traversal(node.children.get(i), lastNode, last, i);
                }
                // closing
                for (int i = 0; i < node.depth + 2; i++) {
                    JSONified += "\t";
                }
                JSONified += "}";
            } else {
                // indentation
                for (int i = 0; i < node.depth + 1; i++) {
                    JSONified += "\t";
                }
                JSONified += "\"" + node.tag + "\": ";
                JSONified += "\"" + node.value + "\"";
            }
            if (!last) {
                JSONified += ",";
            }
            JSONified += '\n';
            return;
        }

    }

}
