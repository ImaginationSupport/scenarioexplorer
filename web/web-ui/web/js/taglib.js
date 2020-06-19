////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Constructor
 *
 * @constructor
 */
function TagLib()
{
	/**
	 * Holds the available icons
	 *
	 * @type {Object.<string, string>}
	 */
	this.Icons = {
		PROJECT : 'fa-project-diagram',
		VIEW : 'fa-book-open',

		CREATE : 'fa-plus',
		UPDATE : 'fa-edit', // 'pen-square'
		DELETE : 'fa-trash', // 'fa-minus-circle'

		EXPORT : 'fa-file-export',
		DOWNLOAD_FILE : 'fa-download',
		CLONE : 'fa-clone',
		CREATE_TEMPLATE : 'fa-cubes',
		IMPORT : 'fa-file-import',

		SAVE : 'fa-check',
		CANCEL : 'fa-times',

		OWNER : 'fa-user-circle',

		ASSIGNED : 'fa-paperclip',

		CONDITIONING_EVENT : 'fa-code-merge fa-rotate-270',
		CONDITIONING_EVENT_PRECONDITION : 'fa-clipboard-check',
		CONDITIONING_EVENT_OUTCOME : 'fa-flag-checkered',
		CONDITIONING_EVENT_OUTCOME_EFFECT : 'fa-star-exclamation',

		EXPAND : 'fa-plus-circle',
		COLLAPSE : 'fa-minus-circle',

		HELP_BUBBLE : 'fa-question-circle',
		NOTIFICATION : 'fa-exclamation-triangle',

		DRAG_DROP_TARGET : 'fa-upload',
		DRAG_DROP_READY : 'fa-bullseye-arrow'

		// NOTE: when changing these, also make sure to change the JSP and taglib
	};

	/**
	 * Holds the string to use in place of null when saving into the DOM
	 *
	 * @private
	 * @type {string}
	 */
	this.NULL_STRING = '=====NULL=====';

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Adds or removes the CSS error highlight classes to the given field
 *
 * @param {string|jQuery} field - the DOM id or jQuery instance
 * @param {boolean} shouldHighlight - true to add the classes, false to remove them if they exist
 */
TagLib.prototype.setErrorHighlight = function( field, shouldHighlight )
{
	let fieldInstance;

	if( typeof field === 'string' )
	{
		fieldInstance = $( '#' + field );
	}
	else
	{
		fieldInstance = /** @type {jQuery} */( field );
	}

	if( shouldHighlight )
	{
		fieldInstance
			.addClass( 'border' )
			.addClass( 'border-danger' )
			.addClass( 'text-danger' );
	}
	else
	{
		fieldInstance
			.removeClass( 'border' )
			.removeClass( 'border-danger' )
			.removeClass( 'text-danger' );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a link
 *
 * @param {?string} id - the DOM id
 * @param {?string} text - the text to display
 * @param {string} uri - the URI to launch
 * @param {boolean} launchSeparateTab - true to launch the link in a separate tab, otherwise use the current tab
 * @param {?jQuery} parent - the parent element to add the new link, or null to skip
 *
 * @returns {jQuery}
 */
TagLib.prototype.generateLink = function( id, text, uri, launchSeparateTab, parent )
{
	const link = $( '<a/>', { 'href' : uri } );

	if( id )
	{
		link.attr( 'id', id );
	}

	if( launchSeparateTab )
	{
		link.attr( 'target', '_blank' );
	}

	if( text )
	{
		link.text( text );
	}

	if( parent )
	{
		link.appendTo( parent );
	}

	return link;
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
TagLib.prototype.generateButton = function( id, text, cssClasses, fontAwesomeClasses, parent )
{
	const button = $( '<button/>' )
		.attr( 'type', 'button' )
		.addClass( 'btn' )
		.addClass( 'btn-primary' );

	if( id )
	{
		button.attr( 'id', id );
	}

	if( fontAwesomeClasses )
	{
		$( '<i/>' )
			.addClass( 'fas' )
			.addClass( fontAwesomeClasses )
			.appendTo( button );

	}

	if( text )
	{
		if( fontAwesomeClasses )
		{
			$( '<span/>' )
				.text( text )
				.addClass( 'pl-2' )
				.appendTo( button );
		}
		else
		{
			button.text( text );
		}
	}

	if( cssClasses )
	{
		button.addClass( cssClasses );
	}

	if( parent )
	{
		button.appendTo( parent );
	}

	return button;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the button text
 *
 * @param {jQuery|string} button - the jQuery instance or the DOM id
 * @param {?string} text - the text to update, or null to leave unchanged
 * @param {?string} iconClassName - the FontAwesome class name to set, or null to remain unchanged
 */
TagLib.prototype.updateButtonText = function( button, text, iconClassName )
{
	const buttonInstance = typeof button === 'string'
		? $( '#' + button )
		: button;

	if( text )
	{
		buttonInstance.find( 'span' ).text( text );
	}

	if( iconClassName )
	{
		const icon = buttonInstance.find( 'i' );

		const currentClasses = icon.attr( 'class' );
		if( currentClasses )
		{
			const currentClassesSplit = currentClasses.toString().split( ' ' );
			for( let i = 0; i < currentClassesSplit.length; ++i )
			{
				if( currentClassesSplit[ i ].startsWith( 'fa-' ) )
				{
					icon.removeClass( currentClassesSplit[ i ] );
				}
			}
		}

		icon.addClass( iconClassName );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

TagLib.prototype.initializeDropdownButton = function( button )
{
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

TagLib.prototype.addDropdownButtonChoice = function( dropdown, label, callback )
{
	const dropdownMenu = dropdown.find( 'div' ).first();

	console.log( dropdownMenu ); // TODO remove!

	if( !label )
	{
		return;
	}

	const dropdownItem = $( '<a/>' )
		.text( label )
		.addClass( 'dropdown-item' )
		.appendTo( dropdownMenu );

	if( callback )
	{
		dropdownItem.on( 'click', callback );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

TagLib.prototype.generateCheckbox = function( id, label, callback, parent )
{
	const holder = $( '<div/>' )
		.addClass( 'd-inline-block' )
		.addClass( 'form-check' )
		.addClass( 'ml-4' );

	if( parent )
	{
		holder.appendTo( parent );
	}

	const checkbox = $( '<input/>', { 'type' : 'checkbox' } )
		.addClass( 'form-check-input' )
		.addClass( 'mt-2' )
		.appendTo( holder );

	// if an id wasn't given, but we want a label, we need to generate an id, so use the label and strip all non-word characters
	if( label && !id )
	{
		id = 'checkbox-' + g_util.replaceAll( label, '[^\w]', '' );
	}

	if( id )
	{
		checkbox.attr( 'id', id );
	}

	if( callback )
	{
		checkbox.on( 'change', callback );
	}

	if( label )
	{
		$( '<label/>' )
			.attr( 'for', id )
			.addClass( 'form-check-label' )
			.text( label )
			.appendTo( holder );
	}

	return holder;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the value of the selected radio button
 *
 * @param {string} radioGroupName
 *
 * @returns {?string}
 */
TagLib.prototype.getSelectedRadioButton = function( radioGroupName )
{
	return $( 'input[name=' + radioGroupName + ']:checked' ).val();
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a FontAwesome icon
 *
 * @param {?string} id - the DOM id
 * @param {string} fontAwesomeClasses - the FontAwesome CSS class name
 * @param {?string} title - the title text to use, if any
 * @param {?jQuery} parent - the parent to add the icon, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
TagLib.prototype.generateStandaloneIcon = function( id, fontAwesomeClasses, title, parent )
{
	const icon = $( '<i/>' )
		.addClass( 'fas' )
		.addClass( fontAwesomeClasses );

	if( id )
	{
		icon.attr( 'id', id );
	}

	if( title != null && title.length > 0 )
	{
		icon.attr( 'title', title );
	}

	if( parent )
	{
		icon.appendTo( parent );
	}

	return icon;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a text element with a FontAwesome icon
 *
 * @param {string} iconClass - the FontAwesome CSS class name
 * @param {string} text - the text
 * @param {?jQuery} parent - the jQuery element to append the elements, or null to skip
 * @param {boolean} useEllipses - True to use text truncation and display:block for the elements, false to use span's
 *
 * @returns {jQuery} the jQuery instance
 */
TagLib.prototype.generateIconWithText = function( iconClass, text, parent, useEllipses )
{
	let holder;
	if( useEllipses )
	{
		holder = $( '<div/>' );

		$( '<i/>' )
			.addClass( 'fas' )
			.addClass( iconClass )
			.addClass( 'float-left' )
			.addClass( 'mt-1' )
			.appendTo( holder );

		const div = $( '<div/>' )
			.addClass( 'pl-2' )
			.addClass( 'text-truncate' )
			.text( text )
			.appendTo( holder );

		this.addHoverOverFullTextDisplay( div );

		if( parent )
		{
			holder.appendTo( parent );
		}
	}
	else
	{
		holder = $( '<span/>' );

		$( '<i/>' )
			.addClass( 'fas' )
			.addClass( iconClass )
			.appendTo( holder );

		$( '<span/>' )
			.addClass( 'pl-2' )
			.text( text )
			.appendTo( holder );

		if( parent )
		{
			holder.appendTo( parent );
		}
	}

	return holder;
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
TagLib.prototype.generateTable = function( id, parent )
{
	const table = $( '<table/>' )
		.addClass( 'table' );

	if( id )
	{
		table.attr( 'id', id );
	}

	if( parent )
	{
		table.appendTo( parent );
	}

	return table;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a table header
 *
 * @param {?jQuery} table - the table to append the new table head, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
TagLib.prototype.generateTableHead = function( table )
{
	const thead = $( '<thead/>' );

	if( table )
	{
		thead.appendTo( table );
	}

	return thead;
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
TagLib.prototype.generateTableBody = function( id, table )
{
	const tbody = $( '<tbody/>' );

	if( id )
	{
		tbody.attr( 'id', id );
	}

	if( table )
	{
		tbody.appendTo( table );
	}

	return tbody;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a table row
 *
 * @param {?jQuery} parent - the thead or tbody to append the row, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
TagLib.prototype.generateTableRow = function( parent )
{
	const tr = $( '<tr/>' );

	if( parent )
	{
		tr.appendTo( parent );
	}

	return tr;
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
TagLib.prototype.generateTableColumnHeader = function( text, tr )
{
	const th = $( '<th/>' )
		.addClass( 'p-2' )
		.addClass( 'bg-secondary' )
		.addClass( 'text-light' )
		.attr( 'scope', 'col' )
		.text( text );

	if( tr )
	{
		th.appendTo( tr );
	}

	return tr;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a table cell
 *
 * @param {string|jQuery|null} content - the text to use
 * @param {jQuery} tr - the table row to append the new column cell, or null to skip
 *
 * @returns {jQuery} the jQuery instance
 */
TagLib.prototype.generateTableCell = function( content, tr )
{
	const td = $( '<td/>' )
		.addClass( 'p-2' )
		.addClass( 'align-middle' );

	if( content )
	{
		if( typeof content === 'string' )
		{
			td.text( content ).css( 'white-space', 'pre-wrap' );
		}
		else
		{
			td.append( content );
		}
	}

	if( tr )
	{
		td.appendTo( tr );
	}

	return td;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a help bubble
 *
 * @param {string} text
 * @param {?jQuery} parent
 *
 * @returns {jQuery}
 */
TagLib.prototype.generateHelpBubble = function( text, parent )
{
	return this.generateStandaloneIcon( null, this.Icons.HELP_BUBBLE, text, parent )
		.addClass( 'text-primary' )
		.addClass( 'ml-1' );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a help sidebar entry
 *
 * @param {string} title
 * @param {string} description
 * @param {jQuery} helpSideBar
 */
TagLib.prototype.generateHelpSideBarEntry = function( title, description, helpSideBar )
{
	$( '<div/>' )
		.addClass( 'help-sidebar-title' )
		.text( title )
		.appendTo( helpSideBar );

	$( '<div/>' )
		.addClass( 'help-sidebar-description' )
		.text( description )
		.appendTo( helpSideBar );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @param {Notification} notification
 * @param {?jQuery} parent
 */
TagLib.prototype.generateNotification = function( notification, parent )
{
	return this.generateIconWithText( this.Icons.NOTIFICATION + ' text-primary', notification.description, parent, false );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a button bar
 *
 * @param {?jQuery} parent - the parent element to append the button bar
 *
 * @returns {jQuery} the jQuery instance
 */
TagLib.prototype.generateButtonBar = function( parent )
{
	const buttonBar = $( '<div/>', { 'role' : 'group', 'aria-label' : 'button group', 'data-toggle' : 'buttons' } )
		.addClass( 'btn-group' )
		.addClass( 'btn-group-toggle' );

	if( parent )
	{
		buttonBar.appendTo( parent );
	}

	return buttonBar;
};

/**
 * Generates a button bar button
 *
 * @param {string} text - the text to display
 * @param {boolean} isSelected - true if this button should be selected, otherwise false
 * @param {function()} callback -the callback to run when the button is clicked
 * @param {jQuery} buttonBar - the parent element to append the button to
 *
 * @returns {jQuery}
 */
TagLib.prototype.generateButtonBarButton = function( text, isSelected, callback, buttonBar )
{
	const button = $( '<button/>', { 'type' : 'button' } )
		.addClass( 'btn' )
		.on( 'click', this.buttonBarButtonClicked.bind( this ) );

	if( isSelected )
	{
		button.addClass( 'btn-primary' );
	}
	else
	{
		button.addClass( 'btn-outline-primary' )
			.addClass( 'bg-white' );
	}

	if( text )
	{
		button.text( text );
	}

	if( callback )
	{
		button.on( 'click', callback );
	}

	if( buttonBar )
	{
		button.appendTo( buttonBar );
	}

	return button;
};

TagLib.prototype.buttonBarButtonClicked = function( e )
{
	const button = $( e.target );

	button
		.parent()
		.children()
		.removeClass( 'btn-primary' )
		.addClass( 'btn-outline-primary' )
		.addClass( 'bg-white' );

	button.addClass( 'btn-primary' )
		.removeClass( 'btn-outline-primary' )
		.removeClass( 'bg-white' );

	return;
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
TagLib.prototype.generateDropdown = function( id, parent )
{
	const dropdown = $( '<select/>' )
		.addClass( 'form-control' );

	if( id )
	{
		dropdown.attr( 'id', id );
	}

	if( parent )
	{
		dropdown.appendTo( parent );
	}

	this.attachDropdownEventHandlers( dropdown );

	return dropdown;
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
TagLib.prototype.generateDropdownOption = function( dropdown, label, value, isSelected, isDisabled, userItem, callback )
{
	const option = $( '<option/>' )
		.val( value === null || value === undefined ? this.NULL_STRING : value );

	if( dropdown )
	{
		option.appendTo( dropdown );
	}

	if( label )
	{
		option.text( label );
	}

	if( isSelected )
	{
		option.attr( 'selected', 'selected' );
	}

	if( isDisabled )
	{
		option.attr( 'disabled', true );
	}

	if( userItem )
	{
		option.data( 'userItem', userItem );
	}

	if( callback )
	{
		option.on( 'click', callback.bind( this ) );
	}

	return option;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Attaches keyboard events to call the click handlers from
 *
 * @param {string|jQuery} dropdown - the dropdown DOM id or jQuery dropdown instance
 */
TagLib.prototype.attachDropdownEventHandlers = function( dropdown )
{
	const dropdownInstance = typeof dropdown === 'string'
		? $( '#' + dropdown )
		: dropdown;

	dropdownInstance.on( 'keyup change', this._dropdownKeyHandler.bind( this, dropdown ) );

	return;
};

/**
 * Internal helper function to run the event handler
 *
 * @private
 *
 * @param {jQuery} dropdown - the jquery dropdown instance
 */
TagLib.prototype._dropdownKeyHandler = function( dropdown )
{
	dropdown.find( ':selected' ).trigger( 'click' );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string|jQuery} dropdown - the dropdown DOM id or jQuery dropdown instance
 *
 * @returns {?string} the current value
 */
TagLib.prototype.getDropdownSelectedValue = function( dropdown )
{
	let rawValue = null;
	if( typeof dropdown === 'string' )
	{
		rawValue = $( '#' + dropdown ).find( ':selected' ).val();
	}
	else
	{
		const dropdownInstance = /** @type {jQuery} */( dropdown );
		rawValue = dropdownInstance.find( ':selected' ).val();
	}

	return rawValue === undefined || rawValue === this.NULL_STRING
		? null
		: rawValue;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Selects the given dropdown item
 *
 * @param {string|jQuery} dropdown - the dropdown DOM id or jQuery dropdown instance
 * @param {?string} selectedValue - the valid of the item to select
 */
TagLib.prototype.setDropdownSelectedItem = function( dropdown, selectedValue )
{
	if( typeof dropdown === 'string' )
	{
		$( '#' + dropdown ).val( selectedValue ? selectedValue : this.NULL_STRING );
	}
	else
	{
		const dropdownInstance = /** @type {jQuery} */( dropdown );
		dropdownInstance.val( selectedValue ? selectedValue : this.NULL_STRING );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} id - the DOM id
 * @param {?string} currentValue - the current value
 * @param {boolean} allowBlank - True to allow the value to be blank
 * @param {?jQuery} parent - the parent to append the new field to
 *
 * @returns {jQuery} the jQuery instance
 */
TagLib.prototype.createTextInput = function( id, currentValue, allowBlank, parent )
{
	const input = $( '<input/>' )
		.attr( 'type', 'text' )
		.addClass( 'form-control' )
		.data( 'allowBlank', allowBlank )
		.on( 'input propertychange paste', this.textInputUpdatedCallback.bind( this ) );

	if( id )
	{
		input.attr( 'id', id );
	}

	if( currentValue !== null && currentValue !== undefined )
	{
		input.val( currentValue.toString() );
	}

	if( parent )
	{
		input.appendTo( parent );
	}

	return input;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} id - the DOM id
 * @param {?number} currentValue - the current value
 * @param {boolean} allowBlank - True to allow the value to be blank
 * @param {?jQuery} parent - the parent to append the new field to
 *
 * @returns {jQuery} the jQuery instance
 */
TagLib.prototype.createIntegerInput = function( id, currentValue, allowBlank, parent )
{
	const input = $( '<input/>' )
		.attr( 'type', 'text' )
		.addClass( 'form-control' )
		.addClass( 'text-right' )
		.addClass( 'w-50' )
		.data( 'allowBlank', allowBlank )
		.on( 'input propertychange paste', this.numberInputUpdatedCallback.bind( this, true ) );

	if( id )
	{
		input.attr( 'id', id );
	}

	if( currentValue !== null && currentValue !== undefined )
	{
		input.val( currentValue.toString() );
	}

	if( parent )
	{
		input.appendTo( parent );
	}

	return input;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} id - the DOM id
 * @param {?number} currentValue - the current value
 * @param {number} decimalPlaces - the number of decimal places to show
 * @param {boolean} allowBlank - True to allow the value to be blank
 * @param {?jQuery} parent - the parent to append the new field to
 *
 * @returns {jQuery}
 */
TagLib.prototype.createDecimalInput = function( id, currentValue, decimalPlaces, allowBlank, parent )
{
	const input = $( '<input/>' )
		.attr( 'type', 'text' )
		.attr( 'id', id )
		.addClass( 'form-control' )
		.addClass( 'text-right' )
		.addClass( 'w-50' )
		.data( 'allowBlank', allowBlank )
		.on( 'input propertychange paste', this.numberInputUpdatedCallback.bind( this, false ) );

	if( currentValue !== null && currentValue !== undefined )
	{
		input.val( currentValue.toFixed( decimalPlaces ).toString() );
	}
	if( parent !== null )
	{
		input.appendTo( parent );
	}

	return input;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {jQuery.Event} e - the jQuery event
 * @private
 */
TagLib.prototype.textInputUpdatedCallback = function( e )
{
	const control = $( e.target );
	const allowBlank = control.data( 'allowBlank' );

	const valueRaw = control.val().toString().trim();

	this.setErrorHighlight( control, valueRaw.length === 0 && !allowBlank );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {boolean} isInteger - true if this is an integer field, false if it is a decimal field
 * @param {jQuery.Event} e - the jQuery event
 * @private
 */
TagLib.prototype.numberInputUpdatedCallback = function( isInteger, e )
{
	const control = $( e.target );
	const allowBlank = control.data( 'allowBlank' );

	const valueRaw = /** @type {string} */( control.val() );

	let isValid = false;
	if( valueRaw.length === 0 && allowBlank )
	{
		isValid = true;
	}
	else
	{
		isValid = isInteger
			? g_util.verifyInteger( valueRaw )
			: g_util.verifyFloat( valueRaw );
	}

	this.setErrorHighlight( control, !isValid );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string|jQuery} field - the DOM id or the jQuery field
 *
 * @returns {string} The trimmed value
 */
TagLib.prototype.getTextInputValue = function( field )
{
	if( typeof field === 'string' )
	{
		return $( '#' + field ).val().trim();
	}
	else
	{
		let fieldInstance = /** @type {jQuery} */( field );
		return fieldInstance.val().toString().trim();
	}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string|jQuery} field - the DOM id or the jQuery field
 * @param {string} value - the string to set
 */
TagLib.prototype.setTextInputValue = function( field, value )
{
	if( typeof field === 'string' )
	{
		$( '#' + field ).val( value.trim() );
	}
	else
	{
		let fieldInstance = /** @type {jQuery} */( field );
		fieldInstance.val( value.trim() );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string|jQuery} field - the DOM id or the jQuery field
 * @param {boolean} allowBlankAsUndefined - True to allow blank values
 *
 * @returns {number|null|undefined} The parsed integer if it was valid, null if it was not, or undefined if blank and allowed to be blank
 */
TagLib.prototype.getIntegerInputValue = function( field, allowBlankAsUndefined )
{
	let valueRaw = null;
	if( typeof field === 'string' )
	{
		valueRaw = $( '#' + field ).val().trim();
	}
	else
	{
		let fieldInstance = /** @type {jQuery} */( field );
		valueRaw = fieldInstance.val().trim();
	}

	if( allowBlankAsUndefined && valueRaw.length === 0 )
	{
		return undefined;
	}

	if( valueRaw.length > 0 && g_util.verifyInteger( valueRaw ) )
	{
		return parseInt( valueRaw, 10 );
	}
	else
	{
		return null;
	}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string|jQuery} field - the DOM id or the jQuery field
 * @param {boolean} allowBlankAsUndefined - True to allow blank values
 *
 * @returns {number|null|undefined} The parsed decimal if it was valid, null if it was not, or undefined if blank and allowed to be blank
 */
TagLib.prototype.getDecimalInputValue = function( field, allowBlankAsUndefined )
{
	let valueRaw = null;
	if( typeof field === 'string' )
	{
		valueRaw = $( '#' + field ).val().trim();
	}
	else
	{
		let fieldInstance = /** @type {jQuery} */( field );
		valueRaw = fieldInstance.val().trim();
	}

	if( allowBlankAsUndefined && valueRaw.length === 0 )
	{
		return undefined;
	}

	if( valueRaw.length > 0 && g_util.verifyFloat( valueRaw ) )
	{
		return parseFloat( valueRaw );
	}
	else
	{
		return null;
	}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} id - the DOM id
 * @param {boolean} currentValue - the current value
 * @param {string} labelTrue - the label to use for the True state
 * @param {string} labelFalse - the label to use for the False state
 * @param {?jQuery} parent - the parent to append the new dropdown to
 *
 * @returns {jQuery}
 */
TagLib.prototype.generateBooleanField = function( id, currentValue, labelTrue, labelFalse, parent )
{
	const holder = $( '<div/>' )
		.attr( 'id', id )
		.attr( 'data-toggle', 'buttons' )
		.addClass( 'btn-group' )
		.addClass( 'btn-group-toggle' )
		.data( 'current-value', currentValue );

	if( parent )
	{
		holder.appendTo( parent );
	}

	const btnTrue = $( '<label/>' )
		.addClass( 'btn' )
		.data( 'button-value', true )
		.appendTo( holder );
	$( '<input/>' )
		.attr( 'type', 'radio' )
		.attr( 'name', id + '-bar' )
		.attr( 'id', id + '-true' )
		.attr( 'autocomplete', 'off' )
		.appendTo( btnTrue );
	$( '<span/>' )
		.text( labelTrue )
		.appendTo( btnTrue );

	const btnFalse = $( '<label/>' )
		.addClass( 'btn' )
		.data( 'button-value', false )
		.appendTo( holder );
	$( '<input/>' )
		.attr( 'type', 'radio' )
		.attr( 'name', id + '-bar' )
		.attr( 'id', id + '-false' )
		.attr( 'autocomplete', 'off' )
		.appendTo( btnFalse );
	$( '<span/>' )
		.text( labelFalse )
		.appendTo( btnFalse );

	btnTrue.on( 'click', this.clickBoolean.bind( this, holder, btnTrue, btnFalse ) );
	btnFalse.on( 'click', this.clickBoolean.bind( this, holder, btnFalse, btnTrue ) );

	if( currentValue )
	{
		this.clickBoolean( holder, btnTrue, btnFalse );
	}
	else
	{
		this.clickBoolean( holder, btnFalse, btnTrue );
	}

	return holder;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks one side of the boolean
 *
 * @param {jQuery} holder - the holder control
 * @param {jQuery} buttonClicked - the button that was clicked
 * @param {jQuery} previousButton - the button that was previously selected
 * @private
 */
TagLib.prototype.clickBoolean = function( holder, buttonClicked, previousButton )
{
	this.setBooleanActive( previousButton, false );
	this.setBooleanActive( buttonClicked, true );

	holder.data( 'current-value', buttonClicked.data( 'button-value' ) );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the styles of the given boolean button
 *
 * @param {jQuery} btn - the button to update
 * @param {boolean} isActive - True if this button show be shown as active
 * @private
 */
TagLib.prototype.setBooleanActive = function( btn, isActive )
{
	btn.toggleClass( 'btn-primary', isActive )
		.toggleClass( 'btn-active', isActive )
		.toggleClass( 'btn-light', !isActive )
		.toggleClass( 'border', !isActive )
		.toggleClass( 'border-primary', !isActive );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @param {string|jQuery} field - the DOM id or the jQuery field
 *
 * @returns {boolean}
 */
TagLib.prototype.getBooleanFieldValue = function( field )
{

	if( typeof field === 'string' )
	{
		return /** @type {boolean} */( $( '#' + field ).data( 'current-value' ) );
	}
	else
	{
		let fieldInstance = /** @type {jQuery} */( field );
		return /** @type {boolean} */( fieldInstance.data( 'current-value' ) );
	}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a color swatch for the given color
 *
 * @param {ScenarioExplorerColor|string} color - the color to display
 * @param {boolean} showLeadingText - true to show the RGB value before the color swatch
 * @param {?jQuery} parent - the parent to append the color swatch
 *
 * @returns {jQuery} the color swatch
 */
TagLib.prototype.generateColorSwatch = function( color, showLeadingText, parent )
{
	const holder = $( '<span/>' );

	if( parent )
	{
		holder.appendTo( parent );
	}

	const rgb = typeof color === 'string'
		? color
		: g_util.formatColor( /** @type {ScenarioExplorerColor} */( color ) );

	if( showLeadingText )
	{
		$( '<span/>' )
			.text( rgb )
			.appendTo( holder );
	}

	$( '<span/>' )
		.addClass( 'color-swatch' )
		.css( 'background-color', rgb )
		.appendTo( holder );

	return holder;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Adds a hover over title element only if the element is not large enough to display the full text
 *
 * @param {jQuery} element
 */
TagLib.prototype.addHoverOverFullTextDisplay = function( element )
{
	if( element !== null )
	{
		element.on( 'mouseenter', this._hoverOverFullTextDisplayHelper )
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Helper function to add the tooltip of the full text when we are truncating the text
 *
 * @private
 *
 * @this jQuery
 */
TagLib.prototype._hoverOverFullTextDisplayHelper = function()
{
	const entry = $( this );
	if( this.offsetWidth < this.scrollWidth && !entry.attr( 'title' ) )
	{
		entry.attr( 'title', entry.text() );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a file input control
 *
 * @param {?string} id - the DOM id
 * @param {?string} labelText - the text to show inside the control
 * @param {?function(string,string)} onSelectCallback - the function to run when the file is selected
 * @param {?jQuery} parent - the parent element
 */
TagLib.prototype.generateFileBrowserField = function( id, labelText, onSelectCallback, parent )
{
	const holder = $( '<div/>' )
		.addClass( 'custom-file' );

	if( parent )
	{
		holder.appendTo( parent );
	}

	const fileControl = $( '<input/>' )
		.attr( 'type', 'file' )
		.addClass( 'custom-file-input' )
		.appendTo( holder );

	if( id )
	{
		fileControl.attr( 'id', id );
	}

	if( onSelectCallback )
	{
		fileControl.on( 'change', this.onFileBrowserChange.bind( this, fileControl, onSelectCallback ) );
	}

	$( '<label/>' )
		.attr( 'for', id )
		.addClass( 'custom-file-label' )
		.text( labelText )
		.appendTo( holder );

	// TODO update the other <label>

	return holder;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery} fileControl
 * @param {function(string,string)} userCallback
 */
TagLib.prototype.onFileBrowserChange = function( fileControl, userCallback )
{
	const inputFiles = fileControl.get( 0 ).files;
	if( inputFiles.length === 0 )
	{
		return;
	}

	if( userCallback )
	{
		for( let i = 0; i < inputFiles.length; ++i )
		{
			let importReader = new FileReader();
			importReader.onloadend = this.onFileBrowserFileContentsLoaded.bind( this, userCallback, inputFiles[ i ].name );
			importReader.readAsText( inputFiles[ i ] );
		}
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {?function(string,string)} userCallback
 * @param {string} filename
 * @param {jQuery.Event} e
 */
TagLib.prototype.onFileBrowserFileContentsLoaded = function( userCallback, filename, e )
{
	if( userCallback )
	{
		userCallback( filename, e.target.result.toString() );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a collapsed section header
 *
 * @param {string} bodyId
 * @param {string} headerText
 * @param {boolean} initiallyExpanded
 * @param {?jQuery} parent
 * @returns {jQuery}
 */
TagLib.prototype.generateCollapsibleSectionHeader = function( bodyId, headerText, initiallyExpanded, parent )
{
	const holder = $( '<div/>' )
		.addClass( 'clickable' )
		.addClass( 'mb-1' )
		.on( 'click', this.toggleCollapsibleSection.bind( this, bodyId ) );

	this.generateStandaloneIcon( bodyId + '-icon', initiallyExpanded ? this.Icons.COLLAPSE : this.Icons.EXPAND, null, holder )
		.addClass( 'text-primary' );

	$( '<span/>' )
		.addClass( 'ml-2' )
		.addClass( 'h3' )
		.text( headerText )
		.appendTo( holder );

	if( bodyId )
	{
		holder.data( 'body-id', bodyId );
	}

	if( parent )
	{
		holder.appendTo( parent );
	}

	return holder;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Toggles a collapsible section
 *
 * @param {string} bodyId - the title element that owns the collapsible body
 */
TagLib.prototype.toggleCollapsibleSection = function( bodyId )
{
	const bodyElement = $( '#' + bodyId );
	const currentlyExpanded = bodyElement.is( ':visible' );
	const iconElement = $( '#' + bodyId + '-icon' );

	// toggle the body
	bodyElement.toggle( 200 );

	// toggle the icon
	if( currentlyExpanded )
	{
		iconElement.removeClass( 'fa-minus-circle' );
		iconElement.addClass( 'fa-plus-circle' );
	}
	else
	{
		iconElement.removeClass( 'fa-plus-circle' );
		iconElement.addClass( 'fa-minus-circle' );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the accordions in the given holder
 *
 * @param {string|jQuery} accordionHolder - the jQuery instance or DOM id of the accordion holder
 */
TagLib.prototype.initializeAccordion = function( accordionHolder )
{
	const accordionHolderElement = typeof accordionHolder === 'string'
		? $( '#' + accordionHolder.toString() )
		: accordionHolder;

	// initialize each accordion
	accordionHolderElement.find( '.card-header' ).each( this._initializeAccordionHelper.bind( this, accordionHolderElement ) );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery} accordionHolderElement
 * @param {number} index
 * @param {jQuery} headerElement
 */
TagLib.prototype._initializeAccordionHelper = function( accordionHolderElement, index, headerElement )
{
	const accordionHeader = $( headerElement );
	const accordionButton = accordionHeader.find( ':button' ).first();
	const accordionBody = $( accordionButton.attr( 'data-target' ) );

	// add the initial icon
	g_taglib._toggleAccordionIcon( headerElement, accordionButton.attr( 'aria-expanded' ) === 'true' );

	// add the CSS class to indicate the whole header is clickable, and attach the event handler
	accordionHeader
		.addClass( 'clickable' )
		.on( 'click', this._accordionHeaderClick.bind( this, headerElement ) );

	// attach the event handlers
	accordionBody
		.on( 'show.bs.collapse', g_taglib._toggleAccordionIcon.bind( g_taglib, headerElement, true ) )
		.on( 'hide.bs.collapse', g_taglib._toggleAccordionIcon.bind( g_taglib, headerElement, false ) );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery} headerElement
 * @param {boolean} isActive
 */
TagLib.prototype._toggleAccordionIcon = function( headerElement, isActive )
{
	const iconHolder = $( headerElement ).find( '.accordion-icon-holder' ).first();

	// remove the previous icon
	iconHolder.empty();

	// create the new icon
	g_taglib.generateStandaloneIcon( null, 'far fa-lg ' + ( isActive ? 'fa-check-circle' : 'fa-circle' ), null, iconHolder );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery} headerElement
 * @param {jQuery.Event} e
 */
TagLib.prototype._accordionHeaderClick = function( headerElement, e )
{
	e.stopPropagation();

	const accordionHeader = $( headerElement );

	// toggle the body collapse
	$( accordionHeader.find( 'button' ).first().attr( 'data-target' ).toString() ).collapse( 'toggle' );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the id of the active accordion, or null if none are active
 *
 * @param {string|jQuery} accordionHolder - the jQuery instance or DOM id of the accordion holder
 *
 * @returns {?string}
 */
TagLib.prototype.getActiveAccordion = function( accordionHolder )
{
	const accordionHolderElement = typeof accordionHolder === 'string'
		? $( '#' + accordionHolder.toString() )
		: accordionHolder;

	const accordions = accordionHolderElement.find( '.card-header' );

	for( let i = 0; i < accordions.length; ++i )
	{
		const accordionHeader = $( accordions[ i ] );
		const accordionButton = accordionHeader.find( ':button' ).first();
		if( accordionButton.attr( 'aria-expanded' ) === 'true' )
		{
			return accordionHeader.attr( 'id' );
		}
	}

	return null;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a slider control
 *
 * @param {?string} id
 * @param {number} min
 * @param {number} max
 * @param {number} step
 * @param {number} precision
 * @param {number} currentValue
 * @param {boolean} enabled
 * @param {function(*)} callback
 * @param {jQuery} parent - the parent element to append the slider to
 *
 * @returns {jQuery}
 */
TagLib.prototype.generateSlider = function( id, min, max, step, precision, currentValue, enabled, callback, parent )
{
	// NOTE: the parent is required, otherwise the .slider() calls will fail

	const slider = $( '<input/>', { 'type' : 'text' } )
		.appendTo( parent );

	if( id )
	{
		slider.attr( 'id', id );
	}

	this.initSlider( slider, min, max, step, precision, currentValue, enabled, callback );

	return slider;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a slider control
 *
 * @param {jQuery} slider
 * @param {number} min
 * @param {number} max
 * @param {number} step
 * @param {number} precision
 * @param {number} currentValue
 * @param {boolean} enabled
 * @param {function(number)} callback
 */
TagLib.prototype.initSlider = function( slider, min, max, step, precision, currentValue, enabled, callback )
{
	slider.slider( {
		'precision' : precision,
		'value' : currentValue,
		'min' : min,
		'max' : max,
		'step' : step,
		'tooltip' : 'hide'
	} );

	// add the handler
	if( callback )
	{
		slider.on( 'slide', this._sliderChangeCallback.bind( this, callback ) );
	}

	if( !enabled )
	{
		slider.slider( 'disable' );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Sets the value of the given slider
 *
 * @param {jQuery} slider
 * @param {number} value
 */
TagLib.prototype.setSliderValue = function( slider, value )
{
	slider.slider( 'setValue', value, true, false );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the value of the slider
 *
 * @param {jQuery} slider
 *
 * @returns {number}
 */
TagLib.prototype.getSliderValue = function( slider )
{
	return slider.slider( 'getValue' );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Sets if the given slider is enabled
 *
 * @param {jQuery} slider
 * @param {boolean} enabled
 */
TagLib.prototype.setSliderEnabled = function( slider, enabled )
{
	slider.slider( enabled ? 'enable' : 'disable' );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {function(number)} callback
 * @param {{value:number}} e
 */
TagLib.prototype._sliderChangeCallback = function( callback, e )
{
	callback( e.value );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initialize the datepicker control on the given jQuery control
 *
 * @param {jQuery} datePickerInput
 * @param {?jQuery} iconField
 * @param {string} dateFormat
 * @param {Date} initialDate
 */
TagLib.prototype.initDatePicker = function( datePickerInput, iconField, dateFormat, initialDate )
{
	datePickerInput.val( g_util.formatDate( initialDate, dateFormat ) );

	// we need to convert the date-fns format to the datepicker format:
	// FROM:	https://date-fns.org/v1.30.1/docs/format
	// TO:		https://bootstrap-datepicker.readthedocs.io/en/stable/options.html#format

	const parameters = /** @type {Array<{replaceThis:string, withThis:string}>}*/( [] );

	// months
	parameters.push( { 'replaceThis' : 'MMMM', 'withThis' : 'MM' } );
	parameters.push( { 'replaceThis' : 'MMM', 'withThis' : 'M' } );
	parameters.push( { 'replaceThis' : 'MM', 'withThis' : 'mm' } );
	parameters.push( { 'replaceThis' : 'M', 'withThis' : 'm' } );

	// month days
	parameters.push( { 'replaceThis' : 'DD', 'withThis' : 'dd' } );
	parameters.push( { 'replaceThis' : 'D', 'withThis' : 'd' } );

	// days of the week
	parameters.push( { 'replaceThis' : 'dddd', 'withThis' : 'DD' } );
	parameters.push( { 'replaceThis' : 'ddd', 'withThis' : 'D' } );

	// years
	parameters.push( { 'replaceThis' : 'YYYY', 'withThis' : 'yyyy' } );
	parameters.push( { 'replaceThis' : 'YY', 'withThis' : 'yy' } );

	const convertedFormat = g_util.replaceTemplateParameters( dateFormat, parameters );

	// console.log( '%s => %s', dateFormat, convertedFormat );

	datePickerInput.datepicker( {
		'format' : convertedFormat,
		'zIndexOffset' : 9999,
		'defaultViewDate' : initialDate
	} );

	if( iconField )
	{
		// iconField.on( 'focus', this._datePickerIconEventCallback.bind( this, datePickerInput ) );
		// iconField.on( 'focusout', this._datePickerIconEventCallback.bind( this, datePickerInput ) );
		iconField.on( 'click', this._datePickerIconFocus.bind( this, datePickerInput ) );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @private
 *
 * @param {jQuery} datePicker
 */
TagLib.prototype._datePickerIconFocus = function( datePicker )
{
	datePicker[ 0 ].focus();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Sets the date of the datepicker
 *
 * @param {jQuery} datePicker
 * @param {?Date} date
 */
TagLib.prototype.setDatePickerDate = function( datePicker, date )
{
	if( !date )
	{
		datePicker.datepicker( 'setDate', new Date() );
	}
	else
	{
		datePicker.datepicker( 'setDate', date );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {jQuery} target
 * @param {string} targetText - the text to display in the target
 * @param {?function(string,string)} dragStartCallback
 * @param {?function(string,string)} dragEndCallback
 * @param {?function(string,string)} dragDropCallback
 */
TagLib.prototype.initDragDropTarget = function( target, targetText, dragStartCallback, dragEndCallback, dragDropCallback )
{
	// note: the target is only where we display the image and change the border... the drop is allowed anywhere on the page

	target.empty();

	let row = $( '<div/>' )
		.addClass( 'row' )
		.appendTo( target );

	// generate the icon
	const iconHolder = $( '<div/>' )
		.addClass( 'col-3' )
		.appendTo( row );
	this.generateStandaloneIcon( null, this.Icons.DRAG_DROP_TARGET + ' fa-10x text-dark', 'Drag and drop here', iconHolder );

	const rightColumn = $( '<div/>' )
		.addClass( 'col-9' )
		.appendTo( row );

	$( '<div/>' )
		.text( targetText )
		.appendTo( rightColumn );

	// TODO this id should be generated so it is unique!
	this.generateFileBrowserField( 'drag-drop-file-upload-alternate', 'Choose file', this.onDragDropSelectManual.bind( this, target, dragDropCallback ), rightColumn )
		.addClass( 'mt-4' );

	$( document )
		.on( 'dragover', this.onDragDropOver.bind( this, iconHolder, target, dragStartCallback, dragEndCallback ) )
		.on( 'drop', this.onDragDrop.bind( this, iconHolder, target, dragEndCallback, dragDropCallback ) );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

TagLib.prototype.onDragDropSelectManual = function( target, dropCallback, filename, fileContents )
{
	if( dropCallback )
	{
		dropCallback( target, filename, fileContents );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user is dragging files over the import holder
 *
 * @private
 * @param {jQuery} iconHolder
 * @param {jQuery} target
 * @param {?function(jQuery)} userDragStartCallback
 * @param {?function(jQuery)} userDragEndCallback
 * @param {jQuery.Event} e
 */
TagLib.prototype.onDragDropOver = function( iconHolder, target, userDragStartCallback, userDragEndCallback, e )
{
	e.stopPropagation();
	e.preventDefault();
	e.originalEvent.dataTransfer.dropEffect = 'copy';

	const cancelDragHighlightTimerId = target.data( 'cancelDragHighlightTimerId' );
	if( cancelDragHighlightTimerId )
	{
		clearTimeout( cancelDragHighlightTimerId );
	}
	else
	{
		this.onDragDropStart( iconHolder, target, userDragStartCallback );
	}

	target.data( 'cancelDragHighlightTimerId', setTimeout( this.onDragDropEnd.bind( this, iconHolder, target, userDragEndCallback ), 200 ) );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery} iconHolder
 * @param {jQuery} target
 * @param {?function(jQuery)} userDragStartCallback
 */
TagLib.prototype.onDragDropStart = function( iconHolder, target, userDragStartCallback )
{
	// update the target background
	target.addClass( 'bg-medium' );

	// update the icon
	iconHolder.empty();
	this.generateStandaloneIcon( null, this.Icons.DRAG_DROP_READY + ' fa-10x text-primary', 'Drag and drop here', iconHolder );

	if( userDragStartCallback )
	{
		userDragStartCallback( target );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery} iconHolder
 * @param {jQuery} target
 * @param {?function(jQuery)} userDragEndCallback
 */
TagLib.prototype.onDragDropEnd = function( iconHolder, target, userDragEndCallback )
{
	clearTimeout( target.data( 'cancelDragHighlightTimerId' ) );

	target.data( 'cancelDragHighlightTimerId', null );

	// update the background
	target.removeClass( 'bg-medium' );

	// update the icon
	iconHolder.empty();
	this.generateStandaloneIcon( null, this.Icons.DRAG_DROP_TARGET + ' fa-10x text-dark', 'Drag and drop here', iconHolder );

	if( userDragEndCallback )
	{
		userDragEndCallback( target );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user drops files onto the import holder
 *
 * @private
 * @param {jQuery} iconHolder
 * @param {jQuery} target
 * @param {?function(jQuery)} userDragEndCallback
 * @param {?function(jQuery,string,string)} userDragDropCallback
 * @param {jQuery.Event} e
 */
TagLib.prototype.onDragDrop = function( iconHolder, target, userDragEndCallback, userDragDropCallback, e )
{
	e.stopPropagation();
	e.preventDefault();

	this.onDragDropEnd( iconHolder, target, userDragEndCallback );

	const files = e.originalEvent.dataTransfer.files;

	if( files.length === 0 )
	{
		return;
	}

	if( userDragDropCallback )
	{
		for( let i = 0; i < files.length; ++i )
		{
			let importReader = new FileReader();
			importReader.onloadend = this.onDragDropFileContentsLoaded.bind( this, target, userDragDropCallback, files[ i ].name );
			importReader.readAsText( files[ i ] );
		}
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery} target
 * @param {?function(jQuery,string,string)} dropCallback
 * @param {string} filename
 * @param e
 */
TagLib.prototype.onDragDropFileContentsLoaded = function( target, dropCallback, filename, e )
{
	if( dropCallback )
	{
		dropCallback( target, filename, e.target.result.toString() );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the notifications in the sidebar
 *
 * @param {string} projectId - the project id
 * @param {Array< Notification >} notifications - the notifications to display
 */
TagLib.prototype.updateSideBarNotifications = function( projectId, notifications )
{
	const notificationsHolder = $( '#notifications-holder' ).empty();

	if( notifications.length > 0 )
	{
		for( let i = 0; i < notifications.length; ++i )
		{
			let holder = $( '<div/>' )
				.addClass( 'clickable' )
				.on( 'click', g_nav.redirectToNotificationHandler.bind( g_nav, notifications[ i ], projectId ) )
				.appendTo( notificationsHolder );
			g_taglib.generateNotification( notifications[ i ], holder );
		}
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} formId
 * @param {function} submitCallback
 * @param {function} cancelCallback
 */
TagLib.prototype.bindFormSubmitEnterEscape = function( formId, submitCallback, cancelCallback )
{
	$( '#' + formId )
		.on( 'keyup', this._bindFormSubmitEnterEscapeHelper.bind( this, submitCallback, cancelCallback ) )
		.on( 'submit', function()
		{
			return false;
		} );

	return;
};

/**
 * Helper function to actually check the form submit for enter/escape keys
 *
 * @private
 * @param {function} submitCallback
 * @param {function} cancelCallback
 * @param {jQuery.Event} e
 */
TagLib.prototype._bindFormSubmitEnterEscapeHelper = function( submitCallback, cancelCallback, e )
{
	if( e.key === 'Enter' )
	{
		// return is valid in a text area, so ignore that here
		if( e.target && e.target.type && e.target.type === 'textarea' )
		{
			return false;
		}

		e.preventDefault();
		e.stopImmediatePropagation();

		if( submitCallback )
		{
			submitCallback();
		}

		return false;
	}
	else if( e.key === 'Escape' )
	{
		// escape is used to close a dropdown, so ignore in this case
		if( e.target && e.target.type && e.target.type === 'select-one' )
		{
			return false;
		}

		e.preventDefault();
		e.stopImmediatePropagation();

		if( cancelCallback )
		{
			cancelCallback();
		}

		return false;
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
