/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.visitor.impl;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.visitor.BaseIntactVisitor;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class JTreeBuilderVisitor extends BaseIntactVisitor{

    private TreeModel treeModel;
    private DefaultMutableTreeNode parent;
    private DefaultMutableTreeNode currentNode;

    public JTreeBuilderVisitor() {
    }

    @Override
    public void nextHierarchyLevel() {
        if (currentNode != null) {
            parent = currentNode;
        }
    }

    @Override
    public void previousHierarchyLevel() {
        if (parent != null) {
            currentNode = parent;
            parent = (DefaultMutableTreeNode) parent.getParent();
        }

    }

    @Override
    public void visitIntactObject(IntactObject intactObject) {
        currentNode = new DefaultMutableTreeNode(intactObject.getClass().getSimpleName());

        if (parent == null) {
           treeModel = new DefaultTreeModel(currentNode);
        } else {
            parent.add(currentNode);
        }
    }

    @Override
    public void visitAnnotatedObject(AnnotatedObject annotatedObject) {
        currentNode.setUserObject(annotatedObject.getClass().getSimpleName()+": "+annotatedObject.getShortLabel());
    }

    @Override
    public void visitXref(Xref xref) {
         currentNode.setUserObject("Xref: "+xref.getPrimaryId());
    }

    @Override
    public void visitAlias(Alias alias) {
        currentNode.setUserObject("Alias: "+alias.getName());
    }

    @Override
    public void visitAnnotation(Annotation annotation) {
        currentNode.setUserObject("Annotation: "+annotation.getAnnotationText());
    }

    @Override
    public void visitBioSource(BioSource bioSource) {
        currentNode.setUserObject("BioSource: "+bioSource.getShortLabel()+" (taxid:"+bioSource.getTaxId()+")");
    }

    @Override
    public void visitCvObject(CvObject cvObject) {
        currentNode.setUserObject(cvObject.getClass().getSimpleName()+": "+cvObject.getShortLabel()+" ("+ cvObject.getMiIdentifier()+")");
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }

    public JTree getJTree() {
        JTree jtree = new JTree(getTreeModel());
        return jtree;
    }

    public void renderTree() {
        renderTree( "Tree for: " + getJTree().getModel().getRoot() );
    }

    public void renderTree( String frameName ) {
        JFrame frame = new JFrame();
        frame.setTitle( frameName );

        JPanel panel = new JPanel();
        frame.getContentPane().add( panel );
        frame.setSize( 600, 600 );
        panel.setLayout( new BorderLayout() );
        panel.add( new JScrollPane( getJTree() ) );

        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );

        frame.setVisible( true );
    }
}