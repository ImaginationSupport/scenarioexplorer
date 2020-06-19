////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * The Scenario Explorer Canvas library
 *
 * @constructor
 */
function ViewCanvas()
{
	/**
	 * @private
	 * @const {number}
	 */
	this.REDRAW_LOOP_INTERVAL = 20;

	/**
	 * Holds the string used to prefix all node id's in the lookup so that the lookup table doesn't try to parse the number as an integer
	 *
	 * @type {string}
	 */
	this.NODE_LOOKUP_PREFIX = 'n-';

	/**
	 * Holds the canvas element
	 *
	 * @type {?jQuery}
	 */
	this.mCanvasElementHolder = null;

	/**
	 * Holds the canvas element
	 *
	 * @type {?jQuery}
	 */
	this.mCanvasElement = null;

	/**
	 * Holds the canvas context
	 *
	 * @type {?CanvasRenderingContext2D}
	 */
	this.mContext = null;

	/**
	 * Holds the callback function when a node is selected
	 *
	 * @private
	 * @type {?function(TreeNode)}
	 */
	this.mOnSelectNodeCallback = null;

	/**
	 * Holds the callback function when a timeline event is selected
	 *
	 * @private
	 * @type {?function(TimelineEvent)}
	 */
	this.mOnSelectTimelineEventCallback = null;

	/**
	 * Holds the callback function when the selected item is deselected
	 *
	 * @private
	 * @type {?function()}
	 */
	this.mOnDeselectCallback = null;

	/**
	 * Holds the number of milliseconds in an hour, used to translate to/from the data
	 *
	 * @const {number}
	 */
	this.MILLISECONDS_IN_HOUR = 60 * 60 * 1000;

	/**
	 * Holds the padding amount to use, in pixels
	 *
	 * @const {number}
	 */
	this.PADDING = 4;

	/**
	 * Holds the size of the hover popup arrow
	 *
	 * @const {number}
	 */
	this.HOVER_POPUP_ARROW_SIZE = 10;

	/**
	 * Holds the month names to use for the indicators
	 *
	 * @const {Array<string>}
	 */
	this.INDICATOR_MONTH_NAMES = [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sept', 'Oct', 'Nov', 'Dec' ];

	/**
	 * Holds the canvas width, in pixels
	 *
	 * @private
	 * @type {number}
	 */
	this.mWidth = -1;

	/**
	 * Holds the canvas height, in pixels
	 *
	 * @private
	 * @type {number}
	 */
	this.mHeight = -1;

	/**
	 * Holds the main background color
	 *
	 * @private
	 * @type {string}
	 */
	this.mBackgroundColor = 'rgb(52,58,64)';

	/**
	 * Holds the background alternate color
	 *
	 * @private
	 * @type {string}
	 */
	this.mBackgroundColorAlt = 'rgb(60,60,60)';

	/**
	 * Holds the background alternate color
	 *
	 * @private
	 * @type {string}
	 */
	this.mAccentColor = 'rgb(1,122,215)';

	/**
	 * Holds the text color
	 *
	 * @type {string}
	 */
	this.mTextColor = 'rgb(240,240,240)';

	/**
	 * Holds the size of the text, in points
	 *
	 * @type {number}
	 */
	this.mFontSize = 11;

	/**
	 * Holds the font face
	 *
	 * @type {string}
	 */
	this.mFontFace = 'Verdana';

	/**
	 * Holds the maximum zoom level
	 *
	 * @const {number}
	 */
	this.mMaxZoom = 100.0;

	/**
	 * Holds the minimum zoom level
	 *
	 * @const {number}
	 */
	this.mMinZoom = 0.7;

	/**
	 * Holds the number of zoom mouse wheel steps to go from the min to the max
	 *
	 * @const {number}
	 */
	this.mNumZoomSteps = 25;

	/**
	 * Holds if the canvas needs redrawn
	 *
	 * @private
	 * @type {boolean}
	 */
	this.mRedrawNeeded = true;

	/**
	 * @private
	 * @dict
	 */
	this.mTreeNodeLookup = null;

	/**
	 * Holds the root node index, or null if not set
	 *
	 * @private
	 * @type {?string}
	 */
	this.mRootNodeId = null;

	/**
	 * Holds the processed timeline events
	 *
	 * @const {Array< ViewTimelineEvent >}
	 */
	this.mTimelineEvents = [];

	/**
	 * Holds the starting date of the view data
	 *
	 * @type {?Date}
	 */
	this.mViewBoundsDateStart = null;

	/**
	 * Holds the end date of the view data
	 *
	 * @type {?Date}
	 */
	this.mViewBoundsDateEnd = null;

	/**
	 * Holds the height of the timeline, in pixels
	 *
	 * @private
	 * @type {number}
	 */
	this.mTimelineHeight = 0;

	/**
	 * Holds the date of the left side of the view
	 *
	 * @type {?Date}
	 */
	this.mViewLeftTime = null;

	/**
	 * Holds the top of the tree
	 *
	 * @private
	 * @type {number}
	 */
	this.mViewTopOffset = 0;

	/**
	 * Holds the view zoom level
	 *
	 * @private
	 * @type {number}
	 */
	this.mZoom = 1.0;

	/**
	 * Holds the current zoom step
	 *
	 * @type {number}
	 */
	this.mZoomStep = -1;

	/**
	 * @private
	 * @type {Array< ViewCanvasItem >}
	 */
	this.mHoveredElements = [];

	/**
	 * @private
	 * @type {?ViewCanvasItem}
	 */
	this.mSelectedElement = null;

	this.mDragTarget = null;

	/**
	 * Holds if we are dragging
	 *
	 * @type {boolean}
	 */
	this.mIsDragging = false;

	/**
	 * Holds the canvas location of the last drag event
	 *
	 * @type {?ViewCanvasPoint}
	 */
	this.mDragLastCanvasPoint = null;

	/**
	 * Holds the datetime format to use
	 *
	 * @type {?string}
	 */
	this.mDateTimeFormat = null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * @typedef {function(Node):ViewCanvasPoint}
//  */
// ViewCanvas.calculateNodePointCallback;
//
// /**
//  *@typedef {function(ViewTimelineEvent):{x: number, y: number, width: number, height: number, padding: number}}
//  */
// ViewCanvas.calculateTimelineEventBoundsCallback;

// /**
//  * @typedef {null|ViewTimelineEvent|Node}
//  */
// ViewCanvas.canvasElement;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the View Canvas
 *
 * @param {jQuery} canvasHolder
 * @param {function(string)} onSelectNodeCallback
 * @param {function(string)} onSelectTimelineEventCallback
 * @param {function()} onDeselectCallback
 */
ViewCanvas.prototype.init = function( canvasHolder, onSelectNodeCallback, onSelectTimelineEventCallback, onDeselectCallback )
{
	this.mCanvasElementHolder = canvasHolder;
	this.mOnSelectNodeCallback = onSelectNodeCallback;
	this.mOnSelectTimelineEventCallback = onSelectTimelineEventCallback;
	this.mOnDeselectCallback = onDeselectCallback;

	const canvasSelector = canvasHolder.find( 'canvas' );
	this.mCanvasElement = canvasSelector[ 0 ];

	this.mContext = this.mCanvasElement.getContext( '2d' );

	$( window ).on( 'resize', this.onResize.bind( this ) );

	$( document ).on( 'mouseup', this.onMouseUp.bind( this ) );

	this.onResize();

	this.runRedrawLoop( true );

	// set up the timer to run the redraw loop (kill a previous one if needed)
	setInterval( this.runRedrawLoop.bind( this, false ), this.REDRAW_LOOP_INTERVAL );

	// register the mouse event callbacks
	canvasSelector
		.on( 'mousemove', this.onMouseMove.bind( this ) )
		.on( 'mousedown', this.onMouseDown.bind( this ) )
		.on( 'mouseup', this.onMouseUp.bind( this ) )
		.on( 'dblclick', this.onDoubleClick.bind( this ) );
	this.mCanvasElement.addEventListener( 'mousewheel', this.onMouseWheel.bind( this ), false );
	this.mCanvasElement.addEventListener( 'DOMMouseScroll', this.onMouseWheel.bind( this ), false );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the timeline events
 *
 * @param {Array< TimelineEvent >} timelineEvents
 */
ViewCanvas.prototype.updateTimelineEvents = function( timelineEvents )
{
	this.mTimelineEvents.length = 0;

	if( timelineEvents === null )
	{
		return;
	}

	let linesNeeded = 0;
	for( let i = 0; i < timelineEvents.length; ++i )
	{
		const newEvent = new ViewCanvasTimelineEvent( timelineEvents[ i ] );

		let found = false;

		// so we need to find a line to put this new event on, so test each line...
		for( let line = 0; line < linesNeeded; ++line )
		{
			// temporarily set the new event to be on this line because it's used in the .testOverlap()
			newEvent.mLine = line;

			let overlapFound = false;

			// then test all events that have already been placed to see if they overlap...
			for( let j = 0; j < this.mTimelineEvents.length; ++j )
			{
				if( this.mTimelineEvents[ j ].testOverlap( newEvent ) )
				{
					// this overlaps, so this line is a fail
					overlapFound = true;
					break;
				}
			}

			// no overlap was found on this line, so keep it here
			if( !overlapFound )
			{
				found = true;
				this.mTimelineEvents.push( newEvent );
				break;
			}
		}

		// could not find a spot in any of the existing lines, so add a new line and put the timeline event here
		if( !found )
		{
			linesNeeded++;

			newEvent.mLine = linesNeeded - 1;
			this.mTimelineEvents.push( newEvent );
		}
	}

	this.mTimelineHeight = linesNeeded === 0
		? 0
		: linesNeeded * ( this.mFontSize + this.PADDING * 4 ) + this.PADDING * 2;

	// this.calculateDataBounds();
	// this.fitToScreen(); // TODO remove?

	this.redraw();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Sets the base view bounds (data can go outside this, but we won't try to scale to it)
 *
 * @param {Date} startDate
 * @param {Date} endDate
 */
ViewCanvas.prototype.setBaseViewBounds = function( startDate, endDate )
{
	if( startDate === null || endDate === null )
	{
		console.warn( 'Canvas base view bounds cannot be null!' );
		return;
	}

	this.mViewBoundsDateStart = startDate;
	this.mViewBoundsDateEnd = endDate;

	// var numMonths = ( this.mViewBoundsDateEnd.getTime() - this.mViewBoundsDateStart.getTime() ) / this.MILLISECONDS_IN_HOUR / 24 / 30;
	// console.log( 'num months: %d', numMonths );

	this.fitToScreen();
	// console.log( 'zoom: %O', this.mZoom );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Sets the datetime format used in the canvas
 *
 * @param {string} dateTimeFormat the new datetime format
 */
ViewCanvas.prototype.setDateTimeFormat = function( dateTimeFormat )
{
	this.mDateTimeFormat = dateTimeFormat;

	this.mRedrawNeeded = true;

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the canvas with the given view
 *
 * @param {ViewTree} viewTree
 */
ViewCanvas.prototype.updateView = function( viewTree )
{
	this.mRootNodeId = viewTree.rootNodeId;

	this.mTreeNodeLookup = {};
	this.mSelectedElement = null;
	this.mHoveredElements = [];

	// create the canvas tree nodes
	for( let i = 0; i < viewTree.treeNodes.length; ++i )
	{
		if( ( this.NODE_LOOKUP_PREFIX + viewTree.treeNodes[ i ].id ) in this.mTreeNodeLookup )
		{
			console.warn( 'Invalid tree!  Duplicate id found: %O at index %d', viewTree.treeNodes[ i ].id, i );
			return;
		}

		this.mTreeNodeLookup[ this.NODE_LOOKUP_PREFIX + viewTree.treeNodes[ i ].id ] = new ViewCanvasNode( viewTree.treeNodes[ i ] );
	}

	// now find all the children
	for( let i = 0; i < viewTree.treeNodes.length; ++i )
	{
		// var at = viewTree.treeNodes[ i ];

		if( viewTree.treeNodes[ i ].parentId !== null )
		{
			// console.log( 'looking for parent: %O', viewTree.treeNodes[ i ].parentId );

			const parent = /** @type {?ViewCanvasNode} */( this.findNode( viewTree.treeNodes[ i ].parentId, false ) );
			const child = /** @type {?ViewCanvasNode} */( this.findNode( viewTree.treeNodes[ i ].id, false ) );

			child.mParent = parent;

			parent.mChildren.push( child );
		}

		// console.log( 'after index: %d', i );
		// this.logTree( 999 );
		//
		// debugger;
	}

	// now calculate all of the draw dates
	for( let i = 0; i < viewTree.treeNodes.length; ++i )
	{
		const node = this.findNode( viewTree.treeNodes[ i ].id, false );

		switch( node.mBase.type )
		{
			case TreeNodeType.STATE:
				const state = /** @type {StateNode} */( node.mBase );

				if( state.start === null || state.end === null )
				{
					console.warn( 'State has null dates!' );
				}
				else
				{
					if( node.mChildren.length === 0 )
					{
						// if this is a leaf node, draw it at the project end date
						node.mDrawDate = this.mViewBoundsDateEnd;
					}
					else
					{
						// draw the state at the center of the start/end dates
						node.mDrawDate = new Date( Math.floor( ( state.start.getTime() + state.end.getTime() ) / 2 ) );
					}
				}
				break;

			case TreeNodeType.OUTCOME:
				// do nothing here, it will be set when we look at the parent (the conditioning event)
				break;

			case TreeNodeType.CONDITIONING_EVENT:
				if( node.mParent === null )
				{
					console.warn( 'Conditioning event parent has null parent!' );
				}
				else
				{
					const state = /** @type {StateNode} */( node.mParent.mBase );

					if( state.end === null )
					{
						console.warn( 'Conditioning event state has null end date!' );
					}
					else
					{
						// draw conditioning events at the end of the state
						node.mDrawDate = state.end;
					}
				}

				// now set the draw dates for all the children
				if( node.mChildren.length > 0 )
				{
					// find the earliest child state start date
					const state = /** @type {StateNode} */( node.mChildren[ 0 ].mChildren[ 0 ].mBase );
					let earliestChildStateStart = state.start;
					for( let j = 1; j < node.mChildren.length; ++j )
					{
						const state = /** @type {StateNode} */( node.mChildren[ j ].mChildren[ 0 ].mBase );

						if( state.start.getTime() < earliestChildStateStart.getTime() )
						{
							earliestChildStateStart = state.start;
						}
					}

					const outcomeNodeDrawDate = new Date( Math.min(
						( node.mDrawDate.getTime() + earliestChildStateStart.getTime() ) / 2,
						node.mDrawDate.getTime() + 1000 * 60 * 60 * 24 * 4
					) );

					// now set all outcomes to half of that distance
					for( let j = 0; j < node.mChildren.length; ++j )
					{
						node.mChildren[ j ].mDrawDate = outcomeNodeDrawDate;
					}
				}
				break;

			default:
				console.warn( 'Unknown node type: %O', node.mBase.type );
				break;
		}
	}

	if( this.getRootNode() === null )
	{
		return;
	}

	this.getRootNode().calculateHeight();

	this.recalculateNodeLocations();

	this.fitToScreen();

	this.redraw();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @returns {?Node}
 */
ViewCanvas.prototype.getRootNode = function()
{
	if( this.mRootNodeId === null || this.mTreeNodeLookup === null )
	{
		return null;
	}

	return /** @type {?Node} */( this.findNode( this.mRootNodeId, false ) );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Finds the canvas tree node with the given id
 *
 * @param {?string} id
 * @param {boolean} includeTimelineEvents
 * @returns {ViewCanvasNode|ViewCanvasTimelineEvent|null}
 */
ViewCanvas.prototype.findNode = function( id, includeTimelineEvents )
{
	if( !id )
	{
		return null;
	}

	const key = this.NODE_LOOKUP_PREFIX + id;
	if( key in this.mTreeNodeLookup )
	{
		return this.mTreeNodeLookup[ key ];
	}

	if( includeTimelineEvents )
	{
		for( let i = 0; i < this.mTimelineEvents.length; ++i )
		{
			if( id === this.mTimelineEvents[ i ].mBase.id )
			{
				return this.mTimelineEvents[ i ];
			}
		}
	}

	console.warn( 'could not find tree node with id: %O', id );
	return null;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
ViewCanvas.prototype.fitToScreen = function()
{
	const rootNode = this.getRootNode();
	const treeDateBounds = this.findNodeDateBounds( rootNode );

	this.mViewTopOffset = ( this.mHeight - this.mTimelineHeight ) / 2;

	if(
		this.mWidth > 0
		&& this.mHeight > 0
		&& this.mViewBoundsDateStart !== null
		&& this.mViewBoundsDateEnd !== null
		&& treeDateBounds !== null
		&& treeDateBounds.min !== null
		&& treeDateBounds.max !== null
	)
	{
		let xPadding = ( treeDateBounds.max.getTime() - treeDateBounds.min.getTime() ) / 20;

		this.mViewLeftTime = new Date( treeDateBounds.min.getTime() - xPadding );

		let idealZoom = ( treeDateBounds.max.getTime() - treeDateBounds.min.getTime() ) / this.MILLISECONDS_IN_HOUR / this.mWidth.toFixed( 2 );

		// find the closest zoom step to the ideal zoom
		for( let i = 0; i < this.mNumZoomSteps; ++i )
		{
			if( this.calcZoomAtStep( i ) > idealZoom )
			{
				this.setZoomStep( i );
				break;
			}
		}
	}
	else
	{
		this.mViewLeftTime = this.mViewBoundsDateStart;

		// add a bit of padding
		if( this.mViewLeftTime !== null )
		{
			this.mViewLeftTime = new Date( this.mViewLeftTime.getTime() - 2 * 24 * this.MILLISECONDS_IN_HOUR );
		}

		this.setZoomStep( 8 );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {?ViewCanvasNode} node
 * @returns {{min:Date, max:Date}|null}
 */
ViewCanvas.prototype.findNodeDateBounds = function( node )
{
	if( node === null )
	{
		return null;
	}

	const bounds = {
		min : null,
		max : null
	};

	if( node.mBase.type === TreeNodeType.STATE )
	{
		const state = /** @type {StateNode} */( node.mBase );
		bounds.min = state.start;
		bounds.max = state.end;
	}

	for( let i = 0; i < node.mChildren.length; ++i )
	{
		const childBounds = this.findNodeDateBounds( node.mChildren[ i ] );
		if( childBounds !== null )
		{
			if( childBounds.min !== null && ( bounds.min === null || childBounds.min < bounds.min ) )
			{
				bounds.min = childBounds.min;
			}

			if( childBounds.max !== null && ( bounds.max === null || childBounds.max > bounds.max ) )
			{
				bounds.max = childBounds.max;
			}
		}
	}

	return bounds;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

ViewCanvas.prototype.setZoomStep = function( newZoomStep )
{
	if( newZoomStep === this.mZoomStep )
	{
		return;
	}

	this.mZoomStep = Math.min( Math.max( newZoomStep, 0 ), this.mNumZoomSteps - 1 );
	this.mZoom = this.calcZoomAtStep( this.mZoomStep );

	// console.log( 'new zoom: %s (step: %d)', this.mZoom.toFixed( 2 ), this.mZoomStep );

	this.recalculateNodeLocations();

	this.redraw();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

ViewCanvas.prototype.calcZoomAtStep = function( zoomStep )
{
	return Math.exp(
		Math.log( this.mMinZoom )
		+ ( Math.log( this.mMaxZoom ) - Math.log( this.mMinZoom ) ) * zoomStep / ( this.mNumZoomSteps - 1 )
	);
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Resizes the canvas when the DOM element resizes
 */
ViewCanvas.prototype.onResize = function()
{
	this.mWidth = this.mCanvasElement.clientWidth;
	this.mHeight = this.mCanvasElement.clientHeight;

	this.mContext.canvas.width = this.mWidth;
	this.mContext.canvas.height = this.mHeight;

	this.runRedrawLoop( true );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redraw the canvas (not immediate, only sets the flag to redraw on the next loop interval)
 */
ViewCanvas.prototype.redraw = function()
{
	this.mRedrawNeeded = true;

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {Array< string >} elementIds
 */
ViewCanvas.prototype.setHoveredElements = function( elementIds )
{
	this.mHoveredElements.length = 0;

	for( let i = 0; i < elementIds.length; ++i )
	{
		const elementFound = this.findNode( elementIds[ i ], true );
		if( elementFound === null )
		{
			console.warn( 'Unknown node id to highlight: %O', elementIds[ i ] );
		}
		else
		{
			this.mHoveredElements.push( elementFound );
		}
	}

	this.mRedrawNeeded = true;

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

ViewCanvas.prototype.setSelectedElement = function( elementId )
{
	this.mSelectedElement = this.findNode( elementId, true );

	this.mRedrawNeeded = true;

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Actually redraws the canvas
 *
 * @private
 * @param {boolean} forceRedraw true to force the redraw, false to only redraw if the redraw flag was set
 */
ViewCanvas.prototype.runRedrawLoop = function( forceRedraw )
{
	// make sure the canvas is valid
	if( this.mWidth <= 0 || this.mHeight <= 0 )
	{
		return;
	}

	// only redraw if the flag is set, or we are forcing the redraw
	if( !forceRedraw && !this.mRedrawNeeded )
	{
		return;
	}

	// save the current settings
	this.mContext.save();

	// wipe the background
	this.mContext.fillStyle = this.mBackgroundColor;
	this.mContext.fillRect( 0, 0, this.mWidth, this.mHeight );

	// restore the original settings
	this.mContext.restore();

	let loadingDataMessage = 'Loading data, please wait...';
	if( ( this.mRootNodeId === null || this.mTreeNodeLookup === null ) && this.mTimelineEvents.length === 0 )
	{
		// draw the text
		this.mContext.font = this.mFontSize + 'pt ' + this.mFontFace;
		this.mContext.fillStyle = this.mTextColor;
		this.mContext.fillText( loadingDataMessage, this.mWidth / 2 - this.mContext.measureText( loadingDataMessage ).width / 2, this.mHeight / 2 );
	}
	else
	{
		// draw the time indicators
		this.drawTimeIndicatorsHelper();

		// draw the tree
		this.drawTreeHelper();

		// draw the time line
		this.drawTimelineHelper();

		if( this.mHoveredElements.length === 1 )
		{
			this.drawHoverPopup( this.mHoveredElements[ 0 ] );
		}
	}

	// set that we don't need to redraw again
	this.mRedrawNeeded = false;

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Draws the time indicators
 *
 * @private
 */
ViewCanvas.prototype.drawTimeIndicatorsHelper = function()
{
	this.mContext.save();

	this.mContext.font = this.mFontSize + 'pt ' + this.mFontFace;

	if( this.mViewLeftTime === null )
	{
		// draw the line
		this.mContext.strokeStyle = 'rgb(255,255,255)';
		this.mContext.beginPath();
		this.mContext.moveTo( this.mWidth / 2, 0 );
		this.mContext.lineTo( this.mWidth / 2, this.mHeight );
		this.mContext.stroke();

		// draw the text
		this.mContext.fillStyle = 'rgba(255,255,255,0.5)'; // TODO should be configurable
		this.mContext.fillText(
			'Now',
			this.mWidth / 2 + this.PADDING,
			this.mHeight - this.mTimelineHeight - this.PADDING * 2
		);

		// restore the original settings
		this.mContext.restore();

		return;
	}

	const indicatorMinDate = new Date( this.convertCanvasXToTime( 0 ).getTime() - 1000 * 60 * 60 * 24 );
	const indicatorMaxDate = new Date( this.convertCanvasXToTime( this.mWidth ).getTime() + 1000 * 60 * 60 * 24 );
	// console.log( 'min: %o  /  max: %o', indicatorMinDate, indicatorMaxDate );

	let workingMonth = this.mViewBoundsDateStart.getMonth();
	let workingYear = this.mViewBoundsDateStart.getFullYear();
	let indicatorDate = new Date( workingYear, workingMonth, 1 );
	let indicatorX = this.convertTimeToCanvasX( indicatorDate );

	let indicators = /** @type {{mX:number, mShortLabel:string, mLongLabel:string, mIsShared:boolean}} */( [] );

	const viewNumDays = ( indicatorMaxDate.getTime() - indicatorMinDate.getTime() ) / ( 1000 * 60 * 60 * 24 );
	const indicatorLabelIncrement = Math.max( Math.floor( viewNumDays / 30 / 10 ), 1 );

	// generate indicator locations to the right of the data bounds start
	let isShaded = false;
	let workingIndicatorIndex = 1; // TODO this is wrong
	while( indicatorDate <= indicatorMaxDate )
	{
		let indicator = {
			'mX' : indicatorX,
			'mIsShaded' : isShaded
		};

		if( workingIndicatorIndex % indicatorLabelIncrement === 0 )
		{
			indicator.mLabel = this.INDICATOR_MONTH_NAMES[ workingMonth ] + ' ' + workingYear.toString();
		}

		indicators.push( indicator );
		isShaded = !isShaded;
		++workingIndicatorIndex;

		++workingMonth;
		while( workingMonth >= 12 )
		{
			workingMonth -= 12;
			++workingYear;
		}

		indicatorDate = new Date( workingYear, workingMonth, 1 );
		indicatorX = this.convertTimeToCanvasX( indicatorDate );
	}

	// generate indicator locations to the left of the data bounds start
	workingMonth = this.mViewBoundsDateStart.getMonth() - 1;
	workingYear = this.mViewBoundsDateStart.getFullYear();
	indicatorDate = new Date( workingYear, workingMonth, 1 );
	indicatorX = this.convertTimeToCanvasX( indicatorDate );
	isShaded = true;
	workingIndicatorIndex = 0;
	while( indicatorDate >= indicatorMinDate )
	{
		let indicator = {
			'mX' : indicatorX,
			'mIsShaded' : isShaded
		};

		if( workingIndicatorIndex % indicatorLabelIncrement === 0 )
		{
			indicator.mLabel = this.INDICATOR_MONTH_NAMES[ workingMonth ] + ' ' + workingYear.toString();
		}

		indicators.unshift( indicator );
		isShaded = !isShaded;
		++workingIndicatorIndex;

		--workingMonth;
		while( workingMonth < 0 )
		{
			workingMonth += 12;
			--workingYear;
		}

		indicatorDate = new Date( workingYear, workingMonth, 1 );
		indicatorX = this.convertTimeToCanvasX( indicatorDate );
	}

	// draw the indicator bands
	// this.mContext.strokeStyle = 'rgb(255,255,255)';
	for( let i = 0; i < indicators.length; ++i )
	{
		// draw the shaded block
		if( i > 0 && indicators[ i ].mIsShaded )
		{
			this.mContext.fillStyle = 'rgba(255,255,255,0.1)'; // TODO should be configurable
			this.mContext.fillRect( indicators[ i - 1 ].mX, 0, indicators[ i ].mX - indicators[ i - 1 ].mX, this.mHeight )
		}

		// draw the line
		// this.mContext.beginPath();
		// this.mContext.moveTo( indicators[ i ].mX, 0 );
		// this.mContext.lineTo( indicators[ i ].mX, this.mHeight );
		// this.mContext.stroke();

		// draw the text
		if( indicators[ i ].mLabel )
		{
			this.mContext.fillStyle = 'rgba(255,255,255,0.5)'; // TODO should be configurable
			this.mContext.fillText(
				indicators[ i ].mLabel,
				indicators[ i ].mX + this.PADDING,
				this.mHeight - this.mTimelineHeight - this.PADDING * 2
			);
		}
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Draws the tree
 *
 * @private
 */
ViewCanvas.prototype.drawTreeHelper = function()
{
	if( this.mRootNodeId === null || this.mTreeNodeLookup === null || this.mViewLeftTime === null )
	{
		return;
	}

	const root = this.getRootNode();

	this.mContext.save();

	// console.log( this.mViewTopOffset );
	// this.mContext.translate( 0, this.mViewTopOffset );

	root.drawConnectingLine( this, null, false, true );

	if( this.mHoveredElements.length > 0 )
	{
		for( let i = 0; i < this.mHoveredElements.length; ++i )
		{
			if( this.mHoveredElements[ i ] instanceof ViewCanvasNode )
			{
				this.highlightTrajectory( /** @type {ViewCanvasNode} */( this.mHoveredElements[ i ] ) );
			}
		}
	}
	else if( this.mSelectedElement instanceof ViewCanvasNode )
	{
		this.highlightTrajectory( /** @type {ViewCanvasNode} */( this.mSelectedElement ) );
	}

	root.drawNode( this, this.mHoveredElements, this.mSelectedElement );

	this.mContext.restore();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Helper function to highlight the trajectory from the given node to its parent
 *
 * @private
 * @param {ViewCanvasNode} node
 */
ViewCanvas.prototype.highlightTrajectory = function( node )
{
	if( node.mParent === null )
	{
		return;
	}

	node.drawConnectingLine(
		this,
		this.calculateNodePoint( node.mParent ),
		true,
		false
	);

	this.highlightTrajectory( node.mParent );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Draws the time line
 *
 * @private
 */
ViewCanvas.prototype.drawTimelineHelper = function()
{
	if( this.mTimelineHeight === 0 )
	{
		return;
	}

	this.mContext.save();

	this.mContext.font = this.mFontSize + 'pt ' + this.mFontFace;

	// draw the timeline entries
	if( this.mViewLeftTime !== null )
	{
		for( let i = 0; i < this.mTimelineEvents.length; ++i )
		{
			this.mTimelineEvents[ i ].draw( this, this.mHoveredElements, this.mSelectedElement );
		}
	}

	// draw the divider line
	this.mContext.lineWidth = 1;
	this.mContext.strokeStyle = 'rgb(200,200,200)';
	this.mContext.beginPath();
	this.mContext.moveTo( 0, this.mHeight - this.mTimelineHeight );
	this.mContext.lineTo( this.mWidth, this.mHeight - this.mTimelineHeight );
	this.mContext.stroke();

	this.mContext.restore();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {ViewCanvasItem} item
 */
ViewCanvas.prototype.drawHoverPopup = function( item )
{
	const hoverDetails = item.getHoverDetails( this );

	// the label is bold, so keep that separate, but add all of the other lines to an array
	const nonBoldLines = [];

	// add the description
	if( hoverDetails.mDescription.trim().length > 0 )
	{
		nonBoldLines.push( hoverDetails.mDescription.trim() );
	}

	// add all of the additional lines
	if( hoverDetails.mAdditionalTextLines.length > 0 )
	{
		// add the addition lines
		for( let i = 0; i < hoverDetails.mAdditionalTextLines.length; ++i )
		{
			nonBoldLines.push( hoverDetails.mAdditionalTextLines[ i ].trim() );
		}
	}

	this.mContext.save();

	// set to bold first
	this.mContext.font = 'bold ' + this.mFontSize + 'pt ' + this.mFontFace;

	let width = this.mContext.measureText( hoverDetails.mLabel.trim() ).width;

	// back to normal
	this.mContext.font = this.mFontSize + 'pt ' + this.mFontFace;

	for( let i = 0; i < nonBoldLines.length; ++i )
	{
		width = Math.max( width, this.mContext.measureText( nonBoldLines[ i ] ).width );
	}

	width += this.PADDING * 2;

	let height = ( nonBoldLines.length + 1 ) * ( this.mFontSize + this.PADDING ) + this.PADDING * 2;

	// apply a min width/height
	width = Math.max( width, 200 );
	height = Math.max( height, 100 );

	// make sure this fits on the screen
	let topLeft = new ViewCanvasPoint( hoverDetails.mPoint.mX, hoverDetails.mPoint.mY - height - this.HOVER_POPUP_ARROW_SIZE );

	// draw the background
	this.mContext.beginPath();
	this.mContext.fillStyle = 'rgb(255,255,255)'; // TODO customizable
	this.mContext.lineWidth = 1;
	this.mContext.moveTo( topLeft.mX, topLeft.mY );
	this.mContext.lineTo( topLeft.mX + width, topLeft.mY );
	this.mContext.lineTo( topLeft.mX + width, topLeft.mY + height );
	this.mContext.lineTo( topLeft.mX + this.HOVER_POPUP_ARROW_SIZE, topLeft.mY + height );
	this.mContext.lineTo( topLeft.mX, topLeft.mY + height + this.HOVER_POPUP_ARROW_SIZE );
	this.mContext.lineTo( topLeft.mX, topLeft.mY );
	this.mContext.fill();

	// draw the label highlight
	this.mContext.beginPath();
	this.mContext.fillStyle = this.mAccentColor;
	this.mContext.moveTo( topLeft.mX, topLeft.mY );
	this.mContext.lineTo( topLeft.mX + width, topLeft.mY );
	this.mContext.lineTo( topLeft.mX + width, topLeft.mY + this.mFontSize + this.PADDING * 2 );
	this.mContext.lineTo( topLeft.mX, topLeft.mY + this.mFontSize + this.PADDING * 2 );
	this.mContext.lineTo( topLeft.mX, topLeft.mY );
	this.mContext.fill();

	// first draw the label
	if( hoverDetails.mLabel.trim().length > 0 )
	{
		// set to bold first again
		this.mContext.font = 'bold ' + this.mFontSize + 'pt ' + this.mFontFace;
		this.mContext.fillStyle = this.mTextColor;

		this.mContext.fillText(
			hoverDetails.mLabel.trim(),
			topLeft.mX + this.PADDING,
			topLeft.mY + this.PADDING + this.mFontSize
		);
	}

	// back to normal for the rest of the text
	this.mContext.fillStyle = 'rgb(0,0,0)'; // TODO should be configurable
	this.mContext.font = this.mFontSize + 'pt ' + this.mFontFace;

	// draw the other lines
	for( let i = 0; i < nonBoldLines.length; ++i )
	{
		if( nonBoldLines[ i ].length > 0 )
		{
			this.mContext.fillText(
				nonBoldLines[ i ],
				topLeft.mX + this.PADDING,
				topLeft.mY + ( this.PADDING + this.mFontSize ) * ( i + 2 ) + this.PADDING
			);
		}
	}

	this.mContext.restore();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

ViewCanvas.prototype.recalculateNodeLocations = function()
{
	if( this.mRootNodeId === null || this.mTreeNodeLookup === null )
	{
		return;
	}

	const root = this.getRootNode();
	root.mY = 0;

	root.calculateChildNodesY();

	// this.logTree( 999 );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {?Date} date
 * @returns {number} the X-coordinate
 */
ViewCanvas.prototype.convertTimeToCanvasX = function( date )
{
	if( date === null || this.mViewLeftTime === null )
	{
		return 0;
	}

	return ( date.getTime() - this.mViewLeftTime.getTime() ) / this.MILLISECONDS_IN_HOUR / this.mZoom;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

ViewCanvas.prototype.convertCanvasXToTime = function( canvasX )
{
	if( this.mViewLeftTime === null || canvasX === null )
	{
		console.warn( 'cannot convert canvas X to time!' );
		return null;
	}
	else
	{
		return new Date( canvasX * this.mZoom * this.MILLISECONDS_IN_HOUR + this.mViewLeftTime.getTime() );
	}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

ViewCanvas.prototype.convertYLocationToCanvas = function( rawYLocation )
{
	return ( rawYLocation - this.mViewTopOffset ) / this.mZoom;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

ViewCanvas.prototype.convertCanvasYToLocation = function( canvasY )
{
	return canvasY * this.mZoom + this.mViewTopOffset;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery.Event} mouseEvent
 */
ViewCanvas.prototype.onMouseMove = function( mouseEvent )
{
	mouseEvent.stopPropagation();

	const mouseEventCanvasPoint = this.mouseEventToCanvasPixelLocation( mouseEvent );

	// var previousHoveredElementId = this.mHoveredElement === null
	// 	? null
	// 	: this.mHoveredElement.mBase.id;

	if( this.mIsDragging )
	{
		if( mouseEvent.which === 1 )
		{
			if( this.mViewLeftTime === null )
			{
				return;
			}

			this.mViewLeftTime = new Date( this.mViewLeftTime.getTime()
				+ this.convertCanvasXToTime( this.mDragLastCanvasPoint.mX ).getTime()
				- this.convertCanvasXToTime( mouseEventCanvasPoint.mX ).getTime() );

			// console.log( '>>> %d', mouseEventCanvasPoint.mY );

			this.mViewTopOffset += mouseEventCanvasPoint.mY - this.mDragLastCanvasPoint.mY;

			this.mDragLastCanvasPoint = mouseEventCanvasPoint;

			// this.recalculateNodeLocations();
		}
		else
		{
			// not the left mouse, so just bail
			this.mIsDragging = false;
		}

		this.redraw();
	}
	else
	{
		this.mHoveredElements.length = 0;

		let element = this.determineElementAtCanvasPoint( mouseEventCanvasPoint );
		if( element !== null )
		{
			this.mHoveredElements.push( element );
		}

		this.redraw();
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery.Event} mouseEvent
 */
ViewCanvas.prototype.onMouseDown = function( mouseEvent )
{
	mouseEvent.stopPropagation();

	if( mouseEvent.which !== 1 )
	{
		return;
	}

	const mouseEventCanvasPoint = this.mouseEventToCanvasPixelLocation( mouseEvent );

	this.mIsDragging = true;
	this.mDragTarget = null; // TODO finish!
	this.mDragLastCanvasPoint = mouseEventCanvasPoint;

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery.Event} mouseEvent
 */
ViewCanvas.prototype.onMouseUp = function( mouseEvent )
{
	mouseEvent.stopPropagation();

	const mouseEventCanvasPoint = this.mouseEventToCanvasPixelLocation( mouseEvent );

	if( this.mIsDragging )
	{
		this.mIsDragging = false;

		this.mSelectedElement = this.determineElementAtCanvasPoint( mouseEventCanvasPoint );

		if( this.mSelectedElement === null )
		{
			if( this.mOnDeselectCallback )
			{
				this.mOnDeselectCallback();
			}
		}
		else if( this.mSelectedElement instanceof ViewCanvasNode )
		{
			if( this.mOnSelectNodeCallback )
			{
				this.mOnSelectNodeCallback( this.mSelectedElement.mBase );
			}
		}
		else if( this.mSelectedElement instanceof ViewCanvasTimelineEvent )
		{
			if( this.mOnSelectTimelineEventCallback )
			{
				this.mOnSelectTimelineEventCallback( this.mSelectedElement.mBase );
			}
		}

		this.redraw();
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery.Event} mouseEvent
 */
ViewCanvas.prototype.onDoubleClick = function( mouseEvent )
{
	mouseEvent.stopPropagation();

	// var mouseEventCanvasPoint = this.mouseEventToCanvasPixelLocation( mouseEvent );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery.Event} mouseEvent
 */
ViewCanvas.prototype.onMouseWheel = function( mouseEvent )
{
	mouseEvent.stopPropagation();

	const delta = Math.max( -1, Math.min( 1, mouseEvent.wheelDelta || -mouseEvent.detail ) );

	if( delta === 0 )
	{
		return;
	}

	const mouseEventCanvasPoint = this.mouseEventToCanvasPixelLocation( mouseEvent );

	const xBeforeZoom = this.convertCanvasXToTime( mouseEventCanvasPoint.mX ).getTime();

	this.setZoomStep( delta > 0 ? Math.max( this.mZoomStep - 1, 0 ) : Math.min( this.mZoomStep + 1, this.mNumZoomSteps - 1 ) );

	const xAfterZoom = this.convertCanvasXToTime( mouseEventCanvasPoint.mX ).getTime();

	this.mViewLeftTime = new Date( this.mViewLeftTime.getTime() + xBeforeZoom - xAfterZoom );

	this.redraw();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {jQuery.Event} mouseEvent
 * @returns {ViewCanvasPoint} the canvas point
 */
ViewCanvas.prototype.mouseEventToCanvasPixelLocation = function( mouseEvent )
{
	const rect = this.mCanvasElement.getBoundingClientRect();

	return new ViewCanvasPoint(
		mouseEvent.clientX - rect.left,
		mouseEvent.clientY - rect.top
	);
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @param {ViewCanvasNode} nodePoint
 * @returns {ViewCanvasPoint}
 */
ViewCanvas.prototype.calculateNodePoint = function( nodePoint )
{
	return new ViewCanvasPoint(
		this.convertTimeToCanvasX( nodePoint.mDrawDate ),
		nodePoint.mY + this.mViewTopOffset
	);
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {ViewCanvasTimelineEvent} timelineEvent
 * @returns {{mX: number, mY: number, mWidth: number, mHeight: number, mPadding: number}}
 */
ViewCanvas.prototype.calculateTimelineEventBounds = function( timelineEvent )
{
	const start = this.convertTimeToCanvasX( timelineEvent.mBase.start );
	const end = this.convertTimeToCanvasX( timelineEvent.mBase.end );
	const barHeight = this.mFontSize + this.PADDING * 2;

	return {
		'mX' : start,
		'mY' : this.mHeight - this.mTimelineHeight + this.PADDING * 2 + timelineEvent.mLine * ( barHeight + this.PADDING * 2 ),
		'mWidth' : end - start,
		'mHeight' : barHeight,
		'mPadding' : this.PADDING
	};
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {ViewCanvasPoint} canvasPoint
 *
 * @returns {?ViewCanvasItem}
 */
ViewCanvas.prototype.determineElementAtCanvasPoint = function( canvasPoint )
{
	const rootNode = this.getRootNode();
	if( rootNode !== null )
	{
		let result = rootNode.hitTest( canvasPoint, this );
		if( result !== null )
		{
			return result;
		}
	}

	for( let i = 0; i < this.mTimelineEvents.length; ++i )
	{
		let result = this.mTimelineEvents[ i ].hitTest( canvasPoint, this );
		if( result !== null )
		{
			return result;
		}
	}

	return null;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Determines the maximum text that will fit in the given width
 *
 * @param {string} fullText the full text string desired
 * @param {number} maxWidth the maximum width possible
 *
 * @returns {string} the string that will fit
 */
ViewCanvas.prototype.determineMaxText = function( fullText, maxWidth )
{
	let workingText = fullText;

	let textWidth = this.mContext.measureText( workingText ).width;

	while( workingText.length > 0 && textWidth > maxWidth )
	{
		workingText = workingText.substr( 0, workingText.length - 1 );
		textWidth = this.mContext.measureText( workingText ).width;
	}

	return workingText;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// ViewCanvas.prototype.logTree = function( maxDepth )
// {
// 	this.logTreeHelper( this.getRootNode(), 0, maxDepth );
//
// 	return;
// };

// /**
//  * @private
//  * @param {ViewCanvasNode} node
//  * @param {number} depth
//  * @param {number} maxDepth
//  */
// ViewCanvas.prototype.logTreeHelper = function( node, depth, maxDepth )
// {
// 	var line = '';
//
// 	var i;
// 	for( i = 0; i < depth; ++i )
// 	{
// 		line += '                    ';
// 	}
//
// 	line += '['
// 		+ 'id:' + node.mBase.id
// 		+ '/t:' + node.mBase.type
// 		+ '/y:' + node.mY.toFixed( 0 )
// 		+ '/h:' + node.mHeight.toString()
// 		+ ']';
//
// 	console.log( line );
//
// 	if( depth >= maxDepth )
// 	{
// 		return;
// 	}
//
// 	for( i = 0; i < node.mChildren.length; ++i )
// 	{
// 		this.logTreeHelper( node.mChildren[ i ], depth + 1, maxDepth );
// 	}
//
// 	return;
// };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
