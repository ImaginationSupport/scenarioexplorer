goog.provide( 'imaginationsupport.start' );

imaginationsupport.start = function()
{
	goog.require( 'imaginationsupport.Core' );

	$(
		function()
		{
			const imaginationSupport = new imaginationsupport.Core();
			imaginationSupport.init( "", true );
			return;
		}
	);

	return;
};

goog.exportSymbol( 'imaginationsupport.start', imaginationsupport.start );
