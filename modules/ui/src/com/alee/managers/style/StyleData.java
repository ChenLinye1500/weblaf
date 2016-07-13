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

package com.alee.managers.style;

import com.alee.painter.Painter;
import com.alee.utils.CollectionUtils;
import com.alee.utils.CompareUtils;
import com.alee.utils.LafUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This object contains style data for single component instance.
 * This is basically all {@link com.alee.managers.style.StyleManager} knows about the component styling.
 *
 * @author Mikle Garin
 */

public final class StyleData implements PropertyChangeListener
{
    /**
     * Component this style data is referencing.
     */
    private final WeakReference<JComponent> component;

    /**
     * Applied skin.
     */
    private Skin skin;

    /**
     * Whether or not skin was pinned.
     * Pinned skin will not be changed when global skin is changed.
     */
    private boolean pinnedSkin;

    /**
     * Style ID.
     */
    private StyleId styleId;

    /**
     * Custom painters.
     */
    private Map<String, Painter> painters;

    /**
     * Related style children.
     */
    private List<WeakReference<JComponent>> children;

    /**
     * Skin change listeners.
     */
    private List<StyleListener> listeners;

    /**
     * Constructs new empty style data object.
     *
     * @param component component this style data is referencing
     */
    public StyleData ( final JComponent component )
    {
        super ();

        // Saving component weak reference
        this.component = new WeakReference<JComponent> ( component );

        // Updating default values
        this.skin = null;
        this.pinnedSkin = false;
        this.styleId = null;
        this.painters = null;
        this.children = null;
        this.listeners = null;

        // Adding style ID listener
        component.addPropertyChangeListener ( StyleId.STYLE_PROPERTY, this );
        component.addPropertyChangeListener ( StyleId.PARENT_STYLE_PROPERTY, this );
    }

    @Override
    public void propertyChange ( final PropertyChangeEvent evt )
    {
        // Retrieving component
        final JComponent component = getComponent ();
        final Object styleId = component.getClientProperty ( StyleId.STYLE_PROPERTY );
        if ( styleId != null )
        {
            // Applying style ID if it was set explicitly
            if ( styleId instanceof StyleId )
            {
                // StyleId specified directly
                setStyleId ( ( StyleId ) styleId );
            }
            else if ( styleId instanceof String )
            {
                // String style ID was passed
                final String id = ( String ) styleId;

                // Trying to retrieve parent
                final Object parent = component.getClientProperty ( StyleId.PARENT_STYLE_PROPERTY );
                if ( parent != null )
                {
                    if ( parent instanceof JComponent )
                    {
                        // Parent provided directly
                        setStyleId ( StyleId.of ( id, ( JComponent ) parent ) );
                    }
                    else if ( parent instanceof WeakReference )
                    {
                        final Object p = ( ( WeakReference ) parent ).get ();
                        if ( p != null && p instanceof JComponent )
                        {
                            // Parent provided through weak reference
                            setStyleId ( StyleId.of ( id, ( JComponent ) p ) );
                        }
                        else
                        {
                            // Simple direct style
                            setStyleId ( StyleId.of ( id ) );
                        }
                    }
                    else
                    {
                        // Simple direct style
                        setStyleId ( StyleId.of ( id ) );
                    }
                }
                else
                {
                    // Simple direct style
                    setStyleId ( StyleId.of ( id ) );
                }
            }
        }
        else
        {
            // Restoring default style ID value
            resetStyleId ( false );
        }
    }

    /**
     * Returns component this style data is referencing.
     *
     * @return component this style data is referencing
     */
    public JComponent getComponent ()
    {
        final JComponent component = this.component.get ();

        // Ensure that component still exists
        if ( component == null )
        {
            throw new StyleException ( "Component for style ID \"" + styleId.getCompleteId () + "\" has been destroyed" );
        }

        // Ensure that component has correct UI first, fix for #376
        // This will never happen if WebLaF is installed before creating any Swing components
        // Component might be missing UI here because it's style ID was applied from upper level component
        if ( !LafUtils.isWebLafUI ( component ) )
        {
            // Trying to update UI
            component.updateUI ();

            // Checking that proper UI was installed
            if ( !LafUtils.isWebLafUI ( component ) )
            {
                // Our attempt to apply WebLaF UI has failed, throwing appropriate exception
                throw new StyleException ( "Unable to apply StyleId to " + component.getClass () + " because it doesn't use WebLaF UI" );
            }
        }

        return component;
    }

    /**
     * Returns currently applied skin.
     *
     * @return currently applied skin
     */
    public Skin getSkin ()
    {
        return skin;
    }

    /**
     * Returns whether or not skin was pinned.
     * Pinned skin will not be changed when global skin is changed.
     *
     * @return true if skin was pinned, false otherwise
     */
    public boolean isPinnedSkin ()
    {
        return pinnedSkin;
    }

    /**
     * Sets whether or not skin was pinned.
     *
     * @param pinnedSkin whether or not skin was pinned
     */
    public void setPinnedSkin ( final boolean pinnedSkin )
    {
        this.pinnedSkin = pinnedSkin;
    }

