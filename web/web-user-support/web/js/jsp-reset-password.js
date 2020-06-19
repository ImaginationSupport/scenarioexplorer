////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function initJSP()
{
	$( '#form-cancel' ).on( 'click', function()
	{
		window.location = '/scenarioexplorer/index.jsp';
		return;
	} );

	$( '#form-submit' ).on( 'click', runSubmit );

	const username = getUriParameter( 'userName' );
	if( username )
	{
		$( '#form-username' ).val( username );
	}

	const email = getUriParameter( 'email' );
	if( email )
	{
		$( '#form-email' ).val( email );
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
	const email = $( '#form-email' ).val().toString().trim();

	if( username.length === 0 && email.length === 0 )
	{
		$( '#form-error-alert' )
			.text( 'Please enter your username or email address.' )
			.removeClass( 'd-none' );
		return false;
	}
	else if( username.length > 0 && email.length > 0 )
	{
		$( '#form-error-alert' )
			.text( 'Please enter only one or the other.' )
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

	// console.log( queryString );

	if( queryString.length > 0 )
	{
		let queryStringSplit = queryString.split( '&' );
		for( i = 0; i < queryStringSplit.length; ++i )
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
