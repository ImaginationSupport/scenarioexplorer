package com.imaginationsupport.helpers;

import java.util.*;

public abstract class YamlHelper
{
	public interface YamlItem
	{
		List< String > generateYamlLines( final int indentLevel );
	}

	public static class YamlObject implements YamlItem
	{
		private final Map< String, YamlItem > mItems;

		public YamlObject()
		{
			mItems = new LinkedHashMap<>();

			return;
		}

		public YamlObject( final Comparator< String > comparator )
		{
			mItems = new TreeMap<>( comparator );

			return;
		}

		public int size()
		{
			return mItems.size();
		}

		public boolean isEmpty()
		{
			return mItems.isEmpty();
		}

		public void add( final String key, final String value )
		{
			mItems.put( key, new YamlString( "\"" + value.replaceAll( "\"", "&quot;" ) + "\"" ) );

			return;
		}

		public void add( final String key, final YamlItem value )
		{
			mItems.put( key, value );

			return;
		}

		public void add( final String key, final boolean value )
		{
			mItems.put( key, new YamlString( value ? "true" : "false" ) );

			return;
		}

		public void add( final int key, final String value )
		{
			mItems.put( Integer.toString( key ), new YamlString( value.replaceAll( "\"", "&quot;" ) ) );

			return;
		}

		public void add( final int key, final YamlItem value )
		{
			mItems.put( Integer.toString( key ), value );

			return;
		}

		@Override
		public String toString()
		{
			return String.join( System.lineSeparator(), generateYamlLines( 0 ) );
		}

		public List< String > generateYamlLines( final int indentLevel )
		{
			final String indent = generateIndentPrefix( indentLevel );
			final List< String > lines = new ArrayList<>();

			for( final Map.Entry< String, YamlItem > entry : mItems.entrySet() )
			{
				if( entry.getValue() instanceof YamlString )
				{
					lines.add( String.format( "%s%s: %s", indent, entry.getKey(), entry.getValue() ) );
				}
				else if( entry.getValue() instanceof YamlObject )
				{
					lines.add( String.format( "%s%s:", indent, entry.getKey() ) );
					lines.addAll( entry.getValue().generateYamlLines( indentLevel + 1 ) );
				}
				else if( entry.getValue() instanceof YamlArray )
				{
					final YamlArray asArray = (YamlArray)entry.getValue();
					if( asArray.isEmpty() )
					{
						lines.add( String.format( "%s%s: []", indent, entry.getKey() ) );
					}
					else
					{
						lines.add( String.format( "%s%s:", indent, entry.getKey() ) );
						lines.addAll( entry.getValue().generateYamlLines( indentLevel + 1 ) );
					}
				}
				else
				{
					lines.add( "[unknown!]" );
				}
			}

			return lines;
		}
	}

	public static class YamlArray implements YamlItem
	{
		private final List< YamlItem > mItems;

		public YamlArray()
		{
			mItems = new ArrayList<>();

			return;
		}

		public void add( final String value )
		{
			mItems.add( new YamlString( "\"" + value + "\"" ) );

			return;
		}

		public void add( final String name, final String value )
		{
			add( name, new YamlString( "\"" + value + "\"" ) );

			return;
		}

		public void add( final String name, final YamlItem value )
		{
			mItems.add( new YamlCollectionEntry( name, value ) );

			return;
		}

		public void add( final YamlItem value )
		{
			mItems.add( value );

			return;
		}

		public int size()
		{
			return mItems.size();
		}

		public boolean isEmpty()
		{
			return mItems.isEmpty();
		}

		public List< String > generateYamlLines( final int indentLevel )
		{
			final String indent = generateIndentPrefix( indentLevel );
			final List< String > lines = new ArrayList<>();

			for( final YamlItem item : mItems )
			{
				if( item instanceof YamlString )
				{
					lines.add( String.format( "%s- %s", indent, item ) );
				}
				else if( item instanceof YamlCollectionEntry )
				{
					final YamlCollectionEntry collectionEntry = (YamlCollectionEntry)item;

					if( ( (YamlCollectionEntry)item ).getValue() instanceof YamlString )
					{
						lines.add( String.format( "%s- %s: %s", indent, collectionEntry.getName(), collectionEntry.getValue() ) );
					}
					else
					{
						lines.add( String.format( "%s- %s:", indent, collectionEntry.getName() ) );
						lines.addAll( collectionEntry.getValue().generateYamlLines( indentLevel + 2 ) );
					}
				}
				else if( item instanceof YamlObject )
				{
					final YamlObject childObject = (YamlObject)item;
					final List< String > childLines = childObject.generateYamlLines( indentLevel + 1 );

					// update the first line
					childLines.set( 0, String.format( "%s- %s", indent, childLines.get( 0 ).trim() ) );

					lines.addAll( childLines );
				}
				else
				{
					lines.add( String.format( "===== not implemented: %s =====", item.getClass() ) );
				}
			}

			return lines;
		}
	}

	private static class YamlString implements YamlItem
	{
		private final String mValue;

		YamlString( final String value )
		{
			mValue = value;

			return;
		}

		@Override
		public String toString()
		{
			return mValue;
		}

		public List< String > generateYamlLines( final int indentLevel )
		{
			return new ArrayList<>();
		}
	}

	private static class YamlCollectionEntry implements YamlItem
	{
		private final String mName;
		private final YamlItem mValue;

		public String getName()
		{
			return mName;
		}

		public YamlItem getValue()
		{
			return mValue;
		}

		YamlCollectionEntry( final String name, final YamlItem value )
		{
			mName = name;
			mValue = value;
			return;
		}

		@Override
		public List< String > generateYamlLines( final int indentLevel )
		{
			return new ArrayList<>();
		}
	}

	private static String generateIndentPrefix( final int indentLevel )
	{
		final StringBuilder indent = new StringBuilder();
		for( int i = 0; i < indentLevel; ++i )
		{
			indent.append( "  " );
		}

		return indent.toString();
	}
}
