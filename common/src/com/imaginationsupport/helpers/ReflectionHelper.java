package com.imaginationsupport.helpers;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public abstract class ReflectionHelper
{
	/**
	 * Entrypoint for using reflection to list the contents of a package
	 *
	 * @param useSystemClassloader true to use the system classloader, false to use the local class classloader
	 * @param packageName          the name of the package to search through
	 *
	 * @return the list of classes
	 */
	public static Set< Class< ? > > listClassesInPackage( final boolean useSystemClassloader, final String packageName ) throws GeneralScenarioExplorerException
	{
		final String packageNameAsPath = packageName.replace( '.', '/' );
		final Set< Class< ? > > classesInPackage = new TreeSet<>( new AlphabeticalApiObjectSorter() );

		try
		{
			final ClassLoader classLoader = useSystemClassloader
				? ClassLoader.getSystemClassLoader()
				: ReflectionHelper.class.getClassLoader();

			final List< URL > urls = Collections.list( classLoader.getResources( packageNameAsPath ) );
			for( final URL url : urls )
			{
				final String protocol = url.getProtocol();
				switch( protocol )
				{
					case "file":
						classesInPackage.addAll( findClassesInFileUrl( packageName, url ) );
						break;

					case "jar":
						classesInPackage.addAll( findClassesInJarUrl( packageName, url ) );
						break;

					default:
						throw new GeneralScenarioExplorerException( String.format( "Unknown resource protocol: %s", protocol ) );
				}
			}
		}
		catch( final IOException e )
		{
			throw new GeneralScenarioExplorerException( "Error listing classes in package!", e );
		}

		return classesInPackage;
	}

	/**
	 * Helper function to get the classes from a file:// URI
	 *
	 * @param packageName the package name this references
	 * @param url         the URL to search in
	 *
	 * @return the classes in this directory
	 */
	private static Set< Class< ? > > findClassesInFileUrl( final String packageName, final URL url ) throws GeneralScenarioExplorerException
	{
		return processFileUrlDirectory( packageName, new File( url.getFile() ) );
	}

	/**
	 * Helper function for file:// URI's
	 *
	 * @param packageName the package name this source references
	 * @param source      the file to search in
	 */
	private static Set< Class< ? > > processFileUrlDirectory( final String packageName, final File source ) throws GeneralScenarioExplorerException
	{
		final Set< Class< ? > > classesFound = new HashSet<>();

		final File[] files = source.listFiles();
		if( files != null )
		{
			for( final File file : files )
			{
				classesFound.addAll( findClassesInFileUrlHelper( packageName, file ) );
			}
		}

		return classesFound;
	}

	/**
	 * Helper function for file:// URI's
	 *
	 * @param packageName the package name this file source references
	 * @param source      the file to search inside
	 */
	private static Set< Class< ? > > findClassesInFileUrlHelper( final String packageName, final File source ) throws GeneralScenarioExplorerException
	{
		final Set< Class< ? > > classesFound = new HashSet<>();

		if( source.isDirectory() )
		{
			processFileUrlDirectory( combinePackageWithSubPackage( packageName, source ), source );
		}
		else if( source.getName().endsWith( ".class" ) )
		{
			try
			{
				final Class< ? > classToTest = Class.forName( combinePackageWithSubPackage( packageName, source ) );

				// ignore anonymous classes because they cause issues sorting, and aren't useful anyone
				if( !classToTest.isAnonymousClass() )
				{
					classesFound.add( classToTest );
				}
			}
			catch( final ClassNotFoundException e )
			{
				throw new GeneralScenarioExplorerException( "Error finding class!", e );
			}
		}

		return classesFound;
	}

	private static Set< Class< ? > > findClassesInJarUrl( final String packageName, final URL url ) throws GeneralScenarioExplorerException
	{
		final Set< Class< ? > > classesFound = new HashSet<>();

		final JarURLConnection connection;
		try
		{
			final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

			connection = (JarURLConnection)url.openConnection();
			final String jarPath = connection.getJarFileURL().getFile();

			try( final JarInputStream jarInputStream = new JarInputStream( new FileInputStream( jarPath ) ) )
			{
				for( JarEntry entry = jarInputStream.getNextJarEntry(); entry != null; entry = jarInputStream.getNextJarEntry() )
				{
					if( !entry.isDirectory() )
					{
						final String entryClassName = entry.getRealName().substring( 0, entry.getRealName().length() - 6 ).replaceAll( "/", "." );
						if( entryClassName.startsWith( packageName + "." ) )
						{
							final Class< ? > classFound = Class.forName( entryClassName, true, systemClassLoader );

							if( !classFound.isSynthetic() && !classFound.isAnonymousClass() )
							{
								classesFound.add( classFound );
							}
						}
					}
				}
			}
			catch( final ClassNotFoundException e )
			{
				throw new GeneralScenarioExplorerException( "Error loading class!", e );
			}
		}
		catch( final IOException e )
		{
			throw new GeneralScenarioExplorerException( "Error loading JAR contents!", e );
		}

		return classesFound;
	}

	private static String combinePackageWithSubPackage( final String packageName, final File source )
	{
		final String sourceName = source.getName();

		if( sourceName.endsWith( ".class" ) )
		{
			return packageName + '.' + sourceName.substring( 0, sourceName.length() - 6 );
		}
		else
		{
			return packageName + '.' + sourceName;
		}
	}

	/**
	 * Sorts the ApiObject classes alphabetically
	 */
	public static class AlphabeticalApiObjectSorter implements Comparator< Class< ? > >
	{
		@Override
		public int compare( final Class< ? > classA, final Class< ? > classB )
		{
			return classA.getCanonicalName().compareTo( classB.getCanonicalName() );
		}
	}
}