    /**
     * Applies new component skin and returns previously applied skin.
     * This method is used in UIs for initial component skin installation.
     *
     * @param skin     skin to apply
     * @param children whether or not should apply the same skin to style children
     * @return previously applied skin
     */
    public Skin applySkin ( final Skin skin, final boolean children )
    {
        // Retrieving component and checking its existance
        final JComponent component = getComponent ();

        // Checking that provided skin is actually different one
        final boolean newSkin = skin != getSkin ();

        // Saving reference to old skin
        final Skin oldSkin;
        if ( newSkin )
        {
            // Removing old skin
            oldSkin = removeSkin ();
        }
        else
        {
            // Simply providing current skin
            oldSkin = getSkin ();
        }

        // Applying new skin to specified component
        if ( newSkin && skin != null )
        {
            skin.applySkin ( component );
            this.skin = skin;
        }

        // Resetting pinned state if skin was changed
        // If it is needed skin will be pinned again after this method call
        if ( newSkin )
        {
            setPinnedSkin ( false );
        }

        // Applying skin to component's style children
        if ( skin != null && children && !CollectionUtils.isEmpty ( this.children ) )
        {
            for ( final WeakReference<JComponent> reference : this.children )
            {
                final JComponent child = reference.get ();
                if ( child != null )
                {
                    StyleManager.setSkin ( child, skin, false );
                }
            }
        }

        // Informing about skin changes
        if ( newSkin )
        {
            // Informing about skin changes
            fireSkinChanged ( component, oldSkin, skin );

            // Informing about skin visual update
            fireSkinUpdated ( component, getStyleId () );
        }

        return oldSkin;
    }

    /**
     * Applies specified custom skin to the skinnable component and all of its children linked via {@link com.alee.managers.style.StyleId}.
     * Actual linked children information is stored within {@link com.alee.managers.style.StyleData} data objects.
     * Custom skin provided using this method will not be replaced if application skin changes.
     *
     * @param skin        skin to be applied
     * @param recursively whether or not should apply skin to child components
     * @return previously applied skin
     */
    public Skin applyCustomSkin ( final Skin skin, final boolean recursively )
    {
        // Replacing component skin
        // Style children are also updated through this call
        // Even though we might encounter style children again in components tree later it will not cause extensive updates
        final Skin previousSkin = applySkin ( skin, true );

        // Pinning applied skin
        // This will keep this skin even if global skin is changed
        setPinnedSkin ( true );

        // Applying new skin to all existing skinnable components
        // This approach is quite different from style children but works better for large UI updates
        if ( recursively )
        {
            final JComponent component = getComponent ();
            for ( int i = 0; i < component.getComponentCount (); i++ )
            {
                final Component child = component.getComponent ( i );
                if ( child instanceof JComponent )
                {
                    StyleManager.setSkin ( ( JComponent ) child, skin, true );
                }
            }
        }

        return previousSkin;
    }

    /**
     * Updates current skin in the skinnable component.
     * This method is used to properly update skin on various changes.
     *
     * @param children whether or not should apply the same skin to style children
     */
    public void updateSkin ( final boolean children )
    {
        // Retrieving component and checking its existance
        final JComponent component = getComponent ();

        // Updating component skin
        getSkin ().updateSkin ( component );

        // Updating children skins
        if ( children && !CollectionUtils.isEmpty ( this.children ) )
        {
            for ( final WeakReference<JComponent> reference : this.children )
            {
                final JComponent child = reference.get ();
                if ( child != null )
                {
                    StyleManager.updateSkin ( child );
                }
            }
        }

        // Informing about skin visual update
        fireSkinUpdated ( component, getStyleId () );
    }

    /**
     * Resets skin for the component and all of its children linked via {@link com.alee.managers.style.StyleId}.
     * Actual linked children information is stored within {@link com.alee.managers.style.StyleData} data objects.
     * Resetting component skin will also include it back into the skin update cycle in case global skin will be changed.
     *
     * @return skin applied to the component after reset
     */
    public Skin resetSkin ()
    {
        // Resetting skin to globally set one if needed
        final Skin skin = getSkin ();
        final Skin globalSkin = StyleManager.getSkin ();
        if ( globalSkin == skin )
        {
            applySkin ( globalSkin, true );
            return globalSkin;
        }
        else
        {
            return skin;
        }
    }

    /**
     * Removes skin currently applied to the specified component.
     * This will uninstall component skin without installing any other on top of previous one and will leave component empty.
     *
     * @return previously applied skin
     */
    public Skin removeSkin ()
    {
        final Skin oldSkin = this.skin;
        if ( this.skin != null )
        {
            // Retrieving component and checking its existance
            final JComponent component = getComponent ();

            // Removing skin
            this.skin.removeSkin ( component );
            this.skin = null;
        }
        return oldSkin;
    }

    /**
     * Returns currently used style ID.
     *
     * @return currently used style ID
     */
    public StyleId getStyleId ()
    {
        return styleId != null ? styleId : StyleId.getDefault ( getComponent () );
    }

