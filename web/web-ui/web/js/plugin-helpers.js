////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * General plugin helper functions
 *
 * @constructor
 */
function PluginHelpers()
{
	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Adds or removes the CSS error highlight classes to the given field
 *
 * @param {(string|jQuery)} field - the DOM id or jQuery instance
 * @param {boolean} shouldHighlight - true to add the classes, false to remove them if they exist
 */
PluginHelpers.prototype.setErrorHighlight = function( field, shouldHighlight )
{
	g_taglib.setErrorHighlight( field, shouldHighlight );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

PluginHelpers.prototype.loadFeatureTypePlugins = function( array, listPluginsCallback, loadPluginSourceCallback )
{
	g_pluginTagLib.fetchFeatureTypePlugins( array, true, listPluginsCallback, loadPluginSourceCallback, null, null, null );

	return;
};

PluginHelpers.prototype.loadPreconditionPlugins = function( array, listPluginsCallback, loadPluginSourceCallback )
{
	g_pluginTagLib.fetchPreconditionPlugins( array, true, listPluginsCallback, loadPluginSourceCallback, null, null, null );

	return;
};

PluginHelpers.prototype.loadOutcomeEffectPlugins = function( array, listPluginsCallback, loadPluginSourceCallback )
{
	g_pluginTagLib.fetchOutcomeEffectPlugins( array, true, listPluginsCallback, loadPluginSourceCallback, null, null, null );

	return;
};

PluginHelpers.prototype.loadProjectorPlugins = function( array, listPluginsCallback, loadPluginSourceCallback )
{
	g_pluginTagLib.fetchProjectorPlugins( array, true, listPluginsCallback, loadPluginSourceCallback, null, null, null );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a form field row (two-column design, the left column holding the label, the right column holding the form field)
 *
 * @param {string} id - the unique id of the row
 * @param {number} labelWidth - the width of the label field (values 1-12)
 * @param {?string} label - the label of the field
 * @param {?string} hoverPopupText - the text of the hoverpopup to use, if any
 * @param {?jQuery} formField - the form field
 * @param {?string} trailingText - text to display to the right of the form field
 * @param {jQuery} parent - the parent jQuery instance
 *
 * @returns {jQuery} the generated row
 */
PluginHelpers.prototype.generateFormRow = function( id, labelWidth, label, hoverPopupText, formField, trailingText, parent )
{
	const row = $( '<div/>' )
		.addClass( 'form-group' )
		.addClass( 'row' );

	if( parent )
	{
		row.appendTo( parent );
	}

	const labelColumn = $( '<label/>' )
		.attr( 'for', id )
		.addClass( 'col-' + labelWidth )
		.addClass( 'col-form-label' )
		.appendTo( row );
	if( label )
	{
		labelColumn.text( label );

		if( hoverPopupText != null && hoverPopupText.length > 0 )
		{
			g_taglib.generateHelpBubble( hoverPopupText, labelColumn );
		}
	}

	const fieldColumn = $( '<div/>' )
		.addClass( 'col-' + ( 12 - labelWidth ).toString() )
		.appendTo( row );

	if( formField )
	{
		( /*** @type {jQuery} */formField )
			.appendTo( fieldColumn );
	}

	if( trailingText )
	{
		if( formField )
		{
			formField.addClass( 'd-inline-block' );
		}

		$( '<span/>' )
			.addClass( 'pl-2' )
			.text( trailingText.trim() )
			.appendTo( fieldColumn );
	}

	return row;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a dropdown box (HTML select)
 *
 * @param {?string} id - the DOM id of the dropdown
 * @param {?jQuery} parent - the parent to append the new dropdown to
 *
 * @returns {jQuery} the jQuery instance
 */
PluginHelpers.prototype.generateDropdown = function( id, parent )
{
	return g_taglib.generateDropdown( id, parent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a dropdown entry
 *
 * @param {jQuery} dropdown - the dropdown jQuery instance
 * @param {string} label - the text shown to the user
 * @param {?string} value - the value of the entry
 * @param {boolean} isSelected - true if this option should be selected
 * @param {boolean} isDisabled - true if this option should be disabled
 * @param {*} userItem - a user-defined entry for storing additional data
 * @param {?function(string, string, *)} callback - a callback function when this option is selected
 *
 * @returns {jQuery} the jQuery instance
 */
PluginHelpers.prototype.generateDropdownEntry = function( dropdown, label, value, isSelected, isDisabled, userItem, callback )
{
	return g_taglib.generateDropdownOption( dropdown, label, value, isSelected, isDisabled, userItem, callback );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @param {string} dropdownUniqueId
 * @returns {?string}
 */
PluginHelpers.prototype.getDropdownSelectedValue = function( dropdownUniqueId )
{
	return g_taglib.getDropdownSelectedValue( dropdownUniqueId );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {jQuery} dropdown
 * @param {string} buttonText
 */
PluginHelpers.prototype.setDropdownButtonText = function( dropdown, buttonText )
{
	g_taglib.setDropdownButtonText( dropdown, buttonText );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} id
 * @param {?string} currentValue
 * @param {boolean} allowBlank - True to allow the value to be blank
 * @param {jQuery} parent
 *
 * @returns {jQuery}
 */
PluginHelpers.prototype.createTextInput = function( id, currentValue, allowBlank, parent )
{
	return g_taglib.createTextInput( id, currentValue, allowBlank, parent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} id
 * @param {?number} currentValue
 * @param {boolean} allowBlank - True to allow the value to be blank
 * @param {jQuery} parent
 *
 * @returns {jQuery}
 */
PluginHelpers.prototype.createIntegerInput = function( id, currentValue, allowBlank, parent )
{
	return g_taglib.createIntegerInput( id, currentValue, allowBlank, parent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} id
 * @param {?number} currentValue
 * @param {number} decimalPlaces
 * @param {boolean} allowBlank - True to allow the value to be blank
 * @param {jQuery} parent
 *
 * @returns {jQuery}
 */
PluginHelpers.prototype.createDecimalInput = function( id, currentValue, decimalPlaces, allowBlank, parent )
{
	return g_taglib.createDecimalInput( id, currentValue, decimalPlaces, allowBlank, parent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string|jQuery} field - the DOM id or the jQuery field
 *
 * @returns {string} The trimmed value
 */
PluginHelpers.prototype.getTextInputValue = function( field )
{
	return g_taglib.getTextInputValue( field );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {(string|jQuery)} field - the DOM id or the jQuery field
 * @param {string} value - the string to set
 */
PluginHelpers.prototype.setTextInputValue = function( field, value )
{
	g_taglib.setTextInputValue( field, value );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string|jQuery} field - the DOM id or the jQuery instance
 * @param {boolean} allowBlankAsUndefined
 *
 * @returns {(number|null|undefined)} The parsed integer if it was valid, null if it was not, or undefined if blank and allowed to be blank
 */
PluginHelpers.prototype.getIntegerInputValue = function( field, allowBlankAsUndefined )
{
	return g_taglib.getIntegerInputValue( field, allowBlankAsUndefined );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string|jQuery} field - the DOM id or the jQuery instance
 * @param {boolean} allowBlankAsUndefined
 *
 * @returns {(number|null|undefined)} The parsed integer if it was valid, null if it was not, or undefined if blank and allowed to be blank
 */
PluginHelpers.prototype.getDecimalInputValue = function( field, allowBlankAsUndefined )
{
	return g_taglib.getDecimalInputValue( field, allowBlankAsUndefined );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string|jQuery} field - the DOM id or the jQuery instance
 * @param {?boolean} currentValue - the current value
 * @param {string} labelTrue - the label to use for the True state
 * @param {string} labelFalse - the label to use for the False state
 * @param {?jQuery} parent - the parent to append the new dropdown to
 *
 * @returns {jQuery}
 */
PluginHelpers.prototype.generateBooleanField = function( field, currentValue, labelTrue, labelFalse, parent )
{
	return g_taglib.generateBooleanField( field, currentValue, labelTrue, labelFalse, parent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @param {string|jQuery} field - the DOM id or the jQuery field
 *
 * @returns {boolean}
 */
PluginHelpers.prototype.getBooleanFieldValue = function( field )
{
	return g_taglib.getBooleanFieldValue( field );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a Scenario Explorer button
 *
 * @param {?string} id - the DOM id
 * @param {?string} text - the text to display
 * @param {?string} cssClasses - the space-separated CSS classes to add
 * @param {?string} fontAwesomeClasses - the font-awesome classes to add
 * @param {?jQuery} parent - the parent element to add the new button, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
PluginHelpers.prototype.generateButton = function( id, text, cssClasses, fontAwesomeClasses, parent )
{
	return g_taglib.generateButton( id, text, cssClasses, fontAwesomeClasses, parent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a table
 *
 * @param {?string} id - the DOM id to assign
 * @param {?jQuery} parent - the parent element to add the new table, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
PluginHelpers.prototype.generateTable = function( id, parent )
{
	return g_taglib.generateTable( id, parent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a table header
 *
 * @param {?jQuery} table - the table to append the new table head, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
PluginHelpers.prototype.generateTableHead = function( table )
{
	return g_taglib.generateTableHead( table );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a table body
 *
 * @param {?string} id - the DOM id to assign
 * @param {?jQuery} table - the table to append the new table body, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
PluginHelpers.prototype.generateTableBody = function( id, table )
{
	return g_taglib.generateTableBody( id, table );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a table row
 *
 * @param {?jQuery} parent - the thead or tbody to append the row, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
PluginHelpers.prototype.generateTableRow = function( parent )
{
	return g_taglib.generateTableRow( parent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a table column header
 *
 * @param {string} text - the text to use
 * @param {?jQuery} tr - the table row to append the new column header, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
PluginHelpers.prototype.generateTableColumnHeader = function( text, tr )
{
	return g_taglib.generateTableColumnHeader( text, tr );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a table cell
 *
 * @param {?string} text - the text to use
 * @param {jQuery} tr - the table row to append the new column cell, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
PluginHelpers.prototype.generateTableCell = function( text, tr )
{
	return g_taglib.generateTableCell( text, tr );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a help sidebar entry
 *
 * @param {string} title
 * @param {string} description
 * @param {jQuery} helpSideBar
 */
PluginHelpers.prototype.generateHelpSideBarEntry = function( title, description, helpSideBar )
{
	g_taglib.generateHelpSideBarEntry( title, description, helpSideBar );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a plugin sandbox and runs the entry user interface
 *
 * @param {jQuery} configHolder
 * @param {ScenarioExplorerPlugin} plugin
 * @param {Object} pluginConfig
 * @param {string} currentValue
 */
PluginHelpers.prototype.generateEntryUserInterfacePluginSandbox = function( configHolder, plugin, pluginConfig, currentValue )
{
	g_pluginTagLib.generateEntryUserInterface( configHolder, plugin, pluginConfig, currentValue );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the value from the plugin sandbox
 *
 * @param {jQuery} configHolder
 * @param {ScenarioExplorerPlugin} plugin
 * @param {Object} pluginConfig
 *
 * @returns {?string} the value to serialize, or null if the value was invalid and should not be accepted
 */
PluginHelpers.prototype.getEntryUserInterfacePluginSandboxData = function( configHolder, plugin, pluginConfig )
{
	return g_pluginTagLib.getEntryUserInterfaceData( configHolder, plugin, pluginConfig );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
