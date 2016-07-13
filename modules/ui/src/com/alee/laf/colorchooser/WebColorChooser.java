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

package com.alee.laf.colorchooser;

import com.alee.managers.log.Log;
import com.alee.managers.style.*;
import com.alee.painter.Paintable;
import com.alee.painter.Painter;
import com.alee.utils.swing.DialogOptions;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import java.awt.*;
import java.util.Map;

/**
 * {@link JColorChooser} extension class.
 * It contains various useful methods to simplify core component usage.
 * <p/>
 * This component should never be used with a non-Web UIs as it might cause an unexpected behavior.
 * You could still use that component even if WebLaF is not your application L&amp;F as this component will use Web-UI in any case.
 *
 * @author Mikle Garin
 * @see JColorChooser
 * @see WebColorChooserUI
 * @see ColorChooserPainter
 */

public class WebColorChooser extends JColorChooser
        implements Styleable, Paintable, ShapeProvider, MarginSupport, PaddingSupport, DialogOptions
{
    /**
     * Constructs new color chooser.
     */
    public WebColorChooser ()
    {
        this ( StyleId.auto );
    }

    /**
     * Constructs new color chooser.
     *
     * @param initialColor initially selected color
     */
    public WebColorChooser ( final Color initialColor )
    {
        this ( StyleId.auto, initialColor );
    }

    /**
     * Constructs new color chooser.
     *
     * @param model color selection model
     */
    public WebColorChooser ( final ColorSelectionModel model )
    {
        this ( StyleId.auto, model );
    }

    /**
     * Constructs new color chooser.
     *
     * @param id style ID
     */
    public WebColorChooser ( final StyleId id )
    {
        this ( id, Color.WHITE );
    }

    /**
     * Constructs new color chooser.
     *
     * @param id           style ID
     * @param initialColor initially selected color
     */
    public WebColorChooser ( final StyleId id, final Color initialColor )
    {
        this ( id, new DefaultColorSelectionModel ( initialColor ) );
    }

    /**
     * Constructs new color chooser.
     *
     * @param id    style ID
     * @param model color selection model
     */
    public WebColorChooser ( final StyleId id, final ColorSelectionModel model )
    {
        super ( model );
        setStyleId ( id );
    }

    public boolean isShowButtonsPanel ()
    {
        return getWebUI ().isShowButtonsPanel ();
    }

    public void setShowButtonsPanel ( final boolean showButtonsPanel )
    {
        getWebUI ().setShowButtonsPanel ( showButtonsPanel );
    }

    public boolean isWebOnlyColors ()
    {
        return getWebUI ().isWebOnlyColors ();
    }

    public void setWebOnlyColors ( final boolean webOnlyColors )
    {
        getWebUI ().setWebOnlyColors ( webOnlyColors );
    }

    public Color getOldColor ()
    {
        return getWebUI ().getOldColor ();
    }

    public void setOldColor ( final Color oldColor )
    {
        getWebUI ().setOldColor ( oldColor );
    }

    public void resetResult ()
    {
        getWebUI ().resetResult ();
    }

    public void setResult ( final int result )
    {
        getWebUI ().setResult ( result );
    }

    public int getResult ()
    {
        return getWebUI ().getResult ();
    }

    public void addColorChooserListener ( final ColorChooserListener colorChooserListener )
    {
        getWebUI ().addColorChooserListener ( colorChooserListener );
    }

    public void removeColorChooserListener ( final ColorChooserListener colorChooserListener )
    {
        getWebUI ().removeColorChooserListener ( colorChooserListener );
    }

    @Override
    public StyleId getDefaultStyleId ()
    {
        return StyleId.colorchooser;
    }

    @Override
    public StyleId getStyleId ()
    {
        return StyleManager.getStyleId ( this );
    }

    @Override
    public StyleId setStyleId ( final StyleId id )
    {
        return StyleManager.setStyleId ( this, id );
    }

    @Override
    public StyleId resetStyleId ()
    {
        return StyleManager.resetStyleId ( this );
    }

    @Override
    public Skin getSkin ()
    {
        return StyleManager.getSkin ( this );
    }

    @Override
    public Skin setSkin ( final Skin skin )
    {
        return StyleManager.setSkin ( this, skin );
    }

    @Override
    public Skin setSkin ( final Skin skin, final boolean recursively )
    {
        return StyleManager.setSkin ( this, skin, recursively );
    }

    @Override
    public Skin resetSkin ()
    {
        return StyleManager.resetSkin ( this );
    }

    @Override
    public void addStyleListener ( final StyleListener listener )
    {
        StyleManager.addStyleListener ( this, listener );
    }

    @Override
    public void removeStyleListener ( final StyleListener listener )
    {
        StyleManager.removeStyleListener ( this, listener );
    }

    @Override
    public Map<String, Painter> getCustomPainters ()
    {
        return StyleManager.getCustomPainters ( this );
    }

    @Override
    public Painter getCustomPainter ()
    {
        return StyleManager.getCustomPainter ( this );
    }

    @Override
    public Painter getCustomPainter ( final String id )
    {
        return StyleManager.getCustomPainter ( this, id );
    }

    @Override
    public Painter setCustomPainter ( final Painter painter )
    {
        return StyleManager.setCustomPainter ( this, painter );
    }

    @Override
    public Painter setCustomPainter ( final String id, final Painter painter )
    {
        return StyleManager.setCustomPainter ( this, id, painter );
    }

    @Override
    public boolean resetPainter ()
    {
        return StyleManager.resetPainter ( this );
    }

    @Override
    public Shape provideShape ()
    {
        return getWebUI ().provideShape ();
    }

    @Override
    public Insets getMargin ()
    {
        return getWebUI ().getMargin ();
    }

    /**
     * Sets new margin.
     *
     * @param margin new margin
     */
    public void setMargin ( final int margin )
    {
        setMargin ( margin, margin, margin, margin );
    }

    /**
     * Sets new margin.
     *
     * @param top    new top margin
     * @param left   new left margin
     * @param bottom new bottom margin
     * @param right  new right margin
     */
    public void setMargin ( final int top, final int left, final int bottom, final int right )
    {
        setMargin ( new Insets ( top, left, bottom, right ) );
    }

    @Override
    public void setMargin ( final Insets margin )
    {
        getWebUI ().setMargin ( margin );
    }

    @Override
    public Insets getPadding ()
    {
        return getWebUI ().getPadding ();
    }

    /**
     * Sets new padding.
     *
     * @param padding new padding
     */
    public void setPadding ( final int padding )
    {
        setPadding ( padding, padding, padding, padding );
    }

    /**
     * Sets new padding.
     *
     * @param top    new top padding
     * @param left   new left padding
     * @param bottom new bottom padding
     * @param right  new right padding
     */
    public void setPadding ( final int top, final int left, final int bottom, final int right )
    {
        setPadding ( new Insets ( top, left, bottom, right ) );
    }

    @Override
    public void setPadding ( final Insets padding )
    {
        getWebUI ().setPadding ( padding );
    }

    /**
     * Returns Web-UI applied to this class.
     *
     * @return Web-UI applied to this class
     */
    private WebColorChooserUI getWebUI ()
    {
        return ( WebColorChooserUI ) getUI ();
    }

    /**
     * Installs a Web-UI into this component.
     */
    @Override
    public void updateUI ()
    {
        if ( getUI () == null || !( getUI () instanceof WebColorChooserUI ) )
        {
            try
            {
                setUI ( ( WebColorChooserUI ) UIManager.getUI ( this ) );
            }
            catch ( final Throwable e )
            {
                Log.error ( this, e );
                setUI ( new WebColorChooserUI () );
            }
        }
        else
        {
            setUI ( getUI () );
        }
    }

    public static Color showDialog ( final Component parent )
    {
        return showDialog ( parent, null, null );
    }

    public static Color showDialog ( final Component parent, final String title )
    {
        return showDialog ( parent, title, null );
    }

    public static Color showDialog ( final Component parent, final Color color )
    {
        return showDialog ( parent, null, color );
    }

    public static Color showDialog ( final Component parent, final String title, final Color color )
    {
        // Creating new dialog
        final WebColorChooserDialog wcc = new WebColorChooserDialog ( parent, title );

        // Initial color
        if ( color != null )
        {
            wcc.setColor ( color );
        }

        // Showing color chooser dialog
        // This will block further method execution until dialog is closed
        wcc.setVisible ( true );

        // Returning selected color
        return wcc.getResult () == OK_OPTION ? wcc.getColor () : null;
    }
}