    /**
     * Sets currently used style ID.
     *
     * @param id new style ID
     * @return previously used style ID
     */
    public StyleId setStyleId ( final StyleId id )
    {
        // Retrieving component and checking its existance
        final JComponent component = getComponent ();

        // Resolving actual provided style ID
        final StyleId styleId = id != null && id.getId () != null ? id : StyleId.getDefault ( component );

        // Perform operation if IDs are actually different
        final StyleId old = getStyleId ();
        if ( !CompareUtils.equals ( styleId, old ) )
        {
            // Saving old style ID reference
            final StyleId oldStyleId = this.styleId;

            // Saving new style ID
            this.styleId = styleId;

            // Removing child reference from old parent style data
            if ( old != null )
            {
                final JComponent oldParent = old.getParent ();
                if ( oldParent != null )
                {
                    StyleManager.getData ( oldParent ).removeChild ( component );
                }
            }

            // Adding child reference into new parent style data
            final JComponent parent = styleId.getParent ();
            if ( parent != null )
            {
                StyleManager.getData ( parent ).addChild ( component );
            }

            // Updating component skin
            if ( parent != null )
            {
                final Skin parentSkin = StyleManager.getSkin ( parent );
                if ( parentSkin != null && parentSkin != getSkin () )
                {
                    // Applying style parent skin
                    applySkin ( parentSkin, false );
                }
                else
                {
                    // Component style parent skin is the same, simply updating current skin
                    updateSkin ( true );
                }
            }
            else
            {
                // There is no parent, simply updating current skin
                updateSkin ( true );
            }

            // Informing about style change
            fireStyleChanged ( component, oldStyleId, styleId );
        }
        return old;
    }

    /**
     * Resets style ID to default value.
     *
     * @param recursively whether or not child styles should also be reset
     * @return previously used style ID
     */
    public StyleId resetStyleId ( final boolean recursively )
    {
        // Resetting child IDs first
        if ( recursively && !CollectionUtils.isEmpty ( children ) )
        {
            // We have to be careful here since resetting child styles might modify childs list
            // That will actually happen more often than not since default styles are usually not structured
            for ( final WeakReference<JComponent> reference : CollectionUtils.copy ( children ) )
            {
                final JComponent child = reference.get ();
                if ( child != null )
                {
                    StyleManager.resetStyleId ( child );
                }
            }
        }

        // Resetting style ID
        return setStyleId ( null );
    }

    /**
     * Returns custom painters.
     *
     * @return custom painters
     */
    public Map<String, Painter> getPainters ()
    {
        return painters;
    }

    /**
     * Sets custom painters.
     *
     * @param painters custom painters
     */
    public void setPainters ( final Map<String, Painter> painters )
    {
        this.painters = painters;
    }

    /**
     * Adds related style child.
     *
     * @param child related style child
     */
    public void addChild ( final JComponent child )
    {
        if ( children == null )
        {
            children = new ArrayList<WeakReference<JComponent>> ( 1 );
        }
        children.add ( new WeakReference<JComponent> ( child ) );
    }

    /**
     * Removes related style child.
     *
     * @param child related style child
     */
    public void removeChild ( final JComponent child )
    {
        final Iterator<WeakReference<JComponent>> iterator = children.iterator ();
        while ( iterator.hasNext () )
        {
            final WeakReference<JComponent> next = iterator.next ();
            if ( next.get () == child )
            {
                iterator.remove ();
            }
        }
    }

    /**
     * Adds style change listener.
     *
     * @param listener style change listener to add
     */
    public void addStyleListener ( final StyleListener listener )
    {
        if ( listeners == null )
        {
            listeners = new ArrayList<StyleListener> ( 1 );
        }
        listeners.add ( listener );
    }

    /**
     * Removes style change listener.
     *
     * @param listener style change listener to remove
     */
    public void removeStyleListener ( final StyleListener listener )
    {
        if ( listeners != null )
        {
            listeners.remove ( listener );
        }
    }

    /**
     * Informs about component skin change.
     *
     * @param component component which style has changed
     * @param oldSkin   previously used skin
     * @param newSkin   currently used skin
     */
    private void fireSkinChanged ( final JComponent component, final Skin oldSkin, final Skin newSkin )
    {
        if ( listeners != null )
        {
            for ( final StyleListener listener : listeners )
            {
                listener.skinChanged ( component, oldSkin, newSkin );
            }
        }
    }

    /**
     * Informs about component style change.
     *
     * @param component  component which style has changed
     * @param oldStyleId previously used style ID
     * @param newStyleId currently used style ID
     */
    private void fireStyleChanged ( final JComponent component, final StyleId oldStyleId, final StyleId newStyleId )
    {
        if ( listeners != null )
        {
            for ( final StyleListener listener : listeners )
            {
                listener.styleChanged ( component, oldStyleId, newStyleId );
            }
        }
    }

    /**
     * Informs about component skin visual update.
     * Skin update might occur when component style ID changes or its parent style component style ID changes.
     *
     * @param component component which style have been updated
     * @param styleId   component style ID
     */
    private void fireSkinUpdated ( final JComponent component, final StyleId styleId )
    {
        if ( listeners != null )
        {
            for ( final StyleListener listener : listeners )
            {
                listener.skinUpdated ( component, styleId );
            }
        }
    }
}