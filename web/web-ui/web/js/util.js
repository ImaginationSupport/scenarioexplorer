////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Constructor
 *
 * @constructor
 */
function Util()
{
	this.mNextUniqueId = 0;

	// this.COLOR_PARSER_BASIC = new RegExp( '^(\\d{1,3}),(\\d{1,3}),(\\d{1,3})$' );
	// this.COLOR_PARSER_HEX_SHORT = new RegExp( '^(#?)([0-9A-Fa-f]{3})$' );
	// this.COLOR_PARSER_HEX_FULL = new RegExp( '^(#?)([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})$' );
	// this.COLOR_PARSER_RGB = new RegExp( '^rgb\\(\\s*(\\d{1,3}),\\s*(\\d{1,3}),\\s*(\\d{1,3})\\s*\\)$' );
	// this.COLOR_PARSER_RGBA = new RegExp( '^rgba\\(\\s*(\\d{1,3}),\\s*(\\d{1,3}),\\s*(\\d{1,3}),\\s*(\\d[.]\\d+)\\s*\\)$' );

	this.URL_PARSER = new RegExp( '^(http[s]?://)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&//=]*)$' );

	this.DEFAULT_DATE_FORMAT = 'YYYY-MM-DD';
	this.DEFAULT_TIME_FORMAT = 'HH:mm:ss';
	this.DEFAULT_DATETIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Parses the given date
 *
 * @param {string} value - the value to parse
 *
 * @returns {?Date} - The parsed Date, or null if it could not be parsed
 */
Util.prototype.parseDateOnly = function( value )
{
	if( !value )
	{
		return null;
	}

	const parsed = /** @type {Date} */( dateFns.parse( value ) );

	parsed.setHours( 0 );
	parsed.setMinutes( 0 );
	parsed.setSeconds( 0 );
	parsed.setMilliseconds( 0 );

	return parsed;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Parses the given date string into the object
 *
 * @param {string} value - the value to parse
 *
 * @returns {?Date} - The parsed Date, or null if it could not be parsed
 */
Util.prototype.parseDateTime = function( value )
{
	if( !value )
	{
		return null;
	}

	return /** @type {Date} */( dateFns.parse( value ) );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Format the given date based on the given format
 *
 * @param {Date} date
 * @param {string} format
 * @returns {string}
 */
Util.prototype.formatDate = function( date, format )
{
	return dateFns.format( date, format );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Tests if the given date is between the given start and end dates
 *
 * @param {?Date} dateStart the time period start date
 * @param {?Date} dateEnd the time period end date
 * @param {?Date} dateTest the date to test
 *
 * @return {boolean} true if all dates are non-null and test date is between (or equal to) the start and end dates
 */
Util.prototype.dateBetween = function( dateStart, dateEnd, dateTest )
{
	if( dateStart === null || dateEnd === null || dateTest === null )
	{
		return false;
	}

	if( dateStart.getTime() > dateEnd.getTime() )
	{
		console.warn( 'Invalid start and end times!' )
	}

	return dateStart.getTime() <= dateTest.getTime()
		&& dateTest.getTime() <= dateEnd.getTime();
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Replaces all parameters in the given template format
 *
 * @param {string} templateFormat
 * @param {Array<{replaceThis:string, withThis:string}>} parameters
 *
 * @returns {string}
 */
Util.prototype.replaceTemplateParameters = function( templateFormat, parameters )
{
	let formatted = '';

	// TODO should sort the items from longest input or can get weird results

	let i = 0;
	let j;
	while( i < templateFormat.length )
	{
		for( j = 0; j < parameters.length; ++j )
		{
			if( templateFormat.substr( i ).startsWith( parameters[ j ].replaceThis ) )
			{
				formatted += parameters[ j ].withThis;
				i += parameters[ j ].replaceThis.length;
				break;
			}
		}

		if( j >= parameters.length )
		{
			formatted += templateFormat[ i ];
			++i;
		}
	}

	return formatted;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Verifies if the given string is a valid URI
 *
 * @param {string} uri
 *
 * @returns {boolean}
 */
Util.prototype.verifyUri = function( uri )
{
	return this.URL_PARSER.exec( uri ) !== null;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * Scenario Explorer Color
//  *
//  * @param {number} r - red component (0-255)
//  * @param {number} g - green component (0-255)
//  * @param {number} b - blue component (0-255)
//  * @param {number} a - alpha component (0.0 - 1.0)
//  * @constructor
//  */
// function ScenarioExplorerColor( r, g, b, a )
// {
// 	this.r = r;
// 	this.g = g;
// 	this.b = b;
// 	this.a = a === undefined || a === null || a < 0 || a > 1 ? 1 : a;
//
// 	return;
// }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * Parses the given string into the color instance - accepted formats: #,#,# or rgb(#,#,#) or rgba(#,#,#,#)
//  *
//  * @param {string} value - value to parse
//  *
//  * @returns {ScenarioExplorerColor|null}
//  */
// Util.prototype.parseColor = function( value )
// {
// 	let match;
//
// 	// first try the basic parser
// 	match = this.COLOR_PARSER_BASIC.exec( value );
// 	if( match !== null )
// 	{
// 		return new ScenarioExplorerColor(
// 			parseInt( match[ 1 ], 10 ),
// 			parseInt( match[ 2 ], 10 ),
// 			parseInt( match[ 3 ], 10 ),
// 			1.0 );
// 	}
//
// 	// now try the rgb parser
// 	match = this.COLOR_PARSER_RGB.exec( value );
// 	if( match !== null )
// 	{
// 		return new ScenarioExplorerColor(
// 			parseInt( match[ 1 ], 10 ),
// 			parseInt( match[ 2 ], 10 ),
// 			parseInt( match[ 3 ], 10 ),
// 			1.0 );
// 	}
//
// 	// now try the rgba parser
// 	match = this.COLOR_PARSER_RGBA.exec( value );
// 	if( match !== null )
// 	{
// 		return new ScenarioExplorerColor(
// 			parseInt( match[ 1 ], 10 ),
// 			parseInt( match[ 2 ], 10 ),
// 			parseInt( match[ 3 ], 10 ),
// 			parseFloat( match[ 4 ] ) );
// 	}
//
// 	// now try the full hex parser
// 	match = this.COLOR_PARSER_HEX_SHORT.exec( value );
// 	if( match !== null )
// 	{
// 		return new ScenarioExplorerColor(
// 			parseInt( match[ 2 ].substr( 0, 1 ) + match[ 2 ].substr( 0, 1 ), 16 ),
// 			parseInt( match[ 2 ].substr( 1, 1 ) + match[ 2 ].substr( 1, 1 ), 16 ),
// 			parseInt( match[ 2 ].substr( 2, 1 ) + match[ 2 ].substr( 2, 1 ), 16 ),
// 			1.0 );
// 	}
//
// 	// now try the short hex parser
// 	match = this.COLOR_PARSER_HEX_FULL.exec( value );
// 	if( match !== null )
// 	{
// 		return new ScenarioExplorerColor(
// 			parseInt( match[ 2 ], 16 ),
// 			parseInt( match[ 3 ], 16 ),
// 			parseInt( match[ 4 ], 16 ),
// 			1.0 );
// 	}
//
// 	return null;
// };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * Formats the given color as a string
//  *
//  * @param {ScenarioExplorerColor} color
//  *
//  * @returns {string}
//  */
// Util.prototype.formatColor = function( color )
// {
// 	return color.a === 1.0
// 		? ( 'rgb(' + color.r + ',' + color.g + ',' + color.b + ')' )
// 		: ( 'rgba(' + color.r + ',' + color.g + ',' + color.b + ',' + color.a.toFixed( 2 ) + ')' );
// };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Shows the error dialog with the given message
 *
 * @param {string} message - the error message to display
 */
Util.prototype.showError = function( message )
{
	$( '.container' ).hide();

	$( '#error-display' ).removeClass( 'd-none' ).show();
	$( '#error-message' ).text( message );
	$( '#error-exception-stacktrace' ).hide();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Shows an exception full-screen message
 *
 * @param {string} message
 * @param {string} stackTrace
 */
Util.prototype.showException = function( message, stackTrace )
{
	$( '.container' ).hide();

	$( '#error-display' ).removeClass( 'd-none' ).show();
	$( '#error-message' ).text( message );
	$( '#error-exception-stacktrace' ).text( 'Exception:\n' + stackTrace );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Shows an alert div
 *
 * @param {jQuery} alert - the jQuery alert div
 * @param {Array<string>} errors - the list of errors
 */
Util.prototype.showAlertDiv = function( alert, errors )
{
	alert.empty();

	$( '<span/>' )
		.text( 'Please fix the following error' + ( errors.length === 1 ? '' : 's' ) )
		.appendTo( alert );

	let ul = $( '<ul/>' )
		.appendTo( alert );

	for( let i = 0; i < errors.length; ++i )
	{
		ul = $( '<li/>' )
			.text( errors[ i ] )
			.appendTo( ul );
	}

	alert.show();
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a unique id number
 *
 * @returns {number} the unique id
 */
Util.prototype.generateUniqueId = function()
{
	return this.mNextUniqueId++;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} value - the value to verify
 *
 * @returns {boolean} True if it was valid, otherwise false
 */
Util.prototype.verifyInteger = function( value )
{
	return parseInt( value, 10 ).toString() === value.trim();
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} value - the value to verify
 *
 * @returns {boolean} True if it was valid, otherwise false
 */
Util.prototype.verifyFloat = function( value )
{
	return !isNaN( parseFloat( value ) )
		&& value.match( /^-?\d*(\.\d+)?$/ ) !== null;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Clones the given object
 *
 * @param {*} source
 * @returns {*}
 */
Util.prototype.cloneSingleObject = function( source )
{
	let base;

	if( source === null || source === undefined || typeof source === 'string' || typeof source === 'boolean' || typeof source === 'number' || typeof source === 'function' )
	{
		return source;
	}

	// see if it's one of our data objects
	if( source instanceof TimelineEvent )
	{
		base = new TimelineEvent( /** @type {SerializedTimelineEvent} */( {} ) );
	}
	else if( source instanceof Feature )
	{
		base = new Feature( /** @type {SerializedFeature} */( {} ) );
	}
	// else if( source instanceof ConditioningEvent )
	// {
	// 	base = new ConditioningEvent( /** @type {SerializedConditioningEvent} */( {} ) );
	// }
	// else if( source instanceof State )
	// {
	// 	base = new State( /** @type {SerializedState} */( {} ) );
	// }
	else if( source instanceof PreconditionInstance )
	{
		base = new PreconditionInstance( /** @type {SerializedPreconditionInstance} */( {} ) );
	}
	// else if( source instanceof Outcome )
	// {
	// 	base = new Outcome( /** @type {SerializedOutcome} */( {} ) );
	// }
	// else if( source instanceof FeatureTypePlugin )
	// {
	// 	base = new FeatureTypePlugin( /** @type {SerializedFeatureTypePlugin} */( {} ) );
	// }
	// else if( source instanceof PreconditionPlugin )
	// {
	// 	base = new PreconditionPlugin( /** @type {SerializedPreconditionPlugin} */( {} ) );
	// }
	// else if( source instanceof OutcomeEffectPlugin )
	// {
	// 	base = new OutcomeEffectPlugin( /** @type {SerializedOutcomeEffectPlugin} */( {} ) );
	// }
	// else if( source instanceof ProjectorPlugin )
	// {
	// 	base = new ProjectorPlugin( /** @type {SerializedProjectorPlugin} */( {} ) );
	// }
	else if( source instanceof View )
	{
		base = new View( /** @type {SerializedView} */( {} ) );
	}
	// else if( source instanceof FuturesBuildingView )
	// {
	// 	base = new FuturesBuildingView( /** @type {SerializedView} */( {} ) );
	// }
	else if( source instanceof User )
	{
		base = new User( /** @type {SerializedUser} */( {} ) );
	}
	else if( source instanceof Project )
	{
		base = new Project( /** @type {SerializedProject} */( {} ) );
	}
	else if( source instanceof ConditioningEvent )
	{
		base = new ConditioningEvent( /** @type {SerializedConditioningEvent} */( {} ) );
	}
	else
	{
		console.warn( 'Unknown base object to clone: %O', source );
		base = {};
	}

	return jQuery.extend( true, base, source );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

Util.prototype.cloneArray = function( source )
{
	if( source === null || source === undefined )
	{
		return source;
	}

	// TODO should check if it's actually an array

	const clone = [];

	for( let i = 0; i < source.length; ++i )
	{
		clone.push( this.cloneSingleObject( source[ i ] ) );
	}

	return clone;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	adds: Array< Object >,
 * 	updates: Array< Object >,
 * 	deletes: Array< string >
 * }} AddUpdateDeleteBreakdown
 */

/**
 *
 * @param {Array< {id: string} >} workingList
 * @param {Array< {id: string} >} originalList
 *
 * @returns {AddUpdateDeleteBreakdown}
 */
Util.prototype.determineAddUpdateDelete = function( workingList, originalList )
{
	const adds = /** @type {Array< Object >} */( [] );
	const updates = /** @type {Array< Object >} */( [] );
	const deletes = /** @type {Array< string >} */( [] );

	let found;

	// go through the working list to see what was already in the original list, looking for adds and updates
	for( let i = 0; i < workingList.length; ++i )
	{
		found = false;
		for( let j = 0; j < originalList.length; ++j )
		{
			if( originalList[ j ].id === workingList[ i ].id )
			{
				// already exists, see if it's changed
				if( JSON.stringify( workingList[ i ], g_api.customJSONSerializer.bind( g_api ) ) !== JSON.stringify( originalList[ j ], g_api.customJSONSerializer.bind( g_api ) ) )
				{
					// changed, so update
					updates.push( workingList[ i ] );
				}

				found = true;
				break;
			}
		}

		if( !found )
		{
			// does not exist, so add
			adds.push( workingList[ i ] );
		}
	}

	// go through the original list looking for what is missing in the working list, looking for deletes
	for( let j = 0; j < originalList.length; ++j )
	{
		found = false;
		for( let i = 0; i < workingList.length; ++i )
		{
			if( originalList[ j ].id === workingList[ i ].id )
			{
				found = true;
				break;
			}
		}

		if( !found )
		{
			// does not exist in new, so delete
			deletes.push( originalList[ j ].id );
		}
	}

	return /** @type {AddUpdateDeleteBreakdown} */( { 'adds' : adds, 'updates' : updates, 'deletes' : deletes } );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/***
 * Tests if the given item exists in the given array
 *
 * @param {Array< * >} array - the array to check
 * @param {*} item - the item to search for
 *
 * @returns {boolean} true if it was found, otherwise false
 */
Util.prototype.existsInArray = function( array, item )
{
	for( let i = 0; i < array.length; ++i )
	{
		if( array[ i ] === item )
		{
			return true;
		}
	}

	return false;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Removes the given item from the given array as many times as it is found
 *
 * @param {Array< * >} array - the array to search
 * @param {*} item - the item to search for
 */
Util.prototype.removeFromArray = function( array, item )
{
	for( let i = 0; i < array.length; ++i )
	{
		if( array[ i ] === item )
		{
			array.splice( i, 1 );
			--i;
		}
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

Util.prototype.replaceAll = function( string, search, replacement )
{
	return string.replace( new RegExp( search, 'g' ), replacement );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Parses the text into CSV rows/columns
 *
 * @param {string} text - the text to parse
 *
 * @returns {?Array< Array< string >>} the parsed lines
 */
Util.prototype.parseCSV = function( text )
{
	if( text == null )
	{
		return null;
	}

	text = this.replaceAll( text, '\r', '' );

	const lines = [];
	const split = text.split( '\n' );

	for( let rowIndex = 0; rowIndex < split.length; ++rowIndex )
	{
		if( split[ rowIndex ].length > 0 )
		{
			// console.log( split[ rowIndex ] );

			const row = [];

			let col;
			let colStartIndex = 0;
			let insideQuote = false;
			for( let i = 0; i < split[ rowIndex ].length; ++i )
			{
				if( split[ rowIndex ].charAt( i ) === ',' && !insideQuote )
				{
					col = /** @type {string} */( split[ rowIndex ].substring( colStartIndex, i ) );
					// console.log( '[%s]', col );

					if( col.startsWith( '"' ) && col.endsWith( '"' ) )
					{
						col = col.substr( 1, col.length - 2 );
					}

					row.push( col );

					colStartIndex = i + 1;
				}
				else if( split[ rowIndex ].charAt( i ) === '"' )
				{
					insideQuote = !insideQuote;
				}
			}

			// add any trailing text as the final column
			if( colStartIndex < split[ rowIndex ].length )
			{
				col = /** @type {string} */( split[ rowIndex ].substring( colStartIndex ) );

				if( col.startsWith( '"' ) && col.endsWith( '"' ) )
				{
					col = col.substr( 1, col.length - 2 );
				}

				row.push( col );
			}

			lines.push( row );

			// console.log( 'end [%s]', split[ rowIndex ].substring( colStartIndex ) );
		}
	}

	// console.log( lines );

	return lines;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
