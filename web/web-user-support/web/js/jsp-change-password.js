////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function initJSP()
{
	$( '#form-cancel' ).click( function()
	{
		window.location = '/scenarioexplorer/index.jsp';
		return;
	} );

	$( '#form-submit' ).on( 'click', runSubmit.bind( this ) );

	const username = getUriParameter( 'userName' );
	if( username )
	{
		$( '#form-username' ).val( username );
	}

	const errorMessage = getUriParameter( 'message' );
	if( errorMessage )
	{
		$( '#form-error-alert' )
			.text( errorMessage )
			.removeClass( 'd-none' );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function runSubmit()
{
	const username = $( '#form-username' ).val().toString().trim();
	const passwordCurrent = $( '#form-current-password' ).val().toString().trim();
	const passwordNew = $( '#form-new-password' ).val().toString().trim();
	const passwordNewAgain = $( '#form-new-password-again' ).val().toString().trim();

	if( username.length === 0 )
	{
		$( '#form-error-alert' )
			.text( 'Please enter your username.' )
			.removeClass( 'd-none' );
		return false;
	}
	else if( passwordCurrent.length === 0 )
	{
		$( '#form-error-alert' )
			.text( 'Please enter your current password.' )
			.removeClass( 'd-none' );
		return false;
	}
	else if( passwordNew !== passwordNewAgain )
	{
		$( '#form-error-alert' )
			.text( 'New passwords do not match!' )
			.removeClass( 'd-none' );
		return false;
	}
	else if( passwordNew.length === 0 )
	{
		$( '#form-error-alert' )
			.text( 'Please enter your new password.' )
			.removeClass( 'd-none' );
		return false;
	}

	$( '#reset-password-form' ).trigger( 'submit' );

	return false;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {string} parameterName - the parameter to get
 *
 * @returns {string|null}
 */
function getUriParameter( parameterName ) // TODO this is copy/paste from navhelper.js?!
{
	const currentUri = window.location.href.toString();

	// parse the URI
	const queryStringStart = currentUri.indexOf( '?' );
	let queryString = queryStringStart === -1
		? ''
		: currentUri.substr( queryStringStart + 1 );

	const queryStringEnd = queryString.indexOf( '#' );
	if( queryStringEnd > -1 )
	{
		queryString = queryString.substr( 0, queryStringEnd );
	}

	if( queryString.length > 0 )
	{
		let queryStringSplit = queryString.split( '&' );
		for( let i = 0; i < queryStringSplit.length; ++i )
		{
			let paramSplit = queryStringSplit[ i ].split( '=' );
			if( paramSplit.length === 2 )
			{
				if( paramSplit[ 0 ] === parameterName )
				{
					return decodeURIComponent( paramSplit[ 1 ] ).replace( new RegExp( '[+]', 'g' ), ' ' );
				}
			}
		}
	}

	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
