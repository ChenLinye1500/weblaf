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

package com.alee.extended.date;

import com.alee.extended.WebComponent;
import com.alee.managers.log.Log;
import com.alee.managers.settings.DefaultValue;
import com.alee.managers.settings.SettingsManager;
import com.alee.managers.settings.SettingsMethods;
import com.alee.managers.settings.SettingsProcessor;
import com.alee.managers.style.StyleId;
import com.alee.managers.style.StyleableComponent;
import com.alee.utils.swing.Customizer;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom date chooser component.
 * It relies on {@link WebCalendar} component as a main date chooser.
 * <p/>
 * This component should never be used with a non-Web UIs as it might cause an unexpected behavior.
 * You could still use that component even if WebLaF is not your application L&amp;F as this component will use Web-UI in any case.
 *
 * @author Mikle Garin
 * @see WebComponent
 * @see WebDateFieldUI
 * @see DateFieldPainter
 */

public class WebDateField extends WebComponent<WebDateFieldUI, WebDateField> implements SettingsMethods
{
    /**
     * Component properties.
     */
    public static final String DATE_PROPERTY = "date";
    public static final String DATE_FORMAT_PROPERTY = "dateFormat";
    public static final String CALENDAR_CUSTOMIZER_PROPERTY = "calendarCustomizer";
    public static final String ALLOW_USER_INPUT_PROPERTY = "allowUserInput";

    /**
     * Date display format.
     */
    protected DateFormat dateFormat = new SimpleDateFormat ( "dd.MM.yyyy" );

    /**
     * Calendar component customizer.
     */
    protected Customizer<WebCalendar> calendarCustomizer;

    /**
     * Currently selected date.
     */
    protected Date date;

    /**
     * Indicating whether manual user input allowed or not.
     * This would commonly enable text date input field.
     */
    protected boolean allowUserInput = true;

    /**
     * Constructs new date field.
     */
    public WebDateField ()
    {
        this ( StyleId.auto );
    }

    /**
     * Constructs new date field with specified selected date.
     *
     * @param date selected date
     */
    public WebDateField ( final Date date )
    {
        this ( StyleId.auto, date );
    }

    /**
     * Constructs new date field.
     *
     * @param id style ID
     */
    public WebDateField ( final StyleId id )
    {
        this ( id, null );
    }

    /**
     * Constructs new date field with specified selected date.
     *
     * @param id   style ID
     * @param date selected date
     */
    public WebDateField ( final StyleId id, final Date date )
    {
        super ();
        setDate ( date );
        updateUI ();
        setStyleId ( id );
    }

    @Override
    public StyleId getDefaultStyleId ()
    {
        return StyleId.datefield;
    }

    /**
     * Returns selected date.
     *
     * @return selected date
     */
    public Date getDate ()
    {
        return date;
    }

    /**
     * Sets selected date.
     *
     * @param date selected date
     */
    public void setDate ( final Date date )
    {
        final Date previous = this.date;
        this.date = date;
        firePropertyChange ( DATE_PROPERTY, previous, date );
        fireDateChanged ( date );
    }

    /**
     * Returns date format.
     *
     * @return date format
     */
    public DateFormat getDateFormat ()
    {
        return dateFormat;
    }

    /**
     * Sets date format.
     *
     * @param dateFormat date format
     */
    public void setDateFormat ( final DateFormat dateFormat )
    {
        final DateFormat previous = this.dateFormat;
        this.dateFormat = dateFormat;
        firePropertyChange ( DATE_FORMAT_PROPERTY, previous, dateFormat );
    }

    /**
     * Returns whether user input allowed or not.
     *
     * @return true if user input allowed, false otherwise
     */
    public boolean isAllowUserInput ()
    {
        return allowUserInput;
    }

    /**
     * Sets whether user input should be allowed or not.
     *
     * @param allowUserInput whether user input should be allowed or not
     */
    public void setAllowUserInput ( final boolean allowUserInput )
    {
        final boolean previous = this.allowUserInput;
        this.allowUserInput = allowUserInput;
        firePropertyChange ( ALLOW_USER_INPUT_PROPERTY, previous, allowUserInput );
    }

    /**
     * Returns calendar component customizer.
     *
     * @return calendar component customizer
     */
    public Customizer<WebCalendar> getCalendarCustomizer ()
    {
        return calendarCustomizer;
    }

