/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.resourceloader.BaseWrappingResourceLoader;
import org.kuali.rice.core.resourceloader.ContextClassLoaderBinder;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;

import edu.iu.uis.eden.plugin.client.PluginListener;

/**
 * A KEW Plugin.  A Plugin represents a distinct classloading space living below (as a child) of the core
 * KEW classloader.  It allows for loading of plugin resources from core components of the system.
 * Essentially a Plugin is a specialized ResourceLoader with a custom classloader and attached configuration.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Plugin extends BaseWrappingResourceLoader {

	private static final Logger LOG = Logger.getLogger(Plugin.class);
    private Config config;
    private List<PluginListener> pluginListeners = new ArrayList<PluginListener>();

    private boolean supressStartupFailure = true;
    private boolean started = false;
    private boolean startupFailure = false;

    public Plugin(QName name, Config config, ClassLoader classLoader) {
    	super(name, classLoader);
    	this.config = config;
    }

    /**
     * Starts the plugin.
     */
    public synchronized void start() {
        if (started) {
            LOG.info(getLogPrefix()+" has already been started.");
            return;
        }
        LOG.info(getLogPrefix()+" Starting...");
        try {
            bindThread();
            try {
                startupFailure = false;
                started = true;
                super.start();
                LOG.info("Starting plugin listeners");
                startPluginListeners();
            } finally {
                unbindThread();
            }
            ClassLoader classLoader = getClassLoader();
            LOG.info(getLogPrefix()+" ...started." + (classLoader != null ? classLoader.toString() : ""));
        } catch (Throwable t) {
            LOG.error(getLogPrefix()+" Failure starting plugin.", t);
            startupFailure = true;
            started = true;
            stop();
            if (!supressStartupFailure) {
            	if (t instanceof Error) {
            		throw (Error)t;
            	} else if (t instanceof RuntimeException) {
            		throw (RuntimeException)t;
            	}
            	throw new WorkflowRuntimeException("Failed to startup plugin.", t);
            }
        }
    }

    /**
     * Stops the plugin.
     */
    public synchronized void stop() {
        if (!started) {
            LOG.info(getLogPrefix()+" has already been stopped.");
            return;
        }
        LOG.info(getLogPrefix()+" Stopping...");
        bindThread();
        try {
            started = false;
            stopPluginListeners();
            // stop resource loaders of super class
            super.stop();
        } catch (Throwable t) {
        	LOG.error(getLogPrefix()+" Failed when attempting to stop the plugin.", t);
        } finally {
            unbindThread();
        }
        resetPlugin();
        LOG.info(getLogPrefix()+" ...stopped.");
    }

    public boolean isStarted() {
        return started;
    }

    public void addPluginListener(PluginListener pluginListener) {
    	pluginListeners.add(pluginListener);
    }

    public void removePluginListener(PluginListener pluginListener) {
    	pluginListeners.remove(pluginListener);
    }

    protected void startPluginListeners() {
        for (Iterator iterator = pluginListeners.iterator(); iterator.hasNext();) {
            PluginListener listener = (PluginListener) iterator.next();
            listener.pluginInitialized(this);
        }
    }

    /**
     * If we fail to stop a plugin listener, try the next one but don't propogate any
     * exceptions out of this method.  Otherwise the plugin ends up dying and can't be
     * reloaded from a hot deploy.
     */
    protected void stopPluginListeners() {
        for (Iterator iterator = pluginListeners.iterator(); iterator.hasNext();) {
            PluginListener listener = (PluginListener) iterator.next();
            try {
            	listener.pluginDestroyed(this);
            } catch (Throwable t) {
            	LOG.error(getLogPrefix()+" Failed when invoking pluginDestroyed on Plugin Listener '"+listener.getClass().getName()+"'.", t);
            }
        }
    }

    public boolean isSupressStartupFailure() {
		return supressStartupFailure;
	}

	public void setSupressStartupFailure(boolean supressStartupFailure) {
		this.supressStartupFailure = supressStartupFailure;
	}

	public void bindThread() {
		ContextClassLoaderBinder.bind(getClassLoader());
    }

    public void unbindThread() {
        ContextClassLoaderBinder.unbind();
    }

	/**
     * Cleanup plugin resources.
     */
    private void resetPlugin() {
        if (!startupFailure) {
        	setClassLoader(null);
        }
        pluginListeners.clear();
    }

    private String getLogPrefix() {
        return toString();
    }

    public Config getConfig() {
    	return config;
    }

    public String toString() {
        return "[Plugin: " + this.getName() + "]";
    }

}