package com.doitnext.swing.widgets.json;

import java.awt.BorderLayout;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;

/**
 * Implements the embeddable swing widget for editing JSON.
 * 
 * This class is not thread safe.
 * 
 * @author Stephen Owens
 *
 *
 * <p>
 * Copyright 2011 Stephen P. Owens : steve@doitnext.com
 * </p>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * </p>
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
public class JSONEditPanel extends JPanel {
	/**
	 * Using default serial version id.
	 */
	private static final long serialVersionUID = 1L;
	
	// Default visibility to let classes in this module access this element directly.
	JTree jTree;
	
	public enum UpdateType { REPLACE, INSERT, APPEND, AS_CHILD };
	
	/**
	 * Default constructor for the JSONEditPanel object.
	 * 
	 * Creates an empty tree.
	 */
	public JSONEditPanel() {		
		setLayout(new BorderLayout());
		JSONJTreeNode root = new JSONJTreeNode(null, -1, new JsonNull());
		jTree = new JTree(root);
		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);		
		add(new JScrollPane(jTree), BorderLayout.CENTER);
	}
	
	public void addTreeSelectionListener(TreeSelectionListener tsl) {
		jTree.addTreeSelectionListener(tsl);
	}
	
	/**
	 * Replaces the current tree structure in the contained JTree component
	 * with a new structure built from the JSON string provided.
	 * 
	 * @param json - the JSON to update the tree control with
	 * @param updateType - if a node is selected when this method is called
	 *    then the updateType indicates where the new json goes:
	 *    REPLACE - replace current selected node with new JSON
	 *    INSERT - place node before selected node
	 *    APPEND - place node after selected node
	 *    AS_CHILD - append to end of child nodes or insert new child node if no
	 *            children present.  Selected node must be of type ARRAY or OBJECT
	 */
	@SuppressWarnings("unchecked")
	public void setJson(String json, UpdateType updateType) {
		TreePath selection = jTree.getSelectionPath();
		if(selection == null && updateType == UpdateType.REPLACE) {
			JsonElement root = new JsonParser().parse(json);
			JSONJTreeNode rootNode = new JSONJTreeNode(null, -1, root);
			jTree.setModel(new DefaultTreeModel(rootNode));
		} else {
			JSONJTreeNode selectedNode = (JSONJTreeNode) selection.getLastPathComponent();
			JSONJTreeNode parent = (JSONJTreeNode)selectedNode.getParent();	
			switch(updateType) {
			case REPLACE: {
					if(parent == null) {
						JsonElement root = new JsonParser().parse(json);
						JSONJTreeNode rootNode = new JSONJTreeNode(null, -1, root);
						jTree.setModel(new DefaultTreeModel(rootNode));
						return;
					}
					JsonElement root = new JsonParser().parse(json);
					JSONJTreeNode replacementNode = new JSONJTreeNode(selectedNode.fieldName, selectedNode.index, root);
					int index = selectedNode.getParent().getIndex(selectedNode);
					selectedNode.removeFromParent();
					parent.insert(replacementNode, index);	
					((DefaultTreeModel)jTree.getModel()).reload(parent);
				}
				break;
			case INSERT:
			case APPEND: {
					if(parent == null)
						return; // TODO: Notify User that this is ignored operation
					JsonElement root = new JsonParser().parse(json);
					JSONJTreeNode replacementNode = new JSONJTreeNode(selectedNode.fieldName, selectedNode.index, root);
					int index = selectedNode.getParent().getIndex(selectedNode);
					if(updateType.equals(UpdateType.APPEND))
						index++;
					parent.insert(replacementNode, index);	
					((DefaultTreeModel)jTree.getModel()).reload(parent);
				}
				break;
			case AS_CHILD: {
					JsonElement root = new JsonParser().parse(json);
					String fieldName = null;
					int arrayIndex = -1;
					if(selectedNode.dataType.equals(JSONJTreeNode.DataType.ARRAY)) {
						Enumeration en = selectedNode.children();
						int count = 0;
						while(en.hasMoreElements()) {
							en.nextElement();
							count++;
						}
						arrayIndex = count;
					}
					else if(selectedNode.dataType.equals(JSONJTreeNode.DataType.OBJECT))
						fieldName = "new-field";
					else 
						return;  // TODO: Display message to user indicating aborted op.
					
					JSONJTreeNode newNode = new JSONJTreeNode(fieldName, arrayIndex, root);
					selectedNode.add(newNode);
					((DefaultTreeModel)jTree.getModel()).reload(selectedNode);
				}
				break;
			}
		}
	}
	
	/**
	 * Renames the selected node if it is a renameable node.
	 */
	public void renameNode() {
		TreePath selection = jTree.getSelectionPath();
		if(selection == null)
			return; // TODO: Notify user no node selected
		JSONJTreeNode node = (JSONJTreeNode) selection.getLastPathComponent();
		JSONJTreeNode parent = (JSONJTreeNode) node.getParent();
		if(parent == null)
			return; // TODO: Notify user cannot rename a root node.
		
		if(!parent.dataType.equals(JSONJTreeNode.DataType.OBJECT))
			return; // TODO: Nofify user cannot rename a node that isn't a field.
		
		String newName = JOptionPane.showInputDialog ( "Enter new name for " + node.fieldName );
		node.fieldName = newName;
		((DefaultTreeModel)jTree.getModel()).nodeChanged(node);		
	}
	
	/**
	 * Deletes selected node or sets entire model to null if root node or no node is selected.
	 */
	public void deleteNode() {
		TreePath selection = jTree.getSelectionPath();
		if(selection == null) {
			// Replace root with emptyness
			jTree.setModel(new DefaultTreeModel(new JSONJTreeNode(null, -1, new JsonNull())));
		} else {
			JSONJTreeNode node = (JSONJTreeNode) selection.getLastPathComponent();
			JSONJTreeNode parent = (JSONJTreeNode) node.getParent();
			if(parent == null) {
				// Replace root with emptyness
				jTree.setModel(new DefaultTreeModel(new JSONJTreeNode(null, -1, new JsonNull())));
			} else {
				node.removeFromParent();
				((DefaultTreeModel)jTree.getModel()).reload(parent);
			}
		}
	}
	
	/**
	 * Returns the current JSON from the JTree
	 * 
	 * @return the JSON as it is represented by the current state of the Tree model
	 */
	public String getJson() {
		TreePath selection = jTree.getSelectionPath();
		JSONJTreeNode node = null;
		if(selection == null) {
			((DefaultTreeModel)jTree.getModel()).reload();
			node = (JSONJTreeNode) jTree.getModel().getRoot();
		} else {
			((DefaultTreeModel)jTree.getModel()).reload(node);
			node = (JSONJTreeNode) selection.getLastPathComponent();
		}
		if(node != null)
			return node.asJsonElement().toString();
		else
			return null;
	}
}
