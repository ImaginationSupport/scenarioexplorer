////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// TODO should move the constants into the canvas so each node doesn't have a separate copy

/**
 * Canvas Tree Node
 *
 * @param {TreeNode} baseNode
 * @constructor
 * @implements {ViewCanvasItem}
 */
function ViewCanvasNode( baseNode )
{
	/**
	 * Holds the base object this node represents
	 *
	 * @const {TreeNode}
	 */
	this.mBase = baseNode;

	/**
	 * Holds the radius
	 *
	 * @const {number}
	 */
	this.RADIUS = 10.0;

	/**
	 * Holds the radius for outcome nodes
	 *
	 * @const {number}
	 */
	this.RADIUS_OUTCOME = 4.0;

	/**
	 * Holds the vertical padding
	 *
	 * @const {number}
	 */
	this.VERTICAL_PADDING = 4.0;

	/**
	 * Holds the ids to the child nodes
	 *
	 * @type {Array< ViewCanvasNode >}
	 */
	this.mChildren = [];

	/**
	 * Holds the date to draw the node (the X value)
	 * @type {Date}
	 */
	this.mDrawDate = null;

	/**
	 * Holds the canvas Y position
	 *
	 * @type {number}
	 */
	this.mY = 0;

	/**
	 * Holds the (sub)tree height
	 *
	 * @type {number}
	 */
	this.mHeight = 0;

	/**
	 * Holds the parent
	 *
	 * @type {?ViewCanvasNode}
	 */
	this.mParent = null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Draws the connecting line from this node to the given parent node
 *
 * @param {ViewCanvas} canvas
 * @param {ViewCanvasPoint} parentPoint
 * @param {boolean} highlighted
 * @param {boolean} drawChildren
 */
ViewCanvasNode.prototype.drawConnectingLine = function( canvas, parentPoint, highlighted, drawChildren )
{
	const thisPoint = canvas.calculateNodePoint( this );

	// draw the line from this node to its parent
	if( parentPoint !== null )
	{
		if( highlighted )
		{
			canvas.mContext.strokeStyle = 'rgba(255,255,0,0.5)';
			canvas.mContext.lineWidth = 2;
		}
		else
		{
			canvas.mContext.strokeStyle = 'rgba(255,255,255,0.5)';
			canvas.mContext.lineWidth = 1;
		}

		canvas.mContext.beginPath();
		canvas.mContext.moveTo( thisPoint.mX, thisPoint.mY );
		canvas.mContext.lineTo( parentPoint.mX, parentPoint.mY );
		canvas.mContext.stroke();
	}

	if( drawChildren )
	{
		for( let i = 0; i < this.mChildren.length; ++i )
		{
			this.mChildren[ i ].drawConnectingLine( canvas, thisPoint, false, true );
		}
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Draws the node
 *
 * @param {ViewCanvas} canvas
 * @param {Array< ViewCanvasItem >} hoveredElements
 * @param {ViewCanvasItem} selectedElement
 */
ViewCanvasNode.prototype.drawNode = function( canvas, hoveredElements, selectedElement )
{
	const thisPoint = canvas.calculateNodePoint( this );

	for( let i = 0; i < this.mChildren.length; ++i )
	{
		this.mChildren[ i ].drawNode( canvas, hoveredElements, selectedElement );
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

	canvas.mContext.beginPath();

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
		canvas.mContext.strokeStyle = 'rgb(220,220,220)';
		canvas.mContext.fillStyle = this.mBase.color;
		canvas.mContext.lineWidth = 1;
	}

	switch( this.mBase.type )
	{
		case TreeNodeType.STATE:
			canvas.mContext.arc( thisPoint.mX, thisPoint.mY, this.RADIUS, 0, 2 * Math.PI, false );
			break;

		case TreeNodeType.OUTCOME:
			canvas.mContext.arc( thisPoint.mX, thisPoint.mY, this.RADIUS_OUTCOME, 0, 2 * Math.PI, false );
			break;

		case TreeNodeType.CONDITIONING_EVENT:
			canvas.mContext.moveTo( thisPoint.mX, thisPoint.mY - this.RADIUS );
			canvas.mContext.lineTo( thisPoint.mX - this.RADIUS, thisPoint.mY );
			canvas.mContext.lineTo( thisPoint.mX, thisPoint.mY + this.RADIUS );
			canvas.mContext.lineTo( thisPoint.mX + this.RADIUS, thisPoint.mY );
			canvas.mContext.lineTo( thisPoint.mX, thisPoint.mY - this.RADIUS );
			break;

		default:
			console.warn( 'Unknown node type: %O', this.mBase.type );
			return;
	}

	canvas.mContext.fill();
	canvas.mContext.stroke();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Calculates the height of this node (stores it internally) and returns the value
 *
 * @returns {number}
 */
ViewCanvasNode.prototype.calculateHeight = function()
{
	if( this.mChildren.length === 0 )
	{
		this.mHeight = this.RADIUS * 2;
	}
	else
	{
		this.mHeight = 0;

		for( let i = 0; i < this.mChildren.length; ++i )
		{
			if( i > 0 )
			{
				this.mHeight += this.VERTICAL_PADDING;
			}

			this.mHeight += this.mChildren[ i ].calculateHeight();
		}
	}

	return this.mHeight;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Recursively calculates the Y-position of child nodes
 */
ViewCanvasNode.prototype.calculateChildNodesY = function()
{
	let yWorking = 0;

	for( let i = 0; i < this.mChildren.length; ++i )
	{
		this.mChildren[ i ].mY = this.mY + -this.mHeight / 2 + this.mChildren[ i ].mHeight / 2 + yWorking;

		yWorking += this.mChildren[ i ].mHeight + this.VERTICAL_PADDING;
	}

	for( let i = 0; i < this.mChildren.length; ++i )
	{
		this.mChildren[ i ].calculateChildNodesY();
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Tests if the given canvas point is inside this node
 *
 * @param {ViewCanvasPoint} canvasPoint
 * @param {ViewCanvas} canvas
 *
 * @returns {?ViewCanvasNode} the node found, otherwise null
 */
ViewCanvasNode.prototype.hitTest = function( canvasPoint, canvas )
{
	const myPoint = canvas.calculateNodePoint( this );

	if( canvasPoint.distanceFrom( myPoint ) <= this.RADIUS )
	{
		return this;
	}

	for( let i = 0; i < this.mChildren.length; ++i )
	{
		let result = this.mChildren[ i ].hitTest( canvasPoint, canvas );
		if( result !== null )
		{
			return result;
		}
	}

	return null;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the unique id of the base object
 *
 * @returns {string}
 */
ViewCanvasNode.prototype.getBaseObjectId = function()
{
	return this.mBase.id;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the setup to display as a hover popup
 *
 * @param {ViewCanvas} canvas
 * @returns {ViewCanvasItem.hoverDetails}
 */
ViewCanvasNode.prototype.getHoverDetails = function( canvas )
{
	const myPoint = canvas.calculateNodePoint( this );

	const additionalTextLines = [];

	const dateTimeFormat = canvas.mDateTimeFormat == null ? g_util.DEFAULT_DATETIME_FORMAT : canvas.mDateTimeFormat;

	switch( this.mBase.type )
	{
		case TreeNodeType.STATE:
			let state = /** @type {StateNode} */( this.mBase );
			myPoint.mY -= this.RADIUS;
			additionalTextLines.push( 'Start: ' + g_util.formatDate( state.start, dateTimeFormat ) );
			additionalTextLines.push( 'End:   ' + g_util.formatDate( state.end, dateTimeFormat ) );
			break;

		case TreeNodeType.CONDITIONING_EVENT:
			myPoint.mY -= this.RADIUS;
			break;

		case TreeNodeType.OUTCOME:
			myPoint.mY -= this.RADIUS_OUTCOME;
			break;

		default:
			console.warn( 'Unknown node type: %O', this.mBase.type );
			break;
	}

	const trajectoryNodes = this.getTrajectoryNodes();
	if( trajectoryNodes.length > 1 )
	{
		additionalTextLines.push( '' );

		for( let i = 0; i < trajectoryNodes.length - 1; ++i )
		{
			if( trajectoryNodes[ i ].mBase instanceof ConditioningEventNode )
			{
				additionalTextLines.push( trajectoryNodes[ i ].mBase.name + ': ' + trajectoryNodes[ i + 1 ].mBase.name );
				++i;
			}
		}
	}

	return {
		'mPoint' : myPoint,
		'mLabel' : this.mBase.name,
		'mDescription' : this.mBase.description,
		'mAdditionalTextLines' : additionalTextLines
	};
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @returns {Array< ViewCanvasNode >}
 */
ViewCanvasNode.prototype.getTrajectoryNodes = function()
{
	const nodes = this.mParent === null
		? []
		: this.mParent.getTrajectoryNodes();

	nodes.push( this );

	return nodes;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
