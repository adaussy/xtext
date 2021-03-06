/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.idea.build.daemon;

import com.google.common.base.Objects;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.eclipse.xtext.builder.standalone.LanguageAccess;
import org.eclipse.xtext.idea.build.daemon.BuildDaemonModule;
import org.eclipse.xtext.idea.build.daemon.IdeaStandaloneBuilder;
import org.eclipse.xtext.idea.build.daemon.Protocol;
import org.eclipse.xtext.idea.build.daemon.XtextBuildParameters;
import org.eclipse.xtext.idea.build.daemon.XtextBuildResultCollector;
import org.eclipse.xtext.idea.build.daemon.XtextLanguages;
import org.eclipse.xtext.idea.build.net.ObjectChannel;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * @author Jan Koehnlein - Initial contribution and API
 */
@SuppressWarnings("all")
public class XtextBuildDaemon {
  public static class Arguments {
    private int port;
  }
  
  public static class Server {
    @Inject
    private Provider<XtextBuildDaemon.Worker> workerProvider;
    
    public void run(final XtextBuildDaemon.Arguments arguments) {
      try {
        ServerSocket serverSocket = null;
        try {
          XtextBuildDaemon.LOG.info((("Starting xtext build daemon at port " + Integer.valueOf(arguments.port)) + "..."));
          SelectorProvider _provider = SelectorProvider.provider();
          final AbstractSelector socketSelector = _provider.openSelector();
          final ServerSocketChannel serverChannel = ServerSocketChannel.open();
          serverChannel.configureBlocking(false);
          InetAddress _byName = InetAddress.getByName("127.0.0.1");
          final InetSocketAddress socketAddress = new InetSocketAddress(_byName, arguments.port);
          ServerSocket _socket = serverChannel.socket();
          _socket.bind(socketAddress);
          serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
          boolean receivedRequests = false;
          XtextBuildDaemon.LOG.info("... success");
          boolean shutdown = false;
          while ((!shutdown)) {
            {
              XtextBuildDaemon.LOG.info("Accepting connections...");
              try {
                socketSelector.select(5000);
                Set<SelectionKey> _selectedKeys = socketSelector.selectedKeys();
                for (final SelectionKey key : _selectedKeys) {
                  boolean _isAcceptable = key.isAcceptable();
                  if (_isAcceptable) {
                    SocketChannel socketChannel = null;
                    try {
                      SocketChannel _accept = serverChannel.accept();
                      socketChannel = _accept;
                      boolean _equals = Objects.equal(socketChannel, null);
                      if (_equals) {
                        if ((!receivedRequests)) {
                          int _soTimeout = serverSocket.getSoTimeout();
                          String _plus = ("No requests within " + Integer.valueOf(_soTimeout));
                          String _plus_1 = (_plus + "ms.");
                          XtextBuildDaemon.LOG.info(_plus_1);
                          shutdown = true;
                        }
                      } else {
                        socketChannel.configureBlocking(true);
                        receivedRequests = true;
                        XtextBuildDaemon.Worker _get = this.workerProvider.get();
                        boolean _serve = _get.serve(socketChannel);
                        shutdown = _serve;
                      }
                    } finally {
                      if (socketChannel!=null) {
                        socketChannel.close();
                      }
                    }
                  }
                }
              } catch (final Throwable _t) {
                if (_t instanceof Exception) {
                  final Exception exc = (Exception)_t;
                  XtextBuildDaemon.LOG.error("Error during build", exc);
                } else {
                  throw Exceptions.sneakyThrow(_t);
                }
              }
            }
          }
        } catch (final Throwable _t) {
          if (_t instanceof Exception) {
            final Exception exc = (Exception)_t;
            XtextBuildDaemon.LOG.error("Error starting server socket", exc);
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
        XtextBuildDaemon.LOG.info("Shutting down");
        if (serverSocket!=null) {
          serverSocket.close();
        }
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    }
  }
  
  public static class Worker {
    @Inject
    private IdeaStandaloneBuilder standaloneBuilder;
    
    @Inject
    private XtextBuildResultCollector resultCollector;
    
    private ObjectChannel channel;
    
    public boolean serve(final SocketChannel socketChannel) {
      ObjectChannel _objectChannel = new ObjectChannel(socketChannel);
      this.channel = _objectChannel;
      final Serializable msg = this.channel.readObject();
      boolean _matched = false;
      if (!_matched) {
        if (msg instanceof Protocol.StopServer) {
          _matched=true;
          XtextBuildDaemon.LOG.info("Received StopServer");
          return true;
        }
      }
      if (!_matched) {
        if (msg instanceof Protocol.BuildRequest) {
          _matched=true;
          XtextBuildDaemon.LOG.info("Received BuildRequest. Start build...");
          final Protocol.BuildResult buildResult = this.build(((Protocol.BuildRequest)msg));
          XtextBuildDaemon.LOG.info("...finished.");
          this.channel.writeObject(buildResult);
          XtextBuildDaemon.LOG.info("Result sent.");
        }
      }
      return false;
    }
    
    public Protocol.BuildResult build(final Protocol.BuildRequest request) {
      Protocol.BuildResult _xblockexpression = null;
      {
        this.resultCollector.setOutput(this.channel);
        final Procedure1<IdeaStandaloneBuilder> _function = new Procedure1<IdeaStandaloneBuilder>() {
          @Override
          public void apply(final IdeaStandaloneBuilder it) {
            it.setFailOnValidationError(false);
            Map<String, LanguageAccess> _languageAccesses = XtextLanguages.getLanguageAccesses();
            it.setLanguages(_languageAccesses);
            XtextBuildParameters _xtextBuildParameters = new XtextBuildParameters(request);
            it.setBuildData(_xtextBuildParameters);
            it.setBuildResultCollector(Worker.this.resultCollector);
            it.launch();
          }
        };
        ObjectExtensions.<IdeaStandaloneBuilder>operator_doubleArrow(
          this.standaloneBuilder, _function);
        _xblockexpression = this.resultCollector.getBuildResult();
      }
      return _xblockexpression;
    }
  }
  
  private final static Logger LOG = Logger.getLogger(XtextBuildDaemon.class);
  
  public static void main(final String[] args) {
    try {
      Category _parent = XtextBuildDaemon.LOG.getParent();
      TTCCLayout _tTCCLayout = new TTCCLayout();
      FileAppender _fileAppender = new FileAppender(_tTCCLayout, "xtext_builder_daemon.log", true);
      _parent.addAppender(_fileAppender);
      XtextBuildDaemon.LOG.setLevel(Level.INFO);
      BuildDaemonModule _buildDaemonModule = new BuildDaemonModule();
      final Injector injector = Guice.createInjector(_buildDaemonModule);
      XtextBuildDaemon.Server _instance = injector.<XtextBuildDaemon.Server>getInstance(XtextBuildDaemon.Server.class);
      XtextBuildDaemon.Arguments _parse = XtextBuildDaemon.parse(args);
      _instance.run(_parse);
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception exc = (Exception)_t;
        String _message = exc.getMessage();
        String _plus = ("Error " + _message);
        System.err.println(_plus);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  private static XtextBuildDaemon.Arguments parse(final String... args) {
    final XtextBuildDaemon.Arguments arguments = new XtextBuildDaemon.Arguments();
    final Iterator<String> i = ((List<String>)Conversions.doWrapArray(args)).iterator();
    while (i.hasNext()) {
      {
        final String arg = i.next();
        boolean _matched = false;
        if (!_matched) {
          if (Objects.equal(arg, "-port")) {
            _matched=true;
            String _next = i.next();
            int _parseInt = Integer.parseInt(_next);
            arguments.port = _parseInt;
          }
        }
        if (!_matched) {
          throw new IllegalArgumentException((("Invalid argument \'" + arg) + "\'"));
        }
      }
    }
    if ((arguments.port == 0)) {
      throw new IllegalArgumentException("Port number must be set");
    }
    return arguments;
  }
}
