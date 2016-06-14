/*
 * This file is part of WebLookAndFeel library.
 *
 * WebLookAndFeel library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebLookAndFeel library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alee.managers.drag.transfer;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Custom {@link javax.swing.TransferHandler} that provides easy and convenient way to support files drag and drop in any Swing component.
 * <p>
 * To provide drag functionality set {@link #dragEnabled} to {@code true} and override {@link #getDraggedFiles()} or
 * {@link #getDraggedFile()} method to provide list of files or single dragged file.
 * You will also have to call "exportAsDrag" method of TransferHandler to initialize drag operation from where you want it to start.
 * That can be done from {@link java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)} method for example.
 * <p>
 * To provide drop functionality set {@link #dropEnabled} to {@code true} and override {@link #filesDropped(java.util.List)} method.
 * In that method you can perform any desired action with the list of dropped files.
 *
 * @author Mikle Garin
 */

public class FilesTransferHandler extends AbstractTransferHandler
{
    /**
     * Constructs new {@link FilesTransferHandler}.
     *
     * @param dragEnabled whether drag operations are allowed
     * @param dropEnabled whether drop operations are allowed
     */
    public FilesTransferHandler ( final boolean dragEnabled, final boolean dropEnabled )
    {
        super ( dragEnabled, dropEnabled );
    }

    @Override
    protected Transferable createTransferable ( final JComponent component )
    {
        if ( isDragEnabled () )
        {
            final List<File> draggedFiles = getDraggedFiles ();
            return draggedFiles != null && draggedFiles.size () > 0 ? new FilesTransferable ( draggedFiles ) : null;
        }
        else
        {
            return null;
        }
    }

    /**
     * Informs that drag action has started and list of dragged files should be provided.
     * You can override this method to provide custom list of dragged files.
     *
     * @return list of dragged files
     */
    public List<File> getDraggedFiles ()
    {
        final File file = getDraggedFile ();
        return file != null ? Arrays.asList ( file ) : null;
    }

    /**
     * Informs that drag action has started and dragged file should be provided.
     * You can override this method to provide custom dragged file.
     *
     * @return dragged file
     */
    public File getDraggedFile ()
    {
        return null;
    }

    @Override
    public boolean canImport ( final TransferHandler.TransferSupport support )
    {
        return isDropEnabled () && FilesTransferable.hasFilesList ( support.getTransferable () );
    }

    @Override
    public boolean importData ( final TransferHandler.TransferSupport info )
    {
        return isDropEnabled () && info.isDrop () && filesDropped ( FilesTransferable.getFilesList ( info.getTransferable () ) );
    }

    /**
     * Informs that specified files were dropped.
     * You can override this method to perform desired actions.
     *
     * @param files list of dropped files
     * @return true if drop was successfully completed, false otherwise
     */
    public boolean filesDropped ( final List<File> files )
    {
        return true;
    }
}