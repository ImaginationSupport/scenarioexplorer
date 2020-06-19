////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	uid: string,
 * 	displayName: string,
 * 	mail: string,
 * 	password: string=
 * }} LdapUser
 */

/**
 * @typedef {{
 * 	id: string,
 * 	userName: string,
 * 	fullName: string,
 * 	isSiteAdmin: boolean,
 * 	projects: Array< string >,
 * 	lastLogin: ?string
 * }} ScenarioExplorerUser
 */

/**
 * Holds the Util library instance
 *
 * @const {Util}
 */
const g_util = new Util();

/**
 * Holds the TagLib library instance
 * @const {TagLib}
 */
const g_taglib = new TagLib();

/**
 * Holds the URI to the backend servlet
 *
 * @const {string}
 */
const BACKEND_URI = 'backend'; // TODO this should be inserted in the build

/**
 * Holds the default password to use
 *
 * @const {string}
 */
const DEFAULT_IMPORT_PASSWORD = 'passw0rd';

/**
 * Holds the LDAP users
 *
 * @private
 *
 * @type {?Array< LdapUser >}
 */
let g_ldapUsers = null;

/**
 * Holds the ScenarioExplorer users
 *
 * @private
 *
 * @type {?Array< ScenarioExplorerUser >}
 */
let g_scenarioExplorerUsers = null;

/**
 * Holds the username jQuery control
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlUserName = null;

/**
 * Holds the real name jQuery control
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlRealName = null;

/**
 * Holds the email address jQuery control
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlEmailAddress = null;

/**
 * Holds the is admin jQuery control
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlIsAdminHolder = null;

/**
 * Holds the is admin true jQuery control
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlIsAdminTrue = null;

/**
 * Holds the is admin false jQuery control
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlIsAdminFalse = null;

/**
 * Holds the password jQuery control
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlPassword = null;

/**
 * Holds the left bar
 *
 * @private
 *
 * @type {jQuery}
 */
let g_ctrlWorkingUsers = null;

/**
 * Holds the user missing control
 *
 * @private
 *
 * @type {jQuery}
 */
let g_ctrlUserMissing = null;

/**
 * Holds the alert control
 *
 * @private
 *
 * @type {jQuery}
 */
let g_ctrlAlert = null;

/**
 * Holds the add/update button
 *
 * @private
 *
 * @type {jQuery}
 */
let g_ctrlAddUpdateButton = null;

/**
 * Holds the delete button
 *
 * @private
 *
 * @type {jQuery}
 */
let g_ctrlDeleteButton = null;

/**
 * Holds the add scenario explorer user control
 *
 * @private
 *
 * @type {jQuery}
 */
let g_ctrlAddScenarioExplorerUserButton = null;

/**
 * @private
 *
 * @type {number}
 */
let g_editIndex = -1;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the JSP
 */
