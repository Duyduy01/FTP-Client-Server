/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.util.List;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author olliv
 */
public class DragListener implements DropTargetListener {

    JTextField jtf = new JTextField();

    public DragListener(JTextField jtf) {
        this.jtf = jtf;
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        // Acept drop item
        dtde.acceptDrop(DnDConstants.ACTION_COPY);

        //
        Transferable tfa = dtde.getTransferable();
        DataFlavor[] dfs = tfa.getTransferDataFlavors();

        for (DataFlavor df : dfs) {
            try {
                if (df.isFlavorJavaFileListType()) {
                    List<File> files = (List<File>) tfa.getTransferData(df);

                    for (File file : files) {
                        jtf.setText(file.getPath());
                    }
                }
            } catch (Exception ex) {
            }
        }
    }
}
