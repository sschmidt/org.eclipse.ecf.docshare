/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.internal.docshare;

import java.util.Hashtable;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.docshare.DocShare;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ecf.docshare"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private BundleContext context;

	private ServiceTracker containerManagerTracker;

	private boolean listenerActive;

	private static final Hashtable docsharechannels = new Hashtable();

	public DocShare getDocShare(ID containerID) {
		return (DocShare) docsharechannels.get(containerID);
	}

	public DocShare addDocShare(ID containerID, IChannelContainerAdapter channelAdapter) throws ECFException {
		return (DocShare) docsharechannels.put(containerID, new DocShare(channelAdapter));
	}

	public DocShare removeDocShare(ID containerID) {
		return (DocShare) docsharechannels.remove(containerID);
	}

	/**
	 * The constructor
	 */
	public Activator() {
		// nothing to do
	}

	public boolean isListenerActive() {
		return listenerActive;
	}

	public void setListenerActive(boolean active) {
		this.listenerActive = active;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext ctxt) throws Exception {
		super.start(ctxt);
		plugin = this;
		this.context = ctxt;
		setListenerActive(true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext ctxt) throws Exception {
		super.stop(ctxt);
		if (containerManagerTracker != null) {
			containerManagerTracker.close();
			containerManagerTracker = null;
		}
		plugin = null;
		this.context = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public IContainerManager getContainerManager() {
		if (containerManagerTracker == null) {
			containerManagerTracker = new ServiceTracker(context, IContainerManager.class.getName(), null);
			containerManagerTracker.open();
		}
		return (IContainerManager) containerManagerTracker.getService();
	}

}
