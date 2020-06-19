////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @param {TimelineEvent} baseTimelineEvent
 * @constructor
 * @implements {ViewCanvasItem}
 */
function ViewCanvasTimelineEvent( baseTimelineEvent )
{
	/**
	 * Holds the base timeline event
	 *
	 * @type {TimelineEvent}
	 */
	this.mBase = baseTimelineEvent;

	/**
	 * @type {number} Holds the line numb    er assigned
	 */
	this.mLine = 0;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the unique id of the base object
 *
 * @return {string}
 */
ViewCanvasTimelineEvent.prototype.getBaseObjectId = function()
{
	return /** @type {string} */( this.mBase.id );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the setup to display as a hover popup
 *
 * @param {ViewCanvas} canvas
 * @return {ViewCanvasItem.hoverDetails}
 */
ViewCanvasTimelineEvent.prototype.getHoverDetails = function( canvas )
{
	const bounds = canvas.calculateTimelineEventBounds( this );

	const dateTimeFormat = canvas.mDateTimeFormat == null ? g_util.DEFAULT_DATETIME_FORMAT : canvas.mDateTimeFormat;

	const additionalTextLines = [];
	additionalTextLines.push( 'Start: ' + g_util.formatDate( this.mBase.start, dateTimeFormat ) );
	additionalTextLines.push( 'End:   ' + g_util.formatDate( this.mBase.end, dateTimeFormat ) );

	return {
		'mPoint' : new ViewCanvasPoint( bounds.mX, bounds.mY ),
		'mLabel' : this.mBase.name,
		'mDescription' : this.mBase.description,
		'mAdditionalTextLines' : additionalTextLines
	};
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Tests if the given timeline event overlaps with this timeline event
 *
 * @param {ViewCanvasTimelineEvent} canvasTimelineEvent
 *
 * @return {boolean} true if it overlaps, otherwise false
 */
ViewCanvasTimelineEvent.prototype.testOverlap = function( canvasTimelineEvent )
{
	if( canvasTimelineEvent.mLine !== this.mLine )
	{
		return false;
	}

	// check a simple overlap, where the start of one is inside the other
	if(
		g_util.dateBetween( this.mBase.start, this.mBase.end, canvasTimelineEvent.mBase.start )
		|| g_util.dateBetween( this.mBase.start, this.mBase.end, canvasTimelineEvent.mBase.end )
	)
	{
		return true;
	}

	// check for total overlaps
	return ( canvasTimelineEvent.mBase.start.getTime() < this.mBase.start.getTime() && canvasTimelineEvent.mBase.end.getTime() > this.mBase.end.getTime() )
		|| ( this.mBase.start.getTime() < canvasTimelineEvent.mBase.start.getTime() && this.mBase.end.getTime() > canvasTimelineEvent.mBase.end.getTime() );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {ViewCanvas} canvas
 * @param {Array< ViewCanvasItem >} hoveredElements
 * @param {ViewCanvasItem} selectedElement

 */
ViewCanvasTimelineEvent.prototype.draw = function( canvas, hoveredElements, selectedElement )
{
	if( this.mBase.start === null || this.mBase.end === null )
	{
		return;
	}

	let isHovered = false;
	const isSelected = selectedElement !== null && selectedElement.getBaseObjectId() === this.getBaseObjectId();

	for( let i = 0; i < hoveredElements.length; ++i )
	{
		if( hoveredElements[ i ].getBaseObjectId() === this.getBaseObjectId() )
		{
			isHovered = true;
			break;
		}
	}

	const bounds = canvas.calculateTimelineEventBounds( this );

	if( isSelected )
	{
		canvas.mContext.strokeStyle = 'rgb(255,255,255)';
		canvas.mContext.fillStyle = 'rgb(255,255,0)';
		canvas.mContext.lineWidth = 2;
	}
	else if( isHovered )
	{
		canvas.mContext.strokeStyle = 'rgb(255,255,255)';
		canvas.mContext.fillStyle = this.mBase.color;
		canvas.mContext.lineWidth = 2;
	}
	else
	{
		canvas.mContext.fillStyle = this.mBase.color;
		canvas.mContext.strokeStyle = this.mBase.color;
		canvas.mContext.lineWidth = 1;
	}

	canvas.mContext.fillRect( bounds.mX, bounds.mY, bounds.mWidth, bounds.mHeight );

	canvas.mContext.beginPath();
	canvas.mContext.moveTo( bounds.mX, bounds.mY );
	canvas.mContext.lineTo( bounds.mX + bounds.mWidth, bounds.mY );
	canvas.mContext.lineTo( bounds.mX + bounds.mWidth, bounds.mY + bounds.mHeight );
	canvas.mContext.lineTo( bounds.mX, bounds.mY + bounds.mHeight );
	canvas.mContext.lineTo( bounds.mX, bounds.mY );
	canvas.mContext.stroke();

	const maxText = canvas.determineMaxText( this.mBase.name, bounds.mWidth - bounds.mPadding * 2 );

	if( maxText !== null && maxText.length > 0 )
	{
		canvas.mContext.fillStyle = isSelected ? 'rgb(0,0,0)' : 'rgb(255,255,255)'; // TODO should be configurable
		canvas.mContext.fillText( maxText, bounds.mX + bounds.mPadding, bounds.mY + bounds.mHeight - bounds.mPadding );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @param {ViewCanvasPoint} canvasPoint
 * @param {ViewCanvas} canvas
 * @returns {?ViewCanvasTimelineEvent}
 */
ViewCanvasTimelineEvent.prototype.hitTest = function( canvasPoint, canvas )
{
	const bounds = canvas.calculateTimelineEventBounds( this );

	return canvasPoint.mX >= bounds.mX
	&& canvasPoint.mX <= bounds.mX + bounds.mWidth
	&& canvasPoint.mY >= bounds.mY
	&& canvasPoint.mY <= bounds.mY + bounds.mHeight
		? this
		: null;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
