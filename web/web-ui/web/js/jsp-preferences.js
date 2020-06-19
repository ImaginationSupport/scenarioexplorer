////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Holds the Scenario Explorer API library instance
 *
 * @type {ScenarioExplorerAPI}
 */
const g_api = new ScenarioExplorerAPI( false, '/scenarioexplorer' );

/**
 * Holds the Util library instance
 *
 * @type {Util}
 */
const g_util = new Util();

/**
 * Holds the Nav Helper instance
 *
 * @type {NavHelper}
 */
const g_nav = new NavHelper();

/**
 * Holds the TagLib library instance
 *
 * @private
 * @type {TagLib}
 */
const g_taglib = new TagLib();

/**
 * Holds the available date formats
 *
 * @type {Array< string >}
 */
const g_availableDateFormats = [
	'dddd, MMMM D, YYYY',
	'MMMM D, YYYY',
	'MMM D, YYYY',
	'MM/DD/YYYY',
	'YYYY-MM-DD',
	'YYYYMMDD'
];

/**
 * Holds the available date formats
 *
 * @type {Array< string >}
 */
const g_availableTimeFormats = [
	'h:mm A',
	'HH:mm:ss',
	'HHmmss'
];

/**
 * Holds the User instance
 *
 * @private
 * @type {?User}
 */
let g_user = null;

/**
 * Holds the date format dropdown
 *
 * @private
 * @type {?jQuery}
 */
let g_dateFormatDropdown = null;

/**
 * Holds the time format dropdown
 *
 * @private
 * @type {?jQuery}
 */
let g_timeFormatDropdown = null;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the JSP to initialize the page
 *
 * @param {string} username
 */
function initJSP( username )
{
	g_api.getUser( username, getCurrentUserCallback.bind( this ) );

	g_dateFormatDropdown = $( '#date-format' );
	g_timeFormatDropdown = $( '#time-format' );

	g_ctrlAlert = $( '#form-error-alert' ).removeClass( 'd-none' ).hide();
	g_ctrlSave = $( '#button-save' )
		.on( 'click', runSave.bind( this ) );
	g_ctrlCancel = $( '#button-cancel' )
		.on( 'click', runCancel.bind( this ) );

	g_taglib.bindFormSubmitEnterEscape( 'preferences-form', runSave.bind( this ), runCancel.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {User} user
 */
function getCurrentUserCallback( user )
{
	g_user = user;

	populatePreferences();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function populatePreferences()
{
	if( g_user === null )
	{
		return;
	}

	const sampleDate = new Date( 2018, 0, 31, 14, 33, 21 );

	// date format
	g_dateFormatDropdown.empty();
	for( let i = 0; i < g_availableDateFormats.length; ++i )
	{
		g_taglib.generateDropdownOption(
			g_dateFormatDropdown,
			g_util.formatDate( sampleDate, g_availableDateFormats[ i ] ),
			g_availableDateFormats[ i ],
			g_user.getDateFormatPreference() === g_availableDateFormats[ i ],
			false,
			null,
			null );
	}

	// time format
	g_timeFormatDropdown.empty();
	for( let i = 0; i < g_availableTimeFormats.length; ++i )
	{
		g_taglib.generateDropdownOption(
			g_timeFormatDropdown,
			g_util.formatDate( sampleDate, g_availableTimeFormats[ i ] ),
			g_availableTimeFormats[ i ],
			g_user.getTimeFormatPreference() === g_availableTimeFormats[ i ],
			false,
			null,
			null );

	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Save button
 *
 * @private
 */
function runSave()
{
	g_user.preferences.dateFormat = g_taglib.getDropdownSelectedValue( g_dateFormatDropdown );
	g_user.preferences.timeFormat = g_taglib.getDropdownSelectedValue( g_timeFormatDropdown );

	g_api.updateUser( g_user, this.updateUserCallback.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function updateUserCallback()
{
	g_nav.redirectToIndex();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Cancel button
 *
 * @private
 */
function runCancel()
{
	g_nav.redirectToIndex();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