function initJSP()
{
	g_ctrlUserMissing = $( '#form-user-missing' )
		.removeClass( 'd-none' )
		.hide();
	g_ctrlAlert = $( '#form-alert' )
		.removeClass( 'd-none' )
		.hide();

	g_ctrlUserName = $( '#username' );
	g_ctrlRealName = $( '#realname' );
	g_ctrlEmailAddress = $( '#email' );
	g_ctrlPassword = $( '#password' );

	g_ctrlIsAdminHolder = $( '#is-site-admin' );
	g_ctrlIsAdminTrue = $( '#is-site-admin-true-holder' );
	g_ctrlIsAdminFalse = $( '#is-site-admin-false-holder' );

	g_ctrlIsAdminTrue
		.data( 'button-value', true )
		.on( 'click', clickBoolean.bind( this, g_ctrlIsAdminHolder, g_ctrlIsAdminTrue, g_ctrlIsAdminFalse ) );
	g_ctrlIsAdminFalse
		.data( 'button-value', false )
		.on( 'click', clickBoolean.bind( this, g_ctrlIsAdminHolder, g_ctrlIsAdminFalse, g_ctrlIsAdminTrue ) );

	const workingUsersHolder = $( '#working-users-holder' )
		.addClass( 'clickable' )
		.on( 'click', onDeselect.bind( this ) );
	g_taglib.initDragDropTarget( workingUsersHolder, this.onFileImport.bind( this ) );

	g_ctrlWorkingUsers = $( '#working-users' );

	g_ctrlAddUpdateButton = $( '#button-add-update' ).on( 'click', onAddUpdate.bind( this ) );

	g_ctrlDeleteButton = $( '#button-delete' ).on( 'click', onDelete.bind( this ) );

	g_ctrlAddScenarioExplorerUserButton = $( '#button-add-scenario-explorer-user' ).on( 'click', onAddScenarioExplorerUser.bind( this ) );

	$( '#button-export-ldif' ).on( 'click', onExportLDIF.bind( this ) );
	$( '#button-export-csv' ).on( 'click', onExportCSV.bind( this ) );

	fetchUserLists();

	initFormFields( null );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function fetchUserLists()
{
	// fetch the ldap users
	jQuery.ajax(
		{
			'url' : BACKEND_URI + '?action=get-ldap-users',
			'method' : 'GET',
			'success' : listLdapUsersCallback.bind( this ),
			'error' : onAjaxError.bind( this ),
			'cache' : false,
			'dataType' : 'json'
		}
	);

	// fetch the scenario explorer users
	jQuery.ajax(
		{
			'url' : BACKEND_URI + '?action=get-scenario-explorer-users',
			'method' : 'GET',
			'success' : listScenarioExplorerUsersCallback.bind( this ),
			'error' : onAjaxError.bind( this ),
			'cache' : false,
			'dataType' : 'json'
		}
	);

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {{success:boolean, users:?Array< LdapUser >}} ajaxResponse
 */
function listLdapUsersCallback( ajaxResponse )
{
	if( !ajaxResponse || !ajaxResponse.success )
	{
		console.warn( ajaxResponse );
		g_util.showError( 'Error downloading LDAP users!' );
		return;
	}

	g_ldapUsers = ajaxResponse.users;

	populateLeftBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {{success:boolean, users:?Array< ScenarioExplorerUser >}} ajaxResponse
 */
function listScenarioExplorerUsersCallback( ajaxResponse )
{
	if( !ajaxResponse || !ajaxResponse.success )
	{
		console.warn( ajaxResponse );
		g_util.showError( 'Error downloading LDAP users!' );
		return;
	}

	g_scenarioExplorerUsers = ajaxResponse.users;

	populateLeftBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {{responseJSON: {errorMessage: string, stackTrace: string, isUserError: boolean}}} response
 * @param {string} responseRaw
 */
function onAjaxError( response, responseRaw )
{
	console.error( response );
	console.error( responseRaw );

	if( !( 'responseJSON' in response ) )
	{
		g_util.showError( 'Error running API command!' );
		return;
	}

	console.error( 'message: %s', response.responseJSON.errorMessage );
	console.error( 'stack trace: %s', response.responseJSON.stackTrace );

	g_util.showException( response.responseJSON.errorMessage.toString(), response.responseJSON.stackTrace.toString() );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function populateLeftBar()
{
	g_ctrlWorkingUsers.empty();

	// add the header
	$( '<li/>' )
		.addClass( 'list-group-item' )
		.addClass( 'list-group-item-action' )
		.addClass( 'font-weight-bold' )
		.addClass( 'bg-secondary' )
		.addClass( 'text-light' )
		.addClass( 'p-2' )
		.text( 'LDAP Users' )
		.appendTo( g_ctrlWorkingUsers );

	// if either of these are null, show the message and bail
	if( g_ldapUsers === null || g_scenarioExplorerUsers === null )
	{
		$( '<li/>' )
			.addClass( 'list-group-item' )
			.addClass( 'list-group-item-action' )
			.addClass( 'p-2' )
			.addClass( 'pl-3' )
			.text( 'Loading, please wait...' )
			.appendTo( g_ctrlWorkingUsers );

		return;
	}

	for( let i = 0; i < g_ldapUsers.length; ++i )
	{
		const scenarioExplorerUser = findScenarioExplorerUser( g_ldapUsers[ i ].uid );

		let entryText = g_ldapUsers[ i ].uid + ' - ' + g_ldapUsers[ i ].displayName;

		if( scenarioExplorerUser && scenarioExplorerUser.isSiteAdmin )
		{
			entryText += ' - ADMIN';
		}

		const entry = $( '<li/>' )
			.addClass( 'list-group-item' )
			.addClass( 'list-group-item-action' )
			.addClass( 'p-2' )
			.addClass( 'pl-3' )
			.addClass( 'clickable' )
			.text( entryText )
			.on( 'click', editExistingUser.bind( this, i ) )
			.appendTo( g_ctrlWorkingUsers );

		if( !scenarioExplorerUser )
		{
			entry.addClass( 'strike' );
		}
	}

	if( g_ldapUsers.length === 0 )
	{
		$( '<li/>' )
			.addClass( 'list-group-item' )
			.addClass( 'list-group-item-action' )
			.addClass( 'p-2' )
			.addClass( 'pl-3' )
			.text( '(None)' )
			.appendTo( g_ctrlWorkingUsers );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks to edit an existing user
 *
 * @private
 *
 * @param {number} index - the ldap users array index
 * @param {jQuery.Event} e - the jQuery event
 */
function editExistingUser( index, e )
{
	e.stopPropagation();

	// mark that we are editing this index
	g_editIndex = index;

	// redraw the list (to reset any previous highlights) and highlight this one
	populateLeftBar();
	g_ctrlWorkingUsers.find( 'li:nth-child(' + ( index + 2 ).toString() + ')' ).addClass( 'bg-medium' );

	initFormFields( g_ldapUsers[ index ] );

	const scenarioExplorerUser = findScenarioExplorerUser( g_ldapUsers[ index ].uid );
	g_ctrlUserMissing.toggle( scenarioExplorerUser === null );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {jQuery.Event} e - the jQuery event
 */
function onDeselect( e )
{
	e.stopPropagation();

	runDeselectEdit();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function runDeselectEdit()
{
	g_editIndex = -1;

	initFormFields( null );

	populateLeftBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {?LdapUser} ldapUser
 */
function initFormFields( ldapUser )
{
	if( ldapUser === null )
	{
		g_ctrlUserName.val( '' );
		g_ctrlRealName.val( '' );
		g_ctrlEmailAddress.val( '' );

		clickBoolean( g_ctrlIsAdminHolder, g_ctrlIsAdminFalse, g_ctrlIsAdminTrue );
	}
	else
	{
		let scenarioExplorerUser = /** @type{?ScenarioExplorerUser} */( findScenarioExplorerUser( ldapUser.uid ) );

		g_ctrlUserName.val( ldapUser.uid );
		g_ctrlRealName.val( ldapUser.displayName );
		g_ctrlEmailAddress.val( ldapUser.mail );

		if( !scenarioExplorerUser || !scenarioExplorerUser.isSiteAdmin )
		{
			clickBoolean( g_ctrlIsAdminHolder, g_ctrlIsAdminFalse, g_ctrlIsAdminTrue );
		}
		else
		{
			clickBoolean( g_ctrlIsAdminHolder, g_ctrlIsAdminTrue, g_ctrlIsAdminFalse );
		}

		// update the create scenario explorer button
		g_ctrlAddScenarioExplorerUserButton.prop( 'disabled', scenarioExplorerUser !== null );
	}

	g_ctrlUserName.prop( 'disabled', ldapUser !== null );

	g_ctrlPassword.val( '' );

	// update the add/update button
	g_ctrlAddUpdateButton.empty();
	$( '<i/>' )
		.addClass( 'fas' )
		.addClass( ldapUser === null ? 'fa-plus' : 'fa-edit' )
		.appendTo( g_ctrlAddUpdateButton );
	$( '<span/>' )
		.addClass( 'pl-2' )
		.text( ldapUser === null ? 'Add' : 'Update' )
		.appendTo( g_ctrlAddUpdateButton );

	// update the delete button
	g_ctrlDeleteButton.prop( 'disabled', ldapUser === null );

	// hide the alert
	g_ctrlAlert.hide();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {jQuery} holder
 * @param {jQuery} buttonClicked
 * @param {jQuery} previousButton
 */
function clickBoolean( holder, buttonClicked, previousButton )
{
	this.setBooleanActive( previousButton, false );
	this.setBooleanActive( buttonClicked, true );

	holder.data( 'current-value', buttonClicked.data( 'button-value' ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {jQuery} btn
 * @param {boolean} isActive
 */
function setBooleanActive( btn, isActive )
{
	btn.toggleClass( 'btn-primary', isActive )
		.toggleClass( 'btn-active', isActive )
		.toggleClass( 'btn-light', !isActive )
		.toggleClass( 'border', !isActive )
		.toggleClass( 'border-primary', !isActive );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the add/update button
 *
 * @private
 */
function onAddUpdate()
{
	const userName = g_ctrlUserName.val().toString().trim();
	const realName = g_ctrlRealName.val().toString().trim();
	const emailAddress = g_ctrlEmailAddress.val().toString().trim();
	const password = g_ctrlPassword.val().toString().trim();
	const isSiteAdmin = g_ctrlIsAdminHolder.data( 'current-value' );

	let errors = [];

	if( userName.length === 0 )
	{
		errors.push( 'Username cannot be empty!' );
	}

	if( realName.length === 0 )
	{
		errors.push( 'Real name cannot be empty!' );
	}

	if( emailAddress.length === 0 )
	{
		errors.push( 'Email address cannot be empty!' );
	}

	if( password.length === 0 && g_editIndex === -1 )
	{
		errors.push( 'Password cannot be empty!' );
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
		return;
	}
	else
	{
		g_ctrlAlert.hide();
	}

	if( g_editIndex === -1 )
	{
		// add
		runAjaxLdapAdd( userName, realName, emailAddress, password, isSiteAdmin, false );
	}
	else
	{
		// update
		runAjaxLdapUpdate( userName, realName, emailAddress, password, isSiteAdmin, false );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks to delete the user
 *
 * @private
 */
function onDelete()
{
	if( g_editIndex === -1 )
	{
		return;
	}

	runAjaxLdapDelete( g_ldapUsers[ g_editIndex ].uid, false );

	runDeselectEdit();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the button to create the scenario explorer user
 *
 * @private
 */
function onAddScenarioExplorerUser()
{
	const userName = g_ctrlUserName.val().toString().trim();
	const realName = g_ctrlRealName.val().toString().trim();
	const isSiteAdmin = g_ctrlIsAdminHolder.data( 'current-value' );

	runAddScenarioExplorerUser( userName, realName, isSiteAdmin, false );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {string} userName
 * @param {string} realName
 * @param {string} emailAddress
 * @param {?string} password
 * @param {boolean} isSiteAdmin
 * @param {boolean} skipChangesCallback
 */
function runAjaxLdapAdd( userName, realName, emailAddress, password, isSiteAdmin, skipChangesCallback )
{
	const data = {
		'action' : 'new-ldap-user',
		'userName' : userName,
		'fullName' : realName,
		'email' : emailAddress,
		'isSiteAdmin' : isSiteAdmin
	};

	if( password !== null && password.length > 0 )
	{
		data.password = password;
	}

	jQuery.ajax(
		{
			'url' : BACKEND_URI,
			'method' : 'POST',
			'success' : skipChangesCallback ? null : changesCallback.bind( this ),
			'error' : onAjaxError.bind( this ),
			'cache' : false,
			'dataType' : 'json',
			'data' : data
		}
	);

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {string} userName
 * @param {string} realName
 * @param {string} emailAddress
 * @param {?string} password
 * @param {boolean} isSiteAdmin
 * @param {boolean} skipChangesCallback
 */
function runAjaxLdapUpdate( userName, realName, emailAddress, password, isSiteAdmin, skipChangesCallback )
{
	const data = {
		'action' : 'update-ldap-user',
		'userName' : userName,
		'fullName' : realName,
		'email' : emailAddress,
		'isSiteAdmin' : isSiteAdmin
	};

	if( password !== null && password.length > 0 )
	{
		data.password = password;
	}

	jQuery.ajax(
		{
			'url' : BACKEND_URI,
			'method' : 'POST',
			'success' : skipChangesCallback ? null : changesCallback.bind( this ),
			'error' : onAjaxError.bind( this ),
			'cache' : false,
			'dataType' : 'json',
			'data' : data
		}
	);

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {string} userName
 * @param {boolean} skipChangesCallback
 */
function runAjaxLdapDelete( userName, skipChangesCallback )
{
	const data = {
		'action' : 'delete-ldap-user',
		'userName' : userName
	};

	jQuery.ajax(
		{
			'url' : BACKEND_URI,
			'method' : 'POST',
			'success' : skipChangesCallback ? null : changesCallback.bind( this ),
			'error' : onAjaxError.bind( this ),
			'cache' : false,
			'dataType' : 'json',
			'data' : data
		}
	);

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {string} userName
 * @param {string} realName
 * @param {boolean} isSiteAdmin
 * @param {boolean} skipChangesCallback
 */
function runAddScenarioExplorerUser( userName, realName, isSiteAdmin, skipChangesCallback )
{
	const data = {
		'action' : 'new-scenario-explorer-user',
		'userName' : userName,
		'fullName' : realName,
		'isSiteAdmin' : isSiteAdmin
	};

	// fetch the scenario explorer users
	jQuery.ajax(
		{
			'url' : BACKEND_URI,
			'method' : 'POST',
			'success' : skipChangesCallback ? null : changesCallback.bind( this ),
			'error' : onAjaxError.bind( this ),
			'cache' : false,
			'dataType' : 'json',
			'data' : data
		}
	);

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the button to export the LDIF file
 *
 * @private
 */
function onExportLDIF()
{
	window.location.href = 'backend?action=export-ldap-ldif';
	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the button to export the CSV file
 *
 * @private
 */
function onExportCSV()
{
	window.location.href = 'backend?action=export-ldap-csv';
	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function changesCallback()
{
	g_ldapUsers = null;
	g_scenarioExplorerUsers = null;

	fetchUserLists();

	initFormFields( null );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the file importer parses the drag/drop file
 *
 * @private
 *
 * @param {string} filename
 * @param {string} contents
 */
function onFileImport( filename, contents )
{
	if( filename.toLowerCase().endsWith( '.csv' ) )
	{
		this.onFileImportCSV( filename, contents );
		return;
	}
	else
	{
		g_util.showAlertDiv( g_ctrlAlert, [ 'Only CSV file import is supported.' ] );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function onFileImportCSV( filename, contents )
{
	const lines = g_util.parseCSV( contents );

	// 0 = username
	// 1 = real name
	// 2 = email
	// 3 = LDAP DN

	let errors = [];
	let usersAdded = 0;
	for( let i = 0; i < lines.length; ++i )
	{
		if( lines[ i ].length >= 3 )
		{
			const userName = lines[ i ][ 0 ].trim().toString().trim();
			const realName = lines[ i ][ 1 ].trim().toString().trim();
			const emailAddress = lines[ i ][ 2 ].trim().toString().trim();

			if( userName === null || userName.length === 0 )
			{
				errors.push( 'Invalid line(' + ( i + 1 ).toString() + '): Invalid Username: ' + userName )
			}
			else if( realName === null || realName.length === 0 )
			{
				errors.push( 'Invalid line(' + ( i + 1 ).toString() + '): Invalid Real Name: ' + realName )
			}
			else if( emailAddress === null || emailAddress.length === 0 )
			{
				errors.push( 'Invalid line(' + ( i + 1 ).toString() + '): Invalid Email address: ' + emailAddress )
			}
			else
			{
				if( findLdapUser( userName ) === null )
				{
					// user does not already exist
					runAjaxLdapAdd( userName, realName, emailAddress, DEFAULT_IMPORT_PASSWORD, false, true );
					++usersAdded;
				}
				else
				{
					// user already exists
					errors.push( 'User "' + userName + '" already exists -- Not replacing!' );
				}
			}
		}
		else
		{
			errors.push( 'Invalid line(' + ( i + 1 ).toString() + '): ' + lines[ i ].join( ',' ) + ' (require at least 3 columns: username, real name, email)' )
		}
	}

	if( usersAdded > 0 )
	{
		// set these to null and refresh so it shows the loading screen
		g_ldapUsers = null;
		g_scenarioExplorerUsers = null;
		populateLeftBar();
		initFormFields( null );

		// this is a bit of a hack... we aren't using the API which has this handled synchronously, so instead just wait a bit and then refresh
		setTimeout( fetchUserLists.bind( this ), 500 );

		errors.push( usersAdded.toString() + ' user' + ( usersAdded === 1 ? '' : 's' ) + ' added.  Password has been set to: ' + DEFAULT_IMPORT_PASSWORD );
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
	}
	else
	{
		g_ctrlAlert.hide();
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {string} userName
 *
 * @returns {?LdapUser}
 */
function findLdapUser( userName )
{
	for( let i = 0; i < g_ldapUsers.length; ++i )
	{
		if( g_ldapUsers[ i ].uid === userName )
		{
			return g_ldapUsers[ i ];
		}
	}

	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {string} userName
 *
 * @returns {?ScenarioExplorerUser}
 */
function findScenarioExplorerUser( userName )
{
	for( let i = 0; i < g_scenarioExplorerUsers.length; ++i )
	{
		if( g_scenarioExplorerUsers[ i ].userName === userName )
		{
			return g_scenarioExplorerUsers[ i ];
		}
	}

	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