    /**
     * Sets calendar component customizer.
     *
     * @param customizer calendar component customizer
     */
    public void setCalendarCustomizer ( final Customizer<WebCalendar> customizer )
    {
        final Customizer<WebCalendar> previous = this.calendarCustomizer;
        this.calendarCustomizer = customizer;
        firePropertyChange ( CALENDAR_CUSTOMIZER_PROPERTY, previous, calendarCustomizer );
    }

    /**
     * Adds date change listener.
     *
     * @param listener date change listener to add
     */
    public void addDateListener ( final DateListener listener )
    {
        listenerList.add ( DateListener.class, listener );
    }

    /**
     * Removes date change listener.
     *
     * @param listener date change listener to remove
     */
    public void removeDateListener ( final DateListener listener )
    {
        listenerList.remove ( DateListener.class, listener );
    }

    /**
     * Notifies about date selection change.
     *
     * @param date selected date
     */
    protected void fireDateChanged ( final Date date )
    {
        for ( final DateListener listener : listenerList.getListeners ( DateListener.class ) )
        {
            listener.dateChanged ( date );
        }
    }

    @Override
    public void registerSettings ( final String key )
    {
        SettingsManager.registerComponent ( this, key );
    }

    @Override
    public <T extends DefaultValue> void registerSettings ( final String key, final Class<T> defaultValueClass )
    {
        SettingsManager.registerComponent ( this, key, defaultValueClass );
    }

    @Override
    public void registerSettings ( final String key, final Object defaultValue )
    {
        SettingsManager.registerComponent ( this, key, defaultValue );
    }

    @Override
    public void registerSettings ( final String group, final String key )
    {
        SettingsManager.registerComponent ( this, group, key );
    }

    @Override
    public <T extends DefaultValue> void registerSettings ( final String group, final String key, final Class<T> defaultValueClass )
    {
        SettingsManager.registerComponent ( this, group, key, defaultValueClass );
    }

    @Override
    public void registerSettings ( final String group, final String key, final Object defaultValue )
    {
        SettingsManager.registerComponent ( this, group, key, defaultValue );
    }

    @Override
    public void registerSettings ( final String key, final boolean loadInitialSettings, final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, key, loadInitialSettings, applySettingsChanges );
    }

    @Override
    public <T extends DefaultValue> void registerSettings ( final String key, final Class<T> defaultValueClass,
                                                            final boolean loadInitialSettings, final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, key, defaultValueClass, loadInitialSettings, applySettingsChanges );
    }

    @Override
    public void registerSettings ( final String key, final Object defaultValue, final boolean loadInitialSettings,
                                   final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, key, defaultValue, loadInitialSettings, applySettingsChanges );
    }

    @Override
    public <T extends DefaultValue> void registerSettings ( final String group, final String key, final Class<T> defaultValueClass,
                                                            final boolean loadInitialSettings, final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, group, key, defaultValueClass, loadInitialSettings, applySettingsChanges );
    }

    @Override
    public void registerSettings ( final String group, final String key, final Object defaultValue, final boolean loadInitialSettings,
                                   final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, group, key, defaultValue, loadInitialSettings, applySettingsChanges );
    }

    @Override
    public void registerSettings ( final SettingsProcessor settingsProcessor )
    {
        SettingsManager.registerComponent ( this, settingsProcessor );
    }

    @Override
    public void unregisterSettings ()
    {
        SettingsManager.unregisterComponent ( this );
    }

    @Override
    public void loadSettings ()
    {
        SettingsManager.loadComponentSettings ( this );
    }

    @Override
    public void saveSettings ()
    {
        SettingsManager.saveComponentSettings ( this );
    }

    /**
     * Returns the L&amp;F object that renders this component.
     *
     * @return LabelUI object
     */
    public DateFieldUI getUI ()
    {
        return ( DateFieldUI ) ui;
    }

    /**
     * Sets the L&amp;F object that renders this component.
     *
     * @param ui {@link com.alee.extended.date.DateFieldUI}
     */
    public void setUI ( final DateFieldUI ui )
    {
        super.setUI ( ui );
    }

    @Override
    public WebDateFieldUI getWebUI ()
    {
        return ( WebDateFieldUI ) getUI ();
    }

    @Override
    public void updateUI ()
    {
        if ( getUI () == null || !( getUI () instanceof WebDateFieldUI ) )
        {
            try
            {
                setUI ( ( WebDateFieldUI ) UIManager.getUI ( this ) );
            }
            catch ( final Throwable e )
            {
                Log.error ( this, e );
                setUI ( new WebDateFieldUI () );
            }
        }
        else
        {
            setUI ( getUI () );
        }
    }

    @Override
    public String getUIClassID ()
    {
        return StyleableComponent.datefield.getUIClassID ();
    }
